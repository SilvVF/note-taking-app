package io.silv.jikannoto.presentation.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.silv.jikannoto.R
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import io.silv.jikannoto.ui.theme.LocalTheme

@Composable
fun CategoryListItem(
    category: String,
    selected: Boolean,
    onSelected: (category: String) -> Unit
) {
    AnimatedCategorySelectionItem(
        modifier = Modifier.padding(8.dp).height(30.dp),
        content = category,
        selected = selected
    ) {
        onSelected(category)
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedCategorySelectionItem(
    modifier: Modifier = Modifier,
    content: String,
    selected: Boolean,
    onSelected: () -> Unit
) {

    val isDark = LocalTheme.current.dark
    val color = LocalCustomTheme.current

    val image = rememberAnimatedVectorPainter(
        AnimatedImageVector.animatedVectorResource(
            id = R.drawable.animated_checkbox
        ),
        atEnd = selected
    )

    val textOffset = remember {
        Animatable(initialValue = 0.dp.value)
    }

    val colorTransition = remember {
        Animatable(
            initialValue = color.text
        )
    }

    LaunchedEffect(key1 = selected, isDark) {
        if (selected) {
            textOffset.animateTo(
                6.dp.value,
                spring()
            )
            textOffset.animateTo(0.dp.value)
            colorTransition.animateTo(
                color.text,
                animationSpec = tween(400, 10)
            )
        } else {
            textOffset.animateTo(0.dp.value)
            colorTransition.animateTo(if (isDark) Color(0xff334155) else Color.LightGray)
        }
    }

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = image,
            contentDescription = "completed",
            modifier = Modifier
                .clickable {
                    onSelected()
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
        )
    }
}