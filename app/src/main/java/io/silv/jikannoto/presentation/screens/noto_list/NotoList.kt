package io.silv.jikannoto.presentation.screens.noto_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.silv.jikannoto.domain.models.NotoItem
import io.silv.jikannoto.presentation.atoms.StickyHeaderColumn
import io.silv.jikannoto.presentation.atoms.SwipeToDeleteLayout
import io.silv.jikannoto.presentation.components.*
import io.silv.jikannoto.presentation.navigation.Screens
import io.silv.jikannoto.ui.theme.LocalCustomTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotoListScreen(
    viewModel: NotoListViewModel = getViewModel(),
    showToast: (String) -> Unit,
    onMenuClick: () -> Unit,
    onNavigateToNotoView: (NotoItem?) -> Unit,
    onNavigate: (Screens) -> Unit,
) {

    val state by viewModel.collectAsState()
    viewModel.collectSideEffect {
        when (it) {
            is NotoListSideEffect.Navigate -> onNavigate(it.route)
            is NotoListSideEffect.NotoSaved -> showToast("saved noto")
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    val color = LocalCustomTheme.current

    BottomSheetScaffold(
        floatingActionButton = {
            AnimatedButton(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(35.dp)
                    .offset(y = -(30.dp)),
                fontSize = 18f,
                label = "create noto",
                onClick = {
                    onNavigateToNotoView(null)
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        sheetBackgroundColor = color.drawer,
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(22.dp),
        sheetGesturesEnabled = true,
        sheetContent = {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .requiredHeight(200.dp)
                    .wrapContentHeight()
            ) {
                items(state.categoryList) {
                    Text(text = it)
                }
            }
        }
    ) {
        StickyHeaderColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color.notoListBackGround),
            headerBarHeight = 50f,
            headerBar = {
                HeaderBar(
                    text = state.searchText,
                    onDotsClicked = {
                        scope.launch {
                            if (scaffoldState.bottomSheetState.isExpanded)
                                scaffoldState.bottomSheetState.collapse()
                            else
                                scaffoldState.bottomSheetState.expand()
                        }
                    },
                    textChange = { viewModel.search(it) },
                    onMenuClicked = onMenuClick
                )
            }
        ) {
            Spacer(modifier = Modifier.height(120.dp))
            Text("${state.categoryFilter} notos")
            key(state.notos) {
                state.notos.forEach { noto ->
                    SwipeToDeleteLayout(
                        onDelete = { viewModel.deleteNoto(noto.id) },
                        onClick = { onNavigateToNotoView(noto) }
                    ) {
                        NotoContent(noto = noto)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}