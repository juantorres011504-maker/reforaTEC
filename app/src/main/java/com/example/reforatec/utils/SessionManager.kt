package com.example.reforatec.utils

import android.content.Context

class SessionManager(context: Context) {
    // Creamos un archivo de preferencias privado
    private val prefs = context.getSharedPreferences("reforatec_sesion", Context.MODE_PRIVATE)

    //AHORA GUARDAMOS UN STRING (EL UID de Firebase)
    fun saveSession(userId: String) {
        prefs.edit().putString("USER_ID", userId).apply()
    }

    //ahora devolvemos un string, devuelve null si no hay nadie logueado
    fun getUserId(): String? {
        return prefs.getString("USER_ID", null)
    }

    //comprueba si hay una sesion activa
    fun isLoggedIn(): Boolean {
        return getUserId() != null
    }

    //borra la sesion
    fun clearSession() {
        prefs.edit().remove("USER_ID").apply()
    }

    //funciones para el tema
    fun saveTheme(theme: String) {
        prefs.edit().putString("APP_THEME", theme).apply()
    }

    fun getTheme(): String {
        return prefs.getString("APP_THEME", "Predeterminado del sistema") ?: "Predeterminado del sistema"
    }
}