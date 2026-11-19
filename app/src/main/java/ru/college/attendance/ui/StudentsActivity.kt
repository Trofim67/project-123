package ru.college.attendance.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.college.attendance.R
import ru.college.attendance.StudentValidator
import ru.college.attendance.data.AppDatabase
import ru.college.attendance.data.StudentEntity
import ru.college.attendance.databinding.ActivityStudentsBinding

/**
 * Экран списка студентов: добавление, просмотр и удаление с подтверждением.
 */
class StudentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentsBinding
    private lateinit var adapter: StudentAdapter
    private val db by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_students)

        adapter = StudentAdapter { student -> confirmDelete(student) }
        binding.recyclerStudents.layoutManager = LinearLayoutManager(this)
        binding.recyclerStudents.adapter = adapter

        binding.btnAddStudent.setOnClickListener { showAddDialog() }

        loadStudents()
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) { db.studentDao().getAll() }
            adapter.submitList(list)
            binding.emptyView.isVisible = list.isEmpty()
        }
    }

    private fun showAddDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_student, null)
        val etFirstName = view.findViewById<EditText>(R.id.etFirstName)
        val etLastName = view.findViewById<EditText>(R.id.etLastName)
        val etGroup = view.findViewById<EditText>(R.id.etGroup)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_add_title)
            .setView(view)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val firstName = etFirstName.text.toString().trim()
                val lastName = etLastName.text.toString().trim()
                val group = etGroup.text.toString().trim()

                val error = validate(firstName, lastName, group)
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                } else {
                    val student = StudentEntity(
                        firstName = firstName,
                        lastName = lastName,
                        groupName = group
                    )
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) { db.studentDao().insert(student) }
                        Toast.makeText(this@StudentsActivity, R.string.msg_student_added, Toast.LENGTH_SHORT).show()
                        loadStudents()
                    }
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun validate(firstName: String, lastName: String, group: String): String? {
        if (firstName.isEmpty() || lastName.isEmpty() || group.isEmpty()) {
            return getString(R.string.error_empty_fields)
        }
        if (!StudentValidator.isValidName(firstName) || !StudentValidator.isValidName(lastName)) {
            return getString(R.string.error_invalid_name)
        }
        if (!StudentValidator.isValidGroup(group)) {
            return getString(R.string.error_invalid_group)
        }
        return null
    }

    private fun confirmDelete(student: StudentEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_confirm_title)
            .setMessage(R.string.delete_confirm_message)
            .setPositiveButton(R.string.action_delete) { _, _ -> deleteStudent(student) }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun deleteStudent(student: StudentEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.studentDao().delete(student)
                db.attendanceDao().deleteByStudent(student.id)
            }
            Toast.makeText(this@StudentsActivity, R.string.msg_student_deleted, Toast.LENGTH_SHORT).show()
            loadStudents()
        }
    }
}
