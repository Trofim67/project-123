package ru.college.attendance

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.college.attendance.databinding.ActivityMainBinding
import ru.college.attendance.ui.AttendanceActivity
import ru.college.attendance.ui.StatisticsActivity
import ru.college.attendance.ui.StudentsActivity

/**
 * Главный экран: переход к студентам, отметке посещаемости и статистике.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStudents.setOnClickListener {
            startActivity(Intent(this, StudentsActivity::class.java))
        }
        binding.btnAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }
        binding.btnStatistics.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
    }
}
