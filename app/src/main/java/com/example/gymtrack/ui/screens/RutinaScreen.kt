package com.example.gymtrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gymtrack.data.model.DiaRutina
import com.example.gymtrack.data.model.Ejercicio
import com.example.gymtrack.data.model.Rutina
import com.example.gymtrack.ui.RutinaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinaScreen(
    viewModel: RutinaViewModel,
    modifier: Modifier = Modifier
) {
    val rutinas by viewModel.allRutinas.collectAsState()
    val selectedRutinaId by viewModel.selectedRutinaId.collectAsState()
    val dias by viewModel.dias.collectAsState()
    val selectedDiaId by viewModel.selectedDiaId.collectAsState()
    val ejerciciosEnDia by viewModel.ejerciciosEnDia.collectAsState()
    val allEjercicios by viewModel.allEjercicios.collectAsState()

    val selectedRutina = rutinas.find { it.id == selectedRutinaId }
    val selectedDia = dias.find { it.id == selectedDiaId }
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingSets by viewModel.recordingSets.collectAsState()

    var showCreateRutinaDialog by remember { mutableStateOf(false) }
    var showCreateDiaDialog by remember { mutableStateOf(false) }
    var showAddEjercicioDialog by remember { mutableStateOf(false) }
    var showCancelRecordingDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            when {
                selectedDia != null && isRecording -> TopAppBar(
                    title = { Text("Registrar: ${selectedDia.nombre}", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        IconButton(onClick = {
                            val hasSets = recordingSets.values.any { sets -> sets.any { it.peso.isNotBlank() || it.repeticiones.isNotBlank() } }
                            if (hasSets) showCancelRecordingDialog = true
                            else viewModel.cancelRecording()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar")
                        }
                    }
                )
                selectedDia != null -> TopAppBar(
                    title = { Text(selectedDia.nombre, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearDiaSelection() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.startRecording(ejerciciosEnDia) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar entrenamiento")
                        }
                    }
                )
                selectedRutina != null -> TopAppBar(
                    title = { Text(selectedRutina.nombreRutina, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
                else -> TopAppBar(title = { Text("Mis Rutinas") })
            }
        },
        floatingActionButton = {
            when {
                selectedDia != null && isRecording -> {
                    FloatingActionButton(onClick = { viewModel.saveRecording() }) {
                        Icon(Icons.Default.Check, contentDescription = "Guardar entrenamiento")
                    }
                }
                selectedDia != null -> {
                    FloatingActionButton(onClick = { showAddEjercicioDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir ejercicio")
                    }
                }
                selectedRutina != null -> {
                    FloatingActionButton(onClick = { showCreateDiaDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir día")
                    }
                }
                else -> {
                    FloatingActionButton(onClick = { showCreateRutinaDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva rutina")
                    }
                }
            }
        }
    ) { padding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(padding)
        when {
            selectedDia != null && isRecording -> RegistroContent(
                ejercicios = ejerciciosEnDia,
                sets = recordingSets,
                onAddSet = { viewModel.addSetToRecording(it) },
                onUpdateSet = { ejercicioId, index, set -> viewModel.updateSetInRecording(ejercicioId, index, set) },
                onRemoveSet = { ejercicioId, index -> viewModel.removeSetFromRecording(ejercicioId, index) },
                modifier = contentModifier
            )
            selectedDia != null -> EjerciciosEnDiaContent(
                ejercicios = ejerciciosEnDia,
                onRemoveEjercicio = { viewModel.removeEjercicioFromDia(it.id) },
                modifier = contentModifier
            )
            selectedRutina != null -> DiasContent(
                dias = dias,
                onDiaClick = { viewModel.selectDia(it.id) },
                onDeleteDia = { viewModel.deleteDia(it) },
                modifier = contentModifier
            )
            else -> RutinasContent(
                rutinas = rutinas,
                onRutinaClick = { viewModel.selectRutina(it.id) },
                onDeleteRutina = { viewModel.deleteRutina(it) },
                modifier = contentModifier
            )
        }
    }

    if (showCreateRutinaDialog) {
        CrearRutinaDialog(
            onDismiss = { showCreateRutinaDialog = false },
            onConfirm = { nombre, objetivo ->
                viewModel.createRutina(nombre, objetivo)
                showCreateRutinaDialog = false
            }
        )
    }

    if (showCreateDiaDialog) {
        CrearDiaDialog(
            onDismiss = { showCreateDiaDialog = false },
            onConfirm = { nombre ->
                viewModel.createDia(nombre)
                showCreateDiaDialog = false
            }
        )
    }

    if (showAddEjercicioDialog && selectedDia != null) {
        AgregarEjercicioADiaDialog(
            ejerciciosDisponibles = allEjercicios,
            ejerciciosEnDia = ejerciciosEnDia,
            onDismiss = { showAddEjercicioDialog = false },
            onAddEjercicio = { ejercicioId ->
                viewModel.addEjercicioToDia(ejercicioId)
            }
        )
    }

    if (showCancelRecordingDialog) {
        AlertDialog(
            onDismissRequest = { showCancelRecordingDialog = false },
            title = { Text("Descartar entrenamiento") },
            text = { Text("¿Estás seguro de que quieres cancelar? Los sets ingresados se perderán.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelRecordingDialog = false
                    viewModel.cancelRecording()
                }) { Text("Descartar") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelRecordingDialog = false }) { Text("Seguir editando") }
            }
        )
    }
}

@Composable
private fun RutinasContent(
    rutinas: List<Rutina>,
    onRutinaClick: (Rutina) -> Unit,
    onDeleteRutina: (Rutina) -> Unit,
    modifier: Modifier = Modifier
) {
    if (rutinas.isEmpty()) {
        Text(
            text = "No hay rutinas creadas.\nPulsa + para crear una.",
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(rutinas, key = { it.id }) { rutina ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onRutinaClick(rutina) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = rutina.nombreRutina,
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (!rutina.objetivo.isNullOrBlank()) {
                            Text(
                                text = rutina.objetivo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { onDeleteRutina(rutina) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar rutina")
                    }
                }
            }
        }
    }
}

@Composable
private fun DiasContent(
    dias: List<DiaRutina>,
    onDiaClick: (DiaRutina) -> Unit,
    onDeleteDia: (DiaRutina) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dias.isEmpty()) {
        Text(
            text = "No hay días en esta rutina.\nPulsa + para añadir un día.",
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(dias, key = { it.id }) { dia ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onDiaClick(dia) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dia.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onDeleteDia(dia) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar día")
                    }
                }
            }
        }
    }
}

@Composable
private fun EjerciciosEnDiaContent(
    ejercicios: List<Ejercicio>,
    onRemoveEjercicio: (Ejercicio) -> Unit,
    modifier: Modifier = Modifier
) {
    if (ejercicios.isEmpty()) {
        Text(
            text = "No hay ejercicios en este día.\nPulsa + para añadir ejercicios.",
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(ejercicios, key = { it.id }) { ejercicio ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ejercicio.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = ejercicio.grupoMuscular,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onRemoveEjercicio(ejercicio) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Quitar ejercicio")
                    }
                }
            }
        }
    }
}

