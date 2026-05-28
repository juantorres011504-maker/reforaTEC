package com.example.reforatec.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reforatec.data.local.entity.ArbolEntity
import com.example.reforatec.ui.screens.ServicioEntity
import com.example.reforatec.data.local.repository.ArbolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleViewModel(private val arbolRepo: ArbolRepository) : ViewModel() {

    private val _arbol = MutableStateFlow<ArbolEntity?>(null)
    val arbol: StateFlow<ArbolEntity?> = _arbol

    private val _historial = MutableStateFlow<List<ServicioEntity>>(emptyList())
    val historial: StateFlow<List<ServicioEntity>> = _historial

    private val _estaCargando = MutableStateFlow(true)
    val estaCargando: StateFlow<Boolean> = _estaCargando

    fun cargarDetalles(arbolId: Int) {
        viewModelScope.launch {
            _estaCargando.value = true
            try {
                _arbol.value = arbolRepo.obtenerArbolPorId(arbolId)
                _historial.value = arbolRepo.obtenerHistorialDesdeNube(arbolId.toString())
            } catch (e: Exception) {
                Log.e("DetalleViewModel", "Error al cargar", e)
            } finally {
                _estaCargando.value = false
            }
        }
    }

    fun agregarServicio(
        arbolId: Int,
        tipo: String,
        altura: String,
        diametro: String,
        comentarios: String,
        fecha: String,
        fotoUriString: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                var urlFinalFirebaseStorage = ""
                if (!fotoUriString.isNullOrBlank()) {
                    val uriLocal = Uri.parse(fotoUriString)
                    urlFinalFirebaseStorage = arbolRepo.subirFotoEvidencia(arbolId.toString(), uriLocal)
                }

                val datosServicio = hashMapOf<String, Any>(
                    "tipo" to tipo,
                    "fecha" to fecha,
                    "hora" to SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
                    "comentarios" to comentarios,
                    "altura" to altura,
                    "diametro" to diametro,
                    "fotoUri" to urlFinalFirebaseStorage
                )

                arbolRepo.agregarServicioANube(arbolId.toString(), datosServicio)

                if (tipo == "Medición" && altura.isNotBlank() && diametro.isNotBlank()) {
                    _arbol.value?.let { arbolActual ->
                        val arbolActualizado = arbolActual.copy(
                            alturaActual = altura.toDoubleOrNull() ?: arbolActual.alturaActual,
                            diametroActual = diametro.toDoubleOrNull() ?: arbolActual.diametroActual,
                            tieneMedicion = true
                        )
                        arbolRepo.actualizarArbol(arbolActualizado)
                    }
                }
                cargarDetalles(arbolId)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error de red al conectar con Firebase")
            }
        }
    }
}

class DetalleViewModelFactory(private val arbolRepo: ArbolRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetalleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetalleViewModel(arbolRepo) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}