package com.example.gymtrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymtrack.data.model.DiaRutina
import com.example.gymtrack.data.model.Ejercicio
import com.example.gymtrack.data.model.EjercicioEnDia
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

    @Insert
    suspend fun insertDia(dia: DiaRutina): Long

    @Query("SELECT * FROM dias_rutina WHERE rutinaId = :rutinaId ORDER BY orden ASC")
    fun getDiasByRutina(rutinaId: Int): Flow<List<DiaRutina>>

    @Query("SELECT COALESCE(MAX(orden), 0) + 1 FROM dias_rutina WHERE rutinaId = :rutinaId")
    suspend fun nextOrdenForDia(rutinaId: Int): Int

    @Delete
    suspend fun deleteDia(dia: DiaRutina)

    @Insert
    suspend fun insertEjercicioEnDia(relacion: EjercicioEnDia)

    @Delete
    suspend fun deleteEjercicioEnDia(relacion: EjercicioEnDia)

    @Query("""
        SELECT e.* FROM ejercicios e 
        INNER JOIN ejercicios_en_dia ed ON e.id = ed.ejercicioId 
        WHERE ed.diaRutinaId = :diaRutinaId 
        ORDER BY ed.orden ASC
    """)
    fun getEjerciciosByDia(diaRutinaId: Int): Flow<List<Ejercicio>>

    @Query("SELECT COALESCE(MAX(orden), 0) + 1 FROM ejercicios_en_dia WHERE diaRutinaId = :diaRutinaId")
    suspend fun nextOrdenForEjercicioEnDia(diaRutinaId: Int): Int
}