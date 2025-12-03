package com.jozeftvrdy.game.guessorder.game.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jozeftvrdy.game.guessorder.game.model.InitialGameData
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateGameScreen(
    onPrimaryBtnClick: (InitialGameData) -> Unit,
    viewModel: CreateGameScreenViewModel = koinViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Create Game Screen")

        Button(
            modifier =
                Modifier
                    .align(alignment = Alignment.BottomCenter),
            onClick = remember(viewModel) {{
                viewModel.onClick(gameData = InitialGameData(0,0))
            }}
        ) {
            Text("Create")
        }
    }
}