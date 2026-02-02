package com.sttalis.missaokids.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.sttalis.missaokids.R
import com.sttalis.missaokids.databinding.ActivityCriancasBinding
import kotlin.random.Random

class CriancasActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityCriancasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriancasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("TarefasKidsPrefs", Context.MODE_PRIVATE)
        val nome = sharedPref.getString("USER_LOGIN", "Campeão")
        binding.tvSaudacao.text = "Olá, $nome! 🚀"

        // CONFIGURAÇÃO DO CABEÇALHO DO MENU
        val headerView = binding.navView.getHeaderView(0)

        // CORREÇÃO: Usamos o novo ID 'ivPerfilPaiMenu' que está no nav_header.xml atualizado
        val ivHeaderImagem = headerView.findViewById<ImageView>(R.id.ivPerfilPaiMenu)
        val tvHeaderNome = headerView.findViewById<TextView>(R.id.tvNomePaiMenu)

        // Definimos o texto para o modo Criança
        if (tvHeaderNome != null) {
            tvHeaderNome.text = "Minha Área"
        }

        // Carregamos o logo animado no lugar da foto de perfil do header
        try {
            if (ivHeaderImagem != null) {
                Glide.with(this).load(R.drawable.logo_animado).into(ivHeaderImagem)
            }
        } catch (e: Exception) {
            ivHeaderImagem?.setImageResource(R.mipmap.ic_launcher)
        }

        // Carrega a foto da criança no card da tela principal
        Glide.with(this)
            .load(R.drawable.ic_launcher_foreground) // Depois trocaremos pela foto real
            .circleCrop()
            .into(binding.ivFotoCrianca)

        binding.btnMenu.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_perfil, R.id.nav_config -> {
                desafioMatematico {
                    startActivity(Intent(this, PaisActivity::class.java))
                    finish()
                }
                return false // Não marca o item como selecionado visualmente
            }
            R.id.nav_recompensas -> Toast.makeText(this, "Em breve!", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun desafioMatematico(onSucesso: () -> Unit) {
        val n1 = Random.nextInt(1, 15)
        val n2 = Random.nextInt(1, 10)
        val resp = n1 + n2

        val opcoes = mutableSetOf(resp)
        while (opcoes.size < 3) {
            val erro = resp + Random.nextInt(-4, 5)
            if (erro > 0 && erro != resp) opcoes.add(erro)
        }

        val lista = opcoes.toList().shuffled()
        val itens = lista.map { it.toString() }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Área dos Pais 🔒")
            .setMessage("Quanto é $n1 + $n2 ?")
            .setCancelable(false)
            .setItems(itens) { _, i ->
                if (lista[i] == resp) onSucesso()
                else Toast.makeText(this, "Resposta errada!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}