package com.example.gymtrack.data

import com.example.gymtrack.data.dao.EjercicioDao
import com.example.gymtrack.data.dao.EntrenamientoDao
import com.example.gymtrack.data.dao.RutinaDao
import com.example.gymtrack.data.model.*
import kotlinx.coroutines.flow.Flow

class GymRepository(
    private val ejercicioDao: EjercicioDao,
    private val rutinaDao: RutinaDao,
    private val entrenamientoDao: EntrenamientoDao
) {

    // --- SECCIÓN EJERCICIOS ---
    val todosLosEjercicios: Flow<List<Ejercicio>> = ejercicioDao.getAllEjercicios()

    suspend fun insertarEjercicio(ejercicio: Ejercicio) {
        ejercicioDao.insertEjercicio(ejercicio)
    }

    suspend fun eliminarEjercicio(ejercicio: Ejercicio) {
        ejercicioDao.deleteEjercicio(ejercicio)
    }

    // --- SECCIÓN RUTINAS ---
    val todasLasRutinas: Flow<List<Rutina>> = rutinaDao.obtenerTodasLasRutinas()

    /**
     * Crea una rutina y le asigna una lista de ejercicios con su orden.
     */
    suspend fun crearRutinaCompleta(rutina: Rutina, ejerciciosIds: List<Int>) {
        val rutinaId = rutinaDao.insertarRutina(rutina).toInt()
        ejerciciosIds.forEachIndexed { indice, ejercicioId ->
            val relacion = EjercicioEnRutina(
                rutinaId = rutinaId,
                ejercicioId = ejercicioId,
                orden = indice + 1
            )
            rutinaDao.insertarEjercicioEnRutina(relacion)
        }
    }

    fun obtenerEjerciciosDeRutina(rutinaId: Int): Flow<List<Ejercicio>> {
        return rutinaDao.obtenerEjerciciosDeRutina(rutinaId)
    }

    // --- SECCIÓN ENTRENAMIENTO ---
    suspend fun registrarSesionConSeries(
        sesion: SesionEntrenamiento,
        series: List<SerieRealizada>
    ) {
        val sesionId = entrenamientoDao.insertarSesion(sesion).toInt()
        series.forEach { serie ->
            // Copiamos la serie asignándole el ID de la sesión recién creada
            val serieConId = serie.copy(sesionId = sesionId)
            entrenamientoDao.insertarSerie(serieConId)
        }
    }

    fun obtenerHistorial(rutinaId: Int): Flow<List<SesionEntrenamiento>> {
        return entrenamientoDao.obtenerHistorialSesiones(rutinaId)
    }
}