
package com.anastassiya.mysticalmanor.bestiary

sealed interface BestiaryUiState<out T> {

    data object Idle : BestiaryUiState<Nothing>

    data object Loading : BestiaryUiState<Nothing>

    data class Success<T>(
        val data: T
    ) : BestiaryUiState<T>

    data class Error(
        val message: String
    ) : BestiaryUiState<Nothing>
}