@Composable
private fun CrearRutinaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }
    val puedeGuardar = nombre.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Rutina") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    isError = nombre.isNotEmpty() && nombre.isBlank()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    label = { Text("Objetivo (opcional)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nombre.trim(), objetivo.trim().ifBlank { null }) },
                enabled = puedeGuardar
            ) { Text("Crear") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun CrearDiaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    val puedeGuardar = nombre.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Día") },
        text = {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del día") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nombre.trim()) },
                enabled = puedeGuardar
            ) { Text("Añadir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun AgregarEjercicioADiaDialog(
    ejerciciosDisponibles: List<Ejercicio>,
    ejerciciosEnDia: List<Ejercicio>,
    onDismiss: () -> Unit,
    onAddEjercicio: (Int) -> Unit
) {
    val idsEnDia = ejerciciosEnDia.map { it.id }.toSet()
    val ejerciciosParaAgregar = ejerciciosDisponibles.filter { it.id !in idsEnDia }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir ejercicio") },
        text = {
            if (ejerciciosParaAgregar.isEmpty()) {
                Text(
                    "No hay más ejercicios disponibles. Crea nuevos ejercicios en la pestaña Ejercicios.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(ejerciciosParaAgregar, key = { it.id }) { ejercicio ->
                        TextButton(
                            onClick = {
                                onAddEjercicio(ejercicio.id)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(ejercicio.nombre, modifier = Modifier.weight(1f))
                                Text(
                                    ejercicio.grupoMuscular,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}
