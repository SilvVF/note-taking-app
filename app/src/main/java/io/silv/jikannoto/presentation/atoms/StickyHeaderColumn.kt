package io.silv.jikannoto.presentation.atoms

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.launch

@Composable
fun StickyHeaderColumn(
    modifier: Modifier = Modifier,
    headerBarHeight: Float = 60f,
    scrollState: ScrollState = rememberScrollState(),
    animationSpec: AnimationSpec<Float> = SpringSpec(),
    headerBar: @Composable BoxScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val ANCHOR_INIT = -10f
    val minY = -headerBarHeight + -10f
    val maxY = headerBarHeight
    val (anchorY, setAnchorY) = remember { mutableStateOf(ANCHOR_INIT) }
    val translationY = remember { Animatable(0f) }
    var progressY by remember { mutableStateOf(0f) }

    LaunchedEffect(!scrollState.isScrollInProgress) {
        // scroll is not in progress anymore below will implement the snapping behaviour
        // the bar will go offscreen if no more than 50% is showing.
        if (progressY > 0.5f || scrollState.value.toFloat() < headerBarHeight) {
            coroutineScope.launch {
                translationY.animateTo(maxY, animationSpec)
            }
        } else {
            coroutineScope.launch {
                translationY.animateTo(minY, animationSpec)
            }
        }
    }

    LaunchedEffect(scrollState.value) {
        val offsetY = scrollState.value.toFloat()
        // dist from the current anchor position
        var distY = offsetY - anchorY
        // anchor is at the start and the dist will be invalid for this position
        // because the anchor does not start at 0
        if (anchorY == ANCHOR_INIT) distY = offsetY
        // gets the value within bounds of min and max position on the screen
        val value = (translationY.value - distY).coerceIn(minY, maxY)
        translationY.snapTo(value)
        setAnchorY(offsetY) // update the anchor for future calculations
        // set the progress used to determine when to snap off or onto the screen
        // this will be a number between 0 - 1 representing current progress
        progressY = lerp(minY.dp, maxY.dp, translationY.value).value
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState, true),
        ) {
            content()
        }
        Box(Modifier.offset(y = translationY.asState().value.dp)) {
            headerBar()
        }
    }
}
