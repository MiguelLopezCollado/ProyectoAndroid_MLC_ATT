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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
    var editingContact by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Contacto?>(null) }


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
                ContactItem(
                    contacto = contacto, 
                    context = context,
                    onDelete = { viewModel.deleteContacto(contacto) },
                    onEdit = { editingContact = contacto }
                )
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

        if (editingContact != null) {
            EditContactDialog(
                contacto = editingContact!!,
                onDismiss = { editingContact = null },
                onSave = { name, phone ->
                    viewModel.updateContacto(editingContact!!.copy(name = name, phone = phone))
                    editingContact = null
                }
            )
        }
    }
}


@Composable
fun ContactItem(contacto: Contacto, context: Context, onDelete: () -> Unit, onEdit: () -> Unit) {
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

            // Botón Eliminar
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }

            // Botón Editar
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary
                )
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

@Composable
fun EditContactDialog(
    contacto: Contacto,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(contacto.name) }
    var phone by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(contacto.phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Contacto") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, phone) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}



