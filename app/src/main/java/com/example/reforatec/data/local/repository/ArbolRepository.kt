package com.example.reforatec.data.local.repository

import android.net.Uri
import android.util.Log
import com.example.reforatec.data.local.entity.ArbolEntity
import com.example.reforatec.data.local.entity.UbicacionEmbed
import com.example.reforatec.data.remote.FirestoreDocument
import com.example.reforatec.data.remote.FirestoreMapValue
import com.example.reforatec.data.remote.FirestoreValue
import com.example.reforatec.data.remote.ReforaTECApi
import com.example.reforatec.data.local.entity.ServicioEntity
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArbolRepository {

    private val PROJECT_ID = "reforatec"

    private val storage = FirebaseStorage.getInstance()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://firestore.googleapis.com/v1/projects/$PROJECT_ID/databases/(default)/documents/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ReforaTECApi::class.java)


    // plantar arbol con REST
    suspend fun plantaArbol(idDocumento: String, datosArbol: HashMap<String, Any>) {
        val ubiMap = datosArbol["ubicacion"] as Map<String, Any>

        val documento = FirestoreDocument(
            name = "",
            fields = mapOf(
                "id" to FirestoreValue(integerValue = datosArbol["id"].toString()),
                "usuarioId" to FirestoreValue(stringValue = datosArbol["usuarioId"].toString()),
                "nombreValor" to FirestoreValue(stringValue = datosArbol["nombreValor"].toString()),
                "especieNombreComun" to FirestoreValue(stringValue = datosArbol["especieNombreComun"].toString()),
                "escuelaCampana" to FirestoreValue(stringValue = datosArbol["escuelaCampana"].toString()),
                "fechaPlantacion" to FirestoreValue(stringValue = datosArbol["fechaPlantacion"].toString()),
                "imagenRes" to FirestoreValue(integerValue = datosArbol["imagenRes"].toString()),
                "tieneMedicion" to FirestoreValue(booleanValue = datosArbol["tieneMedicion"] as? Boolean ?: false),
                "alturaActual" to FirestoreValue(doubleValue = (datosArbol["alturaActual"] as? Number)?.toDouble() ?: 0.0),
                "diametroActual" to FirestoreValue(doubleValue = (datosArbol["diametroActual"] as? Number)?.toDouble() ?: 0.0),
                "ubicacion" to FirestoreValue(
                    mapValue = FirestoreMapValue(
                        fields = mapOf(
                            "calle" to FirestoreValue(stringValue = ubiMap["calle"].toString()),
                            "numeroExterior" to FirestoreValue(stringValue = ubiMap["numeroExterior"].toString()),
                            "colonia" to FirestoreValue(stringValue = ubiMap["colonia"].toString()),
                            "latitud" to FirestoreValue(doubleValue = (ubiMap["latitud"] as? Number)?.toDouble() ?: 0.0),
                            "longitud" to FirestoreValue(doubleValue = (ubiMap["longitud"] as? Number)?.toDouble() ?: 0.0)
                        )
                    )
                )
            )
        )
        api.guardarArbol(idDocumento, documento)
    }

    //obtener mis arboles con REST
    suspend fun obtenerMisArboles(usuarioId: String): List<ArbolEntity> {
        val lista = mutableListOf<ArbolEntity>()
        try {
            val response = api.obtenerArboles()
            if (response.isSuccessful && response.body() != null) {
                val documentos = response.body()!!.documents ?: emptyList()

                for (doc in documentos) {
                    val fields = doc.fields ?: continue
                    val idDueño = fields["usuarioId"]?.stringValue ?: ""

                    if (idDueño == usuarioId) {
                        val ubiFields = fields["ubicacion"]?.mapValue?.fields

                        val arbol = ArbolEntity(
                            id = fields["id"]?.integerValue?.toInt() ?: 0,
                            usuarioId = 0,
                            nombreValor = fields["nombreValor"]?.stringValue ?: "",
                            especieNombreComun = fields["especieNombreComun"]?.stringValue ?: "",
                            escuelaCampana = fields["escuelaCampana"]?.stringValue ?: "",
                            fechaPlantacion = fields["fechaPlantacion"]?.stringValue ?: "",
                            imagenRes = fields["imagenRes"]?.integerValue?.toInt() ?: 0,
                            tieneMedicion = fields["tieneMedicion"]?.booleanValue ?: false,
                            alturaActual = fields["alturaActual"]?.doubleValue ?: 0.0,
                            diametroActual = fields["diametroActual"]?.doubleValue ?: 0.0,
                            ubicacion = UbicacionEmbed(
                                calle = ubiFields?.get("calle")?.stringValue ?: "",
                                numeroExterior = ubiFields?.get("numeroExterior")?.stringValue ?: "",
                                colonia = ubiFields?.get("colonia")?.stringValue ?: "",
                                latitud = ubiFields?.get("latitud")?.doubleValue ?: 0.0,
                                longitud = ubiFields?.get("longitud")?.doubleValue ?: 0.0
                            )
                        )
                        lista.add(arbol)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ArbolRepository", "Error al obtener árboles REST", e)
        }
        return lista
    }

    // obtener arbol por id REST
    suspend fun obtenerArbolPorId(id: Int): ArbolEntity? {
        try {
            val response = api.obtenerArbolPorId(id.toString())
            if (response.isSuccessful && response.body() != null) {
                val fields = response.body()!!.fields ?: return null
                val ubiFields = fields["ubicacion"]?.mapValue?.fields

                return ArbolEntity(
                    id = fields["id"]?.integerValue?.toInt() ?: 0,
                    usuarioId = 0,
                    nombreValor = fields["nombreValor"]?.stringValue ?: "",
                    especieNombreComun = fields["especieNombreComun"]?.stringValue ?: "",
                    escuelaCampana = fields["escuelaCampana"]?.stringValue ?: "",
                    fechaPlantacion = fields["fechaPlantacion"]?.stringValue ?: "",
                    imagenRes = fields["imagenRes"]?.integerValue?.toInt() ?: 0,
                    tieneMedicion = fields["tieneMedicion"]?.booleanValue ?: false,
                    alturaActual = fields["alturaActual"]?.doubleValue ?: 0.0,
                    diametroActual = fields["diametroActual"]?.doubleValue ?: 0.0,
                    ubicacion = UbicacionEmbed(
                        calle = ubiFields?.get("calle")?.stringValue ?: "",
                        numeroExterior = ubiFields?.get("numeroExterior")?.stringValue ?: "",
                        colonia = ubiFields?.get("colonia")?.stringValue ?: "",
                        latitud = ubiFields?.get("latitud")?.doubleValue ?: 0.0,
                        longitud = ubiFields?.get("longitud")?.doubleValue ?: 0.0
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("ArbolRepository", "Error al obtener árbol por ID REST", e)
        }
        return null
    }


    // actualzar arbol con REST
    suspend fun actualizarArbol(arbol: ArbolEntity) {
        try {
            val response = api.obtenerArbolPorId(arbol.id.toString())
            if (response.isSuccessful && response.body() != null) {
                val docExistente = response.body()!!
                val camposMutables = docExistente.fields?.toMutableMap() ?: mutableMapOf()

                camposMutables["alturaActual"] = FirestoreValue(doubleValue = arbol.alturaActual)
                camposMutables["diametroActual"] = FirestoreValue(doubleValue = arbol.diametroActual)
                camposMutables["tieneMedicion"] = FirestoreValue(booleanValue = arbol.tieneMedicion)

                val nuevoDoc = docExistente.copy(fields = camposMutables)
                api.guardarArbol(arbol.id.toString(), nuevoDoc)
            }
        } catch (e: Exception) {
            Log.e("ArbolRepository", "Error al actualizar árbol REST", e)
        }
    }

    // subir foto de evidencia aun con firebase nativo
    suspend fun subirFotoEvidencia(arbolId: String, uriLocal: Uri): String {
        val ref = storage.reference.child("evidencias_servicios/$arbolId/${System.currentTimeMillis()}.jpg")
        ref.putFile(uriLocal).await()
        return ref.downloadUrl.await().toString()
    }

    // agregar el servicio a la nube con REST
    suspend fun agregarServicioANube(arbolId: String, datosServicio: HashMap<String, Any>) {
        val documento = FirestoreDocument(
            name = "",
            fields = mapOf(
                "tipo" to FirestoreValue(stringValue = datosServicio["tipo"].toString()),
                "fecha" to FirestoreValue(stringValue = datosServicio["fecha"].toString()),
                "hora" to FirestoreValue(stringValue = datosServicio["hora"].toString()),
                "comentarios" to FirestoreValue(stringValue = datosServicio["comentarios"].toString()),
                "altura" to FirestoreValue(stringValue = datosServicio["altura"].toString()),
                "diametro" to FirestoreValue(stringValue = datosServicio["diametro"].toString()),
                "fotoUri" to FirestoreValue(stringValue = datosServicio["fotoUri"]?.toString() ?: "")
            )
        )
        api.guardarServicio(arbolId, documento)
    }

    // obtener historial rest
    suspend fun obtenerHistorialDesdeNube(arbolId: String): List<ServicioEntity> {
        val listaServicios = mutableListOf<ServicioEntity>()
        try {
            val response = api.obtenerServiciosDeArbol(arbolId)
            if (response.isSuccessful && response.body() != null) {
                val documentos = response.body()!!.documents ?: emptyList()

                for (doc in documentos) {
                    val fields = doc.fields ?: continue

                    val fotoString = fields["fotoUri"]?.stringValue
                    val fotoFinal = if (fotoString.isNullOrBlank() || fotoString == "null") null else fotoString

                    listaServicios.add(
                        ServicioEntity(
                            id = 0,
                            arbolId = arbolId.toIntOrNull() ?: 0,
                            tipo = fields["tipo"]?.stringValue ?: "",
                            fecha = fields["fecha"]?.stringValue ?: "",
                            hora = fields["hora"]?.stringValue ?: "",
                            comentarios = fields["comentarios"]?.stringValue ?: "",
                            altura = fields["altura"]?.stringValue ?: "",
                            diametro = fields["diametro"]?.stringValue ?: "",
                            fotoUri = fotoFinal
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("ArbolRepository", "Error al obtener servicios REST", e)
        }

        return listaServicios.sortedByDescending { it.fecha }
    }
}