package ru.college.attendance.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.college.attendance.R
import ru.college.attendance.data.StudentEntity
import ru.college.attendance.databinding.ItemStudentBinding

/**
 * Адаптер списка студентов.
 */
class StudentAdapter(
    private val onDelete: (StudentEntity) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private val items = mutableListOf<StudentEntity>()

    fun submitList(list: List<StudentEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class StudentViewHolder(val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = items[position]
        holder.binding.tvStudentName.text = "${student.lastName} ${student.firstName}"
        holder.binding.tvStudentGroup.text =
            holder.binding.root.context.getString(R.string.group_prefix, student.groupName)
        holder.binding.btnDelete.setOnClickListener { onDelete(student) }
    }

    override fun getItemCount(): Int = items.size
}
