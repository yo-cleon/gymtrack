package com.example.gymtrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gymtrack.data.GymRepository
import com.example.gymtrack.data.model.Ejercicio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EjercicioViewModel(private val repository: GymRepository) : ViewModel() {

    // Convertimos el Flow del repositorio en un StateFlow para que la UI de Compose
    // lo escuche y se actualice automáticamente.
    val listaEjercicios: StateFlow<List<Ejercicio>> = repository.todosLosEjercicios
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarEjercicio(nombre: String, grupoMuscular: String, esPesoCorporal: Boolean) {
        viewModelScope.launch {
            val nuevoEjercicio = Ejercicio(
                nombre = nombre,
                grupoMuscular = grupoMuscular,
                esPesoCorporal = esPesoCorporal
            )
            repository.insertarEjercicio(nuevoEjercicio)
        }
    }

    fun borrarEjercicio(ejercicio: Ejercicio) {
        viewModelScope.launch {
            repository.eliminarEjercicio(ejercicio)
        }
    }
}

/**
 * Esta clase es necesaria porque el ViewModel tiene un constructor con parámetros (el repositorio).
 */
class EjercicioViewModelFactory(private val repository: GymRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EjercicioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EjercicioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}