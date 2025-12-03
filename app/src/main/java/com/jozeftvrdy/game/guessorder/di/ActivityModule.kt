package com.jozeftvrdy.game.guessorder.di

import com.jozeftvrdy.game.guessorder.MainActivity
import org.koin.dsl.module

val activityModule = module {
    scope<MainActivity> {
    }
}