package io.silv.jikannoto.presentation.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.silv.jikannoto.presentation.atoms.SqueezeLayout
import io.silv.jikannoto.ui.theme.LocalCustomTheme

@Composable
fun AnimatedButton(
    modifier: Modifier = Modifier,
    fontSize: Number = 12f,
    enabled: Boolean = true,
    fontReductionScale: Float = 0.15f,
    label: String = "Click Me.",
    animationSpec: AnimationSpec<Dp> = spring(),
    heightSqueeze: Dp? = null,
    widthSqueeze: Dp? = null,
    onClick: () -> Unit,
) {

    SqueezeLayout(
        modifier = modifier,
        onClick = {
            if (enabled) {
                onClick()
            }
        },
        heightSqueeze = heightSqueeze ?: 2.dp,
        widthSqueeze = heightSqueeze ?: 3.dp,
        animationSpec = animationSpec,
    ) { progress ->

        val primaryColor = LocalCustomTheme.current.primary
        val colorAnimation = remember(progress) {
            derivedStateOf {
                primaryColor.copy(
                    alpha = 1f - (progress * 0.3f)
                )
            }.value
        }

        val fontSizeAnimation = remember(progress) {
            derivedStateOf {
                (fontSize.toFloat() - ((fontSize.toFloat() * fontReductionScale) * progress)).sp
            }.value
        }

        Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(100))
                .background(colorAnimation),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = fontSizeAnimation
            )
        }
    }
}