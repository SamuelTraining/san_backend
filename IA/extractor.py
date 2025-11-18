import pdfplumber
import os
import spacy
from spacy.matcher import Matcher 

# --- Imports para OCR ---
from pdf2image import convert_from_path
import pytesseract
from PIL import Image 

# --- Configuración de Tesseract ---
# RECUERDA: Arregla esta ruta si Tesseract está instalado en otro lugar.
try:
    pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'
except Exception as e:
    print("ADVERTENCIA (Tesseract): No se encontró Tesseract. El OCR para imágenes no funcionará.")
    print(f"Detalle: {e}")

# --- Configuración de spaCy ---
try:
    nlp = spacy.load("es_core_news_sm", disable=['parser', 'ner'])
    print("Módulo Extractor: Modelo 'es_core_news_sm' cargado.")
except IOError:
    print("Error: Modelo 'es_core_news_sm' no encontrado.")
    from spacy.lang.es import Spanish
    nlp = Spanish()
except Exception as e:
    print(f"Error cargando spaCy: {e}")
    from spacy.lang.es import Spanish
    nlp = Spanish()

# --- Función de extracción (sin cambios) ---
def extraer_texto_pdf(ruta_archivo: str) -> str:
    """
    Abre un PDF y extrae texto.
    Intenta primero con pdfplumber (para PDFs de texto).
    Si falla o saca poco texto, usa OCR (para PDFs escaneados).
    """
    if not os.path.exists(ruta_archivo):
        print(f"Error: El archivo no se encuentra en {ruta_archivo}")
        return ""

    print(f"Abriendo PDF: {ruta_archivo}")
    texto_total = ""
    
    # --- INTENTO 1: Lectura de Texto (pdfplumber) ---
    try:
        with pdfplumber.open(ruta_archivo) as pdf:
            for pagina in pdf.pages:
                texto_pagina = pagina.extract_text()
                if texto_pagina:
                    texto_total += texto_pagina + "\n"
    except Exception as e:
        print(f"Error (pdfplumber) al procesar {ruta_archivo}: {e}")
        texto_total = "" 

    # --- INTENTO 2: Lectura de Imagen (OCR) ---
    if len(texto_total.strip()) < 100: 
        print("Texto no encontrado o muy corto. Reintentando con OCR (Tesseract)...")
        texto_total_ocr = ""
        try:
            # --- ¡REVISA ESTA RUTA! ---
            poppler_path = r"C:\poppler\poppler-24.02.0\bin"
            imagenes = convert_from_path(ruta_archivo, poppler_path=poppler_path)
            
            for i, img in enumerate(imagenes):
                print(f"  ...procesando página {i+1} con OCR...")
                texto_pagina_ocr = pytesseract.image_to_string(img, lang='spa')
                texto_total_ocr += texto_pagina_ocr + "\n"
                
            texto_total = texto_total_ocr 
            
        except Exception as e:
            print(f"Error durante el OCR con Tesseract: {e}")
            print("NOTA: Asegúrate de que la ruta de 'poppler_path' sea correcta.")
            pass 

    return texto_total

# --- Función de limpieza (sin cambios) ---
def limpiar_texto(texto: str) -> str:
    """
    Limpia el texto crudo usando spaCy para prepararlo para la IA.
    """
    if not texto:
        return ""
    
    doc = nlp(texto.lower())
    
    tokens_limpios = []
    for token in doc:
        if not token.is_punct and not token.is_space and not token.is_stop:
            tokens_limpios.append(token.lemma_)
            
    return " ".join(tokens_limpios)


# --- SECCIÓN DE METADATOS (con las reglas flexibles) ---

matcher = Matcher(nlp.vocab)

# 1. Reglas para el Objeto del Contrato
pattern_obj_1 = [{"LOWER": "cláusula"}, {"LIKE_NUM": True}, {"IS_PUNCT": True, "OP": "?"}, {"LOWER": "objeto"}]
pattern_obj_2 = [{"LOWER": "cláusula"}, {"LOWER": "primera"}, {"IS_PUNCT": True, "OP": "?"}, {"LOWER": "objeto"}]
pattern_obj_3 = [{"LOWER": "objeto"}, {"LOWER": "del"}, {"LOWER": "contrato"}]

# 2. Reglas para el Neto a Pagar
pattern_neto_1 = [{"LOWER": "neto"}, {"LOWER": "a"}, {"LOWER": "pagar"}, {"IS_PUNCT": True, "OP": "?"}, {"IS_CURRENCY": True, "OP": "?"}, {"LIKE_NUM": True}]
pattern_neto_2 = [{"LOWER": "valor"}, {"LOWER": "neto"}, {"LOWER": "pagado"}, {"IS_PUNCT": True, "OP": "?"}, {"IS_CURRENCY": True, "OP": "?"}, {"LIKE_NUM": True}]

# 3. Reglas para Asistentes
pattern_asistentes_1 = [{"LOWER": "asistentes"}, {"IS_PUNCT": True, "OP": "?"}]

matcher.add("NETO_PAGAR", [pattern_neto_1, pattern_neto_2])
matcher.add("OBJETO_CONTRATO", [pattern_obj_1, pattern_obj_2, pattern_obj_3])
matcher.add("ASISTENTES", [pattern_asistentes_1])


# --- FUNCIÓN DE EXTRACCIÓN (MODIFICADA) ---
def extraer_metadatos_con_reglas(texto_crudo: str) -> dict:
    """
    Usa el Matcher de spaCy para encontrar entidades clave
    basadas en reglas.
    """
    metadatos_encontrados = {}
    doc = nlp(texto_crudo)
    matches = matcher(doc)
    
    for match_id, start, end in matches:
        rule_id = nlp.vocab.strings[match_id]
        span = doc[start:end]
        
        # --- LÓGICA MODIFICADA ---
        # Si la clave NO existe todavía, la añadimos.
        # Esto hace que la *primera* coincidencia gane.
        
        if rule_id == "NETO_PAGAR" and 'Neto_a_Pagar' not in metadatos_encontrados:
            metadatos_encontrados['Neto_a_Pagar'] = doc[end-1].text
        
        if rule_id == "OBJETO_CONTRATO" and 'Objeto' not in metadatos_encontrados:
            # Guardamos el texto que sigue DESPUÉS del match
            metadatos_encontrados['Objeto'] = doc[end:end+30].text.strip()

        if rule_id == "ASISTENTES" and 'Asistentes' not in metadatos_encontrados:
            metadatos_encontrados['Asistentes'] = doc[end:end+30].text.strip()

    return metadatos_encontrados