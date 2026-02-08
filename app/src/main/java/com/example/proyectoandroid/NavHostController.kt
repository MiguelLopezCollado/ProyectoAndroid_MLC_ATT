import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoandroid.data.local.AppDatabase
import com.example.proyectoandroid.data.remote.RandomUserApi
import com.example.proyectoandroid.data.repository.ContactoRepository
import com.example.proyectoandroid.ui.ContactListScreen
import com.example.proyectoandroid.LoginScreen
import com.example.proyectoandroid.ui.MainViewModel
import com.example.proyectoandroid.utils.NetworkConnectivityObserver
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Función composable que gestiona la navegación de la aplicación.
 * Define las rutas disponibles (Login, Home) y configura las transiciones entre pantallas.
 * También inicializa las dependencias manualmente (sin Hilt/Dagger).
 *
 * @param modifier Modificador opcional para personalizar el layout.
 */
@Composable
fun Navegacion(modifier: Modifier = Modifier) {
    // Obtenemos el contexto actual de Android, necesario para acceder a servicios del sistema.
    val context = LocalContext.current
    
    // Creamos el controlador de navegación que gestiona el stack de pantallas.
    val navController = rememberNavController()

    // ========== INICIALIZACIÓN DE DEPENDENCIAS (Inyección Manual) ==========
    // Esto normalmente se haría con Hilt o Koin, pero aquí lo hacemos manualmente.
    
    //  Base de datos local (Room)
    val database = AppDatabase.getDatabase(context) // Singleton de la BD.
    val dao = database.contactoDao() // DAO para operaciones con la tabla Contactos.
    
    //  API remota (Retrofit)
    val retrofit = Retrofit.Builder()
        .baseUrl("https://randomuser.me/") // URL base de la API.
        .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos Kotlin.
        .build()
    val api = retrofit.create(RandomUserApi::class.java) // Creamos la interfaz de la API.
    
    //  Repositorio
    val repository = ContactoRepository(dao, api)
    
    // Observador de conectividad (para saber si hay internet)
    val connectivityObserver = NetworkConnectivityObserver(context)

    // NAVEGACIÓN
    NavHost(
        navController = navController, // Controlador que maneja las navegaciones.
        startDestination = "Login", // Pantalla inicial

        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, TweenSpec(700))
        },

        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, TweenSpec(700))
        }
    ) {
        composable("Login") { 
            LoginScreen(navController) // Pantalla de inicio
        }
        

        composable("Home") {
            // Creamos el ViewModel usando la Factory
            // 'viewModel()' es de Compose y gestiona el ciclo de vida automáticamente
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(repository, connectivityObserver)
            )
            // Mostramos la pantalla principal de contactos.
            ContactListScreen(navController = navController, viewModel = viewModel)
        }
        //Pantalla que ya no está en uso
        // composable("IrDetallesContacto") { ContactoDetalles(navController) }
    }
}
