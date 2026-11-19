package ru.college.attendance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Юнит-тесты для проверки ввода данных и статусов посещаемости.
 */
class StudentValidatorTest {

    @Test
    fun validName_accepted() {
        assertTrue(StudentValidator.isValidName("Иван"))
        assertTrue(StudentValidator.isValidName("Анна-Мария"))
        assertTrue(StudentValidator.isValidName("O'Neil"))
    }

    @Test
    fun nameWithDigits_rejected() {
        assertFalse(StudentValidator.isValidName("Иван2"))
        assertFalse(StudentValidator.isValidName("123"))
    }

    @Test
    fun emptyName_rejected() {
        assertFalse(StudentValidator.isValidName(""))
        assertFalse(StudentValidator.isValidName("   "))
    }

    @Test
    fun validGroup_accepted() {
        assertTrue(StudentValidator.isValidGroup("ИС-21"))
        assertTrue(StudentValidator.isValidGroup("П 101"))
    }

    @Test
    fun emptyGroup_rejected() {
        assertFalse(StudentValidator.isValidGroup(""))
    }

    @Test
    fun statuses_areValid() {
        assertTrue(AttendanceStatus.isValid(AttendanceStatus.PRESENT))
        assertTrue(AttendanceStatus.isValid(AttendanceStatus.ABSENT))
        assertTrue(AttendanceStatus.isValid(AttendanceStatus.LATE))
        assertFalse(AttendanceStatus.isValid("Неизвестно"))
    }

    @Test
    fun statusSet_hasExactlyThreeValues() {
        assertEquals(3, AttendanceStatus.ALL.size)
    }
}
