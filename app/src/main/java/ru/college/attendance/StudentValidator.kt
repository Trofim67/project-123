package ru.college.attendance

/**
 * Проверка вводимых пользователем данных.
 * Вынесена в чистый Kotlin-объект, чтобы её можно было тестировать
 * юнит-тестами JUnit без эмулятора.
 */
object StudentValidator {

    /** Имя/фамилия: только буквы, дефис и апостроф. */
    fun isValidName(value: String): Boolean {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return false
        return trimmed.all { it.isLetter() || it == '-' || it == '\'' }
    }

    /** Группа: буквы, цифры, дефис и пробел. */
    fun isValidGroup(value: String): Boolean {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return false
        return trimmed.all { it.isLetterOrDigit() || it == '-' || it == ' ' }
    }

    fun isBlank(value: String): Boolean = value.isBlank()
}
