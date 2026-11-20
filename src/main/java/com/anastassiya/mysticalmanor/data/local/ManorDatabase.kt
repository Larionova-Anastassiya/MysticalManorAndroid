
package com.anastassiya.mysticalmanor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        QuestEntity::class,
        QuestHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ManorDatabase : RoomDatabase() {

    abstract fun questDao(): QuestDao

    companion object {

        @Volatile
        private var INSTANCE: ManorDatabase? = null

        fun getDatabase(context: Context): ManorDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ManorDatabase::class.java,
                    "mystical_manor_journal.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
