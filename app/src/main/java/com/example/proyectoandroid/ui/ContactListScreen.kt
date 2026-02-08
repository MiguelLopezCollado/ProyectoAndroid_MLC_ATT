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


/**
 * Pantalla principal que muestra la lista de contactos
 *
 * Controlador de navegación para moverse entre pantallas
 * ViewModel que contiene la lógica de negocio y el estado de la UI.
 */
@Composable
fun ContactListScreen(navController: NavHostController,viewModel: MainViewModel) {
    // Recolectamos el estado de la UI del ViewModel. 'collectAsState' convierte el flujo de datos en un estado de Compose.
    // Cada vez que el ViewModel emita un nuevo estado, esta variable se actualizará y la UI se redibujará.
    val state by viewModel.uiState.collectAsState()
    
    // Obtenemos el contexto actual de la aplicación Android, necesario para iniciar Intents.
    val context = LocalContext.current
    
    // Estado local para controlar qué contacto se está editando actualmente.
    // Si es null, no se está editando nada. Si tiene un contacto, se muestra el diálogo de edición.
    var editingContact by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Contacto?>(null) }


    Column(modifier = Modifier.fillMaxSize()) {
        // Banner de "Sin Conexión"
        // Mostramos este bloque rojo SOLO si el estado indica que no hay conexión a internet.
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
            contentPadding = PaddingValues(16.dp), // Margen interno de la lista
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre cada elemento de la lista
        ) {
            // 'items' itera sobre la lista de contactos del estado y crea un composable ContactItem para cada uno.
            items(state.contactos) { contacto ->
                ContactItem(
                    contacto = contacto, 
                    context = context,
                    // Pasamos las acciones al ViewModel o actualizamos el estado local
                    onDelete = { viewModel.deleteContacto(contacto) }, // Acción de eliminar
                    onEdit = { editingContact = contacto } // guardamos el contacto en la variable de estado
                )
            }

        }

        // Indicador de Carga
        // Se muestra una barra de progreso si el estado 'isLoading' es verdadero
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Mensaje de Error
        // Se muestra si 'error' no es nulo
        if (state.error != null) {
            Text(
                text = state.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Botón Importar
        // Llama a la función 'importarContactos' del ViewModel.
        // Se deshabilita si no hay internet o si ya está cargando.
        Button(
            onClick = { viewModel.importarContactos() },
            enabled = state.isConnected && !state.isLoading,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = ButtonDefaults.buttonColors(Color.Red,Color.Black)
        ) {
            Text("Importar Contactos")
        }

        // Diálogo de Edición
        // Se muestra superpuesto solo si 'editingContact' tiene un valor
        if (editingContact != null) {
            EditContactDialog(
                contacto = editingContact!!, // Pasamos el contacto a editar
                onDismiss = { editingContact = null }, // Al cancelar, limpiamos el estado para cerrar el diálogo.
                onSave = { name, phone ->
                    // Al guardar, llamamos al ViewModel para actualizar y cerramos el diálogo.
                    viewModel.updateContacto(editingContact!!.copy(name = name, phone = phone))
                    editingContact = null
                }
            )
        }
    }
}


/**
 * Componente que representa un elemento individual de contacto en la lista.
 * Permite eliminar, editar, llamar y compartir contacto.
 *
 */
@Composable
fun ContactItem(contacto: Contacto, context: Context, onDelete: () -> Unit, onEdit: () -> Unit) {
    // Card crea un contenedor con elevación (sombra) y bordes redondeados.
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // Alinea los elementos verticalmente al centro.
        ) {
            AsyncImage(
                model = contacto.picture,
                contentDescription = "Foto de ${contacto.name}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape), // Recorta la imagen en forma de círculo.
                contentScale = ContentScale.Crop // Recorta la imagen para llenar el espacio.
            )

            Spacer(modifier = Modifier.width(16.dp)) // Espacio horizontal entre imagen y texto.

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
            // Usa un Intent implícito con ACTION_DIAL para abrir la app de teléfono.
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${contacto.phone}") // URI scheme 'tel:' para números.
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
            // Intenta abrir WhatsApp directamente, o el navegador si falla.
            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    // URI API de WhatsApp para enviar mensaje.
                    data = Uri.parse("https://api.whatsapp.com/send?phone=${contacto.phone}")
                    setPackage("com.whatsapp") // Intenta forzar la app de WhatsApp.
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Si no tiene WhatsApp instalado, abrimos el navegador
                     val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=${contacto.phone}"))
                     context.startActivity(browserIntent)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "WhatsApp",
                    tint = Color(0xFF03EEB5) // Color característico de WhatsApp.
                )
            }
            

        }
    }
}

/**
 * Diálogo emergente para editar el nombre y teléfono de un contacto.
 *
 * @param contacto El contacto actual que se está editando.
 * @param onDismiss Callback para cancelar la edición y cerrar el diálogo.
 * @param onSave Callback con los nuevos datos (nombre, teléfono) al guardar.
 */
@Composable
fun EditContactDialog(
    contacto: Contacto,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    // Variables de estado local para los campos de texto del diálogo.
    // 'remember' con 'mutableStateOf' mantiene el valor mientras escribes.
    var name by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(contacto.name) }
    var phone by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(contacto.phone) }

    AlertDialog(
        onDismissRequest = onDismiss, // Se llama si el usuario toca fuera del diálogo o pulsa atrás.
        title = { Text(text = "Editar Contacto") },
        text = {
            Column {
                // Campo de texto para el Nombre.
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it }, // Actualiza la variable 'name' al escribir.
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo de texto para el Teléfono.
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it }, // Actualiza la variable 'phone' al escribir.
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            // Botón Guardar: Llama a 'onSave' pasando los nuevos valores.
            Button(onClick = { onSave(name, phone) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            // Botón Cancelar: Cierra el diálogo sin guardar.
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}



