package br.com.etecmatao.sorteio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun sortearNumero(view: View){
        var numeroSorteado = Random().nextInt(11)

        var resposta = findViewById<TextView>(R.id.resposta)
        resposta.text = "Número sorteado é $numeroSorteado"
    }
}
