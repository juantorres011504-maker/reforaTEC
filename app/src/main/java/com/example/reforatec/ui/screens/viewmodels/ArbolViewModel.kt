package com.example.reforatec.ui.screens.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reforatec.R
import com.example.reforatec.data.local.entity.ArbolEntity
import com.example.reforatec.data.local.repository.ArbolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class ArbolViewModel(private val repository: ArbolRepository) : ViewModel() {

    private val _misArboles = MutableStateFlow<List<ArbolEntity>>(emptyList())
    val misArboles: StateFlow<List<ArbolEntity>> = _misArboles
    private val _estaCargando = MutableStateFlow(true)
    val estaCargando: StateFlow<Boolean> = _estaCargando

    fun cargarArboles(usuarioId: String) {
        viewModelScope.launch {
            _estaCargando.value = true
            try {
                _misArboles.value = repository.obtenerMisArboles(usuarioId)
            } catch (e: Exception) {
                Log.e("ArbolViewModel", "Error al descargar árboles", e)
            } finally {
                _estaCargando.value = false
            }
        }
    }

    fun agregarArbol(
        usuarioId: String,
        nombreValor: String,
        especie: String,
        escuela: String,
        calle: String,
        numero: String,
        colonia: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val especieMinuscula = especie.lowercase()
        val imagenAsignada = if (especieMinuscula.contains("pino") || especieMinuscula.contains("conífera")) {
            R.drawable.pino
        } else {
            R.drawable.arbol
        }

        val fechaHoy = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val arbolId = abs(System.currentTimeMillis().toInt())

        val datosArbol = hashMapOf<String, Any>(
            "id" to arbolId,
            "usuarioId" to usuarioId,
            "nombreValor" to nombreValor,
            "especieNombreComun" to especie,
            "escuelaCampana" to escuela,
            "fechaPlantacion" to fechaHoy,
            "imagenRes" to imagenAsignada,
            "tieneMedicion" to false,
            "alturaActual" to 0.0,
            "diametroActual" to 0.0,
            "ubicacion" to mapOf(
                "calle" to calle,
                "numeroExterior" to numero,
                "colonia" to colonia,
                "latitud" to 0.0,
                "longitud" to 0.0
            )
        )

        viewModelScope.launch {
            try {
                repository.plantaArbol(arbolId.toString(), datosArbol)

                cargarArboles(usuarioId)

                onSuccess()
            } catch (e: Exception) {
                Log.e("ArbolViewModel", "Error al plantar árbol", e)
                onError(e.message ?: "Error desconocido al guardar en Firestore")
            }
        }
    }
}

class ArbolViewModelFactory(private val repository: ArbolRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArbolViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArbolViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}