package com.jozeftvrdy.game.guessorder.game.play

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.jozeftvrdy.game.guessorder.R
import com.jozeftvrdy.game.guessorder.extension.Spacer
import com.jozeftvrdy.game.guessorder.game.create.TileFill
import com.jozeftvrdy.game.guessorder.game.create.TileFillFraction
import com.jozeftvrdy.game.guessorder.game.create.TilesRow
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.game.model.TurnResult
import com.jozeftvrdy.game.guessorder.ui.provider.DefaultColorProvider
import com.jozeftvrdy.game.guessorder.ui.theme.GuessOrderGameTheme
import com.jozeftvrdy.game.guessorder.ui.theme.ThemePreview
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PlayingArea(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(Color) -> Unit,
) {
    val bgColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.33f)
    val borderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.66f)
    val shape = RoundedCornerShape(24.dp)
    val borderWidth = 4.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            )
            .clip(shape)
            .background(color = bgColor)
            .padding(vertical = borderWidth)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            content(bgColor)
        }
    )
}

@Composable
fun HistoryTurn(
    size: Int,
    getItemColor: (Int, Boolean) -> Color?,
    result: TurnResult?,
) {
    val density = LocalDensity.current

    Row(
        modifier = Modifier.height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val tilesBgColor = MaterialTheme.colorScheme.background
        val resultBgColor = MaterialTheme.colorScheme.secondaryContainer

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(color = tilesBgColor)
        )

        TilesRow(
            modifier = Modifier
                .background(color = tilesBgColor)
                .padding(horizontal = 8.dp),
            count = size,
            tileSize = playGameDimens.historyTileSize,
            getIsSelected = remember {
                {
                    false
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
            getTileItemContent = remember(getItemColor) {
                { index ->
                    {
                        HistoryTurnAnimatedContent(
                            index,
                            getItemColor
                        )
                    }
                }
            }
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

        AnimatedContent(
            result,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { animatedResult ->
            animatedResult?.let {
                TurnResults(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(playGameDimens.historyTileSize.times(4f))
                        .background(color = resultBgColor)
                        .padding(horizontal = 8.dp)
                    ,
                    turnResult = animatedResult,
                )
            }?: Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(playGameDimens.historyTileSize.times(4))
                    .background(color = resultBgColor)
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(color = resultBgColor)
        )
    }
}

@Composable
fun HistoryTurnAnimatedContent(
    index: Int,
    getItemColor: (Int, Boolean) -> Color?,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val fillModifier = Modifier.fillMaxSize()
    AnimatedContent(
        modifier =  Modifier.size(playGameDimens.historyTileSize.times(
            TileFillFraction
        )),
        targetState = getItemColor(index, isDarkTheme),
        transitionSpec = remember { { slideInVertically(tween(500)) { it } togetherWith slideOutVertically(tween(500)) { -it } } },
    ) { animatedColor ->
        if (animatedColor != null) {
            TileFill(
                color = animatedColor,
                modifier = fillModifier
            )
        } else {
            Box(fillModifier)
        }
    }

}

@Composable
fun TurnResults(
    turnResult: TurnResult?,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
) {
    Row(
        modifier = modifier
    ) {
        GreatSuccess(
            color = color,
            contentDescription = turnResult?.greatSuccessCount?.let { count ->
                pluralStringResource(
                    R.plurals.great_success_description,
                    count = count,
                    count
                )
            }?:stringResource(R.string.loading_result_description),
            spacing = 6.dp,
        ) {
            AnimatedResultTextValue(
                count = turnResult?.greatSuccessCount,
                color = color,
            )
        }

        Spacer(16)

        MildSuccess(
            spacing = 6.dp,
            color = color,
            contentDescription = turnResult?.mildSuccessCount?.let { count ->
                pluralStringResource(
                R.plurals.mild_success_description,
                count = count,
                count
                )
            }?:stringResource(R.string.loading_result_description)
        ) {
            AnimatedResultTextValue(
                count = turnResult?.mildSuccessCount,
                color = color,
            )
        }
    }
}

@Composable
fun AnimatedResultTextValue(
    count: Int?,
    color: Color,
) {
    AnimatedContent(count) { state ->
        state?.let { count ->
            ResultCountText(
                count = count,
                color = color,
            )
        }?: CircularProgressIndicator(
            modifier = Modifier.size(playGameDimens.historyResultProgressSize)
        )
    }
}

@Composable
private fun Success(
    contentDescription: String,
    spacing: Dp,
    text: @Composable RowScope.() -> Unit,
    icon: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .clearAndSetSemantics {
                this@clearAndSetSemantics.contentDescription = contentDescription
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        text()

        Spacer(spacing)

        icon()
    }
}

@Composable
private fun GreatSuccess(
    contentDescription: String,
    color: Color,
    spacing: Dp,
    text: @Composable RowScope.() -> Unit,
) {
    Success(
        contentDescription = contentDescription,
        spacing = spacing,
        text = text,
        icon = {
            GreatSuccessIcon(color)
        }
    )
}

@Composable
private fun MildSuccess(
    contentDescription: String,
    color: Color,
    spacing: Dp,
    text: @Composable RowScope.() -> Unit,
) {
    Success(
        contentDescription = contentDescription,
        spacing = spacing,
        text = text,
        icon = {
            MildSuccessIcon(
                color = color
            )
        }
    )
}

@Composable
private fun GreatSuccessIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    ResultIcon(
        modifier = modifier.size(playGameDimens.historyResultIconSize),
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
        modifier = modifier.size(playGameDimens.historyResultIconSize),
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
        fontSize = playGameDimens.historyResultTextSize,
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
        val colorProvider = DefaultColorProvider()

        val list = persistentListOf(
            ItemFill.FillA,
            ItemFill.FillB,
            ItemFill.FillB,
            ItemFill.FillC,
            ItemFill.FillE,
        )

        HistoryTurn(
            size = list.size,
            result = TurnResult(
                greatSuccessCount = 3,
                mildSuccessCount = 1
            ),
            getItemColor = { index, isDarkTheme ->
                colorProvider.provideColorValue(list[index], isDarkTheme)
            }
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