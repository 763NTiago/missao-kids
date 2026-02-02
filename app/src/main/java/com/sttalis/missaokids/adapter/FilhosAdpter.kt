package com.sttalis.missaokids.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sttalis.missaokids.R
import com.sttalis.missaokids.databinding.ItemFilhoBinding
import com.sttalis.missaokids.model.Usuario
import com.sttalis.missaokids.utils.ImageUtil

class FilhosAdapter(private val listaFilhos: List<Usuario>) :
    RecyclerView.Adapter<FilhosAdapter.FilhoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilhoViewHolder {
        val binding = ItemFilhoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilhoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilhoViewHolder, position: Int) {
        holder.bind(listaFilhos[position])
    }

    override fun getItemCount(): Int = listaFilhos.size

    inner class FilhoViewHolder(private val binding: ItemFilhoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filho: Usuario) {
            binding.tvNomeFilhoLista.text = filho.nome ?: filho.login
            binding.tvTarefasPendentes.text = "0 tarefas pendentes"
            binding.pbProgressoFilho.progress = 50
            binding.tvPorcentagem.text = "50%"

            val bitmap = ImageUtil.convertBase64ToBitmap(filho.fotoBase64)
            if (bitmap != null) {
                binding.ivAvatarFilho.setImageBitmap(bitmap)
            } else {
                binding.ivAvatarFilho.setImageResource(R.drawable.ic_person)
            }
        }
    }
}