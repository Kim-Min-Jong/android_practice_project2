package com.fc.todayquotes

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
        holder.bind(quotes[position], isNameRevealed)
    }

    override fun getItemCount(): Int {
        return quotes.size
    }

    inner class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quoteTv: TextView = itemView.findViewById(R.id.quoteTv)
        private val nameTv: TextView = itemView.findViewById(R.id.nameTv)

        fun bind(quote: Quote, isNameRevealed: Boolean) {
            quoteTv.text = quote.quote
            if (isNameRevealed) {
                nameTv.text = quote.name
                nameTv.visibility = View.VISIBLE
            } else{
                nameTv.visibility = View.GONE
            }
        }

    }
}