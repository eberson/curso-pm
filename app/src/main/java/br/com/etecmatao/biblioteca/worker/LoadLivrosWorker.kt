package br.com.etecmatao.biblioteca.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import br.com.etecmatao.biblioteca.R
import br.com.etecmatao.biblioteca.data.BibliotecaDatabase
import br.com.etecmatao.biblioteca.model.Livro
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.lang.Exception

class LoadLivrosWorker (var ctx: Context, params: WorkerParameters): Worker(ctx, params){
    override fun doWork(): Result {
        return try {
           // val books = BibliotecaDatabase.getInstance(ctx)!!.livroDao().select()
            val books: List<Livro> = listOf(
                Livro(1, "Vidas Secas", "Graciliano Ramos", 176, 1938)
            )

            val mapper = jacksonObjectMapper()

            val out = workDataOf(
                "msg" to ctx.resources.getString(R.string.msg_books_found, books.count()),
                "jsonLivros" to mapper.writeValueAsString(books)
            )

            Result.success(out)
        } catch (e: Exception) {
            Result.failure(workDataOf("msg" to e.message))
        }
    }

}