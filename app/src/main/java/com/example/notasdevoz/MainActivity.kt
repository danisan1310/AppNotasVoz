package com.example.notasdevoz

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // NAVEGACIÓN: Controlador de pantallas
                val navController = rememberNavController()
                val vModel: AudioViewModel = viewModel()
                val context = LocalContext.current

                // MANEJO DE ERRORES: Observa mensajes del ViewModel y muestra Toasts
                LaunchedEffect(vModel.statusMessage) {
                    vModel.statusMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        vModel.statusMessage = null
                    }
                }

                // Estructura de navegación entre Home y Record
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(vModel) { navController.navigate("record") }
                    }
                    composable("record") {
                        RecordScreen(vModel) { navController.popBackStack() }
                    }
                }
            }
        }
    }
}

/**
 * PANTALLA PRINCIPAL: Lista de audios y controles globales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vModel: AudioViewModel, onNavigateToRecord: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Notas de Voz") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToRecord) { Text("+") }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Lista dinámica de audios
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(vModel.audioList) { audio ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = { vModel.playAudio(audio.ruta) }
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(audio.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(audio.fecha, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { vModel.deleteAudio(audio) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // PANTALLA DE AUDIO (Integrada): Controles de reproducción
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Estado: ${vModel.playerState}", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { vModel.pauseAudio() }) { Text("PAUSA") }
                        Button(onClick = { vModel.resumeAudio() }) { Text("PLAY") }
                        Button(onClick = { vModel.stopPlayback() }) { Text("STOP") }
                    }
                }
            }
        }
    }
}

/**
 * PANTALLA DE GRABACIÓN: Gestión de permisos y MediaRecorder.
 */
@Composable
fun RecordScreen(vModel: AudioViewModel, onBack: () -> Unit) {
    val context = LocalContext.current

    // PERMISOS EN RUNTIME: Solicita acceso al micrófono
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) vModel.startRecording()
        else Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (vModel.isRecording) "GRABANDO..." else "LISTO PARA GRABAR")
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (vModel.isRecording) { vModel.stopRecording(); onBack() }
                else launcher.launch(Manifest.permission.RECORD_AUDIO)
            }
        ) {
            Text(if (vModel.isRecording) "DETENER Y GUARDAR" else "GRABAR")
        }

        TextButton(onClick = onBack) { Text("VOLVER ATRÁS") }
    }
}