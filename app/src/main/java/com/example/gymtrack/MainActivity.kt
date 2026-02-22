package com.example.gymtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.gymtrack.data.AppDatabase
import com.example.gymtrack.data.GymRepository
import com.example.gymtrack.ui.EjercicioViewModel
import com.example.gymtrack.ui.EjercicioViewModelFactory
import com.example.gymtrack.ui.screens.EjercicioScreen as EjercicioScreen1

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos la base de datos y el repositorio
        val database by lazy { AppDatabase.getDatabase(this) }
        val repository by lazy {
            GymRepository(
                database.ejercicioDao(),
                database.rutinaDao(),
                database.entrenamientoDao()
            )
        }

        // Creamos el ViewModel usando la Factory
        val ejercicioViewModel: EjercicioViewModel by viewModels {
            EjercicioViewModelFactory(repository)
        }

        setContent {
            // Aplicamos el tema de la aplicación (el nombre puede variar según tu proyecto)
            // Si no sabes cómo se llama, puedes usar MaterialTheme directamente
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    EjercicioScreen1(viewModel = ejercicioViewModel)
                }
            }
        }
    }
}