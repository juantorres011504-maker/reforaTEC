package com.example.reforatec.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.reforatec.data.local.dao.UsuarioDao
import com.example.reforatec.data.local.entity.UsuarioEntity
import com.example.reforatec.data.local.dao.ArbolDao
import com.example.reforatec.data.local.entity.ArbolEntity
import com.example.reforatec.data.local.dao.ServicioDao
import com.example.reforatec.data.local.entity.ServicioEntity

//le decimos a room qué tablas tendra nuestra bd
// Si en el futuro agregas la tabla "ArbolEntity", la pondrías aquí: entities = [UsuarioEntity::class, ArbolEntity::class]
@Database(entities = [UsuarioEntity::class, ArbolEntity::class, ServicioEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    //vinculamos nuestro DAO para que la app pueda usar las consultas
    abstract fun usuarioDao(): UsuarioDao

    //vinvulamos nuestro DAO de arboles
    abstract fun arbolDao(): ArbolDao

    //puente de servicio
    abstract fun servicioDao(): ServicioDao

    //usamos companion object para aplicar el patron singleton
    //esto asegura que solo exista una instancia de la bd abierta en todo momento,
    //evitando que el telefono se quede sin memoria.
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reforatec_database" //este es el nombre del archivo sqlite en el telefono
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}