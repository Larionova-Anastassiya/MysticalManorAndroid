
package com.anastassiya.mysticalmanor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Query("SELECT * FROM quests ORDER BY updatedAt DESC, id DESC")
    fun observeAll(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<QuestEntity?>

    @Query("SELECT * FROM quests WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): QuestEntity?

    @Insert
    suspend fun insert(quest: QuestEntity): Long

    @Update
    suspend fun update(quest: QuestEntity)

    @Delete
    suspend fun delete(quest: QuestEntity)

    @Query(
        "SELECT * FROM quest_history " +
                "WHERE questId = :questId ORDER BY timestamp DESC, id DESC"
    )
    fun observeHistory(questId: Long): Flow<List<QuestHistoryEntity>>

    @Insert
    suspend fun insertHistory(event: QuestHistoryEntity)
}
