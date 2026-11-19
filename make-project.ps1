# make-project.ps1
$root = "project-123-fix"
New-Item -ItemType Directory -Force -Path $root | Out-Null
Set-Location $root

# --- корневые файлы ---
@'
pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "AttendanceControl"
include(":app")
'@ | Set-Content settings.gradle.kts

@'
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
}
'@ | Set-Content build.gradle.kts

@'
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
'@ | Set-Content gradle.properties

New-Item -ItemType Directory -Force -Path "gradle/wrapper" | Out-Null
@'
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
'@ | Set-Content gradle/wrapper/gradle-wrapper.properties

# --- app ---
New-Item -ItemType Directory -Force -Path "app/src/main/java/ru/college/attendance/data" | Out-Null
New-Item -ItemType Directory -Force -Path "app/src/main/java/ru/college/attendance/ui" | Out-Null
New-Item -ItemType Directory -Force -Path "app/src/main/res/layout" | Out-Null
New-Item -ItemType Directory -Force -Path "app/src/main/res/values" | Out-Null
New-Item -ItemType Directory -Force -Path "app/src/test/java/ru/college/attendance" | Out-Null

@'
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}
android {
    namespace = "ru.college.attendance"
    compileSdk = 34
    defaultConfig {
        applicationId = "ru.college.attendance"
        minSdk = 24; targetSdk = 34; versionCode = 1; versionName = "1.0"
    }
    buildTypes { release { isMinifyEnabled = false; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro") } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    val room = "2.6.1"
    implementation("androidx.room:room-runtime:$room")
    implementation("androidx.room:room-ktx:$room")
    ksp("androidx.room:room-compiler:$room")
    testImplementation("junit:junit:4.13.2")
}
'@ | Set-Content app/build.gradle.kts

'# empty' | Set-Content app/proguard-rules.pro

@'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application android:allowBackup="true" android:label="@string/app_name"
        android:supportsRtl="true" android:theme="@style/Theme.Attendance">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".ui.StudentsActivity" android:exported="false" />
        <activity android:name=".ui.AttendanceActivity" android:exported="false" />
        <activity android:name=".ui.StatisticsActivity" android:exported="false" />
    </application>
</manifest>
'@ | Set-Content app/src/main/AndroidManifest.xml

# --- Kotlin ---
@'
package ru.college.attendance
enum class AttendanceStatus(val label: String) {
    PRESENT("Присутствовал"), ABSENT("Отсутствовал"), LATE("Опоздал")
}
'@ | Set-Content app/src/main/java/ru/college/attendance/AttendanceStatus.kt

@'
package ru.college.attendance
object StudentValidator {
    private val nameRegex = Regex("^[A-Za-zА-Яа-яЁё\\- ]{2,50}$")
    private val groupRegex = Regex("^[A-Za-zА-Яа-яЁё0-9\\- ]{1,20}$")
    fun isValidFirstName(v: String) = nameRegex.matches(v.trim())
    fun isValidLastName(v: String)  = nameRegex.matches(v.trim())
    fun isValidGroup(v: String)     = groupRegex.matches(v.trim())
}
'@ | Set-Content app/src/main/java/ru/college/attendance/StudentValidator.kt

@'
package ru.college.attendance
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.college.attendance.ui.AttendanceActivity
import ru.college.attendance.ui.StatisticsActivity
import ru.college.attendance.ui.StudentsActivity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btnStudents).setOnClickListener { startActivity(Intent(this, StudentsActivity::class.java)) }
        findViewById<View>(R.id.btnAttendance).setOnClickListener { startActivity(Intent(this, AttendanceActivity::class.java)) }
        findViewById<View>(R.id.btnStatistics).setOnClickListener { startActivity(Intent(this, StatisticsActivity::class.java)) }
    }
}
'@ | Set-Content app/src/main/java/ru/college/attendance/MainActivity.kt

@'
package ru.college.attendance.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "students")
data class StudentEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val firstName: String, val lastName: String, val groupName: String)
'@ | Set-Content app/src/main/java/ru/college/attendance/data/StudentEntity.kt

