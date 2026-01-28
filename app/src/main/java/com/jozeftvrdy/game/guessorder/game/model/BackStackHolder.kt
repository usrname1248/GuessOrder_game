package com.jozeftvrdy.game.guessorder.game.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

class BackStackHolder(startDestination: NavKey) {
    private val _backstack : SnapshotStateList<NavKey> = mutableStateListOf(startDestination)
    val backstack: List<NavKey>
        get() = _backstack

    fun goTo(destination: NavKey){
        _backstack.add(destination)
    }

    fun goBack(){
        _backstack.removeLastOrNull()
    }

    fun removeLastUntil(
        stopCondition: (backstack: MutableList<NavKey>) -> Boolean
    ) {
        do {
            // removeLast causes java.lang.NoSuchMethodError exception
//            _backstack.removeLast()
            _backstack.removeAt(index = _backstack.lastIndex)
        } while (!stopCondition(_backstack))
    }
}