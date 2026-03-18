package com.jozeftvrdy.game.guessorder.game

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jozeftvrdy.game.guessorder.R
import com.jozeftvrdy.game.guessorder.game.create.TileFill
import com.jozeftvrdy.game.guessorder.game.create.TileFillFraction
import com.jozeftvrdy.game.guessorder.game.create.TilesRow
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import com.jozeftvrdy.game.guessorder.game.model.ItemFill
import com.jozeftvrdy.game.guessorder.game.play.playGameDimens
import com.jozeftvrdy.game.guessorder.ui.components.LocalSharedElementsModifierProvider
import com.jozeftvrdy.game.guessorder.ui.provider.ColorProvider
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.roundToLong

@Composable
fun SuccessGameScreen(
    initialGameData: InitialGameData,
    resultCombination: ImmutableList<ItemFill>,
    playedTimeMillis: Long,
    colorProvider: ColorProvider,
    navigateToCreateGameScreen: () -> Unit,
    navigateToMainGameScreen: (InitialGameData) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier
            .weight(1f))

        Text(
            text = stringResource(R.string.success_game_screen_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier
            .weight(0.2f))
        TilesRow(
            count = resultCombination.size,
            tileSize = playGameDimens.playTileSize,
            getIsSelected = remember {
                { _ ->
                    false
                }
            },
            getIsEnabled = remember {
                {
                    true
                }
            },
            betweenItemsSpacer = remember {
                {
                    com.jozeftvrdy.game.guessorder.extension.Spacer(16.dp)
                }
            },
            onTileClick = null
        ) { index ->
            {
                val isDarkTheme = isSystemInDarkTheme()
                val fill = resultCombination[index]
                val color = fill.let {
                    colorProvider.provideColorValue(it, isDarkTheme)
                }

                TileFill(
                    color = color,
                    modifier = LocalSharedElementsModifierProvider.current.createModifierForSuccessItem(index).size(
                        playGameDimens.playTileSize.times(
                            TileFillFraction
                        )
                    )
                )
            }
        }

        com.jozeftvrdy.game.guessorder.extension.Spacer(16)

        Subtitle(stringResource(R.string.success_game_screen_subtitle))
        com.jozeftvrdy.game.guessorder.extension.Spacer(16)
        PlayedTimeComponent(playedTimeMillis)
        com.jozeftvrdy.game.guessorder.extension.Spacer(16)

        Subtitle(stringResource(R.string.success_game_screen_subtitle2))

        Spacer(modifier = Modifier
            .weight(2f))

        Row(
            horizontalArrangement = Arrangement.Center,
        ) {
            MainButton(
                onClick = remember(initialGameData) {
                    {
                        navigateToMainGameScreen(initialGameData)
                    }
                },
                "Start game with same params"
            )
            com.jozeftvrdy.game.guessorder.extension.Spacer(
                size = 64.dp
            )
            MainButton(
                onClick = navigateToCreateGameScreen,
                "Start game with new params"
            )
        }
    }
}

@Composable
private fun Subtitle(
    subtitle: String,
) {
    Text(
        text = subtitle,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun PlayedTimeComponent(
    playedTimeMillis: Long
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Subtitle(stringResource(R.string.success_game_screen_played_time_description))
        com.jozeftvrdy.game.guessorder.extension.Spacer(12)
        Text(
            text = calculateTimePlayedString(playedTimeMillis),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun calculateTimePlayedString(
    playedTimeMillis: Long
): String {
    var playedTimeSeconds = playedTimeMillis.div(1000f).roundToLong()
    val secondsInHour = 3600
    val secondsInMinute = 60
    val playedHours = playedTimeSeconds.div(secondsInHour).toInt()
    playedTimeSeconds -= playedHours * secondsInHour
    val playedMinutes = playedTimeSeconds.div(secondsInMinute).toInt()
    playedTimeSeconds -= playedMinutes * secondsInMinute
    val playedSeconds = playedTimeSeconds.toInt()

    val strings = mutableListOf<String>()
    if (playedHours > 0) {
        strings.add(pluralStringResource(R.plurals.hours_time, playedHours, playedHours))
    }
    if (playedMinutes > 0) {
        strings.add(pluralStringResource(R.plurals.minutes_time, playedMinutes, playedMinutes))
    }
    if (playedSeconds > 0) {
        strings.add(pluralStringResource(R.plurals.seconds_time, playedSeconds, playedSeconds))
    }

    return strings.joinToString(" ").also {
        require(it.isNotEmpty())
    }
}

@Composable
private fun MainButton(
    onClick: () -> Unit,
    text: String,
) {
    Button(
        onClick = onClick,
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}