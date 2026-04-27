package com.example.notasdevoz

import android.app.Application
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel encargado de la lógica de grabación, reproducción y gestión de archivos.
 * Hereda de AndroidViewModel para tener acceso al contexto de la aplicación.
 */
class AudioViewModel(application: Application) : AndroidViewModel(application) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    // ESTADOS EN COMPOSE: Controlan la interfaz dinámicamente
    var isRecording by mutableStateOf(false)
    var audioList = mutableStateListOf<AudioNote>() // Lista reactiva de audios
    var playerState by mutableStateOf("IDLE") // Estado: REPRODUCIENDO, PAUSADO, etc.
    var statusMessage by mutableStateOf<String?>(null) // Para mostrar Toasts de error

    init {
        loadAudios() // Carga los archivos al iniciar la app
    }

    /**
     * GESTIÓN DE FICHEROS: Escanea el almacenamiento interno (filesDir)
     * y actualiza la lista de objetos AudioNote.
     */
    fun loadAudios() {
        try {
            audioList.clear()
            val files = getApplication<Application>().filesDir.listFiles()
            files?.filter { it.extension == "m4a" }
                ?.sortedByDescending { it.lastModified() }
                ?.forEach {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    audioList.add(AudioNote(
                        id = it.name,
                        nombre = it.name,
                        ruta = it.absolutePath,
                        fecha = sdf.format(Date(it.lastModified()))
                    ))
                }
        } catch (e: Exception) {
            statusMessage = "Error al leer archivos internos"
        }
    }

    /**
     * GRABACIÓN: Configura el MediaRecorder y genera nombres automáticos.
     */
    fun startRecording() {
        try {
            // Genera nombre basado en tiempo (Requisito: Gestión de ficheros)
            val fileName = "Grabacion_${System.currentTimeMillis()}.m4a"
            val audioFile = File(getApplication<Application>().filesDir, fileName)

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(getApplication())
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }
            isRecording = true
        } catch (e: Exception) {
            statusMessage = "Error: Verifica los permisos de micrófono"
            isRecording = false
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } finally {
            mediaRecorder = null
            isRecording = false
            loadAudios() // Refresca la lista tras guardar
        }
    }

    /**
     * REPRODUCCIÓN MULTIMEDIA: Controla el ciclo de vida del MediaPlayer.
     */
    fun playAudio(ruta: String) {
        val file = File(ruta)
        // MANEJO DE ERRORES: Archivo no encontrado
        if (!file.exists()) {
            statusMessage = "El archivo no existe en el almacenamiento"
            return
        }

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(ruta)
                prepare() // Requisito: Preparar audio
                playerState = "PREPARADO"
                start()
                playerState = "REPRODUCIENDO"

                // Detectar fin de reproducción (Requisito: Reproducción)
                setOnCompletionListener { playerState = "TERMINADO" }
            }
        } catch (e: Exception) {
            statusMessage = "Error en la reproducción multimedia"
            playerState = "ERROR"
        }
    }

    fun pauseAudio() {
        mediaPlayer?.let { if (it.isPlaying) { it.pause(); playerState = "PAUSADO" } }
    }

    fun resumeAudio() {
        if (playerState == "PAUSADO") { mediaPlayer?.start(); playerState = "REPRODUCIENDO" }
    }

    fun stopPlayback() {
        mediaPlayer?.stop()
        playerState = "DETENIDO"
    }

    fun deleteAudio(audio: AudioNote) {
        if (File(audio.ruta).delete()) loadAudios()
    }
}