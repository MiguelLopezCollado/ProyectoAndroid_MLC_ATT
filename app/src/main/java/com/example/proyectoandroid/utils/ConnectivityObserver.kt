package com.example.proyectoandroid.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Interface para observar el estado de la conectividad.
 */
interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}

/**
 * Implementación de [ConnectivityObserver] usando [ConnectivityManager] de Android.
 * Esta clase se encarga de monitorizar los cambios en la red.
 */
class NetworkConnectivityObserver(
    private val context: Context // Necesitamos el contexto para acceder al servicio del sistema.
) : ConnectivityObserver {

    // Obtenemos el servicio de conectividad del sistema Android.
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Función principal que retorna un Flow (flujo de datos) con el estado de la red.
    override fun observe(): Flow<ConnectivityObserver.Status> {
        // callbackFlow permite crear un Flow que emite valores basados en callbacks (eventos).
        return callbackFlow {
            // Definimos el callback que el sistema llamará cuando cambie la red.
            val callback = object : ConnectivityManager.NetworkCallback() {
                
                // Se llama cuando la red está disponible y funcional.
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(ConnectivityObserver.Status.Available) } // Enviamos estado "Disponible"
                }

                // Se llama cuando la red está a punto de perderse (señal débil o desconectando).
                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(ConnectivityObserver.Status.Losing) } // Enviamos estado "Perdiendo señal"
                }

                // Se llama cuando la red se ha perdido completamente.
                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(ConnectivityObserver.Status.Lost) } // Enviamos estado "Perdida"
                }

                // Se llama cuando no hay ninguna red disponible.
                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(ConnectivityObserver.Status.Unavailable) } // Enviamos estado "No disponible"
                }
            }

            // Verificamos el estado inicial al momento de empezar a observar.
            val currentNetwork = connectivityManager.activeNetwork
            if (currentNetwork == null) {
                // Si no hay red activa al inicio, enviamos "No disponible".
                launch { send(ConnectivityObserver.Status.Unavailable) }
            }
            
            // Registramos nuestro callback para empezar a recibir actualizaciones del sistema.
            connectivityManager.registerDefaultNetworkCallback(callback)
            
            // awaitClose se ejecuta cuando el Flow se cierra (ej. cuando la pantalla se destruye).
            // Es IMPORTANTE para limpiar recursos y no dejar el callback activo.
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged() // Evita emitir el mismo estado dos veces seguidas.
    }
}
