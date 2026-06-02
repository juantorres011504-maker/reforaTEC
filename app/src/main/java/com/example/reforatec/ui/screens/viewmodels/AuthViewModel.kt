package com.example.reforatec.ui.screens.viewmodels

import android.util.Log
import android.util.Log.d
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reforatec.data.local.repository.UsuarioRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuthViewModel(private val repository: UsuarioRepository) : ViewModel() {

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
        Log.d("ReforaTEC_Debug", "Iniciando proceso de registro por REST API...")

        viewModelScope.launch {
            try {
                //1.Crear usuario en FirebaseAuth usando REST
                val uid = repository.registrarRest(correo, contrasena)
                Log.e("ReforaTEC_Debug", "Paso 1 y 2: Auth REST exitoso. UID obtenido: $uid")

                //2.Guardar perfil en Firestore usando REST
                val fechaHoy = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                Log.d("ReforaTEC_Debug", "Paso 3: Intentando guardar perfil en Firestore REST...")

                repository.guardarPerfilFirestoreRest(
                    uid, nombres, apPaterno, apMaterno, telefono, sexo, correo, fechaHoy
                )

                Log.d("ReforaTEC_Debug", "Paso 4: ¡Firestore REST guardó con éxito!")
                onSuccess()

            } catch (e: Exception) {
                val mensaje = e.message ?: "Error desconocido en el registro"
                Log.e("ReforaTEC_Debug", "Error en Registro REST: $mensaje")
                onError(mensaje)
            }
        }
    }

    fun login(
        correo: String,
        contrasena: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val uid = repository.loginRest(correo, contrasena)
                onSuccess(uid)
            } catch (e: Exception) {
                onError(e.message ?: "Correo o contraseña incorrectos.")
            }
        }
    }

    fun enviarCorreoRecuperacion(
        correo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        com.google.firebase.auth.FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.localizedMessage ?: "Error al enviar el correo")
                }
            }
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