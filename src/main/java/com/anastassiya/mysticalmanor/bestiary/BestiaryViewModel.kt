
package com.anastassiya.mysticalmanor.bestiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anastassiya.mysticalmanor.data.remote.MonsterDetailDto
import com.anastassiya.mysticalmanor.data.remote.MonsterSummaryDto
import com.anastassiya.mysticalmanor.data.repository.BestiaryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class BestiaryViewModel(
    private val repository: BestiaryRepository
) : ViewModel() {

    private val _monsters =
        MutableStateFlow<BestiaryUiState<List<MonsterSummaryDto>>>(
            BestiaryUiState.Idle
        )

    val monsters = _monsters.asStateFlow()

    private val _details =
        MutableStateFlow<BestiaryUiState<MonsterDetailDto>>(
            BestiaryUiState.Idle
        )

    val details = _details.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var listJob: Job? = null
    private var detailJob: Job? = null

    private var selectedMonster: String? = null

    fun ensureLoaded() {
        if (_monsters.value == BestiaryUiState.Idle) {
            loadMonsters()
        }
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun loadMonsters() {

        listJob?.cancel()

        listJob = viewModelScope.launch {

            _monsters.value = BestiaryUiState.Loading

            try {

                val result = repository.getMonsters()

                _monsters.value =
                    BestiaryUiState.Success(result)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                _monsters.value =
                    BestiaryUiState.Error(
                        getErrorMessage(e)
                    )
            }
        }
    }

    fun ensureDetailsLoaded(index: String) {

        if (
            selectedMonster != index ||
            _details.value == BestiaryUiState.Idle
        ) {
            loadMonsterDetails(index)
        }
    }

    fun loadMonsterDetails(index: String) {

        detailJob?.cancel()
        selectedMonster = index

        detailJob = viewModelScope.launch {

            _details.value = BestiaryUiState.Loading

            try {

                val result =
                    repository.getMonsterDetails(index)

                _details.value =
                    BestiaryUiState.Success(result)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                _details.value =
                    BestiaryUiState.Error(
                        getErrorMessage(e)
                    )
            }
        }
    }

    private fun getErrorMessage(e: Exception): String {

        return when (e) {

            is SocketTimeoutException ->
                "The magical connection timed out. Try again."

            is IOException ->
                "Could not connect to the server. " +
                        "Check your internet connection."

            is HttpException ->
                when (e.code()) {

                    404 ->
                        "This magical creature was not found."

                    in 500..599 ->
                        "The server is unavailable. Try again later."

                    else ->
                        "Server error: HTTP ${e.code()}."
                }

            else ->
                "An unexpected error occurred while loading data."
        }
    }

    companion object {

        fun factory(
            repository: BestiaryRepository
        ): ViewModelProvider.Factory {

            return object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {

                    if (
                        modelClass.isAssignableFrom(
                            BestiaryViewModel::class.java
                        )
                    ) {
                        return BestiaryViewModel(repository) as T
                    }

                    throw IllegalArgumentException(
                        "Unknown ViewModel class"
                    )
                }
            }
        }
    }
}
