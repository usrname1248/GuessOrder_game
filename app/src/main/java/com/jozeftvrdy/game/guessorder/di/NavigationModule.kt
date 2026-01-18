package com.jozeftvrdy.game.guessorder.di

import androidx.navigation3.runtime.NavKey
import com.jozeftvrdy.game.guessorder.game.create.CreateGameScreen
import com.jozeftvrdy.game.guessorder.game.model.BackStackHolder
import com.jozeftvrdy.game.guessorder.game.play.PlayGameScreen
import com.jozeftvrdy.game.guessorder.navigation.CreateGameNavScreen
import com.jozeftvrdy.game.guessorder.navigation.GameNavScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {

    single { (startDestination: NavKey) ->
        BackStackHolder(startDestination)
    }

    navigation<CreateGameNavScreen> {
        CreateGameScreen(
            onPrimaryBtnClick = {
                get<BackStackHolder>().goTo(GameNavScreen(it))
            }
        )
    }

    navigation<GameNavScreen> { route ->
        PlayGameScreen(
            initialGameData = route.initData,
            onGameFinish = {
                get<BackStackHolder>().goBack()
            }
        )
    }
}