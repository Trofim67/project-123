package ru.college.attendance.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.college.attendance.databinding.ItemHistoryBinding

/**
 * Строка истории посещаемости.
 */
data class HistoryRow(
    val name: String,
    val detail: String
)

/**
 * Адаптер истории посещаемости.
 */
class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val items = mutableListOf<HistoryRow>()

    fun submitList(list: List<HistoryRow>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class HistoryViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val row = items[position]
        holder.binding.tvHistoryName.text = row.name
        holder.binding.tvHistoryDetail.text = row.detail
    }

    override fun getItemCount(): Int = items.size
}
