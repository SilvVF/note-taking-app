package io.silv.jikannoto.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.silv.jikannoto.R
import io.silv.jikannoto.ui.theme.LocalCustomTheme

@Composable
fun ProfileImage(
    modifier: Modifier,
    imageUrl: String = "",
    onImageUrlChange: (String) -> Unit,
) {
    Column {

        val w = LocalConfiguration.current.screenWidthDp
        val h = LocalConfiguration.current.screenHeightDp

        var showEditUrl by remember {
            mutableStateOf(false)
        }
        var imageError by remember {
            mutableStateOf(false)
        }

        Row(
            modifier,
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.profile_placeholder),
                fallback = painterResource(R.drawable.profile_placeholder),
                error = painterResource(R.drawable.profile_placeholder),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f)
                    .clip(CircleShape),
                onError = {
                    imageError = true
                },
                onSuccess = {
                    imageError = false
                }
            )

            IconButton(onClick = { showEditUrl = !showEditUrl }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "edit profile url",
                    tint = LocalCustomTheme.current.text
                )
            }
        }
        AnimatedVisibility(
            visible = showEditUrl,
            enter = slideInVertically(),
            exit = fadeOut()
        ) {
            AnimatedHintTextField(
                text = imageUrl,
                textChangeHandler = { onImageUrlChange(it) },
                hint = "enter image url",
                error = imageError,
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}