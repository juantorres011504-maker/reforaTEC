package com.example.reforatec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.view.WindowInsetsControllerCompat
import com.example.reforatec.ui.theme.ReforaTECTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.reforatec.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        window.decorView.post {
            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = false
            }
        }

        val sessionManager = SessionManager(this)

        setContent {
            var temaActual by remember { mutableStateOf(sessionManager.getTheme()) }

            val usarModoOscuro = when (temaActual) {
                "Modo Claro" -> false
                "Modo Oscuro" -> true
                else -> isSystemInDarkTheme()
            }

            ReforaTECTheme(darkTheme = usarModoOscuro) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReforaTECApp(
                        temaActual = temaActual,
                        onTemaModificado = { nuevoTema ->
                            temaActual = nuevoTema
                            sessionManager.saveTheme(nuevoTema)
                        }
                    )
                }
            }
        }
    }
}