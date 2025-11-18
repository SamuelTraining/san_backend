import joblib 
import os
from extractor import extraer_texto_pdf, limpiar_texto, extraer_metadatos_con_reglas

# --- Definir nuestro umbral de confianza ---
# Si la IA está por debajo de este %, marcaremos para validación.
# Los documentos dicen 70% como ejemplo.
UMBRAL_CONFIANZA = 0.70  # (es decir, 70%)


# --- 1. CARGAR MODELO Y VECTORIZADOR ---
print("--- Cargando IA (Modelo y Vectorizador) ---")
try:
    modelo_ia = joblib.load('modelo_clasificador.pkl')
    vectorizador = joblib.load('vectorizador_tfidf.pkl')
    print("IA cargada correctamente.")
except FileNotFoundError:
    print("Error: No se encontraron los archivos '.pkl'.")
    print("Por favor, ejecuta 'entrenar.py' primero.")
    exit()
except Exception as e:
    print(f"Error inesperado al cargar los modelos: {e}")
    exit()


def predecir_documento(ruta_archivo: str):
    """
    Toma la ruta de un nuevo PDF, lo procesa, extrae metadatos y predice su categoría,
    indicando si requiere validación humana.
    """
    
    print(f"\n--- Procesando nuevo documento: {ruta_archivo} ---")
    
    # --- FASE 1: Extracción (Usa la función con OCR) ---
    texto_crudo = extraer_texto_pdf(ruta_archivo)
    if not texto_crudo:
        print("Error: No se pudo extraer texto del documento.")
        return

    # --- FASE 1.5: EXTRACCIÓN DE METADATOS (NER) ---
    print("...Buscando metadatos (NER)...")
    metadatos = extraer_metadatos_con_reglas(texto_crudo)

    # --- FASE 2: Limpieza (para la clasificación) ---
    texto_limpio = limpiar_texto(texto_crudo)
    if not texto_limpio:
        print("Error: El documento no contiene texto limpiable (quizás estaba vacío).")
        return
    
    # --- FASE 3: Vectorización ---
    try:
        texto_vectorizado = vectorizador.transform([texto_limpio])
    except Exception as e:
        print(f"Error al vectorizar el texto: {e}")
        return
        
    # --- FASE 4: Predicción ---
    prediccion = modelo_ia.predict(texto_vectorizado)
    probabilidades = modelo_ia.predict_proba(texto_vectorizado)
    
    categoria_predicha = prediccion[0]
    confianza = max(probabilidades[0]) # El score más alto
    
    # --- FASE 5: LÓGICA DE VALIDACIÓN (NUEVO) ---
    estado_validacion = ""
    if confianza >= UMBRAL_CONFIANZA:
        estado_validacion = "CLASIFICACIÓN AUTOMÁTICA (Confiable)"
    else:
        estado_validacion = "REQUIERE VALIDACIÓN MANUAL (Baja Confianza)"

    
    # --- RESULTADO (MODIFICADO) ---
    print("\n========= RESULTADO DE LA IA =========")
    print(f"Categoría Predicha: {categoria_predicha}")
    print(f"Confianza (Score): {confianza * 100:.2f}%")
    print(f"Estado: {estado_validacion}") # <-- NUEVA LÍNEA
    
    # Imprimir los metadatos si se encontraron
    if metadatos:
        print("\n--- Metadatos Extraídos (NER) ---")
        for clave, valor in metadatos.items():
            valor_limpio = valor.replace('\n', ' ').strip()
            print(f"  - {clave}: {valor_limpio}...")
    else:
         print("\n--- Metadatos Extraídos (NER) ---")
         print("  - No se encontraron metadatos con las reglas actuales.")
         
    print("========================================")


# --- Zona de Pruebas (Procesamiento Masivo) ---
if __name__ == "__main__":
    
    carpeta_ingesta = "archivos_para_clasificar"
    
    print(f"\n--- Iniciando Proceso de Predicción Masiva ---")
    print(f"Buscando archivos PDF en: {carpeta_ingesta}")

    if not os.path.isdir(carpeta_ingesta):
        print(f"Error: La carpeta '{carpeta_ingesta}' no existe.")
        print("Por favor, crea la carpeta y añade algunos PDFs para predecir.")
        exit()
        
    archivos_procesados = 0
    
    for nombre_archivo in os.listdir(carpeta_ingesta):
        if nombre_archivo.lower().endswith(".pdf"):
            ruta_completa = os.path.join(carpeta_ingesta, nombre_archivo)
            predecir_documento(ruta_completa)
            print("--------------------------------------------------") 
            archivos_procesados += 1

    if archivos_procesados == 0:
        print(f"No se encontraron archivos PDF en la carpeta '{carpeta_ingesta}'.")
    else:
        print(f"\nPredicción masiva finalizada. Se procesaron {archivos_procesados} documentos.")