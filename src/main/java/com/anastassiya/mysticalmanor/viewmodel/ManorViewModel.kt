package com.anastassiya.mysticalmanor.viewmodel

import androidx.lifecycle.ViewModel
import com.anastassiya.mysticalmanor.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class ManorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val rooms = listOf(
        Room(
            "gallery",
            "The Gallery",
            "Ancient paintings cover the walls. One canvas seems alive.",
            "🖼️",
            paintings = listOf(
                Painting(
                    "starry",
                    "The Starry Night",
                    "A village beneath a swirling sky of living stars."
                )
            )
        ),
        Room(
            "library",
            "The Forgotten Library",
            "Dusty books, candlelight and an ancient treasure chest.",
            "📚",
            hasTreasure = true
        ),
        Room(
            "secret",
            "The Secret Gallery",
            "A hidden chamber filled with impossible works of art.",
            "🌙",
            paintings = listOf(
                Painting(
                    "garden",
                    "The Enchanted Garden",
                    "Silver flowers glow beneath an eternal moon."
                ),
                Painting(
                    "kingdom",
                    "The Forgotten Kingdom",
                    "A ruined kingdom beneath crimson clouds."
                )
            ),
            isSecret = true
        )
    )

    val currentRoom: Room?
        get() = rooms.find { it.id == uiState.value.selectedRoomId }

    val currentPainting: Painting?
        get() = rooms.flatMap { it.paintings }
            .find { it.id == uiState.value.selectedPaintingId }

    fun startGame(name: String, hero: HeroClass) {
        val starter = when (hero) {
            HeroClass.ARTIST -> Item(
                "brush", "Magic Brush", "Reveals hidden art.",
                100, ItemType.MAGIC
            )
            HeroClass.EXPLORER -> Item(
                "compass", "Explorer's Compass", "Finds hidden paths.",
                90, ItemType.MAGIC
            )
            HeroClass.MAGE -> Item(
                "crystal", "Arcane Crystal", "Contains magical energy.",
                120, ItemType.MAGIC
            )
        }

        val potion = Item(
            "potion-1", "Healing Potion", "Restores 30 HP.",
            25, ItemType.HEALING, 30
        )

        _uiState.value = GameUiState(
            screen = GameScreen.HALL,
            playerName = name.trim().ifBlank { "Traveler" },
            heroClass = hero,
            health = hero.maxHealth,
            gold = hero.initialGold,
            inventory = listOf(starter, potion),
            message = "The manor doors have locked behind you..."
        )
    }

    fun canEnter(room: Room): Boolean {
        val state = _uiState.value
        return !room.isSecret ||
                state.heroClass == HeroClass.ARTIST ||
                state.visitedRooms.size >= 2
    }

    fun enterRoom(room: Room) {
        if (!canEnter(room)) {
            setMessage("Explore two rooms to unlock the Secret Gallery.")
            return
        }

        _uiState.update {
            it.copy(
                screen = GameScreen.ROOM,
                selectedRoomId = room.id,
                visitedRooms = it.visitedRooms + room.id,
                message = "You enter ${room.name}."
            )
        }
    }

    fun openChest() {
        val state = _uiState.value
        val room = currentRoom ?: return

        if (!room.hasTreasure || room.id in state.openedChests) {
            setMessage("The chest is already empty.")
            return
        }

        val treasure = Item(
            "moon-crystal", "Moon Crystal",
            "A rare crystal found in the library.",
            80, ItemType.TREASURE
        )

        val bonus = if (state.heroClass == HeroClass.EXPLORER) 15 else 0

        _uiState.update {
            it.copy(
                inventory = it.inventory + treasure,
                gold = it.gold + bonus,
                openedChests = it.openedChests + room.id,
                achievements = it.achievements + Achievement(
                    "treasure", "Treasure Hunter", "Open an ancient chest."
                ),
                message = "You found a Moon Crystal! Bonus gold: $bonus"
            )
        }
    }

    fun selectPainting(painting: Painting) {
        _uiState.update {
            it.copy(
                screen = GameScreen.PAINTING,
                selectedPaintingId = painting.id,
                discoveredPaintings = it.discoveredPaintings + painting.id,
                message = "The painting begins to move..."
            )
        }
    }

    fun enterPainting() {
        val painting = currentPainting ?: return

        if (painting.id in uiState.value.completedPaintings) {
            setMessage("This painting has already been restored.")
            return
        }

        _uiState.update {
            it.copy(
                screen = GameScreen.BATTLE,
                enemyHealth = 40,
                achievements = it.achievements + Achievement(
                    "first-painting",
                    "First Painting",
                    "Enter your first magical painting."
                ),
                message = "A ${painting.enemyName} appears!"
            )
        }
    }

    fun attack(special: Boolean = false) {
        val state = _uiState.value
        if (state.screen != GameScreen.BATTLE || state.health <= 0) return

        val damage = if (special) {
            when (state.heroClass) {
                HeroClass.ARTIST -> Random.nextInt(15, 23)
                HeroClass.EXPLORER -> Random.nextInt(12, 19)
                HeroClass.MAGE -> Random.nextInt(20, 29)
            }
        } else {
            when (state.heroClass) {
                HeroClass.ARTIST -> Random.nextInt(12, 21)
                HeroClass.EXPLORER -> Random.nextInt(10, 19)
                HeroClass.MAGE -> Random.nextInt(15, 24)
            }
        }

        val remaining = (state.enemyHealth - damage).coerceAtLeast(0)

        if (remaining == 0) {
            val paintingId = state.selectedPaintingId ?: return
            val key = Item(
                "key-$paintingId", "Golden Key",
                "A reward from a magical painting.",
                50, ItemType.KEY
            )

            _uiState.update {
                it.copy(
                    screen = GameScreen.PAINTING,
                    enemyHealth = 0,
                    gold = it.gold + 20,
                    experience = it.experience + 50,
                    inventory = it.inventory + key,
                    completedPaintings = it.completedPaintings + paintingId,
                    enemiesDefeated = it.enemiesDefeated + 1,
                    achievements = it.achievements + Achievement(
                        "hunter", "Monster Hunter", "Defeat a Shadow Guardian."
                    ),
                    message = "Guardian defeated! +50 XP, +20 gold, Golden Key."
                )
            }
        } else {
            val enemyDamage = Random.nextInt(6, 13)
            val newHealth = (state.health - enemyDamage).coerceAtLeast(0)

            _uiState.update {
                it.copy(
                    enemyHealth = remaining,
                    health = newHealth,
                    message = if (newHealth == 0)
                        "You have fallen! Return to the hall to restart."
                    else
                        "You dealt $damage damage. Guardian dealt $enemyDamage."
                )
            }
        }
    }

    fun usePotion() {
        val state = _uiState.value
        val potion = state.inventory.firstOrNull {
            it.type == ItemType.HEALING
        } ?: run {
            setMessage("No healing potions available.")
            return
        }

        if (state.health >= state.heroClass.maxHealth) {
            setMessage("Your health is already full.")
            return
        }

        _uiState.update {
            it.copy(
                health = (it.health + potion.healingPower)
                    .coerceAtMost(it.heroClass.maxHealth),
                inventory = it.inventory - potion,
                message = "You used ${potion.name}."
            )
        }
    }

    fun escapeBattle() {
        _uiState.update {
            it.copy(
                screen = GameScreen.PAINTING,
                message = "You escaped through the painting portal."
            )
        }
    }

    fun navigate(screen: GameScreen) {
        _uiState.update { it.copy(screen = screen) }
    }

    fun setMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun restart() {
        _uiState.value = GameUiState()
    }
}