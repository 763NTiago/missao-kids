package com.sttalis.missaokids.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.sttalis.missaokids.R
import com.sttalis.missaokids.adapter.FilhosAdapter
import com.sttalis.missaokids.api.RetrofitClient
import com.sttalis.missaokids.databinding.ActivityPaisBinding
import com.sttalis.missaokids.model.FilhoRequest
import com.sttalis.missaokids.model.Usuario
import com.sttalis.missaokids.utils.ImageUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaisActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityPaisBinding
    private lateinit var adapter: FilhosAdapter
    private var paiId: Long = 0
    private var avatarSelecionadoId: Int = R.drawable.ic_person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("TarefasKidsPrefs", Context.MODE_PRIVATE)
        paiId = sharedPref.getLong("USER_ID", 0)
        val nomePai = sharedPref.getString("USER_LOGIN", "Pai")

        setupDrawer(nomePai)
        buscarFilhosNaApi()
    }

    private fun setupDrawer(nomePai: String?) {
        binding.btnMenuPais.setOnClickListener {
            binding.drawerLayoutPais.openDrawer(GravityCompat.START)
        }
        binding.navViewPais.setNavigationItemSelectedListener(this)

        val headerView = binding.navViewPais.getHeaderView(0)
        val tvNomeHeader = headerView.findViewById<TextView>(R.id.tvNomePaiMenu)
        tvNomeHeader.text = "Olá, $nomePai!"

        val ivPerfilPai = headerView.findViewById<ImageView>(R.id.ivPerfilPaiMenu)
        ivPerfilPai.setImageResource(R.drawable.ic_person)
    }

    private fun buscarFilhosNaApi() {
        if (paiId == 0L) return

        val call = RetrofitClient.instance.listarFilhos(paiId)
        call.enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful && response.body() != null) {
                    configurarLista(response.body()!!)
                }
            }
            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(applicationContext, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun configurarLista(lista: List<Usuario>) {
        adapter = FilhosAdapter(lista)
        binding.rvListaFilhos.layoutManager = LinearLayoutManager(this)
        binding.rvListaFilhos.adapter = adapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add_filho -> abrirDialogNovoFilho()
            R.id.nav_add_tarefa -> abrirDialogNovaTarefa()
            R.id.nav_add_recompensa -> Toast.makeText(this, "Em breve", Toast.LENGTH_SHORT).show()
            R.id.nav_perfil_pai -> startActivity(Intent(this, PerfilActivity::class.java))
            R.id.nav_sair -> deslogar()
        }
        binding.drawerLayoutPais.closeDrawer(GravityCompat.START)
        return true
    }

    private fun abrirDialogNovoFilho() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_filho, null)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val etNome = view.findViewById<EditText>(R.id.etNomeFilhoDialog)
        val etLogin = view.findViewById<EditText>(R.id.etLoginFilhoDialog)
        val etSenha = view.findViewById<EditText>(R.id.etSenhaFilhoDialog)
        val btnSalvar = view.findViewById<Button>(R.id.btnSalvarFilhoDialog)

        val avatares = listOf(
            view.findViewById<ImageView>(R.id.avatar1),
            view.findViewById<ImageView>(R.id.avatar2),
            view.findViewById<ImageView>(R.id.avatar3),
            view.findViewById<ImageView>(R.id.avatar4),
            view.findViewById<ImageView>(R.id.avatar5),
            view.findViewById<ImageView>(R.id.avatar6)
        )

        val recursos = listOf(
            R.drawable.ic_person,
            android.R.drawable.ic_menu_camera,
            android.R.drawable.star_big_on,
            android.R.drawable.ic_btn_speak_now,
            android.R.drawable.ic_lock_idle_alarm,
            android.R.drawable.ic_menu_mapmode
        )

        fun selecionarAvatar(index: Int) {
            avatares.forEach { it.alpha = 0.5f; it.setBackgroundResource(R.color.white) }
            avatares[index].alpha = 1.0f
            avatares[index].setBackgroundResource(R.drawable.bg_rainbow_gradient)
            avatarSelecionadoId = recursos[index]
        }

        avatares.forEachIndexed { index, img -> img.setOnClickListener { selecionarAvatar(index) } }
        selecionarAvatar(0)

        btnSalvar.setOnClickListener {
            val nome = etNome.text.toString()
            val login = etLogin.text.toString()
            val senha = etSenha.text.toString()

            if (nome.isNotEmpty() && login.isNotEmpty() && senha.isNotEmpty()) {
                val fotoBase64 = ImageUtil.convertResourceToBase64(this, avatarSelecionadoId)
                val request = FilhoRequest(paiId, nome, login, senha, fotoBase64)

                Toast.makeText(this, "Filho cadastrado!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Preencha tudo!", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun abrirDialogNovaTarefa() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_tarefa, null)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btnSalvarTarefaDialog).setOnClickListener {
            Toast.makeText(this, "Tarefa criada!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deslogar() {
        val sharedPref = getSharedPreferences("TarefasKidsPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}