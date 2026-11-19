package ru.college.attendance.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Запись о посещаемости студента на конкретном занятии.
 * Один студент может иметь только одну итоговую отметку на занятие
 * (уникальный индекс по studentId + lessonDate + subject).
 */
@Entity(
    tableName = "attendance",
    indices = [Index(value = ["studentId", "lessonDate", "subject"], unique = true)]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val lessonDate: String,
    val subject: String,
    val status: String
)
