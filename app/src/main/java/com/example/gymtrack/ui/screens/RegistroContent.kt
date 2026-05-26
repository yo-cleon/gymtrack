package com.example.gymtrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gymtrack.data.model.Ejercicio
import com.example.gymtrack.ui.SetInput

@Composable
fun RegistroContent(
    ejercicios: List<Ejercicio>,
    sets: Map<Int, List<SetInput>>,
    onAddSet: (Int) -> Unit,
    onUpdateSet: (Int, Int, SetInput) -> Unit,
    onRemoveSet: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (ejercicios.isEmpty()) {
        Text(
            text = "No hay ejercicios en este día.",
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
            val setsDeEjercicio = sets[ejercicio.id] ?: emptyList()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = ejercicio.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = ejercicio.grupoMuscular,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    setsDeEjercicio.forEachIndexed { index, set ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(24.dp)
                            )
                            OutlinedTextField(
                                value = set.peso,
                                onValueChange = { onUpdateSet(ejercicio.id, index, set.copy(peso = it)) },
                                label = { Text("Peso") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = set.repeticiones,
                                onValueChange = { onUpdateSet(ejercicio.id, index, set.copy(repeticiones = it)) },
                                label = { Text("Reps") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = set.rpe,
                                onValueChange = { onUpdateSet(ejercicio.id, index, set.copy(rpe = it)) },
                                label = { Text("RPE") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { onRemoveSet(ejercicio.id, index) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar serie",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    TextButton(onClick = { onAddSet(ejercicio.id) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Añadir serie")
                    }
                }
            }
        }
    }
}
