
package com.anastassiya.mysticalmanor.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anastassiya.mysticalmanor.data.local.QuestEntity
import com.anastassiya.mysticalmanor.journal.JournalViewModel
import com.anastassiya.mysticalmanor.ui.theme.ManorGold
import com.anastassiya.mysticalmanor.ui.theme.ManorSurface
import com.anastassiya.mysticalmanor.ui.theme.ManorTitle
import java.text.DateFormat
import java.util.Date

@Composable
fun JournalListScreen(
    vm: JournalViewModel,
    onCreate: () -> Unit,
    onOpen: (Long) -> Unit,
    onBack: () -> Unit
) {
    val quests by vm.quests.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()

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
            "Explorer's Journal",
            "Your personal quests and adventures"
        )

        val completed = quests.count {
            it.status == "COMPLETED"
        }
        val active = quests.count {
            it.status == "ACTIVE"
        }

        Text(
            "Total: ${quests.size}  •  Active: $active  •  " +
                    "Completed: $completed",
            color = ManorGold
        )

        Button(
            onClick = onCreate,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("✦ CREATE NEW QUEST")
        }

        if (quests.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = ManorSurface
                )
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        "📜 Your journal is empty",
                        fontWeight = FontWeight.Bold,
                        color = ManorGold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Create your first quest to begin " +
                                "tracking a new adventure."
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = quests,
                    key = { it.id }
                ) { quest ->
                    QuestListCard(
                        quest = quest,
                        onClick = { onOpen(quest.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestListCard(
    quest: QuestEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
                quest.title,
                fontWeight = FontWeight.Bold,
                color = ManorGold
            )

            Text(
                quest.description,
                maxLines = 2
            )

            Text(
                "${quest.status}  •  🪙 ${quest.rewardGold} gold",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun JournalDetailScreen(
    vm: JournalViewModel,
    questId: Long,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
    onBack: () -> Unit
) {
    val quest by remember(vm, questId) {
        vm.observeQuest(questId)
    }.collectAsStateWithLifecycle(initialValue = null)

    val history by remember(vm, questId) {
        vm.observeHistory(questId)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val busy by vm.busy.collectAsStateWithLifecycle()

    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete this quest?")
            },
            text = {
                Text(
                    "This will permanently delete the quest " +
                            "and its activity history."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !busy,
                    onClick = {
                        showDeleteDialog = false
                        vm.deleteQuest(questId, onDeleted)
                    }
                ) {
                    Text("DELETE", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("CANCEL")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Quest Board")
        }

        if (quest == null) {
            Text("Loading quest or quest not found.")
        } else {
            val current = quest!!

            ManorTitle(
                current.title,
                "Quest #${current.id}"
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = ManorSurface
                )
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(current.description)

                    HorizontalDivider()

                    Text(
                        "Status: ${current.status}",
                        color = ManorGold
                    )

                    Text(
                        "Planned reward: " +
                                "${current.rewardGold} gold"
                    )

                    Text(
                        "Updated: " + DateFormat
                            .getDateTimeInstance()
                            .format(Date(current.updatedAt)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (current.status != "COMPLETED") {
                Button(
                    onClick = {
                        vm.advanceQuest(current.id)
                    },
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (current.status == "DRAFT")
                            "START QUEST"
                        else
                            "COMPLETE QUEST"
                    )
                }

                OutlinedButton(
                    onClick = onEdit,
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("EDIT QUEST")
                }
            } else {
                Text(
                    "✓ Quest completed!",
                    color = ManorGold
                )
            }

            OutlinedButton(
                onClick = {
                    showDeleteDialog = true
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("DELETE QUEST")
            }

            HorizontalDivider()

            Text(
                "Activity History",
                style = MaterialTheme.typography.titleLarge,
                color = ManorGold
            )

            if (history.isEmpty()) {
                Text("No activity yet.")
            }

            history.forEach { event ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = ManorSurface
                    )
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(event.action)
                        Text(
                            DateFormat.getDateTimeInstance()
                                .format(Date(event.timestamp)),
                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JournalFormScreen(
    vm: JournalViewModel,
    questId: Long?,
    initialTitle: String = "",
    initialDescription: String = "",
    initialReward: String = "0",
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    var title by rememberSaveable(questId) {
        mutableStateOf(initialTitle)
    }
    var description by rememberSaveable(questId) {
        mutableStateOf(initialDescription)
    }
    var reward by rememberSaveable(questId) {
        mutableStateOf(initialReward)
    }

    val busy by vm.busy.collectAsStateWithLifecycle()

    val titleValid = title.trim().length in 3..60
    val descriptionValid =
        description.trim().length in 5..500
    val rewardNumber = reward.toIntOrNull()
    val rewardValid =
        rewardNumber != null &&
                rewardNumber in 0..100000

    val canSave =
        titleValid && descriptionValid && rewardValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        ManorTitle(
            if (questId == null) "New Quest" else "Edit Quest",
            "Write your next magical adventure"
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Quest title") },
            isError = title.isNotEmpty() && !titleValid,
            supportingText = {
                Text("3–60 characters")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            isError = description.isNotEmpty() &&
                    !descriptionValid,
            supportingText = {
                Text("5–500 characters")
            },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = reward,
            onValueChange = { reward = it },
            label = { Text("Reward in gold") },
            isError = !rewardValid,
            supportingText = {
                Text("Whole number from 0 to 100000")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            enabled = canSave && !busy,
            onClick = {
                vm.saveQuest(
                    id = questId,
                    title = title,
                    description = description,
                    rewardText = reward,
                    onSuccess = onSaved
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (questId == null)
                    "CREATE QUEST"
                else
                    "SAVE CHANGES"
            )
        }
    }
}
