package com.sttalis.missaokids.activity

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.sttalis.missaokids.R

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)


        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltarPerfil)
        btnVoltar.setOnClickListener {
            finish()
        }
    }
}