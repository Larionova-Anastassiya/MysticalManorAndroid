package com.anastassiya.mysticalmanor.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.anastassiya.mysticalmanor.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anastassiya.mysticalmanor.model.*
import com.anastassiya.mysticalmanor.viewmodel.ManorViewModel

@Composable
fun ManorApp(vm: ManorViewModel = viewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = ManorGold,
            background = ManorPurple,
            surface = ManorSurface,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = ManorPurple
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(18.dp)
            ) {
                if (state.screen != GameScreen.WELCOME) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            vm.navigate(GameScreen.HALL)
                        }) {
                            Text("⌂ Grand Hall")
                        }
                        Text(
                            "✦ MYSTICAL MANOR",
                            color = ManorGold
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }

                when (state.screen) {
                    GameScreen.WELCOME -> WelcomeScreen(vm)
                    GameScreen.HALL -> HallScreen(vm, state)
                    GameScreen.ROOM -> RoomScreen(vm, state)
                    GameScreen.PAINTING -> PaintingScreen(vm, state)
                    GameScreen.BATTLE -> BattleScreen(vm, state)
                    GameScreen.INVENTORY -> InventoryScreen(vm, state)
                    GameScreen.PROFILE -> ProfileScreen(vm, state)
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(vm: ManorViewModel) {
    var name by rememberSaveable { mutableStateOf("") }
    var selectedClass by rememberSaveable {
        mutableStateOf(HeroClass.ARTIST)
    }
    var showDetails by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(30.dp))
        Text("🏰", style = MaterialTheme.typography.displayLarge)
        ManorTitle(
            "MYSTICAL MANOR",
            "A magical adventure awaits beyond the canvas."
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Choose your character", color = ManorGold)

        HeroClass.entries.forEach { hero ->
            val selected = selectedClass == hero
            ManorCard(
                title = "${hero.title}${if (selected) "  ✓" else ""}",
                description = hero.ability +
                        " • ${hero.maxHealth} HP",
                icon = hero.emoji,
                onClick = { selectedClass = hero }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = showDetails,
                onCheckedChange = { showDetails = it }
            )
            Text("Show character details")
        }

        if (showDetails) {
            Text(
                "Selected: ${selectedClass.title} • " +
                        "${selectedClass.maxHealth} HP • " +
                        "${selectedClass.initialGold} gold",
                color = Color.LightGray
            )
        }

        ManorButton("ENTER THE MANOR") {
            vm.startGame(name, selectedClass)
        }
    }
}

@Composable
fun HallScreen(vm: ManorViewModel, state: GameUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ManorTitle(
            "Grand Hall",
            "Welcome back, ${state.playerName} the ${state.heroClass.title}."
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatChip("HEALTH", "${state.health}/${state.heroClass.maxHealth}")
            StatChip("GOLD", "${state.gold}")
            StatChip("XP", "${state.experience}")
        }

        LinearProgressIndicator(
            progress = {
                state.health.toFloat() / state.heroClass.maxHealth
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(state.message, color = Color.LightGray)

        ManorTitle("Explore the manor", "Choose your destination")

        vm.rooms.forEach { room ->
            val available = vm.canEnter(room)
            ManorCard(
                title = if (available) room.name else "Locked: ${room.name}",
                description = if (available) room.description
                else "Explore two rooms, or choose Artist.",
                icon = room.icon,
                enabled = available,
                onClick = { vm.enterRoom(room) }
            )
        }

        Spacer(Modifier.height(6.dp))
        ManorButton("🎒 INVENTORY") {
            vm.navigate(GameScreen.INVENTORY)
        }
        ManorButton("🏆 PROFILE & ACHIEVEMENTS") {
            vm.navigate(GameScreen.PROFILE)
        }
        OutlinedButton(
            onClick = vm::restart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start a new adventure")
        }
    }
}

@Composable
fun RoomScreen(vm: ManorViewModel, state: GameUiState) {
    val room = vm.currentRoom ?: return

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ManorTitle(room.name, room.description)
        }
        item {
            Text(state.message, color = Color.LightGray)
        }

        if (room.hasTreasure) {
            item {
                ManorCard(
                    "Ancient Treasure Chest",
                    if (room.id in state.openedChests)
                        "Already opened"
                    else "A mysterious chest waits in the shadows.",
                    "🗝️",
                    onClick = vm::openChest
                )
            }
        }

        items(room.paintings) { painting ->
            ManorCard(
                painting.title,
                painting.description,
                "🖼️",
                onClick = { vm.selectPainting(painting) }
            )
        }

        item {
            ManorButton("RETURN TO GRAND HALL") {
                vm.navigate(GameScreen.HALL)
            }
        }
    }
}

@Composable
fun PaintingScreen(vm: ManorViewModel, state: GameUiState) {
    val painting = vm.currentPainting ?: return
    val completed = painting.id in state.completedPaintings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.magical_manor),
                contentDescription = "Magical painting in Mystical Manor",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        ManorTitle(painting.title, painting.description)
        Text(
            if (completed) "✓ Painting restored"
            else "The painted world is calling you...",
            color = ManorGold
        )
        Text(state.message, color = Color.LightGray)

        ManorButton(
            if (completed) "WORLD RESTORED" else "ENTER PAINTING",
            enabled = !completed,
            onClick = vm::enterPainting
        )
        OutlinedButton(
            onClick = { vm.navigate(GameScreen.ROOM) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to room")
        }
    }
}

@Composable
fun BattleScreen(vm: ManorViewModel, state: GameUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ManorTitle("⚔ Shadow Guardian", "A battle inside the painting")

        StatChip("GUARDIAN HP", "${state.enemyHealth}/40")
        LinearProgressIndicator(
            progress = { state.enemyHealth / 40f },
            modifier = Modifier.fillMaxWidth()
        )

        StatChip("YOUR HP", "${state.health}/${state.heroClass.maxHealth}")
        LinearProgressIndicator(
            progress = {
                state.health.toFloat() / state.heroClass.maxHealth
            },
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = ManorSurface)
        ) {
            Text(
                state.message,
                modifier = Modifier.padding(20.dp),
                color = Color.White
            )
        }

        val alive = state.health > 0

        ManorButton("⚔ ATTACK", enabled = alive) {
            vm.attack()
        }
        ManorButton("✨ SPECIAL ABILITY", enabled = alive) {
            vm.attack(special = true)
        }
        ManorButton("🧪 USE HEALING POTION", enabled = alive) {
            vm.usePotion()
        }
        OutlinedButton(
            onClick = vm::escapeBattle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Escape")
        }

        if (!alive) {
            ManorButton("RESTART ADVENTURE", onClick = vm::restart)
        }
    }
}

@Composable
fun InventoryScreen(vm: ManorViewModel, state: GameUiState) {
    val totalValue = state.inventory.sumOf { it.value }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ManorTitle(
            "Inventory",
            "${state.inventory.size} items • Value: $totalValue gold"
        )
        Text(state.message, color = Color.LightGray)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.inventory) { item ->
                ManorCard(
                    title = item.name,
                    description = "${item.description} • ${item.value} gold",
                    icon = when (item.type) {
                        ItemType.HEALING -> "🧪"
                        ItemType.MAGIC -> "✨"
                        ItemType.KEY -> "🗝️"
                        ItemType.TREASURE -> "💎"
                    },
                    onClick = {
                        if (item.type == ItemType.HEALING)
                            vm.usePotion()
                        else
                            vm.setMessage(item.description)
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(vm: ManorViewModel, state: GameUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ManorTitle(
            "${state.heroClass.emoji} ${state.playerName}",
            state.heroClass.title
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatChip("XP", "${state.experience}")
            StatChip("GOLD", "${state.gold}")
            StatChip("WINS", "${state.enemiesDefeated}")
        }

        Text("Rooms explored: ${state.visitedRooms.size}")
        Text("Paintings discovered: ${state.discoveredPaintings.size}")
        Text("Paintings restored: ${state.completedPaintings.size}")

        ManorTitle("Achievements", "${state.achievements.size} unlocked")

        state.achievements.forEach { achievement ->
            ManorCard(
                achievement.name,
                achievement.description,
                "🏆",
                onClick = {
                    vm.setMessage(achievement.description)
                }
            )
        }

        if (state.achievements.isEmpty()) {
            Text("No achievements yet. Explore the manor!")
        }

        ManorButton("BACK TO GRAND HALL") {
            vm.navigate(GameScreen.HALL)
        }
    }
}