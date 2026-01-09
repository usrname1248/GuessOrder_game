package com.jozeftvrdy.game.guessorder.extension

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun ViewModel.rememberFunction(function: () -> Unit) : () -> Unit {
    return remember(this) {
        function
    }
}

@Composable
fun <T: Any> ViewModel.rememberFunction(function: (T) -> Unit) : (T) -> Unit {
    return remember(this) {
        function
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> listenToEffects(effectFlow: Flow<T>, onEffect: (T) -> Unit) {
    LaunchedEffect(effectFlow, onEffect) {
        effectFlow.collect(onEffect)
    }
}
