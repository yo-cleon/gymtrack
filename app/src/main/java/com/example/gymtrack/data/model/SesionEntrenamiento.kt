package com.example.gymtrack.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sesiones_entrenamiento",
    foreignKeys = [
        ForeignKey(
            entity = Rutina::class,
            parentColumns = ["id"],
            childColumns = ["rutinaId"]
        )
    ]
)
data class SesionEntrenamiento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rutinaId: Int,
    val fecha: Long, // Guardaremos el timestamp (milisegundos)
    val notas: String? = null
)