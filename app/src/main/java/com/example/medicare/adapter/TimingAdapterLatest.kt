// TimingAdapterLatest.kt
package com.example.medicare.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.R
import com.example.medicare.models.SlotAvailability
import com.example.medicare.models.Timing

class TimingAdapterLatest(
    private val selectedDatePosition: Int,
    private val data: List<SlotAvailability>,
    private val context: Context
) :
    RecyclerView.Adapter<TimingAdapterLatest.ViewHolder>() {

    interface OnClickListener {
        fun onClick(position: Int)
    }

    private var listener: OnClickListener? = null
    private var selectedPosition = RecyclerView.NO_POSITION

    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.timing_view_latest_item)
        val textView: TextView = itemView.findViewById(R.id.texttiming)
        val slots: TextView = itemView.findViewById(R.id.slotsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.timing_item_latest, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item.date

        if (item.remainingSlots.toInt() == 0) {
            // No slots available
            holder.slots.text = "No slots available"
            holder.cardView.isEnabled = false // Disable the cardView
            holder.cardView.setCardBackgroundColor(Color.parseColor("#CCCCCC")) // Grey color
        } else {
            holder.slots.text = "Slots Available: ${item.remainingSlots}"
            holder.cardView.isEnabled = true // Enable the cardView
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF")) // White color
        }

        holder.cardView.setOnClickListener {
            if (item.remainingSlots.toInt() > 0) {
                val previousSelectedPosition = selectedPosition
                if (position == selectedPosition) {
                    selectedPosition = RecyclerView.NO_POSITION
                } else {
                    selectedPosition = position
                }
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)

                listener?.onClick(position)
            }
        }

        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#69BCE9")) // Selected color
        }
    }



    override fun getItemCount(): Int {
        return data.size
    }
}
