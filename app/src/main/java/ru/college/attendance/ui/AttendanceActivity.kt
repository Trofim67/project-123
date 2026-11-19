package ru.college.attendance.ui

import android.os.Bundle
import android.widget.Toast
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
import ru.college.attendance.databinding.ActivityAttendanceBinding

/**
 * Экран отметки посещаемости: выбор занятия (дисциплина, дата, группа),
 * список студентов с выбором статуса и сохранение отметок.
 */
class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private val db by lazy { AppDatabase.getInstance(this) }
    private val rows = mutableListOf<AttendanceRow>()
    private lateinit var adapter: AttendanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_attendance)

        adapter = AttendanceAdapter(rows)
        binding.recyclerAttendance.layoutManager = LinearLayoutManager(this)
        binding.recyclerAttendance.adapter = adapter

        binding.btnStart.setOnClickListener { startAttendance() }
        binding.btnSave.setOnClickListener { saveAttendance() }
    }

    private fun startAttendance() {
        val subject = binding.etSubject.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val group = binding.etGroup.text.toString().trim()

        when {
            subject.isEmpty() ->
                Toast.makeText(this, R.string.error_no_subject, Toast.LENGTH_SHORT).show()
            date.isEmpty() ->
                Toast.makeText(this, R.string.error_no_date, Toast.LENGTH_SHORT).show()
            else -> loadStudents(group)
        }
    }

    private fun loadStudents(group: String) {
        lifecycleScope.launch {
            val students = withContext(Dispatchers.IO) {
                if (group.isEmpty()) db.studentDao().getAll()
                else db.studentDao().getByGroup(group)
            }

            if (students.isEmpty()) {
                binding.recyclerAttendance.isVisible = false
                binding.btnSave.isVisible = false
                binding.emptyView.isVisible = true
                Toast.makeText(this@AttendanceActivity, R.string.error_no_students, Toast.LENGTH_SHORT).show()
                return@launch
            }

            rows.clear()
            rows.addAll(students.map { AttendanceRow(it) })
            adapter.notifyDataSetChanged()

            binding.emptyView.isVisible = false
            binding.recyclerAttendance.isVisible = true
            binding.btnSave.isVisible = true
        }
    }

    private fun saveAttendance() {
        val subject = binding.etSubject.text.toString().trim()
        val date = binding.etDate.text.toString().trim()

        if (rows.any { !AttendanceStatus.isValid(it.status) }) {
            Toast.makeText(this, R.string.error_no_status, Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                rows.forEach { row ->
                    db.attendanceDao().upsert(
                        AttendanceEntity(
                            studentId = row.student.id,
                            lessonDate = date,
                            subject = subject,
                            status = row.status
                        )
                    )
                }
            }
            Toast.makeText(this@AttendanceActivity, R.string.msg_attendance_saved, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
