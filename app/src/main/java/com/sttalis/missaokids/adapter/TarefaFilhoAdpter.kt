package com.sttalis.missaokids.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sttalis.missaokids.R
import com.sttalis.missaokids.model.TarefaResponse

class TarefasFilhoAdapter(
    private val tarefas: List<TarefaResponse>
) : RecyclerView.Adapter<TarefasFilhoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloTarefa)
        val tvValor: TextView = view.findViewById(R.id.tvValorTarefa)
        val btnConcluir: Button = view.findViewById(R.id.btnConcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarefa_filho, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tarefa = tarefas[position]
        holder.tvTitulo.text = tarefa.titulo
        holder.tvValor.text = "+${tarefa.valorEstrelas}"
    }

    override fun getItemCount() = tarefas.size
}