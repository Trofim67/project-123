package ru.college.attendance

/**
 * Допустимые статусы посещаемости. Используются и в базе данных,
 * и в пользовательском интерфейсе, поэтому вынесены в константы.
 */
object AttendanceStatus {
    const val PRESENT = "Присутствовал"
    const val ABSENT = "Отсутствовал"
    const val LATE = "Опоздал"

    val ALL: Set<String> = setOf(PRESENT, ABSENT, LATE)

    fun isValid(status: String): Boolean = status in ALL
}
