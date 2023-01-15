package io.silv.jikannoto.presentation.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import io.silv.jikannoto.ui.theme.LocalSpacing
import org.koin.androidx.compose.getViewModel

@Composable
fun CheckListScreen(
    viewModel: CheckListViewModel = getViewModel()
) {

    val colors = LocalCustomTheme.current
    val s = LocalSpacing.current

    val koiTransition = rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec<Float>(
            tween(10000),
            RepeatMode.Reverse,
            StartOffset(0)
        )
    )
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(io.silv.jikannoto.R.raw.koi_animation))

    Box(
        Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { koiTransition.value },
            Modifier.align(Alignment.TopCenter).fillMaxWidth().fillMaxHeight(0.4f)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .align(
                    Alignment.BottomCenter
                )
                .background(colors.primary)
        ) {
        }
    }
}