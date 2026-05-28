package com.example.reforatec.data.local.repository

import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usuariosCollection = db.collection("usuarios")

    fun registrarUsuario(
        uid: String,
        datosUsuario: HashMap<String, Any>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        usuariosCollection.document(uid).set(datosUsuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun obtenerUsuario(
        uid: String,
        onSuccess: (Map<String, Any>?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        usuariosCollection.document(uid).get()
            .addOnSuccessListener { document ->
                onSuccess(document.data)
            }
            .addOnFailureListener { onError(it) }
    }

    fun actualizarUltimaConexion(
        uid: String,
        fecha: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        usuariosCollection.document(uid).update("ultimaConexion", fecha)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun recuperarPassword(
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