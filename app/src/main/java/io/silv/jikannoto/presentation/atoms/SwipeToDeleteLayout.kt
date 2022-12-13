package io.silv.jikannoto.presentation.atoms

import androidx.annotation.FloatRange
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import kotlin.math.abs
import kotlinx.coroutines.launch

@Composable
fun SwipeToDeleteLayout(
    swipeSensitivity: Float = 0.6f,
    /**
     * value at within the range of 0 through 1 which onDelete Will be invoked defaults to 0.5f
     */
    @FloatRange(from = 0.0, to = 1.0, true, true)
    deletionThreshold: Float = 0.5f,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    /**
     * Exposed Float between 0f through 1f representing current position relative to the
     * screen width
     *  - For Rtl this is progress from the right
     *  - For Ltr this is progress from the left
     */
    swipeProgress: (Float) -> Unit = { },
    icon: ImageVector = Icons.Default.Delete,
    direction: LayoutDirection = LayoutDirection.Rtl,
    content: @Composable BoxScope.() -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val initialOffset = 0f
    val scope = rememberCoroutineScope()
    val boxOffset = remember {
        Animatable(
            initialValue = initialOffset
        )
    }

    /**
     * Current swipe progress meaning the amount that the content has been offset.
     * This value is exposed as a Float between 0f through 1f and is the same for Rtl and Ltr.
     * - For Rtl this is progress from the right
     * - For Ltr this is progress from the left
     */
    val currentSwipeProgress by remember(boxOffset.value) {
        derivedStateOf {
            (abs(boxOffset.value) / screenWidth.toFloat()).coerceIn(0f, 1f)
        }
    }
    LaunchedEffect(key1 = currentSwipeProgress) {
        swipeProgress(currentSwipeProgress)
    }
    var deletionInProgress by remember {
        mutableStateOf(false)
    }
    val transition = updateTransition(targetState = boxOffset.value, label = "boxOffset")
    val clipValue by transition.animateDp(label = "clipValue") { boxOffsetAsFloat ->
        (abs(boxOffsetAsFloat) / 8).coerceIn(0f..16f).dp
    }
    val iconSize by transition.animateDp(label = "iconSize") { boxOffsetAsFloat ->
        (abs(boxOffsetAsFloat) / 3.5f).coerceIn(22f..32f).dp
    }
    val dragState = rememberDraggableState { delta ->
        scope.launch {
            // Snapping effect where the content will animate away when the deletion threshold is met.
            if (currentSwipeProgress < deletionThreshold) {
                val currentOffset = boxOffset.value + (delta * swipeSensitivity)
                boxOffset.snapTo( // coerce makes sure that this can be dragged 1 direction only.
                    if (direction == LayoutDirection.Rtl) {
                        currentOffset.coerceAtMost(0f)
                    } else {
                        currentOffset.coerceAtLeast(0f)
                    }
                )
            } else if (!deletionInProgress) {
                deletionInProgress = true
                boxOffset.animateTo(
                    -(screenWidth.toFloat() * 1.1f)
                )
                onDelete()
            }
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .clickable(interactionSource = remember { MutableInteractionSource() }, null) {
                onClick()
            }
    ) {
        Box(
            modifier = Modifier
                .background(LocalCustomTheme.current.error)
                .matchParentSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                modifier = Modifier
                    .align(
                        if (direction == LayoutDirection.Rtl)
                            Alignment.CenterEnd
                        else
                            Alignment.CenterStart
                    )
                    .size(iconSize)
                    .padding(
                        end = if (direction == LayoutDirection.Rtl) 8.dp else 0.dp,
                        start = if (direction == LayoutDirection.Ltr) 8.dp else 0.dp
                    ),
                tint = LocalCustomTheme.current.background
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .draggable(
                    dragState, Orientation.Horizontal,
                    onDragStopped = {
                        if (!deletionInProgress) {
                            boxOffset.animateTo(initialOffset)
                        }
                    }
                )
                .offset(
                    boxOffset.value.dp
                )
                .clip(
                    RoundedCornerShape(
                        topEnd = if (direction == LayoutDirection.Rtl) clipValue else 0.dp,
                        bottomEnd = if (direction == LayoutDirection.Rtl) clipValue else 0.dp,
                        topStart = if (direction == LayoutDirection.Ltr) clipValue else 0.dp,
                        bottomStart = if (direction == LayoutDirection.Ltr) clipValue else 0.dp,
                    )
                )
        ) {
            content()
        }
    }
    assert(deletionThreshold in 0f..1f)
}
