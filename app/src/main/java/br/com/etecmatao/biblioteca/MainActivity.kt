package br.com.etecmatao.biblioteca

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import br.com.etecmatao.biblioteca.adapters.LivroAdapter
import br.com.etecmatao.biblioteca.data.BibliotecaApplication
import br.com.etecmatao.biblioteca.model.Livro
import br.com.etecmatao.biblioteca.model.Usuario
import br.com.etecmatao.biblioteca.worker.LoadLivrosWorker
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val adapter: LivroAdapter = LivroAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addLivro.setOnClickListener {
            val intent = Intent(MainActivity@ this, NovoLivroActivity::class.java)
            startActivity(intent)
        }

        livrosRecyclerView.layoutManager = LinearLayoutManager(this)
        livrosRecyclerView.adapter = adapter

        swipeContainer.setOnRefreshListener {
            loadLivros()
        }
    }

    private fun loadLivros() {
        var loadBooksRequest = OneTimeWorkRequestBuilder<LoadLivrosWorker>().build()

        var observeResponse = Observer<WorkInfo> {
            if (it == null) {
                return@Observer
            }

            if (it.state == WorkInfo.State.SUCCEEDED) {
                val livrosJson = it.outputData.getString("jsonLivros")!!
                val livros = jacksonObjectMapper().readValue<List<Livro>>(livrosJson)
                adapter.clear()
                adapter.addAll(livros)
            }

            if (it.state == WorkInfo.State.SUCCEEDED || it.state == WorkInfo.State.FAILED) {
                Snackbar.make(
                    swipeContainer,
                    it.outputData.getString("msg")!!,
                    Snackbar.LENGTH_LONG
                ).show()

                swipeContainer.isRefreshing = false;
            }

        }

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(loadBooksRequest)
        workManager.getWorkInfoByIdLiveData(loadBooksRequest.id).observe(this, observeResponse)
    }

    override fun onStart() {
        super.onStart()

        with(application as BibliotecaApplication) {
            if (usuario == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivityForResult(intent, 1010)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1010) {
            val extra = data!!.getStringExtra("jsonUser")

            extra?.let {
                val user = jacksonObjectMapper().readValue<Usuario>(extra)

                with(application as BibliotecaApplication) {
                    usuario = user
                }

                loadLivros()
            }
        }
    }
}

