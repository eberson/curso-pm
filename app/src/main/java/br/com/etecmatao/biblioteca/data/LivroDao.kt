package br.com.etecmatao.biblioteca.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.etecmatao.biblioteca.model.Livro

@Dao
interface LivroDao {
    @Query("select * from ${Livro.TABLE_NAME}")
    fun select(): List<Livro>

    @Query("select * from ${Livro.TABLE_NAME} where id = :id")
    fun select(id: Long): Livro

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(livro: Livro)

    @Query("delete from ${Livro.TABLE_NAME} where id = :id")
    fun delete(id: Long)
}