package com.example.gymtrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtrack.data.GymRepository
import com.example.gymtrack.data.model.Ejercicio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
    data object Loading : UiState<Nothing>
}

@HiltViewModel
class EjercicioViewModel @Inject constructor(
    private val repository: GymRepository
) : ViewModel() {

    val uiState: StateFlow<UiState<List<Ejercicio>>> = repository.allEjercicios
        .map<List<Ejercicio>, UiState<List<Ejercicio>>> { UiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    private val _mensajeError = MutableSharedFlow<String>()
    val mensajeError: SharedFlow<String> = _mensajeError.asSharedFlow()

    fun addEjercicio(nombre: String, grupoMuscular: String, esPesoCorporal: Boolean) {
        viewModelScope.launch {
            try {
                val nuevoEjercicio = Ejercicio(
                    nombre = nombre,
                    grupoMuscular = grupoMuscular,
                    esPesoCorporal = esPesoCorporal
                )
                repository.insertEjercicio(nuevoEjercicio)
            } catch (e: Exception) {
                _mensajeError.emit("Error al guardar el ejercicio")
            }
        }
    }

    fun deleteEjercicio(ejercicio: Ejercicio) {
        viewModelScope.launch {
            try {
                repository.deleteEjercicio(ejercicio)
            } catch (e: Exception) {
                _mensajeError.emit("No se puede borrar: el ejercicio está en uso")
            }
        }
    }
}