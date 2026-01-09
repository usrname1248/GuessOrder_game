package com.jozeftvrdy.game.guessorder.game.create

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.scrollableArea
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jozeftvrdy.game.guessorder.extension.ifNotNull
import com.jozeftvrdy.game.guessorder.ui.dimens.createGameDimens
import com.jozeftvrdy.game.guessorder.ui.theme.GuessOrderGameTheme
import com.jozeftvrdy.game.guessorder.ui.theme.ThemePreview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun TilesRow(
    pickedValueState: MutableState<PickedValue>,
    pickedValueRange: IntRange,
    getColor: @Composable (index: Int) -> Color?,
    getIsSelected: (tileValue: Int, pickedValue: PickedValue) -> Boolean,
    onTileClick: (tileValue: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = createGameDimens
    val selectedColor = MaterialTheme.colorScheme.secondary
    val unselectedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.75f)
    val tileSpacerWeight = 0.15f

    Box(
        modifier = modifier
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        TileProgress(
            pickedValueState = pickedValueState,
            maxValue = pickedValueRange.last,
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth(fraction = run {
                    val tilesCount = pickedValueRange.last
                    val totalWeight = tilesCount + tilesCount.minus(1).times(tileSpacerWeight)
                    val desiredWeight = totalWeight - 1
                    desiredWeight.div(totalWeight)
                })
                .fillMaxHeight(0.15f)
                .align(Alignment.Center)
        )


        Row(
            modifier = modifier
                .zIndex(2f)
        ) {
            (1..pickedValueRange.last).forEachIndexed { index, value ->
                if (index > 0) {
                    Spacer(modifier = Modifier.weight(tileSpacerWeight))
                }
                val isSelectedState = if (value < pickedValueRange.first) {
                    remember(index) {
                        mutableStateOf(true)
                    }
                } else if (value > pickedValueRange.last) {
                    remember(index) {
                        mutableStateOf(false)
                    }
                } else {
                    remember(index) {
                        derivedStateOf {
                            getIsSelected(value, pickedValueState.value)
                        }
                    }
                }

                Tile(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .widthIn(min = dimens.minTileSize, max = dimens.maxTileSize)
                        .aspectRatio(1f),
                    tileColor = getColor(index),
                    isSelectedState = isSelectedState,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    onTileClick = remember(value, pickedValueRange) {
                        {
                            onTileClick.invoke(value.coerceIn(pickedValueRange))
                        }
                    }
                )
            }
        }
    }

}

@Composable
fun TileProgress(
    pickedValueState: MutableState<PickedValue>,
    maxValue: Int,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .drawBehind {
                drawRect(
                    color = unselectedColor,
                )
            }
            .drawBehind {
                drawRect(
                    color = selectedColor,
                    size = this.size.copy(
                        width = this.size.width
                            .times(
                                pickedValueState.value.floatValue - 1
                            )
                            .div(
                                maxValue - 1
                            )
                    )
                )
            }
    )
}

@Composable
fun Tile(
    tileColor: Color?,
    isSelectedState: State<Boolean>,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier,
    onTileClick: (() -> Unit)? = null,
) {
    TileStateless(
        tileColor = tileColor,
        isSelected = isSelectedState.value,
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        modifier = modifier,
        onTileClick = onTileClick
    )
}

@Composable
fun TileStateless(
    tileColor: Color?,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier,
    onTileClick: (() -> Unit)? = null,
) {
    val animationDurationMillis = 440
    val dimens = createGameDimens
    val shape = MaterialTheme.shapes.large

    val transition = updateTransition(isSelected)

    val animatedBorderColorState = transition.animateColor(
        {
            tween(animationDurationMillis)
        }
    ) { state ->
        if (state) {
            selectedColor
        } else {
            unselectedColor
        }
    }
    val animatedBorderWidthState = transition.animateDp(
        {
            tween(animationDurationMillis)
        }
    ) { state ->
        dimens.tileBorderWidth.let {
            if (state) {
                it.times(2)
            } else it
        }
    }

    Box(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer, shape = shape)
            .border(
                width = animatedBorderWidthState.value,
                color = animatedBorderColorState.value,
                shape = shape
            )
            .ifNotNull(onTileClick) {
                Modifier.clickable(onClick = it)
            },
        contentAlignment = Alignment.Center,
    ) {
        if (tileColor != null) {
            Surface(
                shape = CircleShape,
                color = tileColor,
                modifier = Modifier
                    .fillMaxSize(0.75f)
            ) {

            }
        }
    }
}

