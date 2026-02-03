package com.example.proyectoandroid.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.proyectoandroid.data.local.Contacto


@Composable
fun ContactListScreen(navController: NavHostController,viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Banner de "Sin Conexión"
        if (!state.isConnected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin Conexión a Internet",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Lista de Contactos
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.contactos) { contacto ->
                ContactItem(contacto = contacto, context = context)
            }
        }

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (state.error != null) {
            Text(
                text = state.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Botón Importar
        Button(
            onClick = { viewModel.importarContactos() },
            enabled = state.isConnected && !state.isLoading,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = ButtonDefaults.buttonColors(Color.Red,Color.Black)
        ) {
            Text("Importar Contactos")
        }
    }
}


@Composable
fun ContactItem(contacto: Contacto, context: Context) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de perfil con Coil
            AsyncImage(
                model = contacto.picture,
                contentDescription = "Foto de ${contacto.name}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información del contacto
            Column(modifier = Modifier.weight(1f)) {
                Text(text = contacto.name, style = MaterialTheme.typography.titleMedium)
                Text(text = contacto.email, style = MaterialTheme.typography.bodySmall)
                Text(text = contacto.phone, style = MaterialTheme.typography.bodySmall)
            }

            // Botón Llamar
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${contacto.phone}")
                }
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Llamar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Botón WhatsApp
            IconButton(onClick = {
                // Intent para abrir WhatsApp
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=${contacto.phone}")
                    // Opcional: especificar paquete para asegurar que abra la app
                    setPackage("com.whatsapp")
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Si no tiene WhatsApp instalado, intentar abrir navegador o mostrar error
                    // Fallback a navegador sin paquete
                     val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=${contacto.phone}"))
                     context.startActivity(browserIntent)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Share, // Icono genérico de compartir o usar uno específico si hubiera asset
                    contentDescription = "WhatsApp",
                    tint = Color(0xFF03EEB5) // Color WhatsApp
                )
            }
        }
    }
}
