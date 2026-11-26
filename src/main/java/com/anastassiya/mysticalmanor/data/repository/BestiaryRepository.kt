
package com.anastassiya.mysticalmanor.data.repository

import com.anastassiya.mysticalmanor.data.remote.BestiaryApi
import com.anastassiya.mysticalmanor.data.remote.MonsterDetailDto
import com.anastassiya.mysticalmanor.data.remote.MonsterSummaryDto
import com.anastassiya.mysticalmanor.data.remote.RetrofitClient

class BestiaryRepository(
    private val api: BestiaryApi = RetrofitClient.api
) {

    suspend fun getMonsters(): List<MonsterSummaryDto> {
        return api.getMonsters()
            .results
            .sortedBy { it.name }
    }

    suspend fun getMonsterDetails(
        index: String
    ): MonsterDetailDto {
        return api.getMonsterDetails(index)
    }
}
