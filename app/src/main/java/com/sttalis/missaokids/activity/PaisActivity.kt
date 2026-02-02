package com.sttalis.missaokids.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.sttalis.missaokids.model.RecompensaRequest
import com.sttalis.missaokids.model.TarefaRequest
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

        Log.d("PaisActivity", "ID do Pai recuperado: $paiId")

        adapter = FilhosAdapter(emptyList())
        binding.rvListaFilhos.layoutManager = LinearLayoutManager(this)
        binding.rvListaFilhos.adapter = adapter

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
        if (paiId == 0L) {
            Toast.makeText(this, "Erro: ID do usuário inválido. Faça login novamente.", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, "Buscando filhos...", Toast.LENGTH_SHORT).show()

        RetrofitClient.instance.listarFilhos(paiId).enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!
                    Log.d("PaisActivity", "Sucesso! Filhos encontrados: ${lista.size}")
                    configurarLista(lista)
                } else {
                    Log.e("PaisActivity", "Erro na resposta: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(applicationContext, "Não foi possível carregar os dados", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Log.e("PaisActivity", "Falha de conexão: ${t.message}")
                Toast.makeText(applicationContext, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun configurarLista(lista: List<Usuario>) {
        if (lista.isEmpty()) {
            Toast.makeText(this, "Nenhum filho cadastrado ainda.", Toast.LENGTH_SHORT).show()
        }
        adapter = FilhosAdapter(lista)
        binding.rvListaFilhos.adapter = adapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add_filho -> abrirDialogNovoFilho()
            R.id.nav_add_tarefa -> abrirDialogNovaTarefa()
            R.id.nav_add_recompensa -> abrirDialogNovaRecompensa()
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
            avatares.forEach { it.alpha = 0.5f; it.setBackgroundResource(android.R.color.transparent) }
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

                Toast.makeText(this, "Salvando...", Toast.LENGTH_SHORT).show()

                RetrofitClient.instance.adicionarFilho(request).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PaisActivity, "Filho cadastrado!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            buscarFilhosNaApi()
                        } else {
                            Toast.makeText(this@PaisActivity, "Erro ao salvar: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(this@PaisActivity, "Erro de rede (timeout): ${t.message}", Toast.LENGTH_LONG).show()
                        buscarFilhosNaApi()
                    }
                })
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

        val etTitulo = view.findViewById<EditText>(R.id.etTituloTarefaDialog)
        val etValor = view.findViewById<EditText>(R.id.etValorEstrelasDialog)
        val btnSalvar = view.findViewById<Button>(R.id.btnSalvarTarefaDialog)

        btnSalvar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val valor = etValor.text.toString()

            if (titulo.isNotEmpty() && valor.isNotEmpty()) {
                val request = TarefaRequest(
                    paiId = paiId,
                    titulo = titulo,
                    valorEstrelas = valor.toIntOrNull() ?: 1,
                    atribuidaParaId = null
                )

                RetrofitClient.instance.adicionarTarefa(request).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PaisActivity, "Tarefa criada!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this@PaisActivity, "Erro ao criar tarefa", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(this@PaisActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun abrirDialogNovaRecompensa() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_recompensa, null)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val etTitulo = view.findViewById<EditText>(R.id.etTituloRecompensaDialog)
        val etCusto = view.findViewById<EditText>(R.id.etCustoRecompensaDialog)
        val btnSalvar = view.findViewById<Button>(R.id.btnSalvarRecompensaDialog)

        btnSalvar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val custo = etCusto.text.toString()

            if (titulo.isNotEmpty() && custo.isNotEmpty()) {
                val request = RecompensaRequest(
                    paiId = paiId,
                    titulo = titulo,
                    custoEstrelas = custo.toIntOrNull() ?: 10
                )

                RetrofitClient.instance.adicionarRecompensa(request).enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PaisActivity, "Recompensa criada!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this@PaisActivity, "Erro ao criar recompensa", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Toast.makeText(this@PaisActivity, "Erro de rede", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
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