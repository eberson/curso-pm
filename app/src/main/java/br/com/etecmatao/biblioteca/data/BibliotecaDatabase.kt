package br.com.etecmatao.biblioteca.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.etecmatao.biblioteca.model.Livro
import br.com.etecmatao.biblioteca.model.Usuario

@Database(entities = [(Usuario::class), (Livro::class)], version = 2)
abstract class BibliotecaDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun livroDao(): LivroDao

    companion object {
        private var INSTANCE: BibliotecaDatabase? = null

        fun getInstance(context: Context): BibliotecaDatabase? {
            if (INSTANCE == null) {
                synchronized(BibliotecaDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        BibliotecaDatabase::class.java,
                        "biblioteca.db"
                    ).build()
                }
            }

            return INSTANCE
        }
    }

}