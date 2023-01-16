package io.silv.jikannoto.presentation.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import io.silv.jikannoto.R
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import io.silv.jikannoto.ui.theme.LocalSpacing
import io.silv.jikannoto.ui.theme.LocalTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun CheckListScreen(
    viewModel: CheckListViewModel = getViewModel(),
    playAnimation: Boolean,
    onAnimationPlayed: () -> Unit,
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
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.koi_animation)
    )

    val height = LocalConfiguration.current.screenHeightDp.dp.value
    val animatedBoxHeight = remember {
        Animatable(initialValue = if (playAnimation) 0f else height * 0.65f)
    }
    LaunchedEffect(key1 = true) {
        if (playAnimation) {
            animatedBoxHeight.animateTo(
                height * 0.65f,
                tween(700, easing = LinearOutSlowInEasing),
            )
            onAnimationPlayed()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(colors.primary)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { koiTransition.value },
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .height(animatedBoxHeight.value.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .align(
                    Alignment.BottomCenter
                )
                .background(colors.background.copy(alpha = 0.9f))
        ) {
            var c by remember { mutableStateOf(false) }
            AnimatedCheckListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(start = 20.dp),
                content = "jsfdfasfle",
                complete = c,
                onCompleteChanged = { c = it }
            )
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedCheckListItem(
    modifier: Modifier = Modifier,
    content: String,
    complete: Boolean,
    onCompleteChanged: (Boolean) -> Unit
) {

    val isDark = LocalTheme.current.dark
    val color = LocalCustomTheme.current

    val image = rememberAnimatedVectorPainter(
        AnimatedImageVector.animatedVectorResource(
            id = R.drawable.animated_checkbox
        ),
        atEnd = complete
    )

    val textOffset = remember {
        Animatable(initialValue = 0.dp.value)
    }

    val colorTransition = remember {
        androidx.compose.animation.Animatable(
            initialValue = color.text
        )
    }

    LaunchedEffect(key1 = complete, isDark) {
        if (complete) {
            textOffset.animateTo(
                6.dp.value,
                spring()
            )
            textOffset.animateTo(0.dp.value)
            colorTransition.animateTo(
                if (isDark) Color(0xff334155) else Color.LightGray,
                animationSpec = tween(400, 10)
            )
        } else {
            colorTransition.animateTo(color.text)
        }
    }

    val strikeThroughWidthMultiplier by animateFloatAsState(
        targetValue = if (complete) 1f else 0f,
        animationSpec = tween(400)
    )

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = image,
            contentDescription = "completed",
            modifier = Modifier
                .clickable {
                    onCompleteChanged(!complete)
                }
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = content,
            color = colorTransition.value,
            // style = if (complete) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle(),
            modifier = Modifier
                .padding(start = textOffset.value.dp)
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = if (strikeThroughWidthMultiplier != 0f) colorTransition.value
                        else Color.Transparent,
                        start = Offset(0f, this.center.y),
                        end = Offset(strikeThroughWidthMultiplier * this.size.width, this.center.y),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }
        )
    }
}

@Preview()
@Composable
fun AnimatedCheckListItemPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalCustomTheme.current.background)
    ) {
        var c by remember { mutableStateOf(false) }
        AnimatedCheckListItem(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
                .align(Alignment.Center),
            content = "jkjdf",
            complete = c,
            onCompleteChanged = { c = !c },
        )
    }
}
