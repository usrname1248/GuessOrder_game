package com.jozeftvrdy.game.guessorder.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun ScrollableContentIndication(
    modifier: Modifier = Modifier,
    scrollOrientation: Orientation,
    scrollState: ScrollState,
    backgroundColor: Color,
    size: Dp = 24.dp,
    scrollThreshold: Int = 50,
    content: @Composable BoxScope.() -> Unit
) {
    val parentModifier = when (scrollOrientation) {
        Orientation.Vertical -> Modifier
            .width(intrinsicSize = IntrinsicSize.Min)
        Orientation.Horizontal -> Modifier
            .height(intrinsicSize = IntrinsicSize.Min)
    }

    val childModifier = when (scrollOrientation) {
        Orientation.Vertical -> Modifier
            .fillMaxWidth()
            .height(size)
        Orientation.Horizontal -> Modifier
            .fillMaxHeight()
            .width(size)
    }

    Box(
        modifier = modifier
            .then(parentModifier)
    ) {
        content()

        Indication(
            modifier = Modifier
                .zIndex(2f)
                .align(alignment = Alignment.TopStart)
                .then(childModifier)
            ,
            indicationPosition = when (scrollOrientation) {
                Orientation.Vertical -> IndicationPosition.Top
                Orientation.Horizontal -> IndicationPosition.Start
            },
            scrollState = scrollState,
            backgroundColor = backgroundColor,
            scrollThreshold = scrollThreshold
        )


        Indication(
            modifier = Modifier
                .zIndex(2f)
                .align(
                    alignment = when (scrollOrientation) {
                        Orientation.Vertical -> Alignment.BottomStart
                        Orientation.Horizontal -> Alignment.TopEnd
                    }
                )
                .then(childModifier)
            ,
            indicationPosition = when (scrollOrientation) {
                Orientation.Vertical -> IndicationPosition.Bottom
                Orientation.Horizontal -> IndicationPosition.End
            },
            scrollState = scrollState,
            backgroundColor = backgroundColor,
            scrollThreshold = scrollThreshold
        )
    }
}

private enum class IndicationPosition {
    Start,
    Top,
    End,
    Bottom,
    ;
}

@Composable
private fun Indication(
    modifier: Modifier,
    indicationPosition: IndicationPosition,
    scrollState: ScrollState,
    backgroundColor: Color,
    scrollThreshold: Int,
) {
    val colorsList = when (indicationPosition) {
        IndicationPosition.Start,
        IndicationPosition.Top -> {
            listOf(backgroundColor, Color.Transparent)
        }
        IndicationPosition.End,
        IndicationPosition.Bottom -> {
            listOf(Color.Transparent, backgroundColor)
        }
    }


    Canvas(
        modifier = modifier
            .graphicsLayer {
                val scale = when(indicationPosition) {
                    IndicationPosition.Start,
                    IndicationPosition.Top -> {
                        scrollState.value
                    }
                    IndicationPosition.End,
                    IndicationPosition.Bottom -> {
                        (scrollState.maxValue - scrollState.value)
                    }
                }.coerceAtMost(maximumValue = scrollThreshold).div(scrollThreshold.toFloat())
                alpha = scale
            }
    ) {
        val brush = when (indicationPosition) {
            IndicationPosition.Start,
            IndicationPosition.End -> Brush.horizontalGradient(
                colorsList,
                endX = this.size.width
            )
            IndicationPosition.Top,
            IndicationPosition.Bottom -> Brush.verticalGradient(
                colorsList,
                endY = this.size.height
            )
        }

        drawRect(
            brush = brush,
        )
    }
}