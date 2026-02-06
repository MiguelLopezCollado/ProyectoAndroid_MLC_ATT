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


data class MainUiState(
    val contactos: List<Contacto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isConnected: Boolean = true
)


class MainViewModel(
    private val repository: ContactoRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // Combinamos los flujos de contactos, loading, error y conectividad
    val uiState: StateFlow<MainUiState> = combine(
        repository.contactos,
        _isLoading,
        _error,
        connectivityObserver.observe()
    ) { contactos, isLoading, error, connectivityStatus ->
        val isConnected = connectivityStatus == ConnectivityObserver.Status.Available
        MainUiState(
            contactos = contactos,
            isLoading = isLoading,
            error = error,
            isConnected = isConnected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )


     //Importa contactos desde la API randomuser.me.

    fun importarContactos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.importarContactos(10) // Importamos 10 por defecto
            } catch (e: Exception) {
                _error.value = "Error al importar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteContacto(contacto: Contacto) {
        viewModelScope.launch {
             try {
                repository.deleteContacto(contacto)
            } catch (e: Exception) {
                _error.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    //Factory para crear instancias de MainViewModel.

    class Factory(
        private val repository: ContactoRepository,
        private val connectivityObserver: ConnectivityObserver
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repository, connectivityObserver) as T
            }
                throw IllegalArgumentException("Desconocido ViewModel class")
        }
    }
}
