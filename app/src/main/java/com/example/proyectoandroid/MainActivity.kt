package com.example.proyectoandroid

import Navegacion
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectoandroid.ui.theme.ProyectoAndroidTheme

/**
 * MainActivity es el punto de entrada de la aplicación Android.
 * Esta clase se crea cuando el usuario abre la app.
 */
class MainActivity : ComponentActivity() {
    // onCreate se ejecuta cuando la Activity se crea por primera vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        // Aquí definimos toda la UI usando composables.
        setContent {
            ProyectoAndroidTheme {

                Navegacion()
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

}

// Preview para ver cómo se vería 'Greeting' en Android Studio sin ejecutar la app.
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProyectoAndroidTheme {
        Greeting("Android")
    }
}