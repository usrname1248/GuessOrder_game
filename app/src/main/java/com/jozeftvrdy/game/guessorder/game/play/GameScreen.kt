package com.jozeftvrdy.game.guessorder.game.play

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData

@Composable
fun GameScreen(
    initialGameData: InitialGameData,
    onGameFinish: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text("Game Screen")
        }
    }
}