package com.example.gymtrack.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gymtrack.data.dao.EjercicioDao
import com.example.gymtrack.data.dao.EntrenamientoDao
import com.example.gymtrack.data.dao.RutinaDao
import com.example.gymtrack.data.model.Ejercicio
import com.example.gymtrack.data.model.EjercicioEnRutina
import com.example.gymtrack.data.model.Rutina
import com.example.gymtrack.data.model.SerieRealizada
import com.example.gymtrack.data.model.SesionEntrenamiento

@Database(
    entities = [
        Ejercicio::class,
        Rutina::class,
        EjercicioEnRutina::class,
        SesionEntrenamiento::class,
        SerieRealizada::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ejercicioDao(): EjercicioDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun entrenamientoDao(): EntrenamientoDao
}

