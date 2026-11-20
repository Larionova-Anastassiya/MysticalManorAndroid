
package com.anastassiya.mysticalmanor.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anastassiya.mysticalmanor.data.local.QuestEntity
import com.anastassiya.mysticalmanor.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(
    private val repository: JournalRepository
) : ViewModel() {

    val quests: StateFlow<List<QuestEntity>> =
        repository.quests.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()

    fun observeQuest(id: Long) =
        repository.observeQuest(id)

    fun observeHistory(id: Long) =
        repository.observeHistory(id)

    fun clearMessage() {
        _message.value = null
    }

    private fun valid(
        title: String,
        description: String,
        reward: Int?
    ): Boolean {
        return title.trim().length in 3..60 &&
                description.trim().length in 5..500 &&
                reward != null &&
                reward in 0..100000
    }

    fun saveQuest(
        id: Long?,
        title: String,
        description: String,
        rewardText: String,
        onSuccess: () -> Unit
    ) {
        if (_busy.value) return

        val reward = rewardText.toIntOrNull()

        if (!valid(title, description, reward)) {
            _message.value =
                "Title: 3–60 characters; description: 5–500; " +
                        "reward: 0–100000."
            return
        }

        viewModelScope.launch {
            _busy.value = true
            try {
                val saved = if (id == null) {
                    repository.create(
                        title, description, reward!!
                    )
                    true
                } else {
                    repository.update(
                        id, title, description, reward!!
                    )
                }

                if (saved) {
                    _message.value = "Quest saved successfully."
                    onSuccess()
                } else {
                    _message.value =
                        "Quest cannot be edited or was deleted."
                }
            } catch (e: Exception) {
                _message.value = "Could not save quest."
            } finally {
                _busy.value = false
            }
        }
    }

    fun deleteQuest(
        id: Long,
        onSuccess: () -> Unit
    ) {
        if (_busy.value) return

        viewModelScope.launch {
            _busy.value = true
            try {
                if (repository.delete(id)) {
                    _message.value = "Quest deleted."
                    onSuccess()
                } else {
                    _message.value = "Quest not found."
                }
            } catch (e: Exception) {
                _message.value = "Could not delete quest."
            } finally {
                _busy.value = false
            }
        }
    }

    fun advanceQuest(id: Long) {
        if (_busy.value) return

        viewModelScope.launch {
            _busy.value = true
            try {
                _message.value =
                    if (repository.advance(id)) {
                        "Quest progress updated."
                    } else {
                        "This transition is not allowed."
                    }
            } catch (e: Exception) {
                _message.value = "Could not update quest."
            } finally {
                _busy.value = false
            }
        }
    }
}
