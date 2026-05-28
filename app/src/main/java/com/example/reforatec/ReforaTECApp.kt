package com.example.reforatec

import android.widget.Toast
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.example.reforatec.data.local.database.AppDatabase
import com.example.reforatec.data.local.repository.UsuarioRepository
import com.example.reforatec.ui.screens.viewmodels.AuthViewModel
import com.example.reforatec.ui.screens.viewmodels.AuthViewModelFactory
import com.example.reforatec.utils.SessionManager
import com.example.reforatec.ui.viewmodels.PerfilViewModel
import com.example.reforatec.ui.viewmodels.PerfilViewModelFactory
import com.example.reforatec.data.local.repository.ArbolRepository
import com.example.reforatec.ui.screens.viewmodels.ArbolViewModel
import com.example.reforatec.ui.screens.viewmodels.ArbolViewModelFactory

import com.example.reforatec.ui.screens.AjustesScreen
import com.example.reforatec.ui.screens.AgregarArbolScreen
import com.example.reforatec.ui.screens.BienvenidaScreen
import com.example.reforatec.ui.screens.DetalleArbolScreen
import com.example.reforatec.ui.screens.ListaArbolesScreen
import com.example.reforatec.ui.screens.LoginScreen
import com.example.reforatec.ui.screens.PerfilScreen
import com.example.reforatec.ui.screens.RegistroScreen
import com.example.reforatec.ui.screens.RecuperarPasswordScreen

