package com.jozeftvrdy.game.guessorder.game.play

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class PlayGameAnimator(
    val initialGameData: InitialGameData,
    savedStateHandle: SavedStateHandle,
    vmScope: CoroutineScope,
) {

    val animationScope = vmScope

    private val emptyArray: Array<ItemFill?>
        get() = Array(initialGameData.tilesCount) {
            null
        }

    @OptIn(SavedStateHandleSaveableApi::class)
    private val futureCurrentGuess: MutableList<ItemFill?> by savedStateHandle.saveable(
        saver = listSaver(
            save = { it },
            restore = { it.toMutableStateList() },
        )
    ) {
        mutableStateListOf(*emptyArray)
    }

    private val animatedCurrentGuess: SnapshotStateList<ItemFill?> = mutableStateListOf<ItemFill?>().also {
        it.addAll(futureCurrentGuess)
    }

    val currentGuess: List<ItemFill?>
        get() = animatedCurrentGuess


    fun modifyCurrentGuessAnimated(
        modify: Pair<Int, ItemFill?>
    ) = modifyCurrentGuessAnimated(listOf(modify))
    fun modifyCurrentGuessAnimated(
        modify: List<Pair<Int, ItemFill?>>
    ) {
        modify.forEach { (index, fill) ->
            futureCurrentGuess[index] = fill
        }
        animateIndexes(modify)
    }

    private fun animateIndexes(modify: List<Pair<Int, ItemFill?>>) {
        animationScope.launch {
            repeat(modify.size) { index ->
                if (index != 0) {
                    delay(100)
                }
                animateIndex(modify[index].first, modify[index].second)
            }
        }
    }

    private fun animateIndex(index: Int, fill: ItemFill?) {
        animatedCurrentGuess[index] = fill
    }
}