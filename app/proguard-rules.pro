# ProGuard / R8 rules for Affiliate Tracker AI

# Preserve source file and line numbers for clean crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Room entities, DAOs, and database classes
-keep class com.example.data.model.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
