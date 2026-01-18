package com.jozeftvrdy.game.guessorder.base

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val savedUiStateKey = "UiStateKey"

abstract class BaseViewModel<State: Parcelable, Effect>(
) : ViewModel() {

    protected abstract val savedStateHandle: SavedStateHandle

    abstract val initialState: State

    private val _state : MutableStateFlow<State>
        get() =
        savedStateHandle.getMutableStateFlow(
            savedUiStateKey,
            initialState
        )
    val state: StateFlow<State>
        get() = _state
        .onStart {
            onStateObserved()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    protected fun updateState(updateCallback: (oldState: State) -> State) {
        _state.update(updateCallback)
    }

    protected fun newState(newState: State) {
        _state.value = newState
    }

    open fun onStateObserved() {

    }
}