@ExperimentalMaterial3Api
@Composable
fun ReforaTECApp(temaActual: String, onTemaModificado: (String) -> Unit) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)

    val repository = UsuarioRepository()
    val arbolRepository = ArbolRepository()
    val servicioRepository = com.example.reforatec.data.local.repository.ServicioRepository(database.servicioDao())

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(repository)
    )

    val arbolViewModel: ArbolViewModel = viewModel(
        factory = ArbolViewModelFactory(arbolRepository)
    )

    val perfilViewModel: PerfilViewModel = viewModel(
        factory = PerfilViewModelFactory(repository, arbolRepository, servicioRepository)
    )

    val sessionManager = remember { SessionManager(context) }
    val rutaInicial = if (sessionManager.isLoggedIn()) "lista_arboles" else "bienvenida"

    val userIdLogueado = sessionManager.getUserId()

    LaunchedEffect(userIdLogueado) {
        if (userIdLogueado != null) {
            perfilViewModel.actualizarConexion(userIdLogueado)
        }
    }

    NavHost(
        navController = navController,
        startDestination = rutaInicial,
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        enterTransition = {
            fadeIn(animationSpec = tween(600)) +
                    scaleIn(initialScale = 0.95f, animationSpec = tween(400))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(500)) +
                    scaleOut(targetScale = 0.95f, animationSpec = tween(300))
        }
    ) {
        composable(route = "bienvenida") {
            BienvenidaScreen(
                onComencemosClick = {
                    navController.navigate("login")
                }
            )
        }

        composable(route = "login") {
            LoginScreen(
                onLoginClick = { correo, password, detenerCarga ->
                    authViewModel.login(
                        correo = correo,
                        contrasena = password,
                        onSuccess = { uid ->
                            sessionManager.saveSession(uid)
                            Toast.makeText(context, "¡Sesión iniciada!", Toast.LENGTH_SHORT).show()
                            navController.navigate("lista_arboles") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onError = { mensajeError ->
                            Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
                            detenerCarga() // 👇 Apagamos el botón si la contraseña está mal
                        }
                    )
                },
                onForgotPasswordClick = { navController.navigate("recuperar_password") },
                onRegisterClick = { navController.navigate("registro") }
            )
        }

        composable(route = "registro") {
            RegistroScreen(
                onRegistroClick = { nombres, apPat, apMat, tel, sexo, correo, pass ->
                    authViewModel.registrar(
                        nombres = nombres,
                        apPaterno = apPat,
                        apMaterno = apMat,
                        telefono = tel,
                        sexo = sexo,
                        correo = correo,
                        contrasena = pass,
                        onSuccess = {
                            Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show()
                            navController.navigate("login") {
                                popUpTo("registro") { inclusive = true }
                            }
                        },
                        onError = { mensajeError ->
                            Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = "recuperar_password") {
            RecuperarPasswordScreen(
                onEnviarClick = { correo, detenerCarga ->
                    authViewModel.enviarCorreoRecuperacion(
                        correo = correo,
                        onSuccess = {
                            Toast.makeText(context, "Enlace enviado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show()
                            navController.popBackStack() // Regresa al Login si tuvo éxito
                        },
                        onError = { mensajeError ->
                            Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
                            detenerCarga()
                        }
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "lista_arboles",
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            val userId = sessionManager.getUserId()

            LaunchedEffect(userId) {
                if (userId != null) arbolViewModel.cargarArboles(userId)
            }

            val misArboles by arbolViewModel.misArboles.collectAsState()

            val estaCargando by arbolViewModel.estaCargando.collectAsState()

            ListaArbolesScreen(
                navController = navController,
                arboles = misArboles,
                estaCargando = estaCargando,
                onRefresh = {
                    if (userId != null) arbolViewModel.cargarArboles(userId)
                }
            )
        }

        composable(
            route = "perfil",
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            val userId = sessionManager.getUserId()

            LaunchedEffect(userId) {
                if (userId != null) {
                    perfilViewModel.cargarDatosPerfil(userId)
                }
            }

            val usuarioActual by perfilViewModel.usuario.collectAsState()
            val arbolesCuidando by perfilViewModel.totalArboles.collectAsState()
            val serviciosDados by perfilViewModel.totalServicios.collectAsState()

            PerfilScreen(
                navController = navController,
                usuario = usuarioActual,
                totalArboles = arbolesCuidando,
                totalServicios = serviciosDados,
                onLogoutClick = {
                    sessionManager.clearSession()
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                    android.widget.Toast.makeText(context, "Sesión cerrada", android.widget.Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "ajustes",
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            AjustesScreen(
                navController = navController,
                temaActual = temaActual,
                onTemaModificado = onTemaModificado
            )
        }

        composable(
            route = "detalle_arbol/{tipo}/{arbolId}",
            arguments = listOf(
                navArgument("tipo") { type = NavType.StringType },
                navArgument("arbolId") { type = NavType.IntType }
            ),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "Info"
            val arbolId = backStackEntry.arguments?.getInt("arbolId") ?: 0

            val detalleViewModel: com.example.reforatec.ui.viewmodels.DetalleViewModel = viewModel(
                factory = com.example.reforatec.ui.viewmodels.DetalleViewModelFactory(arbolRepository)
            )

            LaunchedEffect(arbolId) {
                detalleViewModel.cargarDetalles(arbolId)
            }

            val arbolEntity by detalleViewModel.arbol.collectAsState()
            val historialServicios by detalleViewModel.historial.collectAsState()
            val estaCargando by detalleViewModel.estaCargando.collectAsState()

            arbolEntity?.let { arbolReal ->
                DetalleArbolScreen(
                    navController = navController,
                    tipoDetalle = tipo,
                    arbol = arbolReal,
                    historial = historialServicios,
                    estaCargando = estaCargando,
                    onRefresh = { detalleViewModel.cargarDetalles(arbolId) },
                    onGuardarServicio = { tipoServicio, alturaAct, diametroAct, comentariosAct, fechaAct, fotoUriAct, onErrorCallback ->
                        detalleViewModel.agregarServicio(
                            arbolId = arbolId,
                            tipo = tipoServicio,
                            altura = alturaAct,
                            diametro = diametroAct,
                            comentarios = comentariosAct,
                            fecha = fechaAct,
                            fotoUriString = fotoUriAct,
                            onSuccess = {
                                Toast.makeText(context, "¡Servicio subido con éxito!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { mensaje ->
                                Toast.makeText(context, "Error: $mensaje", Toast.LENGTH_LONG).show()
                                onErrorCallback()
                            }
                        )
                    }
                )
            }
        }


        composable(
            route = "agregar_arbol",
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            val userId = sessionManager.getUserId()

            AgregarArbolScreen(
                onGuardarClick = { nombre, especie, escuela, calle, numero, colonia, onErrorCallback ->

                    arbolViewModel.agregarArbol(
                        usuarioId = userId ?: "",
                        nombreValor = nombre,
                        especie = especie,
                        escuela = escuela,
                        calle = calle,
                        numero = numero,
                        colonia = colonia,
                        onSuccess = {
                            Toast.makeText(context, "¡Árbol plantado!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { mensajeError ->
                            Toast.makeText(context, "Ups: $mensajeError", Toast.LENGTH_LONG).show()
                            onErrorCallback()
                        }
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}