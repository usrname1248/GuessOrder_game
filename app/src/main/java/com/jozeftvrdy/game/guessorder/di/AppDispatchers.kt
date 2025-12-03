package com.jozeftvrdy.game.guessorder.di

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {
    val IO: CoroutineDispatcher
    val Default: CoroutineDispatcher
    val Main: CoroutineDispatcher
}