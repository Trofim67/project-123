# Правила ProGuard/R8 для модуля app.
# В учебном проекте минификация отключена (isMinifyEnabled = false),
# поэтому файл оставлен для будущего использования.

# Room
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
