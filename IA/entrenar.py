import os
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.naive_bayes import MultinomialNB
from sklearn.metrics import accuracy_score
import joblib # Para guardar nuestro modelo entrenado

# --- 1. IMPORTAR NUESTRAS HERRAMIENTAS ---
# Importamos las funciones que creamos en el otro archivo
from extractor import extraer_texto_pdf, limpiar_texto

print("--- Iniciando Proceso de Entrenamiento ---")

# --- 2. CARGAR Y PROCESAR DATOS ---
ruta_base_datos = "datos_entrenamiento"
datos = [] # Lista para guardar los textos
etiquetas = [] # Lista para guardar a qué categoría pertenecen

# Recorrer todas las subcarpetas (ej. "Contratos", "Actas")
for categoria in os.listdir(ruta_base_datos):
    ruta_categoria = os.path.join(ruta_base_datos, categoria)
    
    # Ignorar si no es una carpeta
    if not os.path.isdir(ruta_categoria):
        continue
        
    print(f"Cargando categoría: {categoria}")
    
    # Recorrer todos los archivos dentro de la carpeta de categoría
    for archivo in os.listdir(ruta_categoria):
        if archivo.endswith(".pdf"):
            ruta_archivo = os.path.join(ruta_categoria, archivo)
            
            # Usar nuestras funciones de Fase 1 y 2
            texto_crudo = extraer_texto_pdf(ruta_archivo)
            texto_limpio = limpiar_texto(texto_crudo)
            
            if texto_limpio:
                datos.append(texto_limpio)
                etiquetas.append(categoria) # La etiqueta es el nombre de la carpeta

print(f"\nSe procesaron {len(datos)} documentos en {len(set(etiquetas))} categorías.")

if len(datos) == 0:
    print("Error: No se encontraron documentos. Revisa la carpeta 'datos_entrenamiento'.")
else:
    # Convertir a DataFrame de Pandas (más fácil de manejar)
    df = pd.DataFrame({
        'texto': datos,
        'categoria': etiquetas
    })

    # --- 3. FASE 3: VECTORIZACIÓN (Convertir texto a números) ---
    print("Iniciando Fase 3: Vectorización (TF-IDF)")
    
    # max_features=5000: solo nos importan las 5000 palabras más comunes
    vectorizador = TfidfVectorizer(max_features=5000)
    
    X = vectorizador.fit_transform(df['texto']) # Los "inputs" (texto como números)
    y = df['categoria']                       # Los "outputs" (las etiquetas)

    # --- 4. FASE 4: ENTRENAMIENTO DEL MODELO ---
    print("Iniciando Fase 4: Entrenamiento (Naive Bayes)")

    # Separar datos: 80% para entrenar, 20% para probar la precisión
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Usamos "Naive Bayes", un modelo clásico, rápido y bueno para texto
    modelo_ia = MultinomialNB()
    modelo_ia.fit(X_train, y_train)

    # --- 5. EVALUACIÓN ---
    predicciones = modelo_ia.predict(X_test)
    precision = accuracy_score(y_test, predicciones)
    
    print(f"\n¡Entrenamiento finalizado!")
    print(f"Precisión del modelo en datos de prueba: {precision * 100:.2f}%")
    
    # --- 6. GUARDAR EL MODELO ---
    # Guardamos el modelo y el vectorizador para usarlos en el futuro
    joblib.dump(modelo_ia, 'modelo_clasificador.pkl')
    joblib.dump(vectorizador, 'vectorizador_tfidf.pkl')
    
    print("\nModelo y Vectorizador guardados como 'modelo_clasificador.pkl' y 'vectorizador_tfidf.pkl'")