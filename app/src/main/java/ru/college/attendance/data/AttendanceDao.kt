package ru.college.attendance.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AttendanceDao {

    @Insert
    suspend fun insert(record: AttendanceEntity): Long

    /** Обновляет существующую отметку или вставляет новую (одна отметка на студента и занятие). */
    @Upsert
    suspend fun upsert(record: AttendanceEntity)

    @Query("SELECT * FROM attendance ORDER BY lessonDate DESC, subject")
    suspend fun getAll(): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY lessonDate DESC")
    suspend fun getByStudent(studentId: Long): List<AttendanceEntity>

    @Query("SELECT COUNT(*) FROM attendance")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM attendance WHERE status = :status")
    suspend fun getCountByStatus(status: String): Int

    /** Количество уникальных занятий (дата + дисциплина). */
    @Query("SELECT COUNT(DISTINCT lessonDate || '|' || subject) FROM attendance")
    suspend fun getDistinctLessonCount(): Int

    @Query("DELETE FROM attendance WHERE studentId = :studentId")
    suspend fun deleteByStudent(studentId: Long)

    @Query("DELETE FROM attendance")
    suspend fun clear()
}
