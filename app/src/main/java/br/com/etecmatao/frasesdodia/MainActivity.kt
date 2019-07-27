package br.com.etecmatao.frasesdodia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var message: TextView

    private val phrases = arrayOf(
        "O importante não é vencer todos os dias, mas lutar sempre.",
        "Maior que a tristeza de não haver vencido é a vergonha de não ter lutado!",
        "É melhor conquistar a si mesmo do que vencer mil batalhas.",
        "Enquanto houver vontade de lutar haverá esperança de vencer."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        message = findViewById(R.id.message)
    }

    fun newPhrase(view: View){
        val size = phrases.size
        val position = Random().nextInt( size )

        message.text = phrases[ position ]
    }
}
