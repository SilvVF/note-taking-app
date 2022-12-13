package io.silv.jikannoto.presentation.screens.noto_view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.silv.jikannoto.domain.models.NotoItem
import io.silv.jikannoto.presentation.components.AnimatedButton
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import org.koin.androidx.compose.getViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotoView(
    notoItem: NotoItem?,
    viewModel: NotoViewViewModel = getViewModel(),
    onBackClicked: () -> Unit
) {

    val state by viewModel.collectAsState()
    val scope = rememberCoroutineScope()
    val color = LocalCustomTheme.current

    viewModel.collectSideEffect {
        when (it) {
            is NotoViewSideEffect.NotoSaved -> Unit
        }
    }

    LaunchedEffect(key1 = notoItem) {
        viewModel.initialNotoItem(notoItem)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.addNoto(state.noto)
                            onBackClicked()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back arrow",
                            tint = color.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = color.headerBar
                ),
                title = {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("edit noto item", color = color.text)
                        AnimatedButton(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .height(30.dp),
                            fontSize = 16f,
                            label = "cancel",
                            onClick = {
                                onBackClicked()
                            }
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color.notoViewBackground)
                    .padding(
                        top = paddingValues.calculateTopPadding() + 12.dp
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text("created at ${state.noto.dateCreated.date}")
                TextField(
                    value = state.noto.title,
                    onValueChange = {
                        viewModel.handleTitleChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = color.text
                    ),
                    placeholder = {
                        Text(
                            text = "title",
                            color = color.subtext
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    )
                )
                Column(
                    Modifier
                        .fillMaxSize()
                        .scrollable(rememberScrollState(), Orientation.Vertical)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = state.noto.content,
                        onValueChange = {
                            viewModel.handleContentChange(it)
                        },
                        modifier = Modifier
                            .fillMaxSize(),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = color.text
                        ),
                        placeholder = {
                            Text(
                                text = "tap here to start typing...",
                                color = color.subtext
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            }
        }
    )
}