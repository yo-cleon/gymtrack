package com.example.gymtrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymtrack.data.model.Ejercicio
import com.example.gymtrack.ui.EjercicioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjercicioScreen(viewModel: EjercicioViewModel) {
    // Escuchamos la lista de ejercicios del ViewModel
    val ejercicios by viewModel.listaEjercicios.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Ejercicios") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn {
                items(ejercicios) { ejercicio ->
                    EjercicioItem(
                        ejercicio = ejercicio,
                        onDelete = { viewModel.borrarEjercicio(ejercicio) }
                    )
                }
            }
        }

        if (mostrarDialogo) {
            AgregarEjercicioDialog(
                onDismiss = { mostrarDialogo = false },
                onConfirm = { nombre, grupo ->
                    viewModel.agregarEjercicio(nombre, grupo, false)
                    mostrarDialogo = false
                }
            )
        }
    }
}

@Composable
fun EjercicioItem(ejercicio: Ejercicio, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = ejercicio.nombre, style = MaterialTheme.typography.titleLarge)
                Text(text = ejercicio.grupoMuscular, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar")
            }
        }
    }
}

@Composable
fun AgregarEjercicioDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var grupo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Ejercicio") },
        text = {
            Column {
                TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = grupo, onValueChange = { grupo = it }, label = { Text("Grupo Muscular") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(nombre, grupo) }) { Text("Guardar") }
        }
    )
}