@'
package ru.college.attendance.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "attendance")
data class AttendanceEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val studentId: Long, val discipline: String, val date: String, val status: String)
'@ | Set-Content app/src/main/java/ru/college/attendance/data/AttendanceEntity.kt

@'
package ru.college.attendance.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao interface StudentDao {
    @Query("SELECT * FROM students ORDER BY lastName, firstName") fun observeAll(): Flow<List<StudentEntity>>
    @Query("SELECT * FROM students ORDER BY lastName, firstName") suspend fun getAll(): List<StudentEntity>
    @Insert suspend fun insert(s: StudentEntity): Long
    @Delete suspend fun delete(s: StudentEntity)
}
'@ | Set-Content app/src/main/java/ru/college/attendance/data/StudentDao.kt

@'
package ru.college.attendance.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao interface AttendanceDao {
    @Insert suspend fun insertAll(items: List<AttendanceEntity>)
    @Query("SELECT * FROM attendance ORDER BY id DESC") fun observeAll(): Flow<List<AttendanceEntity>>
    @Query("SELECT COUNT(DISTINCT discipline || '|' || date) FROM attendance") fun observeLessonCount(): Flow<Int>
    @Query("SELECT COUNT(*) FROM attendance WHERE status = :status") fun observeCountByStatus(status: String): Flow<Int>
}
'@ | Set-Content app/src/main/java/ru/college/attendance/data/AttendanceDao.kt

@'
package ru.college.attendance.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [StudentEntity::class, AttendanceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "attendance.db").build().also { INSTANCE = it }
        }
    }
}
'@ | Set-Content app/src/main/java/ru/college/attendance/data/AppDatabase.kt

@'
package ru.college.attendance.ui
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.college.attendance.R
import ru.college.attendance.StudentValidator
import ru.college.attendance.data.AppDatabase
import ru.college.attendance.data.StudentEntity
class StudentsActivity : AppCompatActivity() {
    private val db by lazy { AppDatabase.get(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)
        val list = findViewById<ListView>(R.id.listStudents)
        val adapter = ArrayAdapter<StudentEntity>(this, android.R.layout.simple_list_item_1, mutableListOf())
        list.adapter = adapter
        lifecycleScope.launch { db.studentDao().observeAll().collect { items -> adapter.clear(); adapter.addAll(items); adapter.notifyDataSetChanged() } }
        list.setOnItemLongClickListener { _, _, pos, _ ->
            val s = adapter.getItem(pos) ?: return@setOnItemLongClickListener false
            AlertDialog.Builder(this).setMessage("Удалить ${s.lastName} ${s.firstName}?")
                .setPositiveButton("Удалить") { _, _ -> lifecycleScope.launch { db.studentDao().delete(s) } }
                .setNegativeButton("Отмена", null).show(); true
        }
        findViewById<Button>(R.id.btnAddStudent).setOnClickListener { showAddDialog() }
    }
    private fun showAddDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_student, null)
        val etFirst = view.findViewById<EditText>(R.id.etFirstName)
        val etLast  = view.findViewById<EditText>(R.id.etLastName)
        val etGroup = view.findViewById<EditText>(R.id.etGroup)
        AlertDialog.Builder(this).setTitle("Новый студент").setView(view)
            .setPositiveButton("Добавить") { _, _ ->
                val f = etFirst.text.toString(); val l = etLast.text.toString(); val g = etGroup.text.toString()
                if (!StudentValidator.isValidFirstName(f) || !StudentValidator.isValidLastName(l) || !StudentValidator.isValidGroup(g)) {
                    Toast.makeText(this, "Проверьте поля", Toast.LENGTH_SHORT).show(); return@setPositiveButton
                }
                lifecycleScope.launch { db.studentDao().insert(StudentEntity(firstName = f, lastName = l, groupName = g)) }
            }.setNegativeButton("Отмена", null).show()
    }
}
'@ | Set-Content app/src/main/java/ru/college/attendance/ui/StudentsActivity.kt

# (AttendanceActivity, StatisticsActivity, ресурсы — вставьте из предыдущего ответа аналогично)
# ... см. блоки в моём предыдущем сообщении ...

Write-Host "Готово. Дальше: Compress-Archive -Path $root\* -DestinationPath ..\project-123-fix.zip"
