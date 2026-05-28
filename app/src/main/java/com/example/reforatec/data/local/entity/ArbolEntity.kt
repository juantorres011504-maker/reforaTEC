package com.example.reforatec.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reforatec.data.model.Ubicacion

data class UbicacionEmbed (
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val calle: String,
    val numeroExterior: String,
    val colonia: String
)

@Entity(tableName = "arboles")
data class ArbolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    //aqiui se relaciona el arbol con la persona
    val usuarioId: Int,

    //identidad
    val nombreValor: String,
    val especieNombreComun: String,
    val escuelaCampana: String,
    val fechaPlantacion: String,

    //aqui se guardara el id de la imagen .pino o .arbol
    val imagenRes: Int,

    //Medicion (inician en 0 y ocultos hasta que se haga el primer serv de medicion)
    val alturaActual: Double = 0.0,
    val diametroActual: Double = 0.0,
    val tieneMedicion: Boolean = false, //esto para mostrar u ocultar en la UI

    @Embedded
    val ubicacion: UbicacionEmbed
)