package com.example.gymtrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
// IMPORT CLAVE: Esta es la línea que KSP suele echar de menos
import com.example.gymtrack.data.model.Ejercicio
import kotlinx.coroutines.flow.Flow

@Dao
interface EjercicioDao {
    @Query("SELECT * FROM ejercicios ORDER BY nombre ASC")
    fun getAllEjercicios(): Flow<List<Ejercicio>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEjercicio(ejercicio: Ejercicio)

    @Delete
    suspend fun deleteEjercicio(ejercicio: Ejercicio)
}