package com.example.gymtrack.data.model

data class SerieConEjercicio(
    val id: Int,
    val sesionId: Int,
    val ejercicioId: Int,
    val nombreEjercicio: String,
    val peso: Double,
    val repeticiones: Int,
    val rpe: Int?
)
