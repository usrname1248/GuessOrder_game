package com.jozeftvrdy.game.guessorder.game.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.repository.GameRepository
import kotlinx.coroutines.launch

class CreateGameScreenViewModel(
    private val gameRepo: GameRepository
): ViewModel() {
    fun onClick(gameData: InitialGameData) {
        viewModelScope.launch {
            gameRepo.saveInitialData(gameData)
        }
    }
}