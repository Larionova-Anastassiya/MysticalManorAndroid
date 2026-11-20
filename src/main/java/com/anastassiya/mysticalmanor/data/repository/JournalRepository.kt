
package com.anastassiya.mysticalmanor.data.repository

import androidx.room.withTransaction
import com.anastassiya.mysticalmanor.data.local.ManorDatabase
import com.anastassiya.mysticalmanor.data.local.QuestEntity
import com.anastassiya.mysticalmanor.data.local.QuestHistoryEntity

class JournalRepository(
    private val database: ManorDatabase
) {
    private val dao = database.questDao()

    val quests = dao.observeAll()

    fun observeQuest(id: Long) = dao.observeById(id)

    fun observeHistory(id: Long) = dao.observeHistory(id)

    suspend fun create(
        title: String,
        description: String,
        reward: Int
    ): Long = database.withTransaction {

        val id = dao.insert(
            QuestEntity(
                title = title.trim(),
                description = description.trim(),
                rewardGold = reward
            )
        )

        dao.insertHistory(
            QuestHistoryEntity(
                questId = id,
                action = "Quest created as DRAFT"
            )
        )

        id
    }

    suspend fun update(
        id: Long,
        title: String,
        description: String,
        reward: Int
    ): Boolean = database.withTransaction {

        val old = dao.getById(id)
            ?: return@withTransaction false

        if (old.status == "COMPLETED") {
            return@withTransaction false
        }

        dao.update(
            old.copy(
                title = title.trim(),
                description = description.trim(),
                rewardGold = reward,
                updatedAt = System.currentTimeMillis()
            )
        )

        dao.insertHistory(
            QuestHistoryEntity(
                questId = id,
                action = "Quest details updated"
            )
        )

        true
    }

    suspend fun delete(id: Long): Boolean =
        database.withTransaction {

            val quest = dao.getById(id)
                ?: return@withTransaction false

            dao.delete(quest)
            true
        }

    suspend fun advance(id: Long): Boolean =
        database.withTransaction {

            val quest = dao.getById(id)
                ?: return@withTransaction false

            val nextStatus = when (quest.status) {
                "DRAFT" -> "ACTIVE"
                "ACTIVE" -> "COMPLETED"
                else -> return@withTransaction false
            }

            dao.update(
                quest.copy(
                    status = nextStatus,
                    updatedAt = System.currentTimeMillis()
                )
            )

            dao.insertHistory(
                QuestHistoryEntity(
                    questId = id,
                    action = "${quest.status} → $nextStatus"
                )
            )

            true
        }
}
