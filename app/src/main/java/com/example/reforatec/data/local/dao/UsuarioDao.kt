package com.example.reforatec.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reforatec.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    //1.crear cuenta (regustro)
    //devuelve un long que representa el id del usuario recien creado
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long

    //2. iniciar sesion
    //busca un usuario que coincida exactmente con el email y la contraseña
    // devuelve usuarioEntity si lo encuentra, o null si los datos son incorrectos
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun login(correo:String, contrasena: String): UsuarioEntity?

    //3. obtener datos para el perfil
    //busca a un usuario por su id para mostrar sus estadisrticas
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: Int): UsuarioEntity?

    //nueva consulta de actualizacion
    @Query("UPDATE usuarios SET ultimaConexion = :fecha WHERE id = :id")
    suspend fun actualizarUltimaConexion(id: Int, fecha: String)
}