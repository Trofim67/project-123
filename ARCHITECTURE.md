# Описание структуры приложения и базы данных Room

## 1. Общая архитектура

Приложение использует классический **View-based** подход: экраны — это `Activity` с XML-разметкой, доступ к данным — через **Room (SQLite)**, операции с базой выполняются в корутинах, чтобы не блокировать интерфейс.

Слои:

```
UI (Activity + XML + Adapter)
        │
        ▼
Data (Room: Entity + DAO + AppDatabase)
        │
        ▼
SQLite (файл attendance.db)
```

## 2. Структура пакетов

| Пакет / класс | Назначение |
| --- | --- |
| `MainActivity` | Главный экран, навигация |
| `AttendanceStatus` | Константы статусов посещаемости (`ПРИСУТСТВОВАЛ` и др.) |
| `StudentValidator` | Проверка ввода (имя, фамилия, группа) — чистый Kotlin |
| `data/StudentEntity` | Сущность студента |
| `data/AttendanceEntity` | Сущность отметки посещаемости |
| `data/StudentDao` | Операции со студентами |
| `data/AttendanceDao` | Операции с отметками и агрегаты для статистики |
| `data/AppDatabase` | Создание базы, доступ к DAO (singleton) |
| `ui/StudentsActivity` | Экран списка студентов |
| `ui/AttendanceActivity` | Экран отметки посещаемости |
| `ui/StatisticsActivity` | Экран статистики и истории |
| `ui/StudentAdapter` | Адаптер списка студентов |
| `ui/AttendanceAdapter` | Адаптер строк отметки |
| `ui/HistoryAdapter` | Адаптер истории |
| `ui/AttendanceRow` | Строка отметки (студент + выбранный статус) |

## 3. База данных Room

База создаётся один раз через `AppDatabase.getInstance(context)` (паттерн singleton) и хранится в файле `attendance.db`.

```kotlin
@Database(entities = [StudentEntity::class, AttendanceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
}
```

### 3.1. Таблица `students`

| Поле | Тип | Описание |
| --- | --- | --- |
| `id` | Long (PK, autoGenerate) | Идентификатор студента |
| `firstName` | String | Имя |
| `lastName` | String | Фамилия |
| `groupName` | String | Учебная группа |

### 3.2. Таблица `attendance`

| Поле | Тип | Описание |
| --- | --- | --- |
| `id` | Long (PK, autoGenerate) | Идентификатор записи |
| `studentId` | Long | Ссылка на студента |
| `lessonDate` | String | Дата занятия |
| `subject` | String | Дисциплина/тема |
| `status` | String | Статус («Присутствовал»/«Отсутствовал»/«Опоздал») |

На таблицу наложен **уникальный индекс** `(studentId, lessonDate, subject)`, поэтому на одном занятии у студента может быть только одна итоговая отметка. Благодаря этому используется `@Upsert` — при повторной отметке того же занятия запись обновляется, а не дублируется.

### 3.3. Доступ к данным (DAO)

- `StudentDao`: `insert`, `delete`, `getAll`, `getByGroup`, `getGroups`, `getById`.
- `AttendanceDao`: `insert`, `upsert`, `getAll`, `getByStudent`, `getTotalCount`, `getCountByStatus`, `getDistinctLessonCount`, `deleteByStudent`, `clear`.

Статистика по количеству занятий вычисляется как количество уникальных пар `дата + дисциплина`:

```sql
SELECT COUNT(DISTINCT lessonDate || '|' || subject) FROM attendance
```

## 4. Поток данных

1. Пользователь вводит данные на экране (`Activity`).
2. Валидация выполняется через `StudentValidator` (без обращения к базе).
3. Операции с базой выполняются в `withContext(Dispatchers.IO)` внутри `lifecycleScope.launch`, поэтому интерфейс не блокируется.
4. Результат отображается через `RecyclerView` и адаптеры.

## 5. Ключевые технические решения

- **Room + `@Upsert`** — одна итоговая отметка на студента и занятие.
- **Coroutines** — работа с базой вне основного потока.
- **ViewBinding** — безопасный доступ к элементам разметки.
- **Чистая валидация** — `StudentValidator` и `AttendanceStatus` не зависят от Android, поэтому покрываются юнит-тестами JUnit.
- **Status как константы** — одинаковые значения используются в базе, интерфейсе и тестах, что исключает рассинхронизацию.
