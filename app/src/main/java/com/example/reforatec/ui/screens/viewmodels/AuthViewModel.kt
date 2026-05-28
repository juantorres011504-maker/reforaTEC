package com.example.reforatec.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reforatec.data.local.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log

class AuthViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    fun registrar(
        nombres: String,
        apPaterno: String,
        apMaterno: String,
        telefono: String,
        sexo: String,
        correo: String,
        contrasena: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        Log.d("ReforaTEC_Debug", "Iniciando proceso de registro...")

        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ReforaTEC_Debug", "Paso 1: Auth exitoso. Cuenta creada.")

                    val uid = task.result?.user?.uid
                    if (uid == null) {
                        Log.e("ReforaTEC_Debug", "Error extraño: El UID llegó nulo")
                        onError("Error: No se pudo obtener el ID del usuario")
                        return@addOnCompleteListener
                    }

                    Log.d("ReforaTEC_Debug", "Paso 2: UID obtenido: $uid")

                    val fechaHoy = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                    val datosUsuario = hashMapOf<String, Any>(
                        "nombres" to nombres,
                        "apPaterno" to apPaterno,
                        "apMaterno" to apMaterno,
                        "telefono" to telefono,
                        "sexo" to sexo,
                        "correo" to correo,
                        "fechaRegistro" to fechaHoy,
                        "ultimaConexion" to fechaHoy
                    )

                    Log.d("ReforaTEC_Debug", "Paso 3: Intentando guardar perfil en Firestore...")

                    repository.registrarUsuario(
                        uid = uid,
                        datosUsuario = datosUsuario,
                        onSuccess = {
                            Log.d("ReforaTEC_Debug", "Paso 4: ¡Firestore guardó con éxito!")
                            onSuccess()
                        },
                        onError = { excepcion ->
                            Log.e("ReforaTEC_Debug", "Error en Firestore", excepcion)
                            onError("Falló Firestore: ${excepcion.message}")
                        }
                    )
                } else {
                    val mensaje = task.exception?.localizedMessage ?: "Error al registrar"
                    Log.e("ReforaTEC_Debug", "Error en Auth: $mensaje")
                    onError(mensaje)
                }
            }
    }

    fun login(
        correo:String,
        contrasena: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        onSuccess(uid)
                    } else {
                        onError("No se pudo obtener el ID del usuario.")
                    }
                } else {
                    onError("Correo o contraseña incorrectos.")
                }
            }
    }

    fun enviarCorreoRecuperacion(
        correo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        repository.recuperarPassword(
            correo = correo,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}

class AuthViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}