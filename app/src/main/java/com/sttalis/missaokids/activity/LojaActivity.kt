package com.sttalis.missaokids.activity

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sttalis.missaokids.R
import com.sttalis.missaokids.adapter.RecompensasAdapter
import com.sttalis.missaokids.api.RetrofitClient
import com.sttalis.missaokids.model.RecompensaResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LojaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loja)

        val sharedPref = getSharedPreferences("TarefasKidsPrefs", Context.MODE_PRIVATE)
        val filhoId = sharedPref.getLong("USER_ID", 0)

        findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener { finish() }

        carregarRecompensas(filhoId)
    }

    private fun carregarRecompensas(filhoId: Long) {
        RetrofitClient.instance.listarRecompensasPorFamilia(filhoId).enqueue(object : Callback<List<RecompensaResponse>> {
            override fun onResponse(call: Call<List<RecompensaResponse>>, response: Response<List<RecompensaResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!
                    val rv = findViewById<RecyclerView>(R.id.rvRecompensas)
                    rv.layoutManager = LinearLayoutManager(this@LojaActivity)
                    rv.adapter = RecompensasAdapter(lista) { item ->
                        confirmarCompra(item)
                    }
                }
            }
            override fun onFailure(call: Call<List<RecompensaResponse>>, t: Throwable) {
                Toast.makeText(this@LojaActivity, "Erro ao carregar loja", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmarCompra(item: RecompensaResponse) {
        AlertDialog.Builder(this)
            .setTitle("Resgatar Prêmio 🎁")
            .setMessage("Quer gastar ${item.custoEstrelas} estrelas para ganhar '${item.titulo}'?")
            .setPositiveButton("Sim, eu quero!") { _, _ ->
                Toast.makeText(this, "Pedido enviado para os pais! 🎉", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}