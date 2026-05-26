package com.example.gymtrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtrack.data.GymRepository
import com.example.gymtrack.data.model.Rutina
import com.example.gymtrack.data.model.SerieConEjercicio
import com.example.gymtrack.data.model.SesionEntrenamiento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val repository: GymRepository
) : ViewModel() {

    val allRutinas: StateFlow<List<Rutina>> = repository.allRutinas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedRutinaId = MutableStateFlow<Int?>(null)
    val selectedRutinaId: StateFlow<Int?> = _selectedRutinaId.asStateFlow()

    val sesiones: StateFlow<List<SesionEntrenamiento>> = _selectedRutinaId
        .flatMapLatest { id ->
            if (id != null) repository.getHistorial(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectRutina(rutinaId: Int) {
        _selectedRutinaId.value = rutinaId
    }

    fun clearSelection() {
        _selectedRutinaId.value = null
    }

    fun getSeriesBySesion(sesionId: Int): Flow<List<SerieConEjercicio>> {
        return repository.getSeriesConEjercicioBySesion(sesionId)
    }
}
