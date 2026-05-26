package com.example.gymtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymtrack.ui.screens.EjercicioScreen
import com.example.gymtrack.ui.screens.HistorialScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var tab by rememberSaveable { mutableIntStateOf(0) }

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = tab == 0,
                                    onClick = { tab = 0 },
                                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                                    label = { Text("Ejercicios") }
                                )
                                NavigationBarItem(
                                    selected = tab == 1,
                                    onClick = { tab = 1 },
                                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                                    label = { Text("Historial") }
                                )
                            }
                        }
                    ) { padding ->
                        when (tab) {
                            0 -> EjercicioScreen(
                                viewModel = hiltViewModel(),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(padding)
                            )
                            1 -> HistorialScreen(
                                viewModel = hiltViewModel(),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(padding)
                            )
                        }
                    }
                }
            }
        }
    }
}