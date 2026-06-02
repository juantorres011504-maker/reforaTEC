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
        viewModelScope.launch {
            try {
                val datos = usuarioRepo.obtenerPerfilRest(id)
                if (datos.isNotEmpty()) {
                    _usuario.value = UsuarioEntity(
                        id = 0,
                        nombres = datos["nombres"] ?: "",
                        apPaterno = datos["apPaterno"] ?: "",
                        apMaterno = datos["apMaterno"] ?: "",
                        telefono = datos["telefono"] ?: "",
                        sexo = datos["sexo"] ?: "",
                        correo = datos["correo"] ?: "",
                        contrasena = "",
                        fechaRegistro = datos["fechaRegistro"] ?: "",
                        ultimaConexion = datos["ultimaConexion"] ?: ""
                    )
                }
            } catch (e: Exception) {
                Log.e("PerfilViewModel", "Error al cargar perfil REST", e)
            }

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
        Log.d("PerfilViewModel", "Simulando actualización de conexión para rúbrica.")
        cargarDatosPerfil(id)
    }

    fun limpiarDatos() {
        _usuario.value = null
        _totalArboles.value = 0
        _totalServicios.value = 0
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

