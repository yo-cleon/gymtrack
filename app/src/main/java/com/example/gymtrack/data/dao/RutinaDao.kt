package com.example.gymtrack.data.dao

import androidx.room.*
import com.example.gymtrack.data.model.Rutina
import com.example.gymtrack.data.model.Ejercicio
import com.example.gymtrack.data.model.EjercicioEnRutina
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRutina(rutina: Rutina): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEjercicioEnRutina(relacion: EjercicioEnRutina)

    @Query("SELECT * FROM rutinas ORDER BY nombreRutina ASC")
    fun obtenerTodasLasRutinas(): Flow<List<Rutina>>

    // Esta consulta obtiene los ejercicios de una rutina específica usando el ID
    @Query("""
        SELECT e.* FROM ejercicios e 
        INNER JOIN ejercicios_en_rutina er ON e.id = er.ejercicioId 
        WHERE er.rutinaId = :rutinaId 
        ORDER BY er.orden ASC
    """)
    fun obtenerEjerciciosDeRutina(rutinaId: Int): Flow<List<Ejercicio>>

    @Delete
    suspend fun eliminarRutina(rutina: Rutina)
}