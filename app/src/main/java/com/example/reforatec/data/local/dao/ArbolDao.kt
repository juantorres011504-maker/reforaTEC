package com.example.reforatec.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reforatec.data.local.entity.ArbolEntity
import com.example.reforatec.data.model.Arbol

@Dao
interface ArbolDao {
    //1.guardar un arbol nuevo (plantar)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarArbol(arbol: ArbolEntity): Long

    //2. obtener solo los arboles dl usuario con la sesion activa
    //los ordenamos porID de forma descendente para que el más nuevo salga siempre arriba
    @Query("SELECT * FROM arboles WHERE usuarioId = :usuarioId ORDER BY id DESC")
    suspend fun obtenerArbolesPorUsuario(usuarioId: Int): List<ArbolEntity>

    @Query("SELECT * FROM arboles WHERE id = :id LIMIT 1")
    suspend fun obtenerArbolPorId(id: Int): ArbolEntity

    @Update
    suspend fun actualizarArbol(arbol: ArbolEntity)

    @Query("SELECT COUNT(*) FROM arboles WHERE usuarioId = :usuarioId")
    suspend fun contarArboles(usuarioId: Int): Int
}