package com.sttalis.missaokids.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sttalis.missaokids.R
import com.sttalis.missaokids.model.RecompensaResponse

class RecompensasAdapter(
    private val lista: List<RecompensaResponse>,
    private val onResgatarClick: (RecompensaResponse) -> Unit
) : RecyclerView.Adapter<RecompensasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.tvTituloRecompensa)
        val custo: TextView = view.findViewById(R.id.tvCusto)
        val botao: Button = view.findViewById(R.id.btnResgatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recompensa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.titulo.text = item.titulo
        holder.custo.text = "${item.custoEstrelas} Estrelas ⭐"

        holder.botao.setOnClickListener {
            onResgatarClick(item)
        }
    }

    override fun getItemCount() = lista.size
}