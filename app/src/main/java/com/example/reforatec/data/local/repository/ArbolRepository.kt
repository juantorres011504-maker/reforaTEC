package com.example.reforatec.data.local.repository

import android.net.Uri
import android.util.Log
import com.example.reforatec.data.local.entity.ArbolEntity
import com.example.reforatec.data.local.entity.UbicacionEmbed
import com.example.reforatec.ui.screens.ServicioEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ArbolRepository {
    private val db = FirebaseFirestore.getInstance()
    private val arbolesCollection = db.collection("arboles")
    private val storage = FirebaseStorage.getInstance()

    suspend fun plantaArbol(idDocumento: String, datosArbol: HashMap<String, Any>) {
        arbolesCollection.document(idDocumento).set(datosArbol).await()
    }

    suspend fun obtenerMisArboles(usuarioId: String): List<ArbolEntity> {
        val result = arbolesCollection.whereEqualTo("usuarioId", usuarioId).get().await()
        val lista = mutableListOf<ArbolEntity>()
        for (document in result) {
            try {
                val ubiMap = document.get("ubicacion") as? Map<String, Any>
                val arbol = ArbolEntity(
                    id = (document.get("id") as? Number)?.toInt() ?: 0,
                    usuarioId = 0,
                    nombreValor = document.getString("nombreValor") ?: "",
                    especieNombreComun = document.getString("especieNombreComun") ?: "",
                    escuelaCampana = document.getString("escuelaCampana") ?: "",
                    fechaPlantacion = document.getString("fechaPlantacion") ?: "",
                    imagenRes = (document.getLong("imagenRes") ?: 0L).toInt(),
                    tieneMedicion = document.getBoolean("tieneMedicion") ?: false,
                    alturaActual = document.getDouble("alturaActual") ?: 0.0,
                    diametroActual = document.getDouble("diametroActual") ?: 0.0,
                    ubicacion = UbicacionEmbed(
                        calle = ubiMap?.get("calle") as? String ?: "",
                        numeroExterior = ubiMap?.get("numeroExterior") as? String ?: "",
                        colonia = ubiMap?.get("colonia") as? String ?: "",
                        latitud = (ubiMap?.get("latitud") as? Double) ?: 0.0,
                        longitud = (ubiMap?.get("longitud") as? Double) ?: 0.0
                    )
                )
                lista.add(arbol)
            } catch (e: Exception) {
                Log.e("ArbolRepository", "Error al mapear árbol", e)
            }
        }
        return lista
    }

    suspend fun obtenerArbolPorId(id: Int): ArbolEntity? {
        val result = arbolesCollection.whereEqualTo("id", id).get().await()
        if (result.isEmpty) return null
        val document = result.documents.first()
        val ubiMap = document.get("ubicacion") as? Map<String, Any>
        return ArbolEntity(
            id = (document.get("id") as? Number)?.toInt() ?: 0,
            usuarioId = 0,
            nombreValor = document.getString("nombreValor") ?: "",
            especieNombreComun = document.getString("especieNombreComun") ?: "",
            escuelaCampana = document.getString("escuelaCampana") ?: "",
            fechaPlantacion = document.getString("fechaPlantacion") ?: "",
            imagenRes = (document.getLong("imagenRes") ?: 0L).toInt(),
            tieneMedicion = document.getBoolean("tieneMedicion") ?: false,
            alturaActual = document.getDouble("alturaActual") ?: 0.0,
            diametroActual = document.getDouble("diametroActual") ?: 0.0,
            ubicacion = UbicacionEmbed(
                calle = ubiMap?.get("calle") as? String ?: "",
                numeroExterior = ubiMap?.get("numeroExterior") as? String ?: "",
                colonia = ubiMap?.get("colonia") as? String ?: "",
                latitud = (ubiMap?.get("latitud") as? Double) ?: 0.0,
                longitud = (ubiMap?.get("longitud") as? Double) ?: 0.0
            )
        )
    }

    suspend fun actualizarArbol(arbol: ArbolEntity) {
        val actualizaciones = mapOf(
            "alturaActual" to arbol.alturaActual,
            "diametroActual" to arbol.diametroActual,
            "tieneMedicion" to arbol.tieneMedicion
        )
        arbolesCollection.document(arbol.id.toString()).update(actualizaciones).await()
    }

    suspend fun subirFotoEvidencia(arbolId: String, uriLocal: Uri): String {
        val ref = storage.reference.child("evidencias_servicios/$arbolId/${System.currentTimeMillis()}.jpg")
        ref.putFile(uriLocal).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun agregarServicioANube(arbolId: String, datosServicio: HashMap<String, Any>) {
        arbolesCollection.document(arbolId).collection("servicios").document().set(datosServicio).await()
    }

    suspend fun obtenerHistorialDesdeNube(arbolId: String): List<ServicioEntity> {
        val result = arbolesCollection.document(arbolId).collection("servicios").orderBy("fecha").get().await()
        val listaServicios = mutableListOf<ServicioEntity>()
        for (document in result) {
            listaServicios.add(
                ServicioEntity(
                    id = 0,
                    tipo = document.getString("tipo") ?: "",
                    fecha = document.getString("fecha") ?: "",
                    hora = document.getString("hora") ?: "",
                    comentarios = document.getString("comentarios") ?: "",
                    fotoUri = document.getString("fotoUri")
                )
            )
        }
        return listaServicios
    }
}