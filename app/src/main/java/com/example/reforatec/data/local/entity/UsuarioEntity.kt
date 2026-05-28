package com.example.reforatec.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    //autogenerate hace que el ID sea solito 1, 2, 3...
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    //datos personales (vienen de registro)
    val nombres: String,
    val apPaterno: String,
    val apMaterno: String,
    val telefono: String,
    val correo: String,
    val contrasena: String,
    val sexo: String,

    //datps del sistema (para llenar el perfil)
    val fechaRegistro : String,
    val ultimaConexion:String,
    val totalArbolesCuidados:Int = 0,
    val totalServiciosDados: Int = 0
)