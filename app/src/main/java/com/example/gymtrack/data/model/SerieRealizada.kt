package com.example.gymtrack.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
//https://gemini.google.com/app/262dfd5205275b1a?hl=es-ES

@Entity(
    tableName = "series_realizadas",
    foreignKeys = [
        ForeignKey(
            entity = SesionEntrenamiento::class,
            parentColumns = ["id"],
            childColumns = ["sesionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ejercicio::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"]
        )
    ],
    indices = [Index("sesionId"), Index("ejercicioId")]
)
data class SerieRealizada(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sesionId: Int,
    val ejercicioId: Int,
    val peso: Double,
    val repeticiones: Int,
    val rpe: Int? = null // Esfuerzo del 1 al 10
)