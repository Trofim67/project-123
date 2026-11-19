package ru.college.attendance.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import ru.college.attendance.AttendanceStatus
import ru.college.attendance.R
import ru.college.attendance.databinding.ItemAttendanceBinding

/**
 * Адаптер строк отметки посещаемости: студент и RadioGroup со статусами.
 */
class AttendanceAdapter(
    private val items: MutableList<AttendanceRow>
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    class AttendanceViewHolder(val binding: ItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rbPresent.text = AttendanceStatus.PRESENT
        binding.rbPresent.tag = AttendanceStatus.PRESENT
        binding.rbAbsent.text = AttendanceStatus.ABSENT
        binding.rbAbsent.tag = AttendanceStatus.ABSENT
        binding.rbLate.text = AttendanceStatus.LATE
        binding.rbLate.tag = AttendanceStatus.LATE
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val row = items[position]
        holder.binding.tvStudentName.text =
            "${row.student.lastName} ${row.student.firstName} (${row.student.groupName})"

        val group = holder.binding.rgStatus
        group.setOnCheckedChangeListener(null)
        group.check(
            when (row.status) {
                AttendanceStatus.ABSENT -> R.id.rbAbsent
                AttendanceStatus.LATE -> R.id.rbLate
                else -> R.id.rbPresent
            }
        )
        group.setOnCheckedChangeListener { _, checkedId ->
            val button = holder.binding.root.findViewById<RadioButton>(checkedId)
            row.status = button?.tag as? String ?: AttendanceStatus.PRESENT
        }
    }

    override fun getItemCount(): Int = items.size
}
