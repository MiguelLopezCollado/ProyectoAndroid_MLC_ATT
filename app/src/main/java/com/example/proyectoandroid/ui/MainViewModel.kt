package com.example.proyectoandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectoandroid.data.local.Contacto
import com.example.proyectoandroid.data.repository.ContactoRepository
import com.example.proyectoandroid.utils.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/**
 * Estado de la UI para la pantalla principal de contactos
 *
 * contactos Lista de contactos recuperados
 * isLoading Indica si hay una operación de carga en curso
 * error Mensaje de error si ocurre alguna excepción
 * isConnected Indica el estado de la conexión a internet
 */
data class MainUiState(
    val contactos: List<Contacto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isConnected: Boolean = true
)


/**
 * ViewModel principal que gestiona la lógica de negocio de la lista de contactos
 * El ViewModel sobrevive a cambios de configuración (como rotar la pantalla) y mantiene el estado
 *
 * repository Repositorio para acceder a los datos de contactos
 * connectivityObserver Observador para saber si tenemos internet
 */
class MainViewModel(
    private val repository: ContactoRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    // Estado interno mutable para 'isLoading'
    private val _isLoading = MutableStateFlow(false)
    // Estado interno mutable para errores. Emite Strings o null.
    private val _error = MutableStateFlow<String?>(null)

    // Estado público de la UI. Combinamos los flujos de datos en un solo objeto 'MainUiState' que la UI consumirá.
    val uiState: StateFlow<MainUiState> = combine(
        repository.contactos, // Flujo de la lista de contactos desde la BD.
        _isLoading,           // Flujo del estado de carga.
        _error,               // Flujo de errores.
        connectivityObserver.observe() // Flujo del estado de la red.
    ) { contactos, isLoading, error, connectivityStatus ->
        // Transformamos los 4 flujos en un solo objeto MainUiState.
        val isConnected = connectivityStatus == ConnectivityObserver.Status.Available
        MainUiState(
            contactos = contactos,
            isLoading = isLoading,
            error = error,
            isConnected = isConnected
        )
    }.stateIn(
        scope = viewModelScope, // El scope del ViewModel. Se cancela cuando el ViewModel muere.
        started = SharingStarted.WhileSubscribed(5000), // Mantiene el flujo activo 5s después de perder suscriptores
        initialValue = MainUiState() // Valor inicial antes de que se emita nada.
    )

    //Bloque init se ejecuta al crear la instancia del ViewModel.
    //Podríamos cargar datos iniciales aquí si quisiéramos.

    /**
     * Importa contactos desde la API remota.
     * Actualiza el estado de carga y error según el resultado.
     */
    fun importarContactos() {
        viewModelScope.launch {
            _isLoading.value = true // Indicamos que empieza a cargar.
            _error.value = null // Limpiamos errores previos.
            try {
                // Llamada suspendida al repositorio
                repository.importarContactos(10) // Importamos 10 por defecto.
            } catch (e: Exception) {
                // Si falla, capturamos la excepción y mostramos el mensaje.
                _error.value = "Error al importar: ${e.message}"
            } finally {
                // Siempre se ejecuta al final, haya error o no.
                _isLoading.value = false // Terminamos la carga.
            }
        }
    }

    /**
     * Elimina un contacto de la base de datos local.
     */
    fun deleteContacto(contacto: Contacto) {
        viewModelScope.launch {
             try {
                repository.deleteContacto(contacto) // Llamada asíncrona a la BD.
            } catch (e: Exception) {
                _error.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    /**
     * Actualiza la información de un contacto existente.
     */
    fun updateContacto(contacto: Contacto) {
        viewModelScope.launch {
            try {
                repository.updateContacto(contacto) // Llamada asíncrona a la BD.
            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message}"
            }
        }
    }



    //Factory para crear instancias de MainViewModel.
    //Necesario porque MainViewModel tiene argumentos en su constructor
    class Factory(
        private val repository: ContactoRepository,
        private val connectivityObserver: ConnectivityObserver
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository, connectivityObserver) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
