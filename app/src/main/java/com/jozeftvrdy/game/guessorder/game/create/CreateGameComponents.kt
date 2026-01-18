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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jozeftvrdy.game.guessorder.extension.Spacer
import com.jozeftvrdy.game.guessorder.extension.ifNotNull
import com.jozeftvrdy.game.guessorder.extension.mixWith
import com.jozeftvrdy.game.guessorder.ui.theme.GuessOrderGameTheme
import com.jozeftvrdy.game.guessorder.ui.theme.ThemePreview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

typealias TileComposableContent = @Composable BoxScope.() -> Unit

@Composable
fun TilesRowWithProgress(
    tilesCount: Int,
    tileSize: Dp,
    pickedValueState: MutableState<PickedValue>,
    getIsSelected: @Composable (index: Int) -> Boolean,
    onTileClick: ((index: Int) -> Unit)?,
    modifier: Modifier = Modifier,
    getIsEnabled: @Composable (index: Int) -> Boolean = remember{{ true }},
    getTileItemContent: (index: Int) -> TileComposableContent
) {
    val selectedColor = MaterialTheme.colorScheme.secondary
    val unselectedColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.75f)

    Box(
        modifier = modifier
            .tileProgress(
                pickedValueState = pickedValueState,
                maxValue = tilesCount.toFloat(),
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                heightFraction = 0.15f,
                padding = PaddingValues(horizontal = tileSize.div(2))
            )
    ) {
        TilesRow(
            count = tilesCount,
            tileSize = tileSize,
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            getIsSelected = getIsSelected,
            getIsEnabled = getIsEnabled,
            onTileClick = onTileClick,
            betweenItemsSpacer = remember {
                {
                    Spacer(12)
                }
            },
            modifier = Modifier
                .zIndex(2f)
            ,
            getTileItemContent = getTileItemContent
        )
    }
}

@Composable
fun TilesRow(
    count: Int,
    tileSize: Dp,
    getIsSelected: @Composable (index: Int) -> Boolean,
    onTileClick: ((index: Int) -> Unit)?,
    modifier: Modifier = Modifier,
    getIsEnabled: @Composable (index: Int) -> Boolean = remember{{false}},
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    unselectedColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.75f),
    betweenItemsSpacer: @Composable RowScope.() -> Unit = {},
    getTileItemContent: (index: Int) -> TileComposableContent,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            if (index > 0) {
                betweenItemsSpacer()
            }

            InternalTile(
                index = index,
                size = tileSize,
                getIsSelected = getIsSelected,
                getIsEnabled = getIsEnabled,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onTileClick = onTileClick,
                getTileItemContent = getTileItemContent
            )
        }
    }
}

@Composable
fun InternalTile(
    index: Int,
    size: Dp,
    getIsSelected: @Composable (index: Int) -> Boolean,
    onTileClick: ((index: Int) -> Unit)?,
    getIsEnabled: @Composable (index: Int) -> Boolean = remember{{false}},
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    unselectedColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.75f),
    getTileItemContent: (index: Int) -> TileComposableContent,
) {
    Tile(
        modifier = Modifier
            .size(size),
        isSelected = getIsSelected(index),
        isEnabled = getIsEnabled(index),
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        onTileClick = onTileClick?.let {
            remember(index) {
                {
                    onTileClick.invoke(index)
                }
            }
        },
        content = remember(index) {
            getTileItemContent(index)
        }
    )
}

@Composable
fun Modifier.tileProgress(
    pickedValueState: MutableState<PickedValue>,
    maxValue: Float,
    selectedColor: Color,
    unselectedColor: Color,
    heightFraction: Float,
    layoutDirection: LayoutDirection = LocalLayoutDirection.current,
    padding: PaddingValues = PaddingValues(),
) = this.drawBehind {
        var progressRect = Rect(Offset.Zero, size)

        with(progressRect) {
            if (padding != PaddingValues()) {
                progressRect = Rect(
                    top = top + padding.calculateTopPadding().toPx(),
                    bottom = bottom - padding.calculateTopPadding().toPx(),
                    left = left + padding.calculateLeftPadding(layoutDirection).toPx(),
                    right = right - padding.calculateRightPadding(layoutDirection).toPx(),
                )
            }
        }

        with(progressRect) {
            val totalHeight = this.size.height
            val desiredHeight = totalHeight.times(heightFraction)
            val topBottomPadding = (totalHeight - desiredHeight) / 2
            progressRect = Rect(
                left = left,
                top = top + topBottomPadding,
                right = right,
                bottom = bottom - topBottomPadding,
            )
        }

        drawRect(
            topLeft = progressRect.topLeft,
            size = progressRect.size,
            color = unselectedColor,
        )

        with(progressRect) {
            val width = right - left
            val newWidth = width.times(
                pickedValueState.value.floatValue - 1
            )
                .div(
                    maxValue - 1
                )

            progressRect = progressRect.copy(
                right = left + newWidth
            )
        }

        drawRect(
            topLeft = progressRect.topLeft,
            size = progressRect.size,
            color = selectedColor,
        )

    }

@Composable
fun Tile(
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = false,
    onTileClick: (() -> Unit)? = null,
    content: TileComposableContent,
) {
    val animationDurationMillis = 440
    val dimens = createGameDimens
    val shape = MaterialTheme.shapes.large
    val disabledColorComposite = Color.Gray
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer

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
            .background(
                if (isEnabled) {
                    backgroundColor
                } else {
                    backgroundColor.mixWith(disabledColorComposite)
                },
                shape = shape
            )
            .border(
                width = animatedBorderWidthState.value,
                color = if (isEnabled) {
                    animatedBorderColorState.value
                } else {
                    animatedBorderColorState.value.mixWith(disabledColorComposite)
                },
                shape = shape
            )
            .ifNotNull(onTileClick) {
                Modifier.clickable(onClick = it)
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun TileFill(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = CircleShape,
        color = color,
        modifier = Modifier
            .fillMaxSize(0.75f)
            .then(modifier)
    ) {

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
        repeat(items.size) { index ->
            CounterItem(
                items[index].toString(10)
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
                    .minus(1)
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
                        items[it].toString(10)
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
        Tile(
            isSelected = false,
            selectedColor = Color.Blue,
            unselectedColor = Color.Blue.copy(0.6f),
            modifier = Modifier.padding(16.dp).size(48.dp),
        ) {

        }
    }
}

@ThemePreview
@Composable
fun SelectedTilePreview() {
    GuessOrderGameTheme {
        Tile(
            isSelected = true,
            selectedColor = Color.Blue,
            unselectedColor = Color.Blue.copy(0.6f),
            modifier = Modifier.padding(16.dp).size(48.dp),
        ) {

        }
    }
}

@ThemePreview
@Composable
fun FilledTilePreview() {
    GuessOrderGameTheme {
        Tile(
            isSelected = false,
            selectedColor = Color.Blue,
            unselectedColor = Color.Blue.copy(0.6f),
            modifier = Modifier.padding(16.dp).size(48.dp),
        ) {
            TileFill(
                color = Color.Red
            )
        }
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