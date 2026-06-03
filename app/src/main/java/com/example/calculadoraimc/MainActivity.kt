package com.example.calculadoraimc

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.fillMaxWidth

object Routes {
    const val INICIO = "inicio"
    const val RESULTADO = "resultado"
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.INICIO
    ) {

        composable(Routes.INICIO) {
            PantallaIngreso(navController)
        }

        composable(
            route = "${Routes.RESULTADO}/{nombre}/{imc}",
            arguments = listOf(
                navArgument("nombre") {
                    type = NavType.StringType
                },
                navArgument("imc") {
                    type = NavType.FloatType
                }
            )
        ) { backStackEntry ->

            val nombre =
                backStackEntry.arguments?.getString("nombre") ?: ""

            val imc =
                backStackEntry.arguments?.getFloat("imc") ?: 0f

            PantallaResultado(
                nombre = nombre,
                imc = imc,
                navController = navController
            )
        }
    }
}
@Composable
fun PantallaIngreso(navController: NavHostController) {

    var nombre by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Calculadora IMC",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = altura,
            onValueChange = { altura = it },
            label = { Text("Altura (m)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val pesoNum = peso.toFloatOrNull()
                val alturaNum = altura.toFloatOrNull()

                when {

                    nombre.isBlank() -> {
                        mensajeError = "Ingrese su nombre"
                    }

                    pesoNum == null -> {
                        mensajeError = "Ingrese un peso válido"
                    }

                    alturaNum == null -> {
                        mensajeError = "Ingrese una altura válida"
                    }

                    pesoNum < 20 || pesoNum > 300 -> {
                        mensajeError = "El peso debe estar entre 20 y 300 kg"
                    }

                    alturaNum < 0.5f || alturaNum > 2.5f -> {
                        mensajeError = "La altura debe estar entre 0.5 y 2.5 m"
                    }

                    else -> {

                        mensajeError = ""

                        val imc = pesoNum / (alturaNum * alturaNum)

                        navController.navigate(
                            "${Routes.RESULTADO}/${Uri.encode(nombre)}/$imc"
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular")
        }
    }
}

@Composable
fun PantallaResultado(
    nombre: String,
    imc: Float,
    navController: NavHostController
) {

    val categoria: String
    val colorCategoria: Color

    when {
        imc < 18.5f -> {
            categoria = "Bajo peso"
            colorCategoria = Color.Red
        }

        imc < 25f -> {
            categoria = "Peso normal"
            colorCategoria = Color.Green
        }

        imc < 30f -> {
            categoria = "Sobrepeso"
            colorCategoria = Color(0xFFFF9800)
        }

        else -> {
            categoria = "Obesidad"
            colorCategoria = Color.Red
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Hola $nombre",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tu resultado es:"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = String.format("%.1f", imc),
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = categoria,
            color = colorCategoria,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}