package ru.college.attendance.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.college.attendance.AttendanceStatus
import ru.college.attendance.R
import ru.college.attendance.data.AppDatabase
import ru.college.attendance.data.AttendanceEntity
import ru.college.attendance.data.StudentEntity
import ru.college.attendance.databinding.ActivityStatisticsBinding

/**
 * Экран статистики: общее количество занятий, количество отметок по статусам
 * и история посещаемости с фильтром по группе.
 */
class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private val db by lazy { AppDatabase.getInstance(this) }
    private val historyAdapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_statistics)

        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerHistory.adapter = historyAdapter

        binding.btnShowHistory.setOnClickListener { loadStatistics() }

        loadStatistics()
    }

    private fun loadStatistics() {
        val group = binding.etGroup.text.toString().trim()
        lifecycleScope.launch {
            val (students, records) = withContext(Dispatchers.IO) {
                db.studentDao().getAll() to db.attendanceDao().getAll()
            }

            binding.tvTotalLessons.text =
                records.distinctBy { "${it.lessonDate}|${it.subject}" }.size.toString()
            binding.tvPresent.text =
                records.count { it.status == AttendanceStatus.PRESENT }.toString()
            binding.tvAbsent.text =
                records.count { it.status == AttendanceStatus.ABSENT }.toString()
            binding.tvLate.text =
                records.count { it.status == AttendanceStatus.LATE }.toString()

            val history = buildHistory(students, records, group)
            historyAdapter.submitList(history)
            binding.emptyView.isVisible = history.isEmpty()
        }
    }

    private fun buildHistory(
        students: List<StudentEntity>,
        records: List<AttendanceEntity>,
        group: String
    ): List<HistoryRow> {
        val studentById = students.associateBy { it.id }
        val filteredIds = if (group.isEmpty()) {
            students.map { it.id }.toSet()
        } else {
            students.filter { it.groupName.equals(group, ignoreCase = true) }.map { it.id }.toSet()
        }

        return records
            .filter { it.studentId in filteredIds }
            .sortedWith(compareByDescending<AttendanceEntity> { it.lessonDate }.thenBy { it.subject })
            .map { record ->
                val student = studentById[record.studentId]
                val name = student?.let { "${it.lastName} ${it.firstName}" }
                    ?: "Студент #${record.studentId}"
                HistoryRow(
                    name = name,
                    detail = "${record.subject} · ${record.lessonDate} · ${record.status}"
                )
            }
    }
}
