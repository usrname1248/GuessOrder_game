package com.jozeftvrdy.game.guessorder.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jozeftvrdy.game.guessorder.R
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData

@Composable
fun SuccessGameScreen(
    initialGameData: InitialGameData,
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
            .weight(0.33f))

        Text(
            text = stringResource(R.string.success_game_screen_subtitle),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
        )

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