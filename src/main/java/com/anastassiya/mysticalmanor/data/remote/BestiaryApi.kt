
package com.anastassiya.mysticalmanor.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface BestiaryApi {

    @GET("monsters")
    suspend fun getMonsters(): MonsterListResponse

    @GET("monsters/{index}")
    suspend fun getMonsterDetails(
        @Path("index") index: String
    ): MonsterDetailDto
}
