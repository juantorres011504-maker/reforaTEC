package com.example.reforatec.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servicios")
data class ServicioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    //esto conecta el servidor con un arbol especifico
    val arbolId: Int,

    val tipo: String,
    val fecha: String,
    val hora: String,
    val comentarios: String,

    //campos solo se llenarán si el tipo es "Medicion"
    val altura: String = "",
    val diametro: String,
    //fotooo
    val fotoUri: String? = null
)