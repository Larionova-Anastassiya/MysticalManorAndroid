
package com.anastassiya.mysticalmanor.data.remote

import com.google.gson.annotations.SerializedName

data class MonsterListResponse(
    val count: Int,
    val results: List<MonsterSummaryDto>
)

data class MonsterSummaryDto(
    val index: String,
    val name: String,
    val url: String
)

data class MonsterDetailDto(
    val index: String,
    val name: String,
    val size: String,
    val type: String,
    val alignment: String,

    @SerializedName("hit_points")
    val hitPoints: Int,

    @SerializedName("armor_class")
    val armorClass: List<ArmorClassDto>,

    @SerializedName("challenge_rating")
    val challengeRating: Double,

    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,

    val xp: Int,

    @SerializedName("special_abilities")
    val specialAbilities: List<MonsterAbilityDto>?,

    val actions: List<MonsterAbilityDto>?
)

data class ArmorClassDto(
    val type: String,
    val value: Int
)

data class MonsterAbilityDto(
    val name: String,
    val desc: String?
)
