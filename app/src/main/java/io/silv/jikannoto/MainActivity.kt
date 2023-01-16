package io.silv.jikannoto

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.silv.jikannoto.presentation.components.DrawerContentNav
import io.silv.jikannoto.presentation.navigation.MainNavigation
import io.silv.jikannoto.ui.theme.JikanNotoTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.orbitmvi.orbit.compose.collectAsState

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // make fully Android Transparent Status bar
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT

        val viewModel: MainActivityViewModel by inject()

        setContent {

            val state by viewModel.collectAsState()
            val navHostController = rememberNavController()
            val destination = navHostController.currentBackStackEntryAsState().value?.destination?.route
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val coroutine = rememberCoroutineScope()

            LaunchedEffect(key1 = destination) {
                if (destination != null)
                    viewModel.navE(destination)
            }

            JikanNotoTheme(state.darkTheme) {
                ModalNavigationDrawer(
                    drawerContent = {
                        DrawerContentNav(
                            drawerState = drawerState,
                            navigationItems = viewModel.navigationItems,
                            selectedScreen = state.currScreens,
                            navigate = {
                                viewModel.changeScreen(it)
                                navHostController.navigate(it.route)
                                coroutine.launch {
                                    drawerState.close()
                                }
                            },
                            darkTheme = state.darkTheme,
                            onToggleDarkTheme = {
                                viewModel.setDarkTheme()
                            },
                            name = state.username
                        )
                    },
                    drawerState = drawerState,
                    content = {
                        MainNavigation(
                            navController = navHostController,
                            authed = state.authed,
                            name = "${state.username.first} ${state.username.second}",
                            onMenuClicked = {
                                coroutine.launch {
                                    drawerState.open()
                                }
                            },
                            showToast = {
                                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
    private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win: Window = activity.window
        val winParams: WindowManager.LayoutParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
}