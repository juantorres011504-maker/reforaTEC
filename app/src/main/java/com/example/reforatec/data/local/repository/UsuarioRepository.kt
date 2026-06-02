package com.example.reforatec.data.local.repository

import com.example.reforatec.data.remote.AuthRequest
import com.example.reforatec.data.remote.FirestoreDocument
import com.example.reforatec.data.remote.FirestoreValue
import com.example.reforatec.data.remote.ReforaTECApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UsuarioRepository {

    private val API_KEY = "AIzaSyBspZjSrn1aRPYif2hL1geDPFzUwWv7QCE"
    private val PROJECT_ID = "reforatec"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://firestore.googleapis.com/v1/projects/reforatec/databases/(default)/documents/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ReforaTECApi::class.java)

    //LOGIN CON RETROFIT
    suspend fun loginRest(correo: String, contrasena: String): String {
        val request = AuthRequest(correo, contrasena)
        val response = api.loginAuth(API_KEY, request)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.localId
        } else {
            throw Exception("Correo o contraseña incorrectos")
        }
    }

    //REGISTRO CON RETROFUT
    suspend fun registrarRest(correo: String, contrasena: String): String {
        val request = AuthRequest(correo, contrasena)
        val response = api.registrarAuth(API_KEY, request)

        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.localId
        } else {
            throw Exception("Error al crear cuenta. Quizá el correo ya existe.")
        }
    }

    //guardar el peerfil en firestore con JSON traducido
    suspend fun guardarPerfilFirestoreRest(
        uid: String, nombres: String, apPaterno: String, apMaterno: String,
        telefono: String, sexo: String, correo: String, fechaHoy: String
    ) {
        //armamos el json
        val documento = FirestoreDocument(
            name = "",
            fields = mapOf(
                "nombres" to FirestoreValue(stringValue = nombres),
                "apPaterno" to FirestoreValue(stringValue = apPaterno),
                "apMaterno" to FirestoreValue(stringValue = apMaterno),
                "telefono" to FirestoreValue(stringValue = telefono),
                "sexo" to FirestoreValue(stringValue = sexo),
                "correo" to FirestoreValue(stringValue = correo),
                "fechaRegistro" to FirestoreValue(stringValue = fechaHoy),
                "ultimaConexion" to FirestoreValue(stringValue = fechaHoy)
            )
        )

        val response = api.crearPerfilUsuario(uid, documento)
        if (!response.isSuccessful) {
            throw Exception("No se pudo guardar el perfil en Firestore")
        }
    }

    //obtener el perfil con rest
    suspend fun obtenerPerfilRest(uid: String): Map<String, String> {
        val response = api.obtenerPerfilUsuario(uid)

        if (response.isSuccessful && response.body() != null) {
            val fields = response.body()!!.fields ?: return emptyMap()

            return mapOf(
                "nombres" to (fields["nombres"]?.stringValue ?: ""),
                "apPaterno" to (fields["apPaterno"]?.stringValue ?: ""),
                "apMaterno" to (fields["apMaterno"]?.stringValue ?: ""),
                "telefono" to (fields["telefono"]?.stringValue ?: ""),
                "sexo" to (fields["sexo"]?.stringValue ?: ""),
                "correo" to (fields["correo"]?.stringValue ?: ""),
                "fechaRegistro" to (fields["fechaRegistro"]?.stringValue ?: ""),
                "ultimaConexion" to (fields["ultimaConexion"]?.stringValue ?: "")
            )
        } else {
            throw Exception("No se pudo descargar el perfil")
        }
    }

}