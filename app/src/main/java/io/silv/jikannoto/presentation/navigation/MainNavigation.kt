package io.silv.jikannoto.presentation.navigation

import UserSettingsScreen
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.silv.jikannoto.domain.models.NotoItem
import io.silv.jikannoto.presentation.screens.noto_list.NotoListScreen
import io.silv.jikannoto.presentation.screens.noto_view.NotoView
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainNavigation(
    navController: NavHostController,
    startDest: Screens,
    authed: Boolean,
    showToast: (String) -> Unit,
    onMenuClicked: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDest.route
    ) {
        composable(
            route = Screens.UserSettings.route
        ) {
            UserSettingsScreen(authed) { navTarget ->
                navController.navigate(navTarget.route)
            }
        }
        composable(
            route = Screens.Home.route
        ) {
            val (currentScreen, setCurrentScreen) = rememberSaveable {
                mutableStateOf(1)
            }
            val (currentNoto, setCurrentNoto) = remember {
                mutableStateOf<NotoItem?>(null)
            }

            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    slideInHorizontally {
                        if (this.targetState == 1) -it
                        else it
                    } with slideOutHorizontally {
                        if (this.targetState == 1) it
                        else -it
                    }
                },
                targetState = currentScreen
            ) {
                when (it) {
                    1 -> NotoListScreen(
                        onNavigateToNotoView = { selectedNoto ->
                            setCurrentScreen(2)
                            setCurrentNoto(selectedNoto ?: NotoItem.blankItem())
                        },
                        onNavigate = { navTarget ->
                            navController.navigate(navTarget.route)
                        },
                        onMenuClick = onMenuClicked,
                        showToast = {
                            showToast(it)
                        }
                    )
                    else -> {
                        NotoView(
                            notoItem = currentNoto,
                            onBackClicked = {
                                setCurrentScreen(1)
                            }
                        )
                    }
                }
            }
        }
    }
}