package io.silv.jikannoto.presentation.atoms

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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

@Composable
fun SqueezeLayout(
    modifier: Modifier,
    enabled: Boolean = true,
    widthSqueeze: Dp = 3.dp,
    heightSqueeze: Dp = 2.dp,
    animationSpec: AnimationSpec<Dp> = spring(),
    onClick: () -> Unit,
    content: @Composable BoxScope.(progress: Float) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val pressed by interactionSource.collectIsPressedAsState()

    val widthSqueezeAnimation by animateDpAsState(
        targetValue = remember(pressed) {
            derivedStateOf {
                when (pressed) {
                    true -> widthSqueeze
                    else -> 0.dp
                }
            }.value
        },
        animationSpec = animationSpec
    )
    val heightSqueezeAnimation by animateDpAsState(
        targetValue = remember(pressed) {
            derivedStateOf {
                when (pressed) {
                    true -> heightSqueeze
                    else -> 0.dp
                }
            }.value
        },
        animationSpec = animationSpec
    )

    val progressFraction by remember(heightSqueeze, widthSqueeze) {
        derivedStateOf {
            (heightSqueezeAnimation.value / heightSqueeze.value).coerceIn(0f..1f)
        }
    }

    Box(
        modifier
            .clickable(interactionSource, null) {
                if (enabled) {
                    onClick()
                }
            }
            .padding(
                top = heightSqueezeAnimation,
                bottom = heightSqueezeAnimation,
                start = widthSqueezeAnimation,
                end = widthSqueezeAnimation
            ),
        contentAlignment = Alignment.Center
    ) {
        content(
            progressFraction
        )
    }
}