package io.silv.jikannoto.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import io.silv.jikannoto.R

@Composable
fun ThemeToggle(
    darkTheme: Boolean,
    modifier: Modifier,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessLow
    )
) {

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            R.raw.light_dark_mode_button
        )
    )
    val progress = animateFloatAsState(
        targetValue = if (darkTheme) 1f else 0f,
        animationSpec = animationSpec
    )

    LottieAnimation(
        composition = composition,
        progress = {
            progress.value
        },
        modifier = modifier
    )
}