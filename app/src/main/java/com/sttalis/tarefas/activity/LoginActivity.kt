package com.sttalis.tarefas.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sttalis.tarefas.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnEntrar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString()
            val senha = binding.etSenha.text.toString()

            if (usuario.isNotEmpty() && senha.isNotEmpty()) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show()
            }
        }
    }
}