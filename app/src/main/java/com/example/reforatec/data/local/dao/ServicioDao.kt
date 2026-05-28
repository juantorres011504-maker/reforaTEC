package com.example.reforatec.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reforatec.data.local.entity.ServicioEntity

@Dao
interface ServicioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarServicio(servicio: ServicioEntity)

    //buscamos solo los servicios de UN árbol en especifico, ordenados del más nuevo al más nuevo
    @Query("SELECT * FROM servicios WHERE arbolId = :arbolId ORDER BY id DESC")
    suspend fun obtenerServicioPorArbol(arbolId: Int): List<ServicioEntity>

    @Query("SELECT COUNT(servicios.id) FROM servicios INNER JOIN arboles ON servicios.arbolId = arboles.id WHERE arboles.usuarioId = :usuarioId")
    suspend fun contarServicios(usuarioId: Int): Int
}