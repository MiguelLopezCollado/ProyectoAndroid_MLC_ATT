/*package com.example.proyectoandroid
Esta pantalla la comentamos por que no la utilizamos


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(Color.Green),
                title = {
                    Text("Home",
                        color = Color.White)
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("IrDetallesContacto")
                },
                containerColor = Color.Gray
            ) {
                Text("+",
                    fontSize = 25.sp,
                    color = Color.White)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues))
        {
            Column {
                val contact = listOf(
                    "Juan" to "+23445655",
                    "Eustaquio" to "+23445655"
                )
                contact.forEach { (name,phone) -> UsuariosContactos(name,phone, navController) }
            }
        }
    }
}*/