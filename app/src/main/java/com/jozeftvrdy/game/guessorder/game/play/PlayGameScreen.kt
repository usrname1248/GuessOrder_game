package com.jozeftvrdy.game.guessorder.game.play

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.DragAndDropSourceScope
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jozeftvrdy.game.guessorder.R
import com.jozeftvrdy.game.guessorder.extension.Spacer
import com.jozeftvrdy.game.guessorder.extension.listenToEffects
import com.jozeftvrdy.game.guessorder.extension.rememberFunction
import com.jozeftvrdy.game.guessorder.game.create.TileFill
import com.jozeftvrdy.game.guessorder.game.create.TilesRow
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.ui.components.ScrollableContentIndication
import com.jozeftvrdy.game.guessorder.ui.provider.ColorProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val mergeTilesDelay = 500L
private const val dragDropBundleKey = "FillItemTypeName"

@Composable
fun PlayGameScreen(
    initialGameData: InitialGameData,
    viewModel: PlayGameViewModel = koinViewModel {
        parametersOf(initialGameData)
    },
    colorProvider: ColorProvider = koinInject(),
    onGameFinish: () -> Unit,
) {
    listenToEffects(viewModel, onGameFinish)

    val state by viewModel.state.collectAsStateWithLifecycle()

    PlayGameContent(
        state = state,
        getColor = remember(colorProvider) {
            colorProvider::provideColorValue
        },
        getCurrentGuess = remember(viewModel) {
            {
                viewModel.currentGuess
            }
        },
        onMainBtnClicked = viewModel.rememberFunction(viewModel::eventOnButtonClicked)
    )
}

@Composable
fun PlayGameContent(
    state: ScreenState,
    getColor: (ItemFill, isDarkTheme: Boolean) -> Color,
    getCurrentGuess: () -> MutableList<ItemFill?>,
    onMainBtnClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        HistoryComponent(
            modifier = Modifier
                .weight(1f),
            historyItems = state.history,
            getColor = getColor,
        )
        PlayingAreaComponent(
            initialGameData = state.initialGameData,
            getColor = getColor,
            getCurrentGuess = getCurrentGuess,
            onMainBtnClicked = onMainBtnClicked,
        )
    }
}

@Composable
private fun HistoryComponent(
    modifier: Modifier = Modifier,
    historyItems: HistoryItems,
    getColor: (ItemFill, isDarkTheme: Boolean) -> Color,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        repeat(historyItems.size) { index ->
            HistoryItem(
                historyItems[index],
                getColor = getColor,
            )
        }
    }
}

