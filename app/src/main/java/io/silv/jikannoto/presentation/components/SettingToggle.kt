package io.silv.jikannoto.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import io.silv.jikannoto.R
import io.silv.jikannoto.ui.theme.LocalCustomTheme

@Composable
fun SettingToggle(
    modifier: Modifier = Modifier,
    setting: String,
    toggled: Boolean,
    textColor: Color = LocalCustomTheme.current.subtext,
    onToggle: () -> Unit
) {
    val animationProgress by animateFloatAsState(
        targetValue = if (toggled) 0.5f else 0f,
        animationSpec = tween(300)
    )
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            R.raw.elastic_switch_blue
        )
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = setting,
            color = animateColorAsState(
                targetValue = if (toggled) textColor else textColor.copy(alpha = 0.8f)
            ).value
        )
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier.width(60.dp).height(35.dp)
                    .clickable(MutableInteractionSource(), null) {
                        onToggle()
                    }
            )
            LottieAnimation(
                composition = composition,
                progress = {
                    animationProgress
                },
                modifier = Modifier
                    .size(85.dp)
            )
        }
    }
}