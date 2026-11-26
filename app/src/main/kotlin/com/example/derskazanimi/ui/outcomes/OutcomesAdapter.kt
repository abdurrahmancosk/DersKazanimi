package com.example.derskazanimi.ui.outcomes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.derskazanimi.R

data class OutcomeItemDisplay(
    val dateRange: String,
    val className: String,
    val kazanim: String
)

class OutcomesAdapter(
    private val items: List<OutcomeItemDisplay>
) : RecyclerView.Adapter<OutcomesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateRangeText: TextView = itemView.findViewById(R.id.outcomeDateRangeText)
        private val classText: TextView = itemView.findViewById(R.id.outcomeClassText)
        private val kazanimText: TextView = itemView.findViewById(R.id.outcomeKazanimText)

        fun bind(item: OutcomeItemDisplay) {
            dateRangeText.text = item.dateRange
            classText.text = item.className
            kazanimText.text = item.kazanim
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outcome, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
