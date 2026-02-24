package com.jozeftvrdy.game.guessorder.game.play

import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jozeftvrdy.game.guessorder.base.BaseViewModel
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.game.model.onFailure
import com.jozeftvrdy.game.guessorder.repository.PlayGameRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

private const val savedStateSolutionKey = "savedSolutionKey"
private const val savedStatePlayedTimeKey = "savedStatePlayedTime"

class PlayGameViewModel(
    override val savedStateHandle: SavedStateHandle,
    val initialGameData: InitialGameData,
    private val playRepo: PlayGameRepository,
): BaseViewModel<ScreenState, ScreenEffect>() {

    var savedSolution: List<ItemFill>?
        get() {
            return savedStateHandle[savedStateSolutionKey]
        }
        set(value) {
            savedStateHandle[savedStateSolutionKey] = value
        }

    override val initialState: ScreenState
        get() = ScreenState(
            persistentListOf(),
            initialGameData
        )

    val animator = PlayGameAnimator(
        initialGameData = initialGameData,
        savedStateHandle = savedStateHandle,
        vmScope = viewModelScope
    )

    val currentGuess: List<ItemFill?>
        get() = animator.currentGuess

    var timePlayedMillis: Long
        get() = savedStateHandle[savedStatePlayedTimeKey] ?:0
        set(value) {
            savedStateHandle[savedStatePlayedTimeKey] = value
        }

    var timerStartTime: Long? = null

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

        stopTimer()

        clearCurrentGuessOnClick()

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
            val solution = savedSolution?:run {
                playRepo.generateSolution(
                    turnGuess = guess,
                    gameData = initialGameData
                ).also {
                    savedSolution = it
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

                if (result.greatSuccessCount == guess.size) {
                    sendEffect(ScreenEffect.NavigateToPostGame(
                        timePlayedMillis.also {
                            require(it > 0)
                        }
                    ))
                } else {
                    startTimer(forceStart = true)
                }
            }
        }
    }

    fun eventOnGuessModify(index: Int, fill: ItemFill?) {
        animator.modifyCurrentGuessAnimated(
            index to fill
        )
    }

    fun clearCurrentGuessOnClick() {
        animator.modifyCurrentGuessAnimated(
            currentGuess.mapIndexed { index, _ ->
                index to null
            }
        )
    }

    fun startTimer() {
        startTimer(forceStart = false)
    }

    private fun startTimer(forceStart: Boolean) {
        if (!forceStart && state.value.history.isEmpty()) return

        timerStartTime = getNowTimeForTimer()
    }

    fun stopTimer() {
        val timerEndTime = getNowTimeForTimer()
        val localTimerStartTime = timerStartTime?:return
        require(timerEndTime > localTimerStartTime)
        timePlayedMillis += timerEndTime - localTimerStartTime
        timerStartTime = null
    }

    fun getNowTimeForTimer(): Long = SystemClock.elapsedRealtime()
}