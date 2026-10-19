package com.anastassiya.mysticalmanor.model

enum class HeroClass(
    val title: String,
    val emoji: String,
    val ability: String,
    val maxHealth: Int,
    val initialGold: Int
) {
    ARTIST("Artist", "🎨", "Reveal the Secret Gallery immediately", 100, 20),
    EXPLORER("Explorer", "🧭", "Extra health and a treasure bonus", 120, 25),
    MAGE("Mage", "🔮", "Powerful magical attacks", 90, 30)
}

enum class ItemType {
    HEALING, MAGIC, KEY, TREASURE
}

data class Item(
    val id: String,
    val name: String,
    val description: String,
    val value: Int,
    val type: ItemType,
    val healingPower: Int = 0
)

data class Achievement(
    val id: String,
    val name: String,
    val description: String
)

data class Painting(
    val id: String,
    val title: String,
    val description: String,
    val enemyName: String = "Shadow Guardian"
)

data class Room(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val paintings: List<Painting> = emptyList(),
    val hasTreasure: Boolean = false,
    val isSecret: Boolean = false
)

enum class GameScreen {
    WELCOME, HALL, ROOM, PAINTING, BATTLE, INVENTORY, PROFILE
}

data class GameUiState(
    val screen: GameScreen = GameScreen.WELCOME,
    val playerName: String = "",
    val heroClass: HeroClass = HeroClass.ARTIST,
    val health: Int = 100,
    val gold: Int = 20,
    val experience: Int = 0,
    val inventory: List<Item> = emptyList(),
    val achievements: Set<Achievement> = emptySet(),
    val visitedRooms: Set<String> = emptySet(),
    val discoveredPaintings: Set<String> = emptySet(),
    val completedPaintings: Set<String> = emptySet(),
    val openedChests: Set<String> = emptySet(),
    val selectedRoomId: String? = null,
    val selectedPaintingId: String? = null,
    val enemyHealth: Int = 40,
    val message: String = "Welcome to Mystical Manor.",
    val enemiesDefeated: Int = 0
)