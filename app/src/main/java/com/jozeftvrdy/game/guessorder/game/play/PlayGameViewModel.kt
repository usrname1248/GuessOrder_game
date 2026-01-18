package com.jozeftvrdy.game.guessorder.game.play

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.jozeftvrdy.game.guessorder.base.BaseViewModel
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.game.model.onFailure
import com.jozeftvrdy.game.guessorder.repository.PlayGameRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

private const val savedStateSolutionKey = "savedSolutionKey"

class PlayGameViewModel(
    override val savedStateHandle: SavedStateHandle,
    val initialGameData: InitialGameData,
    private val playRepo: PlayGameRepository,
): BaseViewModel<ScreenState, ScreenEffect>() {

    private val emptyArray: Array<ItemFill?>
        get() = Array(initialGameData.tilesCount) {
            null
        }
    @OptIn(SavedStateHandleSaveableApi::class)
    val currentGuess: MutableList<ItemFill?> by savedStateHandle.saveable(
        saver = listSaver(
            save = { it },
            restore = { it.toMutableStateList() },
        )
    ) {
        mutableStateListOf(*emptyArray)
    }

    override val initialState: ScreenState
        get() = ScreenState(
            persistentListOf(),
            initialGameData
        )

    fun eventOnButtonClicked() {
        if (currentGuess.size != initialGameData.tilesCount) {
            sendEffect(ScreenEffect.ShowUnexpectedError)
            return
        }

        val guess: List<ItemFill> = currentGuess.map {
            it ?: run {
                sendEffect(ScreenEffect.ShowEmptyGuessError)
                return
            }
        }

        currentGuess.replaceAll {
            null
        }

        var index: Int? = null
        updateState { oldState ->
            oldState.copy(
                history = oldState.history.toMutableList().apply {
                    this.add(
                        HistoryItem(
                            guess = guess.toPersistentList(),
                            result = null
                        )
                    )
                    index = this.lastIndex
                }
            )
        }

        viewModelScope.launch {
            val solution = savedStateHandle[savedStateSolutionKey]?:run {
                playRepo.generateSolution(
                    turnGuess = guess,
                    gameData = initialGameData
                ).also {
                    savedStateHandle[savedStateSolutionKey] = it
                }
            }

            playRepo.validateTurnGuess(
                turnGuess = guess,
                solution = solution,
            ).onFailure {
                sendEffect(
                    ScreenEffect.ShowUnexpectedError
                )
                return@launch
            }.let { result ->
                if (result.greatSuccessCount == guess.size) {
                    sendEffect(ScreenEffect.NavigateToPostGame)
                    return@launch
                }

                requireNotNull(index)
                updateState { oldState ->
                    oldState.copy(
                        history = oldState.history.toMutableList().apply {
                            val item = this[index].copy(
                                result = result
                            )
                            this[index] = item
                        }
                    )
                }
            }
        }
    }
}