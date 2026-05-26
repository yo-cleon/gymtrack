package com.example.gymtrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gymtrack.data.model.SerieConEjercicio
import com.example.gymtrack.data.model.SerieRealizada
import com.example.gymtrack.data.model.SesionEntrenamiento
import kotlinx.coroutines.flow.Flow

@Dao
interface EntrenamientoDao {

    @Insert
    suspend fun insertSesion(sesion: SesionEntrenamiento): Long

    @Insert
    suspend fun insertSerie(serie: SerieRealizada)

    @Query("SELECT * FROM sesiones_entrenamiento WHERE rutinaId = :rutinaId ORDER BY fecha DESC")
    fun getSesionesByRutina(rutinaId: Int): Flow<List<SesionEntrenamiento>>

    @Query("SELECT * FROM series_realizadas WHERE sesionId = :sesionId")
    fun getSeriesBySesion(sesionId: Int): Flow<List<SerieRealizada>>

    @Query("""
        SELECT s.id, s.sesionId, s.ejercicioId, e.nombre AS nombreEjercicio,
               s.peso, s.repeticiones, s.rpe
        FROM series_realizadas s
        INNER JOIN ejercicios e ON s.ejercicioId = e.id
        WHERE s.sesionId = :sesionId
    """)
    fun getSeriesConEjercicioBySesion(sesionId: Int): Flow<List<SerieConEjercicio>>
}