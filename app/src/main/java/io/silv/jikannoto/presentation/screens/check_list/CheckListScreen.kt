package io.silv.jikannoto.presentation.screens.check_list

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import io.silv.jikannoto.R
import io.silv.jikannoto.presentation.atoms.SwipeToDeleteLayout
import io.silv.jikannoto.presentation.components.SlideAwayWelcomePrompt
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import io.silv.jikannoto.ui.theme.LocalSpacing
import io.silv.jikannoto.ui.theme.LocalTheme
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckListScreen(
    viewModel: CheckListViewModel = getViewModel(),
    playAnimation: Boolean,
    name: String,
    onAnimationPlayed: () -> Unit,
) {

    val colors = LocalCustomTheme.current
    val s = LocalSpacing.current
    val state by viewModel.collectAsState()

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

    val animatedBoxHeight = remember {
        Animatable(initialValue = if (playAnimation) 0f else 0.65f)
    }
    LaunchedEffect(key1 = true) {
        if (playAnimation) {
            animatedBoxHeight.animateTo(
                0.65f,
                tween(durationMillis = 600, easing = LinearOutSlowInEasing),
            )
            delay(3000)
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
        SlideAwayWelcomePrompt(inView = playAnimation, name = name)
        Box(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .fillMaxHeight(animatedBoxHeight.value)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .align(
                    Alignment.BottomCenter
                )
                .background(colors.background)
        ) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(
                    state.checkListItems,
                    key = { it.id }
                ) {
                    var c by remember { mutableStateOf(it.completed) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(
                                spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                            )
                    ) {
                        SwipeToDeleteLayout(
                            onDelete = {
                                viewModel.deleteCheckListItem(it.id)
                            },
                            onClick = { },
                        ) {
                            AnimatedCheckListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(35.dp)
                                    .background(colors.background)
                                    .padding(start = 20.dp),
                                content = it.name,
                                complete = c,
                                onCompleteChanged = { c = it }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = { viewModel.addCheckListItem("test adding item") }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
            }
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
        Animatable(
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
            textOffset.animateTo(0.dp.value)
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
                .clickable(remember { MutableInteractionSource() }, null) {
                    onCompleteChanged(!complete)
                }
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = content,
            color = colorTransition.value,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = textOffset.value.dp)
                .offset(y = -(2).dp)
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
