package com.example.proyectoandroid

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


/**
 * Pantalla de inicio de sesión
 * Permite ingresar usuario y contraseña. Por ahora, sin validación real
 *
 * Controlador de navegación para cambiar a la pantalla "Home"
 */
@Composable
fun LoginScreen(navController: NavHostController) {
    // 'remember' + 'mutableStateOf' mantienen el valor mientras escribes.
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Column organiza los elementos verticalmente.
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda la pantalla.
            .padding(16.dp), // Margen interno de 16dp en todos los lados.
        horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente.
        verticalArrangement = Arrangement.Center // Centra verticalmente.
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de texto para el Usuario.
        OutlinedTextField(
            value = username,
            onValueChange = { username = it }, // Actualiza el estado al escribir.
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para la Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Oculta el texto
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Iniciar Sesión.
        Button(
            onClick = { navController.navigate("Home") }, // Navega a la pantalla "Home"
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color.Green, Color.Black)
            // enabled = username.isNotBlank() && password.isNotBlank() // Está desactivado para pruebas
        ) {
            Text("Iniciar Sesión")
        }
    }
}
