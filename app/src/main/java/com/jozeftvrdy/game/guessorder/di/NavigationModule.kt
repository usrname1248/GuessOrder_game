package com.jozeftvrdy.game.guessorder.di

import androidx.navigation3.runtime.NavKey
import com.jozeftvrdy.game.guessorder.game.SuccessGameScreen
import com.jozeftvrdy.game.guessorder.game.create.CreateGameScreen
import com.jozeftvrdy.game.guessorder.game.model.BackStackHolder
import com.jozeftvrdy.game.guessorder.game.play.PlayGameScreen
import com.jozeftvrdy.game.guessorder.navigation.CreateGameNavScreen
import com.jozeftvrdy.game.guessorder.navigation.GameNavScreen
import com.jozeftvrdy.game.guessorder.navigation.SuccessGameNavScreen
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import kotlin.uuid.ExperimentalUuidApi

@OptIn(KoinExperimentalAPI::class, ExperimentalUuidApi::class)
val navigationModule = module {

    single { (startDestination: NavKey) ->
        BackStackHolder(startDestination)
    }

    navigation<CreateGameNavScreen> {
        CreateGameScreen(
            onPrimaryBtnClick = {
                get<BackStackHolder>().goTo(
                    GameNavScreen(
                        initData = it,
                    ),
                )
            }
        )
    }

    navigation<GameNavScreen> { route ->
        PlayGameScreen(
            initialGameData = route.initData,
            onGameFinish = { successResult, playedTimeMillis ->
                get<BackStackHolder>().goTo(SuccessGameNavScreen(
                    route.initData,
                    successResult,
                        playedTimeMillis,
                ))
            }
        )
    }

    navigation<SuccessGameNavScreen> { route ->
        SuccessGameScreen(
            initialGameData = route.usedData,
            resultCombination = route.successResult,
            playedTimeMillis = route.playedTimeMillis,
            navigateToMainGameScreen = { usedData ->
                get<BackStackHolder>().apply {
                    removeLastUntil { backstack ->
                        backstack.last() is CreateGameNavScreen
                    }
                    goTo(GameNavScreen(usedData))
                }
            },
            colorProvider = koinInject(),
            navigateToCreateGameScreen = {
                get<BackStackHolder>().apply {
                    removeLastUntil { backstack ->
                        backstack.last() is CreateGameNavScreen
                    }
                }
            }
        )
    }
}