@Composable
private fun PlayingAreaComponent(
    initialGameData: InitialGameData,
    getColor: (ItemFill, isDarkTheme: Boolean) -> Color,
    getCurrentGuess: () -> MutableList<ItemFill?>,
    onMainBtnClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlayingArea(
        modifier = modifier
    ) { bgColor ->
        PlayingAreaContent(
            initialGameData = initialGameData,
            bgColor = bgColor,
            getColor = getColor,
            getCurrentGuess = getCurrentGuess,
            onMainBtnClicked = onMainBtnClicked,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlayingAreaContent(
    initialGameData: InitialGameData,
    bgColor: Color,
    getColor: (ItemFill, isDarkTheme: Boolean) -> Color,
    getCurrentGuess: () -> MutableList<ItemFill?>,
    onMainBtnClicked: () -> Unit,
) {
    val fixedFill = remember (initialGameData.colorsCount) {
        ItemFill.getFirstN(initialGameData.colorsCount).toImmutableList()
    }
    val selectedFixedTileIndexState : MutableState<Int?> = remember {
        mutableStateOf(null)
    }

    val selectedMutableTileIndexState : MutableState<Int?> = remember {
        mutableStateOf(null)
    }

    listenToIsSelectedStates(
        getCurrentGuess = getCurrentGuess,
        selectedFixedTileIndexState = selectedFixedTileIndexState,
        selectedMutableTileIndexState = selectedMutableTileIndexState,
        fixedFill = fixedFill
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(24)

        PlayableAreaButton(
            onClick = remember(getCurrentGuess, selectedFixedTileIndexState, selectedMutableTileIndexState) {
                {
                    getCurrentGuess().replaceAll {
                        null
                    }

                    selectedFixedTileIndexState.value = null
                    selectedMutableTileIndexState.value = null
                }
            },
            text = stringResource(R.string.play_game_screen_secondary_button_reset)
        )

        Spacer(8)

        val scrollState = rememberScrollState()
        ScrollableContentIndication(
            modifier = Modifier.weight(1f, fill = false),
            scrollOrientation = Orientation.Horizontal,
            scrollState = scrollState,
            backgroundColor = bgColor,
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
            ) {
                Spacer(8)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Mutable Row
                    PlayingAreaTilesRow(
                        getCurrentGuess(),
                        selectedIndexState = selectedMutableTileIndexState,
                        getColor = getColor,
                        dragAndDropTileType = DragAndDropTileType.Receive,
                        onTap = remember(selectedMutableTileIndexState) {
                            { index ->
                                val isSelected = selectedMutableTileIndexState.value == index
                                selectedMutableTileIndexState.value = if (isSelected) {
                                    null
                                } else {
                                    index
                                }
                            }
                        },
                        onDoubleTap = remember(getCurrentGuess) {
                            { index ->
                                getCurrentGuess()[index] = null
                            }
                        },
                        onDataReceived = remember {
                            { index, fill ->
                                val currentGuess = getCurrentGuess()
                                currentGuess[index] = fill
                            }
                        },
                    )

                    Spacer(12)


                    // Fixed row
                    PlayingAreaTilesRow(
                        fixedFill,
                        selectedIndexState = selectedFixedTileIndexState,
                        getColor = getColor,
                        dragAndDropTileType = DragAndDropTileType.Send,
                        onTap = remember(selectedFixedTileIndexState) {
                            { index ->
                                val isSelected = selectedFixedTileIndexState.value == index
                                selectedFixedTileIndexState.value = if (isSelected) {
                                    null
                                } else {
                                    index
                                }
                            }
                        },
                        onDoubleTap = remember(getCurrentGuess, fixedFill) {
                            fillFirstEmptyTile@{ index ->
                                val currentGuess = getCurrentGuess()
                                val fill = fixedFill[index]
                                val firstEmptyIndex = currentGuess.indexOfFirst { fill -> fill == null }
                                if (firstEmptyIndex < 0) {
                                    return@fillFirstEmptyTile
                                }
                                currentGuess[firstEmptyIndex] = fill
                            }
                        },
                        onDataReceived = remember {
                            { index, fill ->
                                assert(false) // sender cannot receive data, but we keep backup
                                val currentGuess = getCurrentGuess()
                                currentGuess[index] = fill
                            }
                        },
                    )
                }

                Spacer(8)
            }
        }


        Spacer(8)

        PlayableAreaButton(
            onClick = onMainBtnClicked,
            text = stringResource(R.string.play_game_screen_main_button)
        )
        Spacer(24)
    }
}

@Composable
fun PlayableAreaButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text(
            text,
            modifier = Modifier.width(intrinsicSize = IntrinsicSize.Min),
            textAlign = TextAlign.Center
        )
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun listenToIsSelectedStates(
    getCurrentGuess: () -> MutableList<ItemFill?>,
    selectedFixedTileIndexState: MutableState<Int?>,
    selectedMutableTileIndexState: MutableState<Int?>,
    fixedFill: ImmutableList<ItemFill>,
) {
    val coroutineScope = rememberCoroutineScope()
    var unSelectJob : Job? = remember {
        null
    }

    DisposableEffect(getCurrentGuess, selectedFixedTileIndexState.value, selectedMutableTileIndexState.value, fixedFill) {

        unSelectJob?.cancel()
        unSelectJob = coroutineScope.launch {
            delay(mergeTilesDelay)
            ensureActive()
            val selectedFixedRowIndex = selectedFixedTileIndexState.value
            val selectedMutableRowIndex = selectedMutableTileIndexState.value
            if (selectedFixedRowIndex != null && selectedMutableRowIndex != null) {
                val selectedFixedColor = fixedFill[selectedFixedRowIndex]
                getCurrentGuess()[selectedMutableRowIndex] = selectedFixedColor
                selectedFixedTileIndexState.value = null
                selectedMutableTileIndexState.value = null
            }
        }

        onDispose {
            unSelectJob.cancel()
        }
    }
}

enum class DragAndDropTileType {
    Send,
    Receive,
}

@Composable
private fun PlayingAreaTilesRow(
    list: List<ItemFill?>,
    selectedIndexState: State<Int?>,
    getColor: (ItemFill, isDarkTheme: Boolean) -> Color,
    dragAndDropTileType: DragAndDropTileType,
    onTap: (Int) -> Unit,
    onDoubleTap: (Int) -> Unit,
    onDataReceived: (Int, ItemFill) -> Unit,
    modifier: Modifier = Modifier
) {
    TilesRow(
        list.size,
        tileSize = playGameDimens.playTileSize,
        modifier = Modifier
            .then(modifier),
        getIsSelected = remember(selectedIndexState) {
            { index ->
                selectedIndexState.value == index
            }
        },
        getIsEnabled = remember(dragAndDropTileType) {
            {
                when (dragAndDropTileType) {
                    DragAndDropTileType.Send -> {
                        false
                    }
                    DragAndDropTileType.Receive -> {
                        true
                    }
                }
            }
        },
        onTileClick = null,
        betweenItemsSpacer = remember {
            {
                Spacer(16.dp)
            }
        },
        getTileItemContent = remember(list, getColor) {
            { index ->
                {
                    val isDarkTheme = isSystemInDarkTheme()
                    val fill = list[index]
                    val color = fill?.let {
                        getColor(it, isDarkTheme)
                    }
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .then(
                                Modifier.toDragAndDropModifier(
                                    type = dragAndDropTileType,
                                    onTap = remember(index) {
                                        {
                                            onTap(index)
                                        }
                                    },
                                    onDoubleTap = remember(index) {
                                        {
                                            onDoubleTap(index)
                                        }
                                    },
                                    onDataReceived = remember(index) {
                                        { data ->
                                            onDataReceived(index, data)
                                        }
                                    },
                                    fill = fill
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        color?.let {
                            TileFill(
                                color = color,
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Modifier.toDragAndDropModifier(
    type: DragAndDropTileType,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onDataReceived: (ItemFill) -> Unit,
    fill: ItemFill?,
): Modifier {
    val rememberedOnTap: (Any) -> Unit = remember(onTap) {
        {
            onTap()
        }
    }
    val rememberedOnDoubleTap: (Any) -> Unit = remember(onDoubleTap) {
        {
            onDoubleTap()
        }
    }

    return when (type) {
        DragAndDropTileType.Send -> {
            val oldObsoleteDragCallback: suspend DragAndDropSourceScope.() -> Unit = {
                detectTapGestures(
                    onLongPress = {
                        fill?.let {
                            startTransfer(
                                DragAndDropTransferData(
                                    ClipData.newIntent("", Intent().apply {
                                        putExtra(dragDropBundleKey, fill.name)
                                    })
                                )
                            )
                        }
                    },
                    onTap = rememberedOnTap,
                    onDoubleTap = rememberedOnDoubleTap,
                )
            }
            this.dragAndDropSource(
                oldObsoleteDragCallback
            )
        }

        DragAndDropTileType.Receive -> {
            val callback = object : DragAndDropTarget {
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    val bundle = event.toAndroidDragEvent().clipData.getItemAt(0).intent
                    val rawDroppedValue =
                        bundle?.getStringExtra(dragDropBundleKey) ?: return false

                    val fill: ItemFill = try {
                        ItemFill.valueOf(rawDroppedValue)
                    } catch (_: IllegalArgumentException) {
                        return false
                    }

                    onDataReceived(fill)
                    return true
                }
            }

            this
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { _ ->
                        true
                    },
                    target = callback
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = rememberedOnTap,
                        onDoubleTap = rememberedOnDoubleTap
                    )
                }
        }
    }
}

@Composable
private fun HistoryItem(
    historyItem: HistoryItem,
    getColor: (ItemFill, isDarkTheme: Boolean) -> Color,
) {
    HistoryTurn(
        guessedValues = historyItem.guess,
        result = historyItem.result,
        getColor = getColor
    )
}

@Composable
@SuppressLint("ComposableNaming")
private fun listenToEffects(
    viewModel: PlayGameViewModel,
    onGameFinish: () -> Unit,
) {
    val context = LocalContext.current
    val emptyTilesMessage = stringResource(R.string.empty_tiles_confirmed_message)
    val unexpectedErrorMessage = stringResource(R.string.unexpected_error_message)
    listenToEffects(viewModel.effect) { effect ->
        when (effect) {
            ScreenEffect.NavigateToPostGame -> onGameFinish()
            ScreenEffect.ShowEmptyGuessError -> {
                Toast.makeText(
                    context,
                    emptyTilesMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
            ScreenEffect.ShowUnexpectedError -> {
                Toast.makeText(
                    context,
                    unexpectedErrorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}