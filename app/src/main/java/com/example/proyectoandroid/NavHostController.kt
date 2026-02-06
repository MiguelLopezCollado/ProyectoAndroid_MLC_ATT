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

@Composable
fun Navegacion(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val navController = rememberNavController()

    // Inicializar dependencias (Restaurando lógica eliminada de MainActivity)
    val database = AppDatabase.getDatabase(context)
    val dao = database.contactoDao()
    
    val retrofit = Retrofit.Builder()
        .baseUrl("https://randomuser.me/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api = retrofit.create(RandomUserApi::class.java)
    
    val repository = ContactoRepository(dao, api)
    val connectivityObserver = NetworkConnectivityObserver(context)

    NavHost(
        navController = navController,
        startDestination = "Login",
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, TweenSpec(700))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, TweenSpec(700))
        }
    ) {
        composable("Login") { 
            LoginScreen(navController) 
        }
        
        composable("Home") {
            // Inyección de dependencias para el ViewModel
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(repository, connectivityObserver)
            )
            ContactListScreen(navController = navController, viewModel = viewModel)
        }
        
        // composable("IrDetallesContacto") { ContactoDetalles(navController) }
    }
}