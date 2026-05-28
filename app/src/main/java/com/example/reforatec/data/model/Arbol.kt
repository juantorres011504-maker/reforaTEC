package com.example.reforatec.data.model

data class Especie(
    val id:String,
    val nombreComun: String,
    val nombreCientifico: String,
    val descripcion: String
)

data class Ubicacion(
    val latitud: Double,
    val longitud: Double,
    val calle: String,
    val colonia: String,
    val numeroExterior: String
)

data class Arbol(
    val id: String,
    val especie: Especie,
    val nombreValor: String,
    val estaActivo: Boolean,
    val fechaPlantacion: String,
    val alturaActual: Double,
    val diametroActual: Double,
    val ubicacion: Ubicacion,
    val notas: String,
    val escuelaCampana: String,
    val imagenRes: Int
)