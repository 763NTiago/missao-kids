package com.sttalis.tarefas.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sttalis.tarefas.databinding.ActivityHomeBinding
import com.sttalis.tarefas.databinding.ItemTarefaBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val listaTarefas = mutableListOf<Tarefa>()
    private lateinit var adapter: TarefaAdapter
    private var tarefaSelecionadaPosicao: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarLista()
        carregarDadosFicticios()
    }

    private fun configurarLista() {
        adapter = TarefaAdapter(listaTarefas) { posicao ->
            abrirCamera(posicao)
        }
        binding.rvTarefas.layoutManager = LinearLayoutManager(this)
        binding.rvTarefas.adapter = adapter
    }

    private fun carregarDadosFicticios() {
        listaTarefas.add(Tarefa("Arrumar a Cama", "Pendente"))
        listaTarefas.add(Tarefa("Escovar os Dentes", "Pendente"))
        listaTarefas.add(Tarefa("Guardar os Brinquedos", "Pendente"))
        listaTarefas.add(Tarefa("Fazer o TPC", "Pendente"))
        adapter.notifyDataSetChanged()
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val photo = result.data?.extras?.get("data") as? Bitmap
            photo?.let {
                if (tarefaSelecionadaPosicao != -1) {
                    val tarefa = listaTarefas[tarefaSelecionadaPosicao]
                    tarefa.status = "Concluído! ✅"
                    tarefa.fotoProva = it
                    adapter.notifyItemChanged(tarefaSelecionadaPosicao)
                    Toast.makeText(this, "Boa! Tarefa concluída.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun abrirCamera(posicao: Int) {
        tarefaSelecionadaPosicao = posicao
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            cameraLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao abrir câmara", Toast.LENGTH_SHORT).show()
        }
    }

    data class Tarefa(
        var nome: String,
        var status: String,
        var fotoProva: Bitmap? = null
    )

    inner class TarefaAdapter(
        private val tarefas: List<Tarefa>,
        private val onCameraClick: (Int) -> Unit
    ) : RecyclerView.Adapter<TarefaAdapter.ViewHolder>() {

        inner class ViewHolder(val itemBinding: ItemTarefaBinding) : RecyclerView.ViewHolder(itemBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemTarefaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val tarefa = tarefas[position]
            with(holder.itemBinding) {
                tvNomeTarefa.text = tarefa.nome
                tvStatus.text = tarefa.status

                if (tarefa.fotoProva != null) {
                    btnCamera.visibility = View.GONE
                    ivProva.visibility = View.VISIBLE
                    ivProva.setImageBitmap(tarefa.fotoProva)
                    tvStatus.setTextColor(android.graphics.Color.GREEN)
                } else {
                    btnCamera.visibility = View.VISIBLE
                    ivProva.visibility = View.GONE
                    btnCamera.setOnClickListener { onCameraClick(position) }
                }
            }
        }

        override fun getItemCount() = tarefas.size
    }
}