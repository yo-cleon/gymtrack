package com.example.gymtrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rutinas")
data class Rutina(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombreRutina: String,
    val objetivo: String? = null // El "?" significa que puede ser opcional
)