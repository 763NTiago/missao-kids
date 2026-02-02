package com.sttalis.missaokids.activity

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sttalis.missaokids.R
import com.sttalis.missaokids.api.RetrofitClient
import com.sttalis.missaokids.model.LoginRequest
import com.sttalis.missaokids.model.Usuario
import com.sttalis.missaokids.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("TarefasKidsPrefs", Context.MODE_PRIVATE)
        val jaLogado = sharedPref.getBoolean("IS_LOGGED_IN", false)
        val perfilSalvo = sharedPref.getString("USER_PERFIL", "")

        if (jaLogado) {
            direcionarUsuario(perfilSalvo)
            return
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            Glide.with(this)
                .load(R.drawable.logo_animado)
                .into(binding.logoAnimado)
        } catch (e: Exception) {
        }

        iniciarMusica()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnEntrar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val senha = binding.etSenha.text.toString().trim()

            if (usuario.isNotEmpty() && senha.isNotEmpty()) {
                fazerLoginNaApi(usuario, senha)
            } else {
                Toast.makeText(this, "Preencha usuário e senha!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fazerLoginNaApi(user: String, pass: String) {
        binding.btnEntrar.isEnabled = false
        binding.btnEntrar.text = "Aguarde..."

        val request = LoginRequest(user, pass)
        val call = RetrofitClient.instance.fazerLogin(request)

        call.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                binding.btnEntrar.isEnabled = true
                binding.btnEntrar.text = "ENTRAR"

                if (response.isSuccessful && response.body() != null) {
                    val usuarioLogado = response.body()!!

                    val sharedPref = getSharedPreferences("TarefasKidsPrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putBoolean("IS_LOGGED_IN", true)
                        putLong("USER_ID", usuarioLogado.id)
                        putString("USER_LOGIN", usuarioLogado.login)
                        putString("USER_PERFIL", usuarioLogado.perfil)
                        apply()
                    }

                    direcionarUsuario(usuarioLogado.perfil)
                } else {
                    Toast.makeText(applicationContext, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                binding.btnEntrar.isEnabled = true
                binding.btnEntrar.text = "ENTRAR"
                Toast.makeText(applicationContext, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun direcionarUsuario(perfil: String?) {
        pararMusica()
        val intent = if (perfil == "ROLE_ADMIN" || perfil == "ROLE_RESPONSAVEL") {
            Intent(this, PaisActivity::class.java)
        } else {
            Intent(this, FilhoActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun iniciarMusica() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.musica_login)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(0.5f, 0.5f)
            mediaPlayer?.start()
        } catch (e: Exception) {
        }
    }

    private fun pararMusica() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        pararMusica()
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) mediaPlayer?.start()
    }
}