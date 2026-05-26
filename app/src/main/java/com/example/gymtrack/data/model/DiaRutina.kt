package com.example.gymtrack.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dias_rutina",
    foreignKeys = [
        ForeignKey(
            entity = Rutina::class,
            parentColumns = ["id"],
            childColumns = ["rutinaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("rutinaId")]
)
data class DiaRutina(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rutinaId: Int,
    val nombre: String,
    val orden: Int
)
