package com.fc.todayquotes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuotesPagerAdapter(
    private val quotes: List<Quote>,
    private val isNameRevealed: Boolean,
) : RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder =
        QuoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_quote, parent, false)
        )

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val actualPosition = position % quotes.size
        holder.bind(quotes[actualPosition], isNameRevealed)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    inner class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quoteTv: TextView = itemView.findViewById(R.id.quoteTv)
        private val nameTv: TextView = itemView.findViewById(R.id.nameTv)

        @SuppressLint("SetTextI18n")
        fun bind(quote: Quote, isNameRevealed: Boolean) {
            quoteTv.text = "\"${quote.quote}\""
            if (isNameRevealed) {
                nameTv.text = "- ${quote.name}"
                nameTv.visibility = View.VISIBLE
            } else{
                nameTv.visibility = View.GONE
            }
        }

    }
}