
package com.anastassiya.mysticalmanor.bestiary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anastassiya.mysticalmanor.data.remote.MonsterDetailDto
import com.anastassiya.mysticalmanor.data.remote.MonsterSummaryDto
import com.anastassiya.mysticalmanor.ui.theme.ManorGold
import com.anastassiya.mysticalmanor.ui.theme.ManorSurface
import com.anastassiya.mysticalmanor.ui.theme.ManorTitle

@Composable
fun BestiaryListScreen(
    vm: BestiaryViewModel,
    onOpenMonster: (String) -> Unit,
    onBack: () -> Unit
) {

    val state by vm.monsters.collectAsStateWithLifecycle()
    val searchQuery by vm.searchQuery.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.ensureLoaded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Grand Hall")
        }

        ManorTitle(
            "The Arcane Bestiary",
            "Discover creatures from forgotten worlds"
        )

        Text(
            text = "🌐 Online magical encyclopedia",
            color = ManorGold,
            style = MaterialTheme.typography.labelMedium
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = vm::updateSearch,
            label = {
                Text("Search for a creature")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = vm::loadMonsters,
            enabled = state != BestiaryUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("↻ REFRESH BESTIARY")
        }

        when (val current = state) {

            BestiaryUiState.Idle,
            BestiaryUiState.Loading -> {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    BestiaryLoading(
                        "Summoning magical creatures..."
                    )
                }
            }

            is BestiaryUiState.Error -> {

                BestiaryError(
                    message = current.message,
                    onRetry = vm::loadMonsters
                )
            }

            is BestiaryUiState.Success -> {

                val filtered = remember(
                    current.data,
                    searchQuery
                ) {
                    current.data.filter {
                        it.name.contains(
                            searchQuery.trim(),
                            ignoreCase = true
                        )
                    }
                }

                Text(
                    text = "Creatures discovered: " +
                            "${filtered.size} / ${current.data.size}",
                    color = ManorGold
                )

                if (filtered.isEmpty()) {

                    BestiaryEmpty(
                        "No magical creatures found."
                    )

                } else {

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        items(
                            items = filtered,
                            key = { it.index }
                        ) { monster ->

                            MonsterCard(
                                monster = monster,
                                onClick = {
                                    onOpenMonster(
                                        monster.index
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonsterCard(
    monster: MonsterSummaryDto,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ManorSurface
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = creatureEmoji(monster.name),
                style = MaterialTheme.typography.headlineMedium
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = monster.name,
                    color = ManorGold,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "View creature details",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "›",
                color = ManorGold,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun BestiaryDetailScreen(
    vm: BestiaryViewModel,
    monsterIndex: String,
    onBack: () -> Unit
) {

    val state by vm.details.collectAsStateWithLifecycle()

    LaunchedEffect(vm, monsterIndex) {
        vm.ensureDetailsLoaded(monsterIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Arcane Bestiary")
        }

        when (val current = state) {

            BestiaryUiState.Idle,
            BestiaryUiState.Loading -> {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    BestiaryLoading(
                        "Uncovering creature secrets..."
                    )
                }
            }

            is BestiaryUiState.Error -> {

                BestiaryError(
                    message = current.message,
                    onRetry = {
                        vm.loadMonsterDetails(monsterIndex)
                    }
                )
            }

            is BestiaryUiState.Success -> {

                if (current.data.index == monsterIndex) {

                    MonsterDetails(
                        monster = current.data,
                        modifier = Modifier.weight(1f)
                    )

                } else {

                    BestiaryLoading(
                        "Uncovering creature secrets..."
                    )
                }
            }
        }
    }
}

@Composable
private fun MonsterDetails(
    monster: MonsterDetailDto,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = creatureEmoji(monster.name),
                    style = MaterialTheme.typography.displayLarge
                )

                ManorTitle(
                    monster.name,
                    "${monster.size} • ${monster.type}"
                )
            }
        }

        item {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                MonsterStat(
                    label = "Hit Points",
                    value = monster.hitPoints.toString(),
                    modifier = Modifier.weight(1f)
                )

                MonsterStat(
                    label = "Armor Class",
                    value = monster.armorClass
                        .joinToString(" / ") {
                            it.value.toString()
                        }
                        .ifBlank { "N/A" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                MonsterStat(
                    label = "Challenge Rating",
                    value = monster.challengeRating.toString(),
                    modifier = Modifier.weight(1f)
                )

                MonsterStat(
                    label = "Experience",
                    value = monster.xp.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ManorSurface
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Creature Information",
                        color = ManorGold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text("Size: ${monster.size}")
                    Text("Type: ${monster.type}")
                    Text("Alignment: ${monster.alignment}")
                }
            }
        }

        item {

            Text(
                text = "Ability Scores",
                color = ManorGold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                MonsterStat(
                    "STR",
                    monster.strength.toString(),
                    Modifier.weight(1f)
                )

                MonsterStat(
                    "DEX",
                    monster.dexterity.toString(),
                    Modifier.weight(1f)
                )

                MonsterStat(
                    "CON",
                    monster.constitution.toString(),
                    Modifier.weight(1f)
                )
            }
        }

        item {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                MonsterStat(
                    "INT",
                    monster.intelligence.toString(),
                    Modifier.weight(1f)
                )

                MonsterStat(
                    "WIS",
                    monster.wisdom.toString(),
                    Modifier.weight(1f)
                )

                MonsterStat(
                    "CHA",
                    monster.charisma.toString(),
                    Modifier.weight(1f)
                )
            }
        }

        val abilities =
            monster.specialAbilities.orEmpty()

        if (abilities.isNotEmpty()) {

            item {

                Text(
                    text = "Special Abilities",
                    color = ManorGold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(abilities) { ability ->

                MonsterAbilityCard(
                    name = ability.name,
                    description = ability.desc.orEmpty()
                )
            }
        }

        val actions = monster.actions.orEmpty()

        if (actions.isNotEmpty()) {

            item {

                Text(
                    text = "Creature Actions",
                    color = ManorGold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(actions) { action ->

                MonsterAbilityCard(
                    name = action.name,
                    description = action.desc.orEmpty()
                )
            }
        }

        item {

            Text(
                text = "Source: D&D 5e SRD API",
                style = MaterialTheme.typography.labelSmall,
                color = ManorGold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MonsterStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = ManorSurface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = value,
                color = ManorGold,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MonsterAbilityCard(
    name: String,
    description: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ManorSurface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = name,
                color = ManorGold,
                fontWeight = FontWeight.Bold
            )

            if (description.isNotBlank()) {

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun BestiaryLoading(
    message: String
) {

    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        CircularProgressIndicator(
            color = ManorGold
        )

        Text(
            text = message,
            color = ManorGold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BestiaryError(
    message: String,
    onRetry: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ManorSurface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "⚠",
                style = MaterialTheme.typography.displaySmall
            )

            Text(
                text = "The magical connection failed",
                color = ManorGold,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("TRY AGAIN")
            }
        }
    }
}

@Composable
private fun BestiaryEmpty(
    message: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ManorSurface
        )
    ) {

        Text(
            text = "📜 $message",
            modifier = Modifier.padding(24.dp),
            color = ManorGold
        )
    }
}

private fun creatureEmoji(name: String): String {

    return when {

        name.contains("dragon", true) -> "🐉"

        name.contains("wolf", true) -> "🐺"

        name.contains("bear", true) -> "🐻"

        name.contains("spider", true) -> "🕷️"

        name.contains("ghost", true) -> "👻"

        name.contains("skeleton", true) -> "💀"

        name.contains("snake", true) -> "🐍"

        name.contains("bat", true) -> "🦇"

        name.contains("owl", true) -> "🦉"

        name.contains("giant", true) -> "👹"

        else -> "✦"
    }
}
