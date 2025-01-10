package com.example.modulo7

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AvaliacaoAdapter(private val avaliacoes: List<Avaliacao>, private val onItemClick: (Avaliacao) -> Unit) :
    RecyclerView.Adapter<AvaliacaoAdapter.AvaliacaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaliacaoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_avaliacao, parent, false)
        return AvaliacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvaliacaoViewHolder, position: Int) {
        val avaliacao = avaliacoes[position]
        holder.bind(avaliacao)
    }

    override fun getItemCount(): Int {
        return avaliacoes.size
    }

    inner class AvaliacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewAvaliacao: TextView = itemView.findViewById(R.id.textViewAvaliacao)
        private val textViewAvaliacao2: TextView = itemView.findViewById(R.id.textViewAvaliacao2)

        fun bind(avaliacao: Avaliacao) {
            textViewAvaliacao2.text = "STATUS: ${avaliacao.status}"
            textViewAvaliacao.text = "AVALIAÇÃO ${adapterPosition + 1}"
            itemView.setOnClickListener { onItemClick(avaliacao) }
        }
    }
}
