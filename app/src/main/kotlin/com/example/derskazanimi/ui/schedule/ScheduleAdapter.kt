package com.example.derskazanimi.ui.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.derskazanimi.R

data class ScheduleItemDisplay(
    val dayName: String,
    val className: String,
    val timeSlot: String
)

class ScheduleAdapter(
    private val items: List<ScheduleItemDisplay>
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.dayNameText)
        private val classTextView: TextView = itemView.findViewById(R.id.classNameText)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeSlotText)

        fun bind(item: ScheduleItemDisplay) {
            dayTextView.text = item.dayName
            classTextView.text = item.className
            timeTextView.text = item.timeSlot
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
