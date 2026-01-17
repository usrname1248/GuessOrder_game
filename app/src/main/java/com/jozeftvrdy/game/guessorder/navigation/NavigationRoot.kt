package com.jozeftvrdy.game.guessorder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.jozeftvrdy.game.guessorder.game.create.CreateGameScreen
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.play.PlayGameScreen
import kotlinx.serialization.Serializable

@Serializable
data object CreateGameNavScreen : NavKey

data class GameNavScreen(
    val initData: InitialGameData
): NavKey

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        CreateGameNavScreen
    )

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                CreateGameNavScreen -> {
                    NavEntry(
                        key = key
                    ) {
                        CreateGameScreen(
                            onPrimaryBtnClick = {
                                backStack.add(GameNavScreen(it))
                            }
                        )
                    }
                }
                is GameNavScreen -> {
                    NavEntry(
                        key = key
                    ) {
                        PlayGameScreen(
                            initialGameData = key.initData,
                            onGameFinish = {
                                backStack.clear()
                                backStack.add(CreateGameNavScreen)
                            }
                        )
                    }
                }
                else -> error("Invalid navigation state.")
            }
        }
    )
}