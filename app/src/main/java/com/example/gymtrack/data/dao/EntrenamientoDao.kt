package com.example.gymtrack.data.dao

import androidx.room.*
import com.example.gymtrack.data.model.SesionEntrenamiento
import com.example.gymtrack.data.model.SerieRealizada
import kotlinx.coroutines.flow.Flow

@Dao
interface EntrenamientoDao {

    @Insert
    suspend fun insertarSesion(sesion: SesionEntrenamiento): Long

    @Insert
    suspend fun insertarSerie(serie: SerieRealizada)

    // Obtener todas las sesiones de una rutina (para ver el historial)
    @Query("SELECT * FROM sesiones_entrenamiento WHERE rutinaId = :rutinaId ORDER BY fecha DESC")
    fun obtenerHistorialSesiones(rutinaId: Int): Flow<List<SesionEntrenamiento>>

    // Obtener todas las series de una sesión específica
    @Query("SELECT * FROM series_realizadas WHERE sesionId = :sesionId")
    fun obtenerSeriesDeSesion(sesionId: Int): Flow<List<SerieRealizada>>
}