package com.example.gymtrack.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "ejercicios_en_dia",
    primaryKeys = ["diaRutinaId", "ejercicioId"],
    foreignKeys = [
        ForeignKey(
            entity = DiaRutina::class,
            parentColumns = ["id"],
            childColumns = ["diaRutinaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ejercicio::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("ejercicioId")]
)
data class EjercicioEnDia(
    val diaRutinaId: Int,
    val ejercicioId: Int,
    val orden: Int
)
