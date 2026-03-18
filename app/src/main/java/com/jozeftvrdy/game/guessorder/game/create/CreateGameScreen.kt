package com.jozeftvrdy.game.guessorder.game.create

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jozeftvrdy.game.guessorder.R
import com.jozeftvrdy.game.guessorder.extension.listenToEffects
import com.jozeftvrdy.game.guessorder.extension.rememberFunction
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.ui.components.FullScreenProgressIndicator
import com.jozeftvrdy.game.guessorder.ui.components.LocalSharedElementsModifierProvider
import com.jozeftvrdy.game.guessorder.ui.dimens.screenDimens
import com.jozeftvrdy.game.guessorder.ui.provider.ColorProvider
import kotlinx.collections.immutable.toPersistentList
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

object PickedValueSaver: Saver<MutableState<PickedValue>, Int> {
    override fun SaverScope.save(value: MutableState<PickedValue>): Int = value.value.floatValue.fastRoundToInt()

    override fun restore(value: Int): MutableState<PickedValue> = mutableStateOf(
        PickedValue(
            floatValue = value.toFloat(),
            source = PickedValueSource.Init,
        )
    )
}

@Composable
fun CreateGameScreen(
    onPrimaryBtnClick: (InitialGameData) -> Unit,
    viewModel: CreateGameScreenViewModel = koinViewModel(),
    colorProvider: ColorProvider = koinInject()
) {
    listenToEffects(viewModel.effect) { effect ->
        when (effect) {
            is ScreenEffect.NavigateToGame -> onPrimaryBtnClick(effect.gameData)
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CreateGameScreenAnyContent(
            screenState = state,
            onPrimaryBtnClick = viewModel.rememberFunction(viewModel::eventOnPrimaryBtnClick),
            colorProvider = colorProvider,
        )
    }
}

@Composable
fun CreateGameScreenAnyContent(
    screenState: State<ScreenState>,
    onPrimaryBtnClick: (InitialGameData) -> Unit,
    colorProvider: ColorProvider,
) {
    CreateGameScreenContent(
        screenState = screenState.value,
        onPrimaryBtnClick = onPrimaryBtnClick,
        colorProvider = colorProvider,
    )
}

@Composable
fun CreateGameScreenContent(
    screenState: ScreenState,
    onPrimaryBtnClick: (InitialGameData) -> Unit,
    colorProvider: ColorProvider,
) {
    AnimatedContent(screenState) { animatedState ->
        when (animatedState) {
            ScreenState.Loading -> CreateGameScreenLoadingContent()
            is ScreenState.Loaded -> CreateGameScreenDataContent(
                loadedState = animatedState,
                onPrimaryBtnClick = onPrimaryBtnClick,
                colorProvider = colorProvider,
            )
        }
    }
}

@Composable
fun CreateGameScreenLoadingContent(
) {
    FullScreenProgressIndicator()
}

@Composable
fun CreateGameScreenDataContent(
    loadedState: ScreenState.Loaded,
    onPrimaryBtnClick: (InitialGameData) -> Unit,
    colorProvider: ColorProvider,
) {
    val dimens = createGameDimens

    // Tiles states
    val numberOfTilesState = rememberSaveable(
        loadedState.tileCountRowData.initialCount,
        saver = PickedValueSaver
    ) {
        mutableStateOf(
            PickedValue(
                floatValue = loadedState.tileCountRowData.initialCount.toFloat(),
                source = PickedValueSource.Init,
            )
        )
    }

    val isTilesCounterClickedState = remember {
        mutableStateOf(false)
    }

    val tilesCounterOffsetState = remember {
        mutableStateOf(IntOffset(0, 0))
    }

    val tilesCounterSizeState = remember {
        mutableStateOf(IntSize.Zero)
    }

    val tilesValues = remember(loadedState.tileCountRowData.maxCount) {
        List(loadedState.tileCountRowData.maxCount) { index ->
            index + 1
        }.toPersistentList()
    }

    // Color states
    val numberOfColorsState = rememberSaveable(
        loadedState.fillCountRowData.initialCount,
        saver = PickedValueSaver
    ) {
        mutableStateOf(
            PickedValue(
                floatValue = loadedState.fillCountRowData.initialCount.toFloat(),
                source = PickedValueSource.Init,
            )
        )
    }

    val isColorsCounterClickedState = remember {
        mutableStateOf(false)
    }

    val colorsCounterOffsetState = remember {
        mutableStateOf(IntOffset(0, 0))
    }

    val colorsCounterSizeState = remember {
        mutableStateOf(IntSize.Zero)
    }

    val colorsValues = remember(loadedState.fillCountRowData.maxCount) {
        List(loadedState.fillCountRowData.maxCount) { index ->
            index + 1
        }.toPersistentList()
    }

    // Parent states
    val parentSizeState = remember {
        mutableStateOf(IntSize.Zero)
    }
    val parentOffset = remember {
        mutableStateOf(IntOffset.Zero)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                parentSizeState.value = it
            }
            .onPlaced { layoutCoordinates ->
                parentOffset.value = layoutCoordinates.positionInRoot().let {
                    IntOffset(it.x.roundToInt(), it.y.roundToInt())
                }
            }
    ) {
        CounterOverlay(
            location = tilesCounterOffsetState.value.let {
                IntOffset(
                    it.x - parentOffset.value.x,
                    it.y - parentOffset.value.y
                )
            },
            itemSize = tilesCounterSizeState.value,
            parentSize = parentSizeState.value,
            numberOfTilesState,
            isTilesCounterClickedState,
            items = tilesValues,
            modifier = Modifier.zIndex(3f),
        )

        CounterOverlay(
            location = colorsCounterOffsetState.value.let {
                IntOffset(
                    it.x - parentOffset.value.x,
                    it.y - parentOffset.value.y
                )
            },
            itemSize = colorsCounterSizeState.value,
            parentSize = parentSizeState.value,
            numberOfColorsState,
            isColorsCounterClickedState,
            items = colorsValues,
            modifier = Modifier.zIndex(3f),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenDimens.screenContentPadding)
        ) {

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxHeight()
                    .zIndex(1f),

            ) {

                Spacer(modifier = Modifier.weight(1f))
                Description(R.string.create_game_screen_desc)


                Spacer(modifier = Modifier.weight(0.2f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Counter(
                        pickedValueState = numberOfTilesState,
                        isClicked = isTilesCounterClickedState,
                        items = tilesValues,
                        onPlaced = { layoutCoordinates ->
                            tilesCounterOffsetState.value = layoutCoordinates.positionInRoot().let {
                                IntOffset(it.x.roundToInt(), it.y.roundToInt())
                            }
                            tilesCounterSizeState.value = layoutCoordinates.size
                        },
                        modifier = Modifier
                            .zIndex(2f),
                    )

                    com.jozeftvrdy.game.guessorder.extension.Spacer(12)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        com.jozeftvrdy.game.guessorder.extension.Spacer(12)

                        TilesRowWithProgress(
                            tilesCount = loadedState.tileCountRowData.maxCount,
                            dimens.tileSize,
                            numberOfTilesState,
                            getIsSelected = remember {
                                { index ->
                                    val isSelected by derivedStateOf {
                                        numberOfTilesState.value.floatValue >= index + 1
                                    }
                                    isSelected
                                }
                            },
                            getIsEnabled = remember(loadedState.tileCountRowData.selectableRange) {{ index ->
                                loadedState.tileCountRowData.selectableRange.contains(index + 1)
                            }},
                            onTileClick = remember(numberOfTilesState, (loadedState.tileCountRowData.selectableRange)) {
                                { index ->
                                    numberOfTilesState.value = PickedValue(
                                        (index + 1).coerceIn(loadedState.tileCountRowData.selectableRange).toFloat(),
                                        PickedValueSource.TileClick
                                    )
                                }
                            },
                            getTileItemContent = remember { { { } } },
                        )

                        com.jozeftvrdy.game.guessorder.extension.Spacer(12)
                    }
                }
                Spacer(modifier = Modifier.weight(0.2f))

                Description(R.string.create_game_screen_desc2)
                Spacer(modifier = Modifier.weight(0.2f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Counter(
                        pickedValueState = numberOfColorsState,
                        isClicked = isColorsCounterClickedState,
                        items = colorsValues,
                        onPlaced = { layoutCoordinates ->
                            colorsCounterOffsetState.value = layoutCoordinates.positionInRoot().let {
                                IntOffset(it.x.roundToInt(), it.y.roundToInt())
                            }
                            colorsCounterSizeState.value = layoutCoordinates.size
                        },
                        modifier = Modifier
                            .zIndex(2f),
                    )

                    com.jozeftvrdy.game.guessorder.extension.Spacer(12)
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                    ) {
                        com.jozeftvrdy.game.guessorder.extension.Spacer(12)

                        TilesRowWithProgress(
                            tilesCount = loadedState.fillCountRowData.maxCount,
                            tileSize = dimens.tileSize,
                            numberOfColorsState,
                            getIsSelected = remember {
                                { index ->
                                    val isSelected by derivedStateOf {
                                        numberOfColorsState.value.floatValue >= (index + 1)
                                    }
                                    isSelected
                                }
                            },
                            getIsEnabled = remember(loadedState.fillCountRowData.selectableRange) {
                                { index ->
                                    loadedState.fillCountRowData.selectableRange.contains(index + 1)
                                }
                            },
                            onTileClick = remember(numberOfColorsState, loadedState.fillCountRowData.selectableRange) {
                                { index ->
                                    numberOfColorsState.value = PickedValue(
                                        (index + 1).coerceIn(loadedState.fillCountRowData.selectableRange).toFloat(),
                                        PickedValueSource.TileClick
                                    )
                                }
                            },
                            getTileItemContent = remember(loadedState.fills, colorProvider) {
                                { index ->
                                    val fill = loadedState.fills[index]
                                    {
                                        val isDarkTheme = isSystemInDarkTheme()
                                        val color = colorProvider.provideColorValue(fill, isDarkTheme)

                                        TileFill(
                                            modifier = LocalSharedElementsModifierProvider.current.createModifierForPickedColorRow(index).size(dimens.tileSize.times(TileFillFraction)),
                                            color = color
                                        )
                                    }
                                }
                            }
                        )
                        com.jozeftvrdy.game.guessorder.extension.Spacer(12)
                    }
                }
                Spacer(modifier = Modifier.weight(0.2f))

                Spacer(modifier = Modifier.weight(1f))
                ConfirmButton(
                    onClick = remember(onPrimaryBtnClick, numberOfTilesState, numberOfColorsState) {
                        {
                            onPrimaryBtnClick(InitialGameData(
                                colorsCount = numberOfColorsState.value.floatValue.fastRoundToInt(),
                                tilesCount = numberOfTilesState.value.floatValue.fastRoundToInt(),
                            ))
                        }
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun Description(
    resId: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        stringResource(resId),
        modifier = modifier.
            padding(horizontal = 24.dp)
    )
}

@Composable
private fun ConfirmButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        elevation = null,
    ) {
        Text(stringResource(R.string.create_game_screen_main_button))
    }
}