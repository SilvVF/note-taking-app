package io.silv.jikannoto.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.silv.jikannoto.ui.theme.LocalCustomTheme

@Composable
fun HeaderBar(
    text: String,
    textChange: (String) -> Unit,
    onMenuClicked: () -> Unit,
    onDotsClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 10.dp,
                RoundedCornerShape(
                    topEnd = 37.dp,
                    topStart = 50.dp,
                    bottomEnd = 37.dp,
                    bottomStart = 45.dp
                )
            )
            .fillMaxWidth(0.9f)
            .height(50.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(LocalCustomTheme.current.headerBar),
        contentAlignment = Alignment.Center
    ) {
        HeaderContent(
            textChange = textChange,
            onMenuClicked = onMenuClicked,
            text = text,
            onDotsClicked = onDotsClicked
        )
    }
}

@Composable
fun HeaderContent(
    modifier: Modifier = Modifier,
    text: String,
    textChange: (String) -> Unit,
    onDotsClicked: () -> Unit,
    onMenuClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier
                .padding(start = 12.dp)
                .size(24.dp),
            onClick = onMenuClicked

        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Search",
                modifier = Modifier
                    .size(22.dp)
            )
        }
        OutlinedTextField(
            value = text,
            interactionSource = MutableInteractionSource(),
            onValueChange = { textChange(it) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black,
            ),
            placeholder = {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "Search...", textAlign = TextAlign.Center, modifier = Modifier.offset(y = -(2).dp))
                }
            },
            textStyle = TextStyle(
                fontWeight = FontWeight.SemiBold,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
        IconButton(
            onClick = onDotsClicked,
            modifier = Modifier.padding(end = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "categories",
                modifier = Modifier
                    .size(22.dp)
            )
        }
    }
}