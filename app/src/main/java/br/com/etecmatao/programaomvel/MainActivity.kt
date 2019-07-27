package br.com.etecmatao.programaomvel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun clickButton(view: View){
        var label = findViewById<TextView>(R.id.label)
        label.text = getString(R.string.msg_resposta)
    }


}
