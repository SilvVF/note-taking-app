package io.silv.jikannoto.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import kotlinx.coroutines.launch

@Composable
fun AnimatedHintTextField(
    modifier: Modifier = Modifier,
    hint: String = "hint?",
    text: String,
    error: Boolean = false,
    fontSize: Int = 16,
    boxHeight: Dp = 50.dp,
    textChangeHandler: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val (hasFocus, setHasFocus) = remember { mutableStateOf(false) }

    val borderColor = animateColorAsState(
        targetValue = when {
            error -> Color.Red
            (hasFocus || text.isNotEmpty()) -> LocalCustomTheme.current.primary
            else -> LocalCustomTheme.current.text
        }
    ).value

    Box(
        modifier
            .focusRequester(focusRequester)
            .onFocusEvent {
                scope.launch {
                    setHasFocus(it.hasFocus)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(22))
                .height(boxHeight)
                .fillMaxWidth()
                .border(
                    1.dp,
                    color = borderColor,
                    RoundedCornerShape(22)
                )
                .padding(4.dp)
                .padding(start = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = hint,
                    color = LocalCustomTheme.current.text,
                    fontSize = animateFloatAsState(
                        targetValue = when {
                            (hasFocus || text.isNotEmpty()) -> 12f
                            else -> 16f
                        }
                    ).value.sp
                )

                BasicTextField(
                    value = text,
                    onValueChange = textChangeHandler,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .height(
                            animateDpAsState(
                                targetValue = when {
                                    (hasFocus || text.isNotEmpty()) -> 30.dp
                                    else -> 0.dp
                                }
                            ).value
                        )
                        .fillMaxWidth(0.9f)
                        .focusable(),
                    textStyle = TextStyle(
                        fontSize = fontSize.sp,
                        color = LocalCustomTheme.current.text
                    ),
                    singleLine = true,
                    maxLines = 1
                )
            }
            AnimatedVisibility(
                visible = text.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = {
                    textChangeHandler("")
                }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "clear")
                }
            }
        }
    }
}