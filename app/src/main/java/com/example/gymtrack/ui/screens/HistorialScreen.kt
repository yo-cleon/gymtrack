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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymtrack.data.model.Rutina
import com.example.gymtrack.data.model.SerieConEjercicio
import com.example.gymtrack.data.model.SesionEntrenamiento
import com.example.gymtrack.ui.HistorialViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel,
    modifier: Modifier = Modifier
) {
    val rutinas by viewModel.allRutinas.collectAsState()
    val selectedRutinaId by viewModel.selectedRutinaId.collectAsState()
    val sesiones by viewModel.sesiones.collectAsState()

    Scaffold(modifier = modifier,
        topBar = {
            if (selectedRutinaId != null) {
                val rutina = rutinas.find { it.id == selectedRutinaId }
                TopAppBar(
                    title = { Text(rutina?.nombreRutina ?: "Rutina") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            } else {
                TopAppBar(title = { Text("Historial de rutinas") })
            }
        }
    ) { padding ->
        if (selectedRutinaId == null) {
            RutinaList(
                rutinas = rutinas,
                onRutinaClick = { viewModel.selectRutina(it.id) },
                modifier = Modifier.padding(padding)
            )
        } else {
            SesionList(
                sesiones = sesiones,
                viewModel = viewModel,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun RutinaList(
    rutinas: List<Rutina>,
    onRutinaClick: (Rutina) -> Unit,
    modifier: Modifier = Modifier
) {
    if (rutinas.isEmpty()) {
        Text(
            text = "No hay rutinas creadas",
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(rutinas) { rutina ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onRutinaClick(rutina) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = rutina.nombreRutina,
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (!rutina.objetivo.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                        text = rutina.objetivo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SesionList(
    sesiones: List<SesionEntrenamiento>,
    viewModel: HistorialViewModel,
    modifier: Modifier = Modifier
) {
    if (sesiones.isEmpty()) {
        Text(
            text = "No hay sesiones registradas para esta rutina",
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(sesiones, key = { it.id }) { sesion ->
            SesionCard(sesion = sesion, viewModel = viewModel)
        }
    }
}

@Composable
private fun SesionCard(
    sesion: SesionEntrenamiento,
    viewModel: HistorialViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val seriesFlow = remember(sesion.id) { viewModel.getSeriesBySesion(sesion.id) }
    val series by seriesFlow.collectAsState(initial = emptyList())

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm") }
    val fechaStr = remember(sesion.fecha) {
        Instant.ofEpochMilli(sesion.fecha)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(dateFormatter)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fechaStr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir"
                )
            }

            if (!sesion.notas.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sesion.notas,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                if (series.isEmpty()) {
                    Text(
                        text = "No hay series registradas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    series.groupBy { it.nombreEjercicio }.forEach { (nombreEjercicio, seriesDelEjercicio) ->
                        Text(
                            text = nombreEjercicio,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                        seriesDelEjercicio.forEachIndexed { index, serie ->
                            val rpeText = serie.rpe?.let { " · RPE: $it" } ?: ""
                            Text(
                                text = "Serie ${index + 1}: ${formatearPeso(serie.peso)} × ${serie.repeticiones} reps$rpeText",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatearPeso(peso: Double): String {
    return if (peso == peso.toLong().toDouble()) {
        peso.toLong().toString() + " kg"
    } else {
        "%.1f kg".format(peso)
    }
}
