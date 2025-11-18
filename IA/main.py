import uvicorn  # El servidor que corre la API
from fastapi import FastAPI
from pydantic import BaseModel
import os

# Importar nuestras funciones de IA y el umbral de confianza
from extractor import (
    extraer_texto_pdf, 
    limpiar_texto, 
    extraer_metadatos_con_reglas
)
import joblib # Para cargar los modelos

# --- 1. Definir los modelos de datos ---
# Esto le dice a FastAPI cómo debe ser el JSON de entrada (Petición)
class PeticionDocumento(BaseModel):
    ruta_archivo: str # Java nos enviará un JSON como: {"ruta_archivo": "..."}

# Esto define el JSON de salida (Respuesta)
class RespuestaClasificacion(BaseModel):
    categoria_predicha: str
    confianza: float
    estado_validacion: str
    metadatos: dict
    error: str | None = None # El error será 'None' si todo sale bien


# --- 2. Crear la Aplicación FastAPI ---
app = FastAPI(
    title="API de Clasificación Documental",
    description="Un microservicio que usa IA para clasificar documentos y extraer metadatos."
)

# --- 3. Cargar la IA (solo una vez, al iniciar el servidor) ---
print("Cargando modelos de IA...")
UMBRAL_CONFIANZA = 0.70  # 70%
try:
    modelo_ia = joblib.load('modelo_clasificador.pkl')
    vectorizador = joblib.load('vectorizador_tfidf.pkl')
    print("¡Modelos cargados! Servidor listo.")
except FileNotFoundError:
    print("ERROR CRÍTICO: No se encontraron los archivos 'modelo_clasificador.pkl' o 'vectorizador_tfidf.pkl'")
    print("Por favor, ejecuta 'entrenar.py' primero.")
    modelo_ia = None
    vectorizador = None

# --- 4. Crear el "Endpoint" (La URL) ---
# @app.post() significa que esta función se activa cuando alguien
# envía una petición HTTP POST a la URL "/clasificar"
@app.post("/clasificar", response_model=RespuestaClasificacion)
async def clasificar_documento(peticion: PeticionDocumento):
    """
    Recibe la ruta de un archivo, lo procesa y devuelve la clasificación.
    """
    if not modelo_ia or not vectorizador:
        return RespuestaClasificacion(
            categoria_predicha="", 
            confianza=0, 
            estado_validacion="ERROR", 
            metadatos={}, 
            error="Servidor no inicializado: Modelos de IA no cargados."
        )

    # Verificar si el archivo existe
    if not os.path.exists(peticion.ruta_archivo):
        return RespuestaClasificacion(
            categoria_predicha="", 
            confianza=0, 
            estado_validacion="ERROR", 
            metadatos={}, 
            error=f"Archivo no encontrado en la ruta: {peticion.ruta_archivo}"
        )

    # --- Ejecutar el mismo proceso que 'predecir.py' ---
    try:
        # 1. Extraer
        texto_crudo = extraer_texto_pdf(peticion.ruta_archivo)
        if not texto_crudo:
            return RespuestaClasificacion(categoria_predicha="", confianza=0, estado_validacion="ERROR", metadatos={}, error="No se pudo extraer texto del PDF.")

        # 1.5. Extraer Metadatos
        metadatos = extraer_metadatos_con_reglas(texto_crudo)

        # 2. Limpiar
        texto_limpio = limpiar_texto(texto_crudo)
        if not texto_limpio:
            return RespuestaClasificacion(categoria_predicha="", confianza=0, estado_validacion="ERROR", metadatos={}, error="El documento no contiene texto limpiable.")

        # 3. Vectorizar
        texto_vectorizado = vectorizador.transform([texto_limpio])

        # 4. Predecir
        prediccion = modelo_ia.predict(texto_vectorizado)
        probabilidades = modelo_ia.predict_proba(texto_vectorizado)

        categoria_predicha = prediccion[0]
        confianza = float(max(probabilidades[0])) # Convertir a float

        # 5. Validar
        if confianza >= UMBRAL_CONFIANZA:
            estado_validacion = "CLASIFICACIÓN AUTOMÁTICA (Confiable)"
        else:
            estado_validacion = "REQUIERE VALIDACIÓN MANUAL (Baja Confianza)"

        # ¡Devolver la respuesta JSON!
        return RespuestaClasificacion(
            categoria_predicha=categoria_predicha,
            confianza=confianza,
            estado_validacion=estado_validacion,
            metadatos=metadatos,
            error=None
        )

    except Exception as e:
        # Capturar cualquier error inesperado
        return RespuestaClasificacion(
            categoria_predicha="", 
            confianza=0, 
            estado_validacion="ERROR", 
            metadatos={}, 
            error=f"Error interno del servidor: {str(e)}"
        )
    # --- 5. Correr el Servidor ---
if __name__ == "__main__":
    # Esta es la línea corregida que inicia el servidor
    uvicorn.run(app, host="127.0.0.1", port=8000)