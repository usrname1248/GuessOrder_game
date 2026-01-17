package com.jozeftvrdy.game.guessorder.game.play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jozeftvrdy.game.guessorder.R
import com.jozeftvrdy.game.guessorder.extension.Spacer
import com.jozeftvrdy.game.guessorder.game.create.ImmutableTilesRow
import com.jozeftvrdy.game.guessorder.game.model.TurnResult
import com.jozeftvrdy.game.guessorder.ui.theme.GuessOrderGameTheme
import com.jozeftvrdy.game.guessorder.ui.theme.ThemePreview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PlayingArea(
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val bgColor = MaterialTheme.colorScheme.surface
    val topBorderColor = MaterialTheme.colorScheme.primaryContainer
    val topBorderWidth = 6.dp
    val topBorderWidthPx = with(density) {
        topBorderWidth.toPx()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = bgColor)
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        0f to topBorderColor,
                        0.33f to topBorderColor,
                        1f to bgColor,
                        endY = topBorderWidthPx
                    ),
                )
            }
            .padding(vertical = topBorderWidth)
        ,
        content = content
    )
}

@Composable
fun HistoryTurn(
    guessedValues: ImmutableList<Color>,
    result: TurnResult,
) {
    val density = LocalDensity.current

    val falseState = remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val tilesBgColor = MaterialTheme.colorScheme.background
        val resultBgColor = MaterialTheme.colorScheme.secondaryContainer

        ImmutableTilesRow(
            modifier = Modifier
                .weight(1f, fill = false)
                .background(color = tilesBgColor)
                .padding(horizontal = 8.dp),
            pickedValueRange = guessedValues.indices,
            getColor = remember(guessedValues) {
                { index ->
                    guessedValues[index]
                }
            },
            getIsSelectedState = remember {
                { _, _ ->
                    falseState
                }
            },
            betweenItemsSpacer = remember {
                {
                    Spacer(8)
                }
            },
            selectedColor = Color.Unspecified,
            unselectedColor = Color.Unspecified,
            onTileClick = null,
        )

        val spaceBetween = 36.dp
        val spaceBetweenPx = with(density) {
            spaceBetween.toPx()
        }

        Spacer(
            modifier = Modifier
                .width(spaceBetween)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        0f to tilesBgColor,
                        spaceBetweenPx to resultBgColor,
                    ),
                )
        )

        TurnResults(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = resultBgColor)
                .padding(horizontal = 8.dp)
            ,
            turnResult = result,
        )
    }
}

@Composable
fun TurnResults(
    turnResult: TurnResult,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
) {
    Row(
        modifier = modifier
    ) {
        turnResult.greatSuccessCount.let { count ->
            Success(
                count,
                color = color,
                contentDescription = pluralStringResource(
                    R.plurals.great_success_description,
                    count = count,
                    count
                ),
                spacing = 6.dp,
            ) {
                GreatSuccessIcon(
                    color = color
                )
            }
        }

        Spacer(16)

        turnResult.mildSuccessCount.let { count ->
            Success(
                count,
                spacing = 6.dp,
                color = color,
                contentDescription = pluralStringResource(
                    R.plurals.mild_success_description,
                    count = count,
                    count
                ),
            ) {
                MildSuccessIcon(
                    color = color
                )
            }
        }
    }
}

@Composable
private fun Success(
    count: Int,
    color: Color,
    contentDescription: String,
    spacing: Dp,
    icon: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .clearAndSetSemantics {
                this@clearAndSetSemantics.contentDescription = contentDescription
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ResultCountText(
            count = count,
            color = color,
        )

        Spacer(spacing)

        icon()
    }
}

@Composable
private fun GreatSuccessIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    ResultIcon(
        modifier = modifier,
        iconPainter = painterResource(R.drawable.ic_great_success_24),
        color = color,
        contentDescription = stringResource(R.string.great_success_icon_description),
    )
}

@Composable
private fun MildSuccessIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    ResultIcon(
        modifier = modifier,
        iconPainter = painterResource(R.drawable.ic_mild_success_24),
        color = color,
        contentDescription = stringResource(R.string.mild_success_icon_description)
    )
}

@Composable
private fun ResultIcon(
    modifier: Modifier,
    iconPainter: Painter,
    color: Color,
    contentDescription: String,
) {
    Icon(
        modifier = modifier,
        painter = iconPainter,
        contentDescription = contentDescription,
        tint = color
    )
}

@Composable
private fun ResultCountText(
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        count.toString(10),
        modifier = modifier,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 32f.sp,
        color = color,
    )
}

@Composable
@ThemePreview
private fun TurnResultPreview() {
    GuessOrderGameTheme {
        TurnResults(
            TurnResult(
                2,
                2,
            )
        )
    }
}

@Composable
@ThemePreview
private fun HistoryTurnPreview() {
    GuessOrderGameTheme {
        HistoryTurn(
            guessedValues = persistentListOf(
                Color.Red,
                Color.Blue,
                Color.Green,
                Color.Magenta,
                Color.Cyan,
            ),
            result = TurnResult(
                greatSuccessCount = 3,
                mildSuccessCount = 1
            )
        )
    }
}

@Composable
@ThemePreview
private fun PlayingAreaPreview() {
    GuessOrderGameTheme {
        PlayingArea {
            Text("this is Playing area.")
        }
    }
}