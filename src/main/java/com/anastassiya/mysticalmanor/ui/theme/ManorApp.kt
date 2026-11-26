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

import androidx.activity.compose.BackHandler

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp



import androidx.lifecycle.viewmodel.compose.viewModel

import com.anastassiya.mysticalmanor.data.repository.JournalRepository
import com.anastassiya.mysticalmanor.journal.JournalViewModel
import com.anastassiya.mysticalmanor.journal.JournalViewModelFactory
import com.anastassiya.mysticalmanor.journal.JournalListScreen
import com.anastassiya.mysticalmanor.journal.JournalDetailScreen
import com.anastassiya.mysticalmanor.journal.JournalFormScreen

import com.anastassiya.mysticalmanor.bestiary.BestiaryViewModel
import com.anastassiya.mysticalmanor.bestiary.BestiaryListScreen
import com.anastassiya.mysticalmanor.bestiary.BestiaryDetailScreen
import com.anastassiya.mysticalmanor.data.repository.BestiaryRepository



@Composable
fun ManorApp(
    journalRepository: JournalRepository,
    vm: ManorViewModel = viewModel()
) {

    val state by vm.uiState.collectAsStateWithLifecycle()

    val journalVm: JournalViewModel = viewModel(
        factory = remember(journalRepository) {
            JournalViewModelFactory(journalRepository)
        }
    )

    val bestiaryRepository = remember {
        BestiaryRepository()
    }

    val bestiaryVm: BestiaryViewModel = viewModel(
        factory = remember(bestiaryRepository) {
            BestiaryViewModel.factory(bestiaryRepository)
        }
    )

    val navController = rememberNavController()

    LaunchedEffect(state.screen) {

        val targetRoute = state.screen.name.lowercase()

        if (navController.currentDestination?.route != targetRoute) {

            navController.navigate(targetRoute) {

                launchSingleTop = true

                if (state.screen == GameScreen.WELCOME) {
                    popUpTo("welcome") {
                        inclusive = false
                    }
                }
            }
        }
    }

    val journalMessage by journalVm.message
        .collectAsStateWithLifecycle()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(journalMessage) {
        val message = journalMessage
        if (message != null) {
            journalVm.clearMessage()
            snackbarHostState.showSnackbar(message)
        }
    }

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
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                MagicalParticles(
                    modifier = Modifier.fillMaxSize()
                )

                NavHost(
                    navController = navController,
                    startDestination = "welcome",

                    enterTransition = {
                        fadeIn(
                            animationSpec = tween(450)
                        ) + scaleIn(
                            initialScale = 0.96f,
                            animationSpec = tween(450)
                        )
                    },

                    exitTransition = {
                        fadeOut(
                            animationSpec = tween(220)
                        ) + scaleOut(
                            targetScale = 1.03f,
                            animationSpec = tween(220)
                        )
                    },

                    popEnterTransition = {
                        fadeIn(
                            animationSpec = tween(350)
                        )
                    },

                    popExitTransition = {
                        fadeOut(
                            animationSpec = tween(200)
                        )
                    }
                ) {

                    composable("welcome") {
                        ManorScreenFrame(GameScreen.WELCOME, vm) {
                            WelcomeScreen(vm)
                        }
                    }

                    composable("hall") {
                        ManorScreenFrame(GameScreen.HALL, vm) {
                            HallScreen(
                                vm = vm,
                                state = state,
                                onOpenJournal = {
                                    navController.navigate("journal")
                                },
                                onOpenBestiary = {
                                    navController.navigate("bestiary")
                                }
                            )

                        }
                    }

                    composable("room") {
                        ManorScreenFrame(GameScreen.ROOM, vm) {
                            RoomScreen(vm, state)
                        }
                    }

                    composable("painting") {
                        ManorScreenFrame(GameScreen.PAINTING, vm) {
                            PaintingScreen(vm, state)
                        }
                    }

                    composable("battle") {
                        ManorScreenFrame(GameScreen.BATTLE, vm) {
                            BattleScreen(vm, state)
                        }
                    }

                    composable("inventory") {
                        ManorScreenFrame(GameScreen.INVENTORY, vm) {
                            InventoryScreen(vm, state)
                        }
                    }

                    composable("profile") {
                        ManorScreenFrame(GameScreen.PROFILE, vm) {
                            ProfileScreen(vm, state)
                        }
                    }


                    composable("journal") {
                        JournalListScreen(
                            vm = journalVm,
                            onCreate = {
                                navController.navigate("journal/new")
                            },
                            onOpen = { id ->
                                navController.navigate("journal/detail/$id")
                            },
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("journal/new") {
                        JournalFormScreen(
                            vm = journalVm,
                            questId = null,
                            onSaved = {
                                navController.popBackStack()
                            },
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("journal/detail/{questId}") { entry ->

                        val questId = entry.arguments
                            ?.getString("questId")
                            ?.toLongOrNull()

                        if (questId == null) {
                            Text("Invalid quest ID")
                        } else {
                            JournalDetailScreen(
                                vm = journalVm,
                                questId = questId,
                                onEdit = {
                                    navController.navigate(
                                        "journal/edit/$questId"
                                    )
                                },
                                onDeleted = {
                                    navController.popBackStack()
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    composable("journal/edit/{questId}") { entry ->

                        val questId = entry.arguments
                            ?.getString("questId")
                            ?.toLongOrNull()

                        if (questId == null) {
                            Text("Invalid quest ID")
                        } else {
                            val quest by remember(journalVm, questId) {
                                journalVm.observeQuest(questId)
                            }.collectAsStateWithLifecycle(initialValue = null)

                            if (quest == null) {
                                Text("Loading quest...")
                            } else {
                                key(questId) {
                                    JournalFormScreen(
                                        vm = journalVm,
                                        questId = questId,
                                        initialTitle = quest!!.title,
                                        initialDescription = quest!!.description,
                                        initialReward =
                                            quest!!.rewardGold.toString(),
                                        onSaved = {
                                            navController.popBackStack()
                                        },
                                        onBack = {
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }
                        }
                    }


                    composable("bestiary") {

                        BestiaryListScreen(
                            vm = bestiaryVm,

                            onOpenMonster = { index ->
                                navController.navigate(
                                    "bestiary/detail/$index"
                                )
                            },

                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("bestiary/detail/{monsterIndex}") { entry ->

                        val monsterIndex = entry.arguments
                            ?.getString("monsterIndex")

                        if (monsterIndex.isNullOrBlank()) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text("Invalid creature ID")

                                TextButton(
                                    onClick = {
                                        navController.popBackStack()
                                    }
                                ) {
                                    Text("Go back")
                                }
                            }

                        } else {

                            BestiaryDetailScreen(
                                vm = bestiaryVm,
                                monsterIndex = monsterIndex,

                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun ManorScreenFrame(
    screen: GameScreen,
    vm: ManorViewModel,
    content: @Composable () -> Unit
) {

    val showHeader = screen != GameScreen.WELCOME

    BackHandler(enabled = showHeader) {

        val previousScreen = when (screen) {

            GameScreen.HALL -> GameScreen.WELCOME

            GameScreen.ROOM,
            GameScreen.INVENTORY,
            GameScreen.PROFILE -> GameScreen.HALL

            GameScreen.PAINTING -> GameScreen.ROOM

            GameScreen.BATTLE -> GameScreen.PAINTING

            GameScreen.WELCOME -> GameScreen.WELCOME
        }

        vm.navigate(previousScreen)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(18.dp)
    ) {

        if (showHeader) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = {
                        vm.navigate(GameScreen.HALL)
                    }
                ) {
                    Text("⌂ Grand Hall")
                }

                Text(
                    "✦ MYSTICAL MANOR",
                    color = ManorGold
                )
            }

            Spacer(Modifier.height(10.dp))
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            content()
        }
    }
}

@Composable
fun PlayerNameField(
    name: String,
    onNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = {
            Text("Your name")
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
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

        //state hosting
        PlayerNameField(
            name = name,
            onNameChange = { newName ->
                name = newName
            }
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
fun HallScreen(
    vm: ManorViewModel,
    state: GameUiState,
    onOpenJournal: () -> Unit = {},
    onOpenBestiary: () -> Unit = {}
) {
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
        ManorButton(
            text = "📜 EXPLORER'S JOURNAL",
            onClick = onOpenJournal
        )
        ManorButton(
            text = "🐉 ARCANE BESTIARY",
            onClick = onOpenBestiary
        )
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
                val chestOpened = room.id in state.openedChests

                AnimatedVisibility(
                    visible = chestOpened,
                    enter = fadeIn() + scaleIn()
                ) {
                    Text(
                        text = "✦ TREASURE FOUND · Moon Crystal",
                        color = ManorGold
                    )
                }

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

    val portalGlow by animateFloatAsState(
        targetValue = if (state.isEnteringPainting) 1.08f else 1f,
        animationSpec = tween(650),
        label = "Portal glow"
    )

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
                modifier = Modifier
                    .fillMaxSize()
                    .scale(portalGlow)
            )
        }

        AnimatedVisibility(
            visible = state.isEnteringPainting,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "✦ Entering the painting...",
                color = ManorGold
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
            text = if (completed) {
                "WORLD RESTORED"
            } else {
                "ENTER PAINTING"
            },
            enabled = !completed && !state.isEnteringPainting,
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
    val shake = remember {
        Animatable(0f)
    }

    LaunchedEffect(state.combatPulse) {

        if (state.combatPulse > 0) {

            shake.animateTo(
                targetValue = 9f,
                animationSpec = tween(65)
            )

            shake.animateTo(
                targetValue = -9f,
                animationSpec = tween(65)
            )

            shake.animateTo(
                targetValue = 0f,
                animationSpec = tween(100)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = shake.value.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ManorTitle("⚔ Shadow Guardian", "A battle inside the painting")

        StatChip("GUARDIAN HP", "${state.enemyHealth}/40")
        val enemyFraction by animateFloatAsState(
            targetValue = state.enemyHealth / 40f,
            animationSpec = tween(500),
            label = "Enemy health"
        )
        LinearProgressIndicator(
            progress = { enemyFraction },
            modifier = Modifier.fillMaxWidth()
        )

        StatChip("YOUR HP", "${state.health}/${state.heroClass.maxHealth}")
        val healthFraction by animateFloatAsState(
            targetValue = state.health.toFloat() /
                    state.heroClass.maxHealth,
            animationSpec = tween(500),
            label = "Player health"
        )
        LinearProgressIndicator(
            progress = { healthFraction },
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
            modifier = Modifier.weight(1f),
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