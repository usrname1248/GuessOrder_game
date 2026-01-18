package com.jozeftvrdy.game.guessorder.game.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jozeftvrdy.game.guessorder.base.BaseViewModel
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.repository.GameRepository
import kotlinx.coroutines.launch

private const val SelectableTilesMinValue = 2
private const val SelectableTilesMaxValue = 8
private const val TilesMaxValue = 8
private const val SelectableColorsMinValue = 2
private const val SelectableColorsMaxValue = 8
private const val ColorsMaxValue = 8

class CreateGameScreenViewModel(
    override val savedStateHandle: SavedStateHandle,
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
            val initialData = gameRepo.getInitialData()
            updateState {
                ScreenState.Loaded(
                    tileCountRowData = ScreenState.Loaded.RowData(
                        initialCount = initialData.tilesCount,
                        maxCount = TilesMaxValue,
                        selectableRange = SelectableTilesMinValue..SelectableTilesMaxValue,
                    ),
                    fillCountRowData = ScreenState.Loaded.RowData(
                        initialCount = initialData.colorsCount,
                        maxCount = ColorsMaxValue,
                        selectableRange = SelectableColorsMinValue..SelectableColorsMaxValue,
                    ),
                    fills = ItemFill.getFirstN(ColorsMaxValue)
                )
            }
        }
    }
}