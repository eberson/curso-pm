package br.com.etecmatao.biblioteca

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import br.com.etecmatao.biblioteca.worker.SaveUserWorker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


class RegisterActivity : AppCompatActivity() {

    lateinit var campoNome: TextInputEditText
    lateinit var campoEmail: TextInputEditText
    lateinit var campoTelefone: TextInputEditText
    lateinit var campoSenha: TextInputEditText
    lateinit var campoConfirmaSenha: TextInputEditText

    lateinit var fields: Array<TextInputEditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        campoNome = findViewById(R.id.txtNome)
        campoEmail = findViewById(R.id.txtEmail)
        campoTelefone = findViewById(R.id.txtTelefone)
        campoSenha = findViewById(R.id.txtSenha)
        campoConfirmaSenha = findViewById(R.id.txtConformaSenha)

        fields = arrayOf(campoNome, campoEmail, campoTelefone, campoSenha, campoConfirmaSenha)
    }

    fun validar(): Boolean {
        var error = false

        for (field in fields) {
            field.error = null

            if (TextUtils.isEmpty(field.text.toString())) {
                field.error = resources.getString(R.string.msg_required_field)
                error = true
            }
        }

        if (!TextUtils.equals(campoSenha.text.toString(), campoConfirmaSenha.text.toString())) {
            campoConfirmaSenha.error = resources.getString(R.string.msg_password_different)
            error = true
        }

        return !error
    }

    fun salvar(v: View) {
        if (!validar()) {
            return
        }

        val input = workDataOf(
            "nome" to campoNome.text.toString(),
            "email" to campoEmail.text.toString(),
            "telefone" to campoTelefone.text.toString(),
            "senha" to campoSenha.text.toString()
        )

        var request = OneTimeWorkRequestBuilder<SaveUserWorker>()
            .setInputData(input)
            .build()

        var resultObserver = Observer<WorkInfo> {
            if (it == null){
                return@Observer
            }

            if (it.state == WorkInfo.State.SUCCEEDED || it.state == WorkInfo.State.FAILED){
                val state = it.state

                val snackBar = Snackbar.make(
                    v,
                    it.outputData.getString("result")!!,
                    Snackbar.LENGTH_LONG
                )

                snackBar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)

                        if (state == WorkInfo.State.SUCCEEDED){
                            finish()
                        }
                    }
                })

                snackBar.show()
            }
        }


        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(request)
        workManager.getWorkInfoByIdLiveData(request.id).observe(this, resultObserver)
    }
}
