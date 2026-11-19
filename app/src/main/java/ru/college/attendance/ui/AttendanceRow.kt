package ru.college.attendance.ui

import ru.college.attendance.AttendanceStatus
import ru.college.attendance.data.StudentEntity

/**
 * Строка отметки: студент и выбранный для него статус посещаемости.
 */
data class AttendanceRow(
    val student: StudentEntity,
    var status: String = AttendanceStatus.PRESENT
)
