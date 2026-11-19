package ru.college.attendance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность студента. Хранится в таблице `students`.
 */
@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val groupName: String
)
