package io.silv.jikannoto.presentation.atoms

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
fun SqueezeLayout(
    modifier: Modifier,
    enabled: Boolean = true,
    widthSqueeze: Dp = 4.dp,
    heightSqueeze: Dp = 3.dp,
    animationSpec: AnimationSpec<Float> = spring(),
    onClick: () -> Unit,
    content: @Composable BoxScope.(progress: Float) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val pressed by interactionSource.collectIsPressedAsState()
    var startAnimation by remember { mutableStateOf(false) }

    val widthSqueezeAnimation = remember {
        Animatable(0.dp.value)
    }
    val heightSqueezeAnimation = remember {
        Animatable(0.dp.value)
    }

    LaunchedEffect(key1 = startAnimation) {
        if (startAnimation) {
            val resultHeight = async {
                heightSqueezeAnimation.animateTo(
                    heightSqueeze.value * 0.8f,
                    animationSpec = animationSpec
                )
            }
            val resultWidth = async {
                widthSqueezeAnimation.animateTo(
                    widthSqueeze.value * 0.8f,
                    animationSpec = animationSpec
                )
            }
            awaitAll(resultHeight, resultWidth)
            startAnimation = false
        }
    }

    LaunchedEffect(key1 = pressed, startAnimation) {
        println(pressed)
        if (pressed) {
            launch {
                widthSqueezeAnimation.animateTo(
                    widthSqueeze.value,
                    animationSpec
                )
            }
            launch {
                heightSqueezeAnimation.animateTo(
                    heightSqueeze.value,
                    animationSpec
                )
            }
        } else if (!startAnimation) {
            launch {
                widthSqueezeAnimation.animateTo(
                    0.dp.value,
                    tween(50)
                )
            }
            launch {
                heightSqueezeAnimation.animateTo(
                    0.dp.value,
                    tween(50)
                )
            }
        }
    }

    val progressFraction by remember(heightSqueeze, widthSqueeze) {
        derivedStateOf {
            (heightSqueezeAnimation.value / heightSqueeze.value).coerceIn(0f..1f)
        }
    }

    Box(
        modifier
            .clickable(interactionSource, null) {
                startAnimation = true
                if (enabled) {
                    onClick()
                }
            }
            .padding(
                top = heightSqueezeAnimation.value.dp,
                bottom = heightSqueezeAnimation.value.dp,
                start = widthSqueezeAnimation.value.dp,
                end = widthSqueezeAnimation.value.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        content(
            progressFraction
        )
    }
}