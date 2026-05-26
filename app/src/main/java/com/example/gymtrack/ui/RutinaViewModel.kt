package com.example.gymtrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtrack.data.GymRepository
import com.example.gymtrack.data.model.DiaRutina
import com.example.gymtrack.data.model.Ejercicio
import com.example.gymtrack.data.model.Rutina
import com.example.gymtrack.data.model.SerieRealizada
import com.example.gymtrack.data.model.SesionEntrenamiento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RutinaViewModel @Inject constructor(
    private val repository: GymRepository
) : ViewModel() {

    val allRutinas: StateFlow<List<Rutina>> = repository.allRutinas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEjercicios: StateFlow<List<Ejercicio>> = repository.allEjercicios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedRutinaId = MutableStateFlow<Int?>(null)
    val selectedRutinaId: StateFlow<Int?> = _selectedRutinaId.asStateFlow()

    val dias: StateFlow<List<DiaRutina>> = _selectedRutinaId
        .flatMapLatest { id ->
            if (id != null) repository.getDiasByRutina(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDiaId = MutableStateFlow<Int?>(null)
    val selectedDiaId: StateFlow<Int?> = _selectedDiaId.asStateFlow()

    val ejerciciosEnDia: StateFlow<List<Ejercicio>> = _selectedDiaId
        .flatMapLatest { id ->
            if (id != null) repository.getEjerciciosByDia(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ejerciciosNoEnDia: StateFlow<List<Ejercicio>> = combineFlowsForEjerciciosNoEnDia()

    private fun combineFlowsForEjerciciosNoEnDia(): StateFlow<List<Ejercicio>> {
        return allEjercicios
        // Filtering happens in the screen to keep it simple
    }

    fun selectRutina(rutinaId: Int) {
        _selectedRutinaId.value = rutinaId
        _selectedDiaId.value = null
    }

    fun clearSelection() {
        _selectedRutinaId.value = null
        _selectedDiaId.value = null
    }

    fun selectDia(diaId: Int) {
        _selectedDiaId.value = diaId
    }

    fun clearDiaSelection() {
        _selectedDiaId.value = null
    }

    fun createRutina(nombre: String, objetivo: String?) {
        viewModelScope.launch {
            repository.createRutina(nombre, objetivo)
        }
    }

    fun deleteRutina(rutina: Rutina) {
        viewModelScope.launch {
            repository.deleteRutina(rutina)
            if (_selectedRutinaId.value == rutina.id) {
                clearSelection()
            }
        }
    }

    fun createDia(nombre: String) {
        val rutinaId = _selectedRutinaId.value ?: return
        viewModelScope.launch {
            repository.createDia(rutinaId, nombre)
        }
    }

    fun deleteDia(dia: DiaRutina) {
        viewModelScope.launch {
            repository.deleteDia(dia)
            if (_selectedDiaId.value == dia.id) {
                clearDiaSelection()
            }
        }
    }

    fun addEjercicioToDia(ejercicioId: Int) {
        val diaId = _selectedDiaId.value ?: return
        viewModelScope.launch {
            repository.addEjercicioToDia(diaId, ejercicioId)
        }
    }

    fun removeEjercicioFromDia(ejercicioId: Int) {
        val diaId = _selectedDiaId.value ?: return
        viewModelScope.launch {
            repository.removeEjercicioFromDia(diaId, ejercicioId)
        }
    }

    fun getEjerciciosNoEnDia(): Flow<List<Ejercicio>> {
        val diaId = _selectedDiaId.value ?: return flowOf(emptyList())
        return repository.getEjerciciosByDia(diaId)
    }

    // Recording state
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSets = MutableStateFlow<Map<Int, List<SetInput>>>(emptyMap())
    val recordingSets: StateFlow<Map<Int, List<SetInput>>> = _recordingSets.asStateFlow()

    fun startRecording(ejerciciosEnDia: List<Ejercicio>) {
        _isRecording.value = true
        _recordingSets.value = ejerciciosEnDia.associate { it.id to emptyList<SetInput>() }
    }

    fun cancelRecording() {
        _isRecording.value = false
        _recordingSets.value = emptyMap()
    }

    fun addSetToRecording(ejercicioId: Int) {
        val current = _recordingSets.value.toMutableMap()
        val sets = current.getOrDefault(ejercicioId, emptyList()).toMutableList()
        sets.add(SetInput())
        current[ejercicioId] = sets
        _recordingSets.value = current
    }

    fun updateSetInRecording(ejercicioId: Int, index: Int, set: SetInput) {
        val current = _recordingSets.value.toMutableMap()
        val sets = current.getOrDefault(ejercicioId, emptyList()).toMutableList()
        if (index in sets.indices) {
            sets[index] = set
            current[ejercicioId] = sets
            _recordingSets.value = current
        }
    }

    fun removeSetFromRecording(ejercicioId: Int, index: Int) {
        val current = _recordingSets.value.toMutableMap()
        val sets = current.getOrDefault(ejercicioId, emptyList()).toMutableList()
        if (index in sets.indices) {
            sets.removeAt(index)
            current[ejercicioId] = sets
            _recordingSets.value = current
        }
    }

    fun saveRecording(): Boolean {
        val rutinaId = _selectedRutinaId.value ?: return false
        val sets = _recordingSets.value

        val allSeries = sets.flatMap { (ejercicioId, setList) ->
            setList.mapNotNull { setInput ->
                val peso = setInput.peso.toDoubleOrNull() ?: return@mapNotNull null
                val repeticiones = setInput.repeticiones.toIntOrNull() ?: return@mapNotNull null
                if (peso <= 0 || repeticiones <= 0) return@mapNotNull null
                SerieRealizada(
                    sesionId = 0,
                    ejercicioId = ejercicioId,
                    peso = peso,
                    repeticiones = repeticiones,
                    rpe = setInput.rpe.toIntOrNull()
                )
            }
        }

        if (allSeries.isEmpty()) return false

        viewModelScope.launch {
            repository.registerSesionConSeries(
                sesion = SesionEntrenamiento(
                    rutinaId = rutinaId,
                    fecha = System.currentTimeMillis()
                ),
                series = allSeries
            )
            cancelRecording()
        }
        return true
    }
}

data class SetInput(
    val peso: String = "",
    val repeticiones: String = "",
    val rpe: String = ""
)
