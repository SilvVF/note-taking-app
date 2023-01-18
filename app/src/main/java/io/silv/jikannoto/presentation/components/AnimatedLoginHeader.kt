package io.silv.jikannoto.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.silv.jikannoto.presentation.screens.user_settings.currentFraction
import io.silv.jikannoto.ui.theme.LocalCustomTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AnimatedLoginHeader(
    scaffoldState: BottomSheetScaffoldState,
    expanded: Boolean,
    onSettingsIconClick: () -> Unit,
    onUpArrowClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize()
    ) {
        val bottomSheetState = remember(scaffoldState.currentFraction) {
            mutableStateOf(scaffoldState.currentFraction)
        }
        val transition = updateTransition(
            targetState = bottomSheetState,
            label = "bottom sheet progress"
        )
        val titleFontSize by transition.animateFloat(label = "font-size") { progress ->
            18f + (12 * (progress.value * 5.5f).coerceIn(0f, 1f))
        }
        val titlePadding by transition.animateDp(label = "padding") { progress ->
            if (progress.value > 0.4f) (28f * ((progress.value).coerceIn(0f, 1f) - 0.4f)).dp
            else 0.dp
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .fillMaxWidth(0.2f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(100))
                    .background(
                        Color.LightGray.copy(alpha = 0.7f)
                    )
            )
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Log in or sign up",
                    fontSize = titleFontSize.sp,
                    color = LocalCustomTheme.current.text,
                    modifier = Modifier.padding(top = titlePadding + 8.dp)
                )
                Text(
                    text = "login to sync notes online",
                    color = animateColorAsState(
                        targetValue = if (scaffoldState.bottomSheetState.isExpanded)
                            Color.Transparent
                        else LocalCustomTheme.current.subtext,
                        animationSpec = tween(300)
                    ).value,
                )
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 8.dp, end = 16.dp)
            ) {
                IconButton(
                    onClick = if (expanded) onSettingsIconClick else onUpArrowClick
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Outlined.Settings
                        else Icons.Outlined.KeyboardArrowUp,
                        contentDescription = null,
                        tint = LocalCustomTheme.current.text,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}