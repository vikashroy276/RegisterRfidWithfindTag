package com.hal.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RegistrationItem::class, IssuanceItem::class, BinItem::class],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun inventoryDao(): RegistrationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE BinItem ADD COLUMN newColumn TEXT DEFAULT '' NOT NULL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "registration_db"
                )
                    // Attach migration BEFORE build
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() // Optional, for dev safety
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
