package com.jozeftvrdy.game.guessorder.game.create

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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
import com.jozeftvrdy.game.guessorder.ui.dimens.screenDimens
import com.jozeftvrdy.game.guessorder.ui.theme.tileColors
import kotlinx.collections.immutable.toPersistentList
import org.koin.androidx.compose.koinViewModel
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
    viewModel: CreateGameScreenViewModel = koinViewModel()
) {
    listenToEffects(viewModel.effect) { effect ->
        when (effect) {
            is ScreenEffect.NavigateToGame -> onPrimaryBtnClick(effect.gameData)
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { scaffoldPadding ->
        CreateGameScreenAnyContent(
            scaffoldPadding = scaffoldPadding,
            screenState = state,
            onPrimaryBtnClick = viewModel.rememberFunction(viewModel::eventOnPrimaryBtnClick)
        )
    }
}

@Composable
fun CreateGameScreenAnyContent(
    scaffoldPadding: PaddingValues,
    screenState: State<ScreenState>,
    onPrimaryBtnClick: (InitialGameData) -> Unit
) {
    CreateGameScreenContent(
        scaffoldPadding = scaffoldPadding,
        screenState = screenState.value,
        onPrimaryBtnClick = onPrimaryBtnClick,
    )
}

@Composable
fun CreateGameScreenContent(
    scaffoldPadding: PaddingValues,
    screenState: ScreenState,
    onPrimaryBtnClick: (InitialGameData) -> Unit
) {
    AnimatedContent(screenState) { animatedState ->
        when (animatedState) {
            ScreenState.Loading -> CreateGameScreenLoadingContent(
                scaffoldPadding = scaffoldPadding
            )
            is ScreenState.Loaded -> CreateGameScreenDataContent(
                scaffoldPadding = scaffoldPadding,
                loadedState = animatedState,
                onPrimaryBtnClick = onPrimaryBtnClick,
            )
        }
    }
}

@Composable
fun CreateGameScreenLoadingContent(
    scaffoldPadding: PaddingValues
) {
    FullScreenProgressIndicator(
        modifier = Modifier
            .padding(scaffoldPadding)
    )
}

@Composable
fun CreateGameScreenDataContent(
    scaffoldPadding: PaddingValues,
    loadedState: ScreenState.Loaded,
    onPrimaryBtnClick: (InitialGameData) -> Unit,
) {
    val colorsList = tileColors
    val isTileSelected: (tileValue: Int, pickedValue: PickedValue) -> Boolean = remember {
        { tileValue, pickedValue ->
            pickedValue.floatValue >= tileValue
        }
    }

    // Tiles states
    val numberOfTilesState = rememberSaveable(
        saver = PickedValueSaver
    ) {
        mutableStateOf(
            PickedValue(
                floatValue = loadedState.savedInitialGameData.tilesCount.toFloat(),
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

    val tilesValues = remember {
        loadedState.tilesValueRange.toPersistentList()
    }


    // Color states
    val numberOfColorsState = rememberSaveable(
        saver = PickedValueSaver
    ) {
        mutableStateOf(
            PickedValue(
                floatValue = loadedState.savedInitialGameData.colorsCount.toFloat(),
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

    val colorsValues = remember {
        loadedState.colorsValueRange.toPersistentList()
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
                .padding(scaffoldPadding)
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

                    com.jozeftvrdy.game.guessorder.extension.Spacer(24)

                    TilesRow(
                        numberOfTilesState,
                        pickedValueRange = loadedState.tilesValueRange,
                        getColor = remember { { null } },
                        getIsSelected = isTileSelected,
                        onTileClick = remember(numberOfTilesState) {
                            { tileValue ->
                                numberOfTilesState.value = PickedValue(
                                    tileValue.toFloat(),
                                    PickedValueSource.TileClick
                                )
                            }
                        }
                    )
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

                    com.jozeftvrdy.game.guessorder.extension.Spacer(24)

                    TilesRow(
                        numberOfColorsState,
                        pickedValueRange = loadedState.colorsValueRange,
                        getColor = remember(loadedState, colorsList) {
                            {
                                colorsList[it]
                            }
                        },
                        getIsSelected = isTileSelected,
                        onTileClick = remember(numberOfColorsState) {
                            {
                                numberOfColorsState.value = PickedValue(
                                    it.toFloat(),
                                    PickedValueSource.TileClick
                                )
                            }
                        }
                    )
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
        Text("Create")
    }
}