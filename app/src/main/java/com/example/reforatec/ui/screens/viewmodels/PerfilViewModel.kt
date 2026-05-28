package com.example.reforatec.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reforatec.data.local.entity.UsuarioEntity
import com.example.reforatec.data.local.repository.ArbolRepository
import com.example.reforatec.data.local.repository.ServicioRepository
import com.example.reforatec.data.local.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerfilViewModel(
    private val usuarioRepo: UsuarioRepository,
    private val arbolRepo: ArbolRepository,
    private val servicioRepo: ServicioRepository
) : ViewModel() {

    private val _usuario = MutableStateFlow<UsuarioEntity?>(null)
    val usuario: StateFlow<UsuarioEntity?> = _usuario

    private val _totalArboles = MutableStateFlow(0)
    val totalArboles: StateFlow<Int> = _totalArboles

    private val _totalServicios = MutableStateFlow(0)
    val totalServicios: StateFlow<Int> = _totalServicios

    fun cargarDatosPerfil(id: String) {
        usuarioRepo.obtenerUsuario(
            uid = id,
            onSuccess = { datos ->
                if (datos != null) {
                    _usuario.value = UsuarioEntity(
                        id = 0,
                        nombres = datos["nombres"] as? String ?: "",
                        apPaterno = datos["apPaterno"] as? String ?: "",
                        apMaterno = datos["apMaterno"] as? String ?: "",
                        telefono = datos["telefono"] as? String ?: "",
                        sexo = datos["sexo"] as? String ?: "",
                        correo = datos["correo"] as? String ?: "",
                        contrasena = "",
                        fechaRegistro = datos["fechaRegistro"] as? String ?: "",
                        ultimaConexion = datos["ultimaConexion"] as? String ?: ""
                    )
                }
            },
            onError = { excepcion ->
                Log.e("PerfilViewModel", "Error al cargar perfil", excepcion)
            }
        )

        viewModelScope.launch {
            try {
                val misArbolesNube = arbolRepo.obtenerMisArboles(id)
                _totalArboles.value = misArbolesNube.size

                var contadorServicios = 0
                for (arbol in misArbolesNube) {
                    val historialArbol = arbolRepo.obtenerHistorialDesdeNube(arbol.id.toString())
                    contadorServicios += historialArbol.size
                }
                _totalServicios.value = contadorServicios

            } catch (e: Exception) {
                Log.e("PerfilViewModel", "Error al contar árboles y servicios", e)
            }
        }
    }

    fun actualizarConexion(id: String) {
        val horaActual = SimpleDateFormat("'Hoy', hh:mm a", Locale.getDefault()).format(Date())
        usuarioRepo.actualizarUltimaConexion(
            uid = id,
            fecha = horaActual,
            onSuccess = {
                cargarDatosPerfil(id)
            },
            onError = { excepcion ->
                Log.e("PerfilViewModel", "Error al actualizar conexión", excepcion)
                cargarDatosPerfil(id)
            }
        )
    }
}

class PerfilViewModelFactory(
    private val usuarioRepo: UsuarioRepository,
    private val arbolRepo: ArbolRepository,
    private val servicioRepo: ServicioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerfilViewModel(usuarioRepo, arbolRepo, servicioRepo) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}