package com.example.gymtrack.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "ejercicios_en_rutina",
    primaryKeys = ["rutinaId", "ejercicioId"], // Clave compuesta
    foreignKeys = [
        ForeignKey(
            entity = Rutina::class,
            parentColumns = ["id"],
            childColumns = ["rutinaId"],
            onDelete = ForeignKey.CASCADE // Si borras la rutina, se borra esta relación
        ),
        ForeignKey(
            entity = Ejercicio::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.RESTRICT // No permite borrar un ejercicio si está en una rutina
        )
    ],
    indices = [Index("ejercicioId")] // Optimiza la búsqueda
)
data class EjercicioEnRutina(
    val rutinaId: Int,
    val ejercicioId: Int,
    val orden: Int
)