package br.com.etecmatao.biblioteca

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import br.com.etecmatao.biblioteca.worker.LoginWorker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    lateinit var campoEmail:TextInputEditText
    lateinit var campoSenha:TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        campoEmail = findViewById(R.id.txtUsuario)
        campoSenha = findViewById(R.id.txtSenha)
    }

    fun startUserRegister(v: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun validate():Boolean{
        var valid = true
        campoEmail.error = null
        campoSenha.error = null

        if (TextUtils.isEmpty(campoEmail.text.toString())){
            campoEmail.error = resources.getString(R.string.msg_required_field)
            valid = false
        }

        if (TextUtils.isEmpty(campoSenha.text.toString())){
            campoSenha.error = resources.getString(R.string.msg_required_field)
            valid = false
        }

        return valid
    }

    fun login(v:View){
        if(!validate()){
            return
        }

        var credential = workDataOf(
            "email" to campoEmail.text.toString(),
            "senha" to campoSenha.text.toString()
        )

        var loginRequest = OneTimeWorkRequestBuilder<LoginWorker>()
            .setInputData(credential)
            .build()

        var observeResponse = Observer<WorkInfo> {
            if (it == null){
                return@Observer
            }

            if (it.state == WorkInfo.State.SUCCEEDED){
                val jsonUser = it.outputData.getString("jsonUser")

                Snackbar.make(
                    this.window.decorView,
                    it.outputData.getString("msg")!!,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.btn_ok) {
                    intent = LoginActivity@this.intent
                    intent.putExtra("jsonUser", jsonUser)

                    LoginActivity@this.setResult(Activity.RESULT_OK, intent)
                    LoginActivity@this.finish()
                }.show()
            }

            if (it.state == WorkInfo.State.FAILED){
                Snackbar.make(
                    v,
                    it.outputData.getString("msg")!!,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(loginRequest)
        workManager.getWorkInfoByIdLiveData(loginRequest.id).observe(this, observeResponse)
    }

}
