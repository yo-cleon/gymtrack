package com.example.gymtrack.data

import com.example.gymtrack.data.dao.EjercicioDao
import com.example.gymtrack.data.dao.EntrenamientoDao
import com.example.gymtrack.data.dao.RutinaDao
import com.example.gymtrack.data.model.DiaRutina
import com.example.gymtrack.data.model.Ejercicio
import com.example.gymtrack.data.model.EjercicioEnDia
import com.example.gymtrack.data.model.EjercicioEnRutina
import com.example.gymtrack.data.model.Rutina
import com.example.gymtrack.data.model.SerieConEjercicio
import com.example.gymtrack.data.model.SerieRealizada
import com.example.gymtrack.data.model.SesionEntrenamiento
import kotlinx.coroutines.flow.Flow

class GymRepository(
    private val ejercicioDao: EjercicioDao,
    private val rutinaDao: RutinaDao,
    private val entrenamientoDao: EntrenamientoDao
) {

    val allEjercicios: Flow<List<Ejercicio>> = ejercicioDao.getAllEjercicios()

    suspend fun insertEjercicio(ejercicio: Ejercicio) {
        ejercicioDao.insertEjercicio(ejercicio)
    }

    suspend fun deleteEjercicio(ejercicio: Ejercicio) {
        ejercicioDao.deleteEjercicio(ejercicio)
    }

    val allRutinas: Flow<List<Rutina>> = rutinaDao.getAllRutinas()

    suspend fun createRutinaCompleta(rutina: Rutina, ejerciciosIds: List<Int>) {
        val rutinaId = rutinaDao.insertRutina(rutina).toInt()
        ejerciciosIds.forEachIndexed { indice, ejercicioId ->
            val relacion = EjercicioEnRutina(
                rutinaId = rutinaId,
                ejercicioId = ejercicioId,
                orden = indice + 1
            )
            rutinaDao.insertEjercicioEnRutina(relacion)
        }
    }

    fun getEjerciciosByRutina(rutinaId: Int): Flow<List<Ejercicio>> {
        return rutinaDao.getEjerciciosByRutina(rutinaId)
    }

    suspend fun registerSesionConSeries(
        sesion: SesionEntrenamiento,
        series: List<SerieRealizada>
    ) {
        val sesionId = entrenamientoDao.insertSesion(sesion).toInt()
        series.forEach { serie ->
            val serieConId = serie.copy(sesionId = sesionId)
            entrenamientoDao.insertSerie(serieConId)
        }
    }

    fun getHistorial(rutinaId: Int): Flow<List<SesionEntrenamiento>> {
        return entrenamientoDao.getSesionesByRutina(rutinaId)
    }

    fun getSeriesConEjercicioBySesion(sesionId: Int): Flow<List<SerieConEjercicio>> {
        return entrenamientoDao.getSeriesConEjercicioBySesion(sesionId)
    }

    suspend fun createRutina(nombre: String, objetivo: String?): Int {
        return rutinaDao.insertRutina(Rutina(nombreRutina = nombre, objetivo = objetivo)).toInt()
    }

    suspend fun deleteRutina(rutina: Rutina) {
        rutinaDao.deleteRutina(rutina)
    }

    fun getDiasByRutina(rutinaId: Int): Flow<List<DiaRutina>> {
        return rutinaDao.getDiasByRutina(rutinaId)
    }

    suspend fun createDia(rutinaId: Int, nombre: String): Int {
        val orden = rutinaDao.nextOrdenForDia(rutinaId)
        return rutinaDao.insertDia(DiaRutina(rutinaId = rutinaId, nombre = nombre, orden = orden)).toInt()
    }

    suspend fun deleteDia(dia: DiaRutina) {
        rutinaDao.deleteDia(dia)
    }

    fun getEjerciciosByDia(diaRutinaId: Int): Flow<List<Ejercicio>> {
        return rutinaDao.getEjerciciosByDia(diaRutinaId)
    }

    suspend fun addEjercicioToDia(diaRutinaId: Int, ejercicioId: Int) {
        val orden = rutinaDao.nextOrdenForEjercicioEnDia(diaRutinaId)
        rutinaDao.insertEjercicioEnDia(EjercicioEnDia(diaRutinaId = diaRutinaId, ejercicioId = ejercicioId, orden = orden))
    }

    suspend fun removeEjercicioFromDia(diaRutinaId: Int, ejercicioId: Int) {
        rutinaDao.deleteEjercicioEnDia(EjercicioEnDia(diaRutinaId = diaRutinaId, ejercicioId = ejercicioId, orden = 0))
    }
}