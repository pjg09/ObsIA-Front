// Ruta: app/src/main/java/com/obsIA/engine/NativeEngine.kt

package com.obsIA.engine

/**
 * Android Native Bridge para el motor médico Obsia. El package com.obsIA.engine debe coincidir
 * exactamente con las firmas JNI del .so compilado. NO cambiar el package name sin recompilar la
 * librería nativa.
 */
class NativeEngine {

    companion object {
        init {
            System.loadLibrary("obsia_jni")
        }
    }

    /**
     * Inicializa el motor.
     * @param modelPath Ruta absoluta al archivo .gguf en el filesystem del dispositivo.
     * @param ragPath Ruta absoluta al archivo chunks.json en el filesystem del dispositivo.
     * @param nThreads Número de hilos (2-4 recomendado para mobile).
     * @return 0 en éxito, valor negativo en error.
     */
    external fun init(modelPath: String, ragPath: String, nThreads: Int): Int

    /**
     * Envía un mensaje al asistente médico. BLOQUEANTE — llamar siempre desde Dispatchers.IO.
     * @param query Síntomas o pregunta del usuario.
     * @return JSON string con campos: "status", "response_text", "processing_ms".
     */
    external fun processQuery(query: String): String

    /** Verifica si el motor está listo para recibir queries. */
    external fun isReady(): Boolean

    /** Libera memoria y recursos del modelo. */
    external fun release()

    /** Retorna el uso actual de memoria del proceso en MB. */
    external fun getMemoryUsageMB(): Int
}