@Composable
fun Counter(
    pickedValueState: MutableState<PickedValue>,
    isClicked: MutableState<Boolean>,
    items: ImmutableList<Int>,
    onPlaced: (LayoutCoordinates) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = createGameDimens
    val density = LocalDensity.current
    val singleTilePixelSize = remember(density, dimens) {
        with(density) {
            dimens.counterSize.roundToPx()
        }
    }
    val initialScrollState = remember {
        pickedValueState.value.floatValue
            .minus(items.first())
            .times(singleTilePixelSize)
            .roundToInt()
    }
    val scrollState = rememberScrollState(
        initialScrollState
    )

    val scrollableState = rememberScrollableState {
        val scroll = it.div(singleTilePixelSize)

        var currentScrollValue = pickedValueState.value.floatValue
        val pickedValueSource = PickedValueSource.CounterScroll

        if ((currentScrollValue + scroll) < items.first()) {
            val prevValue = currentScrollValue
            currentScrollValue = items.first().toFloat()
            pickedValueState.value = PickedValue(
                currentScrollValue,
                pickedValueSource
            )
            (prevValue - items.first()).times(singleTilePixelSize)
        } else if ((currentScrollValue + scroll) > items.last()) {
            val prevValue = currentScrollValue
            currentScrollValue = items.last().toFloat()
            pickedValueState.value = PickedValue(
                currentScrollValue,
                pickedValueSource
            )
            (items.first() - prevValue).times(singleTilePixelSize)
        } else {
            pickedValueState.value = PickedValue(
                currentScrollValue + scroll,
                pickedValueSource
            )
            it
        }
    }

    LaunchedEffect(pickedValueState.value) {
        val pickedValue = pickedValueState.value
        val intY = pickedValue.floatValue
            .minus(items.first())
            .times(singleTilePixelSize).roundToInt()
        launch {
            val animate = when (pickedValue.source) {
                PickedValueSource.TileClick -> true
                PickedValueSource.CounterScroll,
                PickedValueSource.Init -> false
            }

            if (animate) {
                scrollState.animateScrollTo(
                    intY
                )
            } else {
                scrollState.scrollTo(intY)
            }
        }
    }

    val animationScope = rememberCoroutineScope()
    val animationJobState = remember {
        mutableStateOf<Job?>(null)
    }

    LaunchedEffect(scrollableState.isScrollInProgress) {
        if (!scrollableState.isScrollInProgress) {
            val startValue = pickedValueState.value.floatValue
            val targetValue = startValue.roundToInt().toFloat()

            if (startValue != targetValue) {
                animationJobState.value = animationScope.launch {
                    animate(startValue, targetValue) { currentValue, _ ->
                        pickedValueState.value = PickedValue(
                            currentValue,
                            PickedValueSource.CounterScroll
                        )
                    }
                }
            }
        }
    }

    Column(
        modifier
            .onPlaced(onPlaced)
            .scrollableArea(
                state = scrollableState,
                orientation = Orientation.Vertical,
            )
            .pointerInput("") {
                awaitEachGesture {
                    while (true) {
                        awaitPointerEvent().let {
                            when (it.type) {
                                PointerEventType.Press -> isClicked.value = true
                                PointerEventType.Release -> isClicked.value = false
                            }
                        }
                    }
                }
            }
            .width(dimens.counterSize)
            .height(dimens.counterSize)
            .background(
                brush = Brush.verticalGradient(
                    0f to MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                    0.25f to MaterialTheme.colorScheme.primaryContainer,
                    0.75f to MaterialTheme.colorScheme.primaryContainer,
                    1f to MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                ),
                shape = MaterialTheme.shapes.medium,
            )
            .verticalScroll(
                scrollState,
                enabled = false
            )
    ) {
        repeat(items.size) {
            CounterItem(
                items[it].toString()
            )
        }
    }
}

