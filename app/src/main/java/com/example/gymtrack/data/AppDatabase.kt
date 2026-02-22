package com.example.gymtrack.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gymtrack.data.dao.* // Importaremos todos los DAOs
import com.example.gymtrack.data.model.* // Importaremos todas las tablas

@Database(
    entities = [
        Ejercicio::class,
        Rutina::class,
        EjercicioEnRutina::class,
        SesionEntrenamiento::class,
        SerieRealizada::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Aquí expondremos los DAOs que la app usará para leer/escribir
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun entrenamientoDao(): EntrenamientoDao

    /* Nota: A medida que crees los DAOs para Rutina, Sesion, etc.,
       los añadiremos aquí abajo igual que el de arriba.
    */

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gym_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

