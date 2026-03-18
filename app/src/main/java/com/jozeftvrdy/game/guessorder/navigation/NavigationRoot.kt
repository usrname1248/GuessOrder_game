package com.jozeftvrdy.game.guessorder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jozeftvrdy.game.guessorder.game.model.BackStackHolder
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data object CreateGameNavScreen : NavKey

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class GameNavScreen(
    val initData: InitialGameData,
    val gameId: Uuid = Uuid.random()
): NavKey

@Serializable
data class SuccessGameNavScreen(
    val usedData: InitialGameData,
    val successResult: ImmutableList<ItemFill>,
    val playedTimeMillis: Long,
): NavKey

@OptIn(KoinExperimentalAPI::class)
@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    backStackHolder: BackStackHolder,
) {
    NavDisplay(
        modifier = modifier,
        backStack = backStackHolder.backstack,
        onBack = {
            backStackHolder.goBack()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = koinEntryProvider()
    )
}