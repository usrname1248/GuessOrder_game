package com.jozeftvrdy.game.guessorder.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionDefaults
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.navigation3.ui.LocalNavAnimatedContentScope

data class SharedElementsDataHolder (
    private val transitionScope: SharedTransitionScope? = null,
) {
    val slowAnimation = spring<Rect>(
        stiffness = Spring.StiffnessLow
    )
    val slowAnimationBounds: (Rect, Rect) -> FiniteAnimationSpec<Rect> = { _,_ ->
        slowAnimation
    }

    @Composable
    fun createModifierForSuccessItem(index: Int): Modifier = createModifier("successGuessDataAt$index", slowAnimationBounds)

    @Composable
    fun createModifierForPickedColorRow(index: Int): Modifier = createModifier("pickedColorsDataAt$index", slowAnimationBounds)

    @Composable
    private fun createModifier(
        key: Any,
        boundsTransform: BoundsTransform = SharedTransitionDefaults.BoundsTransform,
    ): Modifier {
        val sharedContentState = transitionScope?.rememberSharedContentState(key)?:return Modifier
        val animationScope = LocalNavAnimatedContentScope.current
        return createModifier(
            sharedContentState,
            animationScope,
            boundsTransform,
        )
    }

    private fun createModifier(
        sharedContentState: SharedContentState,
        animatedVisibilityScope: AnimatedVisibilityScope,
        boundsTransform: BoundsTransform = SharedTransitionDefaults.BoundsTransform,
    ): Modifier = transitionScope?.run {
            return Modifier.sharedElement(
                sharedContentState,
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
            )
        }?:Modifier
}

val LocalSharedElementsModifierProvider = compositionLocalOf {
    SharedElementsDataHolder()
}

@Composable
fun SharedTransitionScope.CreateSharedElementsModifierProvider(
    content: @Composable () -> Unit
) {
    val sharedElements = SharedElementsDataHolder(
        transitionScope = this
    )

    CompositionLocalProvider(LocalSharedElementsModifierProvider provides sharedElements) {
        content()
    }
}