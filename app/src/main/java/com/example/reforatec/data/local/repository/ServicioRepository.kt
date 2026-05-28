package com.example.reforatec.data.local.repository

import com.example.reforatec.data.local.dao.ServicioDao
import com.example.reforatec.data.local.entity.ServicioEntity

class ServicioRepository(private val servicioDao: ServicioDao) {
    suspend fun agregarServicio(servicio: ServicioEntity) {
        servicioDao.insertarServicio(servicio)
    }

    suspend fun obtenerHistorial(arbolId: Int): List<ServicioEntity> {
        return servicioDao.obtenerServicioPorArbol(arbolId)
    }

    suspend fun contarServicios(usuarioId: Int): Int {
        return servicioDao.contarServicios(usuarioId)
    }
}