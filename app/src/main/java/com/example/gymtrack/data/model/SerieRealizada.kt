package com.example.gymtrack.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    ]
)
data class SerieRealizada(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sesionId: Int,
    val ejercicioId: Int,
    val peso: Double,
    val repeticiones: Int,
    val rpe: Int? = null // Esfuerzo del 1 al 10
)