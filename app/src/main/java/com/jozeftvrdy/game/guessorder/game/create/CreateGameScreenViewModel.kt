package com.jozeftvrdy.game.guessorder.game.create

import androidx.lifecycle.viewModelScope
import com.jozeftvrdy.game.guessorder.base.BaseViewModel
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.repository.GameRepository
import kotlinx.coroutines.launch

sealed class ScreenState {
    data object Loading: ScreenState()
    data class Loaded(
        val savedInitialGameData: InitialGameData,
        val tilesValueRange: IntRange,
        val colorsValueRange: IntRange,
    ): ScreenState()

}

sealed class ScreenEffect {
    data class NavigateToGame(val gameData: InitialGameData): ScreenEffect()
}

private const val TilesFromValue = 2
private const val TilesToValue = 8
private const val ColorsFromValue = 2
private const val ColorsToValue = 8

class CreateGameScreenViewModel(
    private val gameRepo: GameRepository
): BaseViewModel<ScreenState, ScreenEffect>() {

    override val initialState: ScreenState
        get() = ScreenState.Loading

    fun eventOnPrimaryBtnClick(gameData: InitialGameData) {
        gameRepo.saveInitialDataAsync(gameData)
        sendEffect(ScreenEffect.NavigateToGame(gameData))
    }

    override fun onStateObserved() {
        if (state.value != ScreenState.Loading) {
            return
        }

        viewModelScope.launch {
            val tilesRange = TilesFromValue..TilesToValue
            val colorsRange = ColorsFromValue..ColorsToValue

            val initialData = gameRepo.getInitialData().let { data ->
                data.copy(
                    tilesCount = data.tilesCount.coerceIn(tilesRange),
                    colorsCount = data.colorsCount.coerceIn(colorsRange),
                )
            }
            updateState {
                ScreenState.Loaded(
                    initialData,
                    tilesValueRange = tilesRange,
                    colorsValueRange = colorsRange,
                )
            }
        }
    }
}