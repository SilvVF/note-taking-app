package io.silv.jikannoto

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import io.silv.jikannoto.data.AppDataStoreRepository
import io.silv.jikannoto.presentation.navigation.Screens
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class MainActivityViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val appDataStoreRepository: AppDataStoreRepository
) : ViewModel(), ContainerHost<AppState, AppGlobalEvent> {

    override val container = container<AppState, AppGlobalEvent>(AppState()) {
        init()
    }

    private fun init() = intent {
        val url = appDataStoreRepository.profileImageUrlFlow.first()
        reduce {
            state.copy(
                imageUrl = url
            )
        }
        viewModelScope.launch {
            collectAppDataStoreData()
            firebaseAuth.addAuthStateListener {
                authEvent(it.currentUser != null)
            }
        }
    }

    private fun authEvent(authed: Boolean) = intent {
        reduce { state.copy(authed = authed) }
    }

    private suspend fun collectAppDataStoreData() = intent {
        appDataStoreRepository.collectAllFlow.collectLatest { data ->
            reduce {
                state.copy(
                    loading = false,
                    darkTheme = data.darkTheme,
                    username = data.firstname to data.lastName,
                )
            }
        }
    }

    @OptIn(OrbitExperimental::class)
    fun handleImageUrlChange(url: String) = blockingIntent {
        reduce {
            state.copy(
                imageUrl = url
            )
        }
        intent { appDataStoreRepository.setProfileImageUrl(url) }
    }
    fun setDarkTheme() = intent {
        appDataStoreRepository.setDarkTheme(!state.darkTheme)
    }

    fun changeScreen(screens: Screens) = intent {
        reduce { state.copy(currScreens = screens) }
    }
    fun navE(sc: String) = intent {
        reduce {
            state.copy(
                currScreens = when (sc) {
                    Screens.UserSettings.route -> Screens.UserSettings
                    Screens.CheckList.route -> Screens.CheckList
                    Screens.Noto.route -> Screens.Noto
                    else -> Screens.CheckList
                }
            )
        }
    }

    val navigationItems = listOf(
        NavItem(icon = Icons.Default.CheckCircle, name = "Checklist", route = Screens.CheckList, i = 0),
        NavItem(painter = R.drawable.inventory_2_48px, name = "Notos", route = Screens.Noto, i = 1),
        NavItem(painter = R.drawable.account_circle_48px, name = "User Settings", route = Screens.UserSettings, i = 2),
    )
}

data class AppState(
    val authed: Boolean = false,
    val darkTheme: Boolean = false,
    val loading: Boolean = true,
    val username: Pair<String, String> = "" to "",
    val currScreens: Screens = Screens.CheckList,
    val imageUrl: String = ""
)
data class NavItem(
    @DrawableRes val painter: Int? = null,
    val icon: ImageVector = Icons.Default.Check,
    val name: String,
    val route: Screens,
    val i: Int
)

sealed class AppGlobalEvent {
    data class ShowSnackBar(val message: String) : AppGlobalEvent()
}