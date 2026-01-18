package com.jozeftvrdy.game.guessorder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jozeftvrdy.game.guessorder.game.model.BackStackHolder
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import kotlinx.serialization.Serializable
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@Serializable
data object CreateGameNavScreen : NavKey

@Serializable
data class GameNavScreen(
    val initData: InitialGameData
): NavKey

@OptIn(KoinExperimentalAPI::class)
@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    backStackHolder: BackStackHolder,
) {
    NavDisplay(
        modifier = modifier,
        backStack = backStackHolder.backStack,
        onBack = {
            backStackHolder.goBack()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = koinEntryProvider()
    )
}