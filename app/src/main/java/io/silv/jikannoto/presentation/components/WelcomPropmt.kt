package io.silv.jikannoto.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.silv.jikannoto.domain.models.dateTimeNow
import io.silv.jikannoto.ui.theme.LocalCustomTheme

@Composable
fun BoxScope.SlideAwayWelcomePrompt(inView: Boolean, name: String) {

    val offset = LocalConfiguration.current.screenHeightDp * 0.3f

    AnimatedVisibility(visible = inView, enter = slideInHorizontally(), exit = slideOutHorizontally { x -> -x }) {
        Box(
            Modifier
                .align(Alignment.TopStart)
                .padding(top = 60.dp)
                .clip(RoundedCornerShape(bottomEnd = 12.dp, topEnd = 12.dp))
                .background(LocalCustomTheme.current.background)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${when (dateTimeNow().hour) {
                    in 2..11 -> "good morning"
                    in 12..16 -> "good afternoon"
                    else -> "hello"
                }} $name",
                fontSize = 22.sp,
                color = LocalCustomTheme.current.text
            )
        }
    }
    if (!inView) {
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(),
            modifier = Modifier.offset(y = offset.dp)
        ) {
            Text(
                text = "Welcome $name",
                color = LocalCustomTheme.current.text,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topEnd = 8.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .background(
                        LocalCustomTheme.current.background.copy(alpha = 0.4f)
                    )
                    .padding(4.dp)
            )
        }
    }
}