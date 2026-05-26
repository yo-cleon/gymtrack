package com.example.gymtrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymtrack.data.model.Ejercicio
import com.example.gymtrack.data.model.EjercicioEnRutina
import com.example.gymtrack.data.model.Rutina
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutina(rutina: Rutina): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEjercicioEnRutina(relacion: EjercicioEnRutina)

    @Query("SELECT * FROM rutinas ORDER BY nombreRutina ASC")
    fun getAllRutinas(): Flow<List<Rutina>>

    @Query("""
        SELECT e.* FROM ejercicios e 
        INNER JOIN ejercicios_en_rutina er ON e.id = er.ejercicioId 
        WHERE er.rutinaId = :rutinaId 
        ORDER BY er.orden ASC
    """)
    fun getEjerciciosByRutina(rutinaId: Int): Flow<List<Ejercicio>>

    @Delete
    suspend fun deleteRutina(rutina: Rutina)
}