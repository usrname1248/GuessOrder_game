package com.jozeftvrdy.game.guessorder.game.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

class BackStackHolder(startDestination: NavKey) {
    val backStack : SnapshotStateList<NavKey> = mutableStateListOf(startDestination)

    fun goTo(destination: NavKey){
        backStack.add(destination)
    }

    fun goBack(){
        backStack.removeLastOrNull()
    }
}