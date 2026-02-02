package com.sttalis.missaokids.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.sttalis.missaokids.R
import com.sttalis.missaokids.adapter.TarefasFilhoAdapter
import com.sttalis.missaokids.api.RetrofitClient
import com.sttalis.missaokids.model.TarefaResponse
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilhoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var filhoId: Long = 0
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filho)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view_filho)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)

        navView.setNavigationItemSelectedListener(this)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val sharedPref = getSharedPreferences("TarefasKidsPrefs", Context.MODE_PRIVATE)
        filhoId = sharedPref.getLong("USER_ID", 0)
        val nome = sharedPref.getString("USER_LOGIN", "Campeão")

        findViewById<TextView>(R.id.tvOlaFilho).text = "Olá, $nome!"
        val ivAvatar = findViewById<CircleImageView>(R.id.ivAvatarFilho)
        ivAvatar.setImageResource(R.drawable.ic_person)

        val headerView = navView.getHeaderView(0)
        val tvHeaderNome = headerView.findViewById<TextView>(R.id.tvNomePaiMenu)
        tvHeaderNome.text = nome

        carregarTarefas()
    }

    private fun carregarTarefas() {
        if (filhoId == 0L) return

        RetrofitClient.instance.listarTarefasPorFamilia(filhoId).enqueue(object : Callback<List<TarefaResponse>> {
            override fun onResponse(call: Call<List<TarefaResponse>>, response: Response<List<TarefaResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val tarefas = response.body()!!
                    val rv = findViewById<RecyclerView>(R.id.rvTarefasFilho)
                    rv.layoutManager = LinearLayoutManager(this@FilhoActivity)
                    rv.adapter = TarefasFilhoAdapter(tarefas)
                }
            }
            override fun onFailure(call: Call<List<TarefaResponse>>, t: Throwable) {
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_sair -> {
                deslogar()
            }
            R.id.nav_recompensas -> {
                startActivity(Intent(this, LojaActivity::class.java))
            }
            R.id.nav_perfil, R.id.nav_config -> {
                Toast.makeText(this, "Área restrita aos pais!", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
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