@Composable
fun CounterOverlay(
    location: IntOffset,
    itemSize: IntSize,
    parentSize: IntSize,
    pickedValueState: MutableState<PickedValue>,
    isClickedState: MutableState<Boolean>,
    items: ImmutableList<Int>,
    modifier: Modifier = Modifier,
    emptyItemsCount: Int = 2
) {
    val dimens = createGameDimens
    val density = LocalDensity.current
    val singleTilePixelSize = remember(density, dimens) {
        with(density) {
            dimens.counterSize.roundToPx()
        }
    }

    val topAdditionalSize = min(singleTilePixelSize.times(emptyItemsCount), location.y)
    val bottomAdditionalSize = min(
        singleTilePixelSize.times(emptyItemsCount),
        parentSize.height - (location.y + itemSize.height)
    )
    val fullSizeFloat = topAdditionalSize + singleTilePixelSize + bottomAdditionalSize.toFloat()

    val fakeScrollState = rememberScrollState(
        0
    )
    val backgroundColor = MaterialTheme.colorScheme.background

    LaunchedEffect(pickedValueState.value.floatValue, topAdditionalSize, emptyItemsCount) {
        val intY = (
                (pickedValueState.value.floatValue + emptyItemsCount)
                    .minus(items.first())
                    .times(singleTilePixelSize) - topAdditionalSize).roundToInt()
        launch {
            fakeScrollState.scrollTo(intY)
        }
    }

    Box(
        modifier = modifier
            .size(
                width = with(density) { itemSize.width.toDp() },
                height = with(density) { (itemSize.height + topAdditionalSize + bottomAdditionalSize).toDp() }
            )
            .offset {
                IntOffset(
                    x = location.x,
                    y = (location.y - topAdditionalSize)
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = isClickedState.value,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically) {
                it.div(8)
            },
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically) {
                it.div(8)
            },
        ) {
            Column(
                Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            0f to backgroundColor,
                            topAdditionalSize.div(fullSizeFloat) to MaterialTheme.colorScheme.primaryContainer,
                            (topAdditionalSize + singleTilePixelSize).div(fullSizeFloat) to MaterialTheme.colorScheme.primaryContainer,
                            1f to backgroundColor,
                        ),
                        shape = MaterialTheme.shapes.large,
                    )
                    .drawBehind {
                        val topF = topAdditionalSize.toFloat()
                        val bottomF = this.size.height - bottomAdditionalSize.toFloat()

                        drawRect(
                            topLeft = Offset(x = 0f, y = topF),
                            size = Size(this.size.width, bottomF - topF),
                            brush = Brush.verticalGradient(
                                0f to backgroundColor,
                                0.15f to Color.Transparent,
                                0.85f to Color.Transparent,
                                1f to backgroundColor,
                                startY = topF,
                                endY = bottomF
                            ),
                        )
                    }
                    .verticalScroll(
                        fakeScrollState,
                        enabled = false
                    )
            ) {
                repeat(emptyItemsCount) {
                    CounterItem(null)
                }
                repeat(items.size) {
                    CounterItem(
                        items[it].toString()
                    )
                }
                repeat(emptyItemsCount) {
                    CounterItem(null)
                }
            }
        }
    }
}

enum class PickedValueSource {
    CounterScroll,
    TileClick,
    Init,
}

data class PickedValue(
    val floatValue: Float,
    val source: PickedValueSource,
)

@Composable
private fun CounterItem(
    string: String?,
) {
    Box(
        modifier = Modifier.size(createGameDimens.counterSize),
        contentAlignment = Alignment.Center,
    ) {
        string?.let {
            Text(
                text = string,
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
}

@ThemePreview
@Composable
fun EmptyTilePreview() {
    GuessOrderGameTheme {
        TileStateless(
            tileColor = null,
            isSelected = false,
            selectedColor = Color.Blue,
            unselectedColor = Color.Blue.copy(0.6f),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ThemePreview
@Composable
fun SelectedTilePreview() {
    GuessOrderGameTheme {
        TileStateless(
            tileColor = null,
            isSelected = true,
            selectedColor = Color.Blue,
            unselectedColor = Color.Blue.copy(0.6f),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@ThemePreview
@Composable
fun FilledTilePreview() {
    GuessOrderGameTheme {
        TileStateless(
            tileColor = Color.Red,
            isSelected = false,
            selectedColor = Color.Blue,
            unselectedColor = Color.Blue.copy(0.6f),
            modifier = Modifier.padding(16.dp),
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@ThemePreview
@Composable
fun CounterPreview() {
    GuessOrderGameTheme {
        Counter(
            pickedValueState = mutableStateOf(PickedValue(1f, PickedValueSource.Init)),
            isClicked = mutableStateOf(false),
            items = persistentListOf(0, 1, 2),
            onPlaced = {}
        )
    }
}