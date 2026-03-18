package com.jozeftvrdy.game.guessorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jozeftvrdy.game.guessorder.game.model.BackStackHolder
import com.jozeftvrdy.game.guessorder.navigation.CreateGameNavScreen
import com.jozeftvrdy.game.guessorder.navigation.NavigationRoot
import com.jozeftvrdy.game.guessorder.ui.components.CreateSharedElementsModifierProvider
import com.jozeftvrdy.game.guessorder.ui.theme.GuessOrderGameTheme
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {

    val backStackHolder: BackStackHolder by inject {
        parametersOf(
            CreateGameNavScreen
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuessOrderGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SharedTransitionLayout {
                        CreateSharedElementsModifierProvider(
                        ) {
                            NavigationRoot(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .displayCutoutPadding()
                                    .padding(innerPadding),
                                backStackHolder,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GuessOrderGameTheme {
        Greeting("Android")
    }
}