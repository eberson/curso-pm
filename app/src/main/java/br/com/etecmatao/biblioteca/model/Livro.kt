package br.com.etecmatao.biblioteca.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.etecmatao.biblioteca.model.Livro.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Livro(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = COLUMN_TITLE) var titulo: String,
    @ColumnInfo(name = COLUMN_AUTHOR) var autor: String,
    @ColumnInfo(name = COLUMN_TOTAL_PAGES) var qtdePaginas: Int,
    @ColumnInfo(name = COLUMN_YEAR_RELEASED) var anoLancamento: Int
){
    companion object{
        const val TABLE_NAME = "livros"
        const val COLUMN_TITLE = "titulo"
        const val COLUMN_AUTHOR = "autor"
        const val COLUMN_TOTAL_PAGES = "qtde_paginas"
        const val COLUMN_YEAR_RELEASED = "ano_lancamento"
    }
}