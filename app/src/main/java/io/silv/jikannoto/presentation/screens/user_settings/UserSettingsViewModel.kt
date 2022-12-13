package io.silv.jikannoto.presentation.screens.user_settings

import androidx.lifecycle.ViewModel
import io.silv.jikannoto.data.AppDataStoreRepository
import io.silv.jikannoto.domain.UserRepository
import io.silv.jikannoto.domain.result.onException
import io.silv.jikannoto.domain.result.onSuccess
import io.silv.jikannoto.presentation.navigation.Screens
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class UserSettingsViewModel(
    private val userRepository: UserRepository,
    private val appDataStoreRepository: AppDataStoreRepository
) : ViewModel(), ContainerHost<UserSettingsState, UserSettingsScreenEffect> {

    override val container = container<UserSettingsState, UserSettingsScreenEffect>(UserSettingsState()) {
        intent {
            val initial = appDataStoreRepository.collectAllFlow.first()
            reduce {
                state.copy(
                    settings = Settings(
                        darkTheme = initial.darkTheme,
                        firstName = initial.firstname,
                        lastName = initial.lastName,
                        alwaysSync = false
                    )
                )
            }
        }
    }

    fun changeDarkTheme(darkTheme: Boolean) = intent {
        appDataStoreRepository.setDarkTheme(!darkTheme)
        reduce {
            state.copy(
                settings = state.settings.copy(darkTheme = !darkTheme)
            )
        }
    }
    fun usernameTextHandler(username: String) = intent(false) {
        reduce { state.copy(username = username) }
    }
    fun passwordTextHandler(password: String) = intent {
        reduce { state.copy(password = password) }
    }

    fun changeFirstName(firstName: String) = intent {
        reduce {
            state.copy(
                settings = state.settings.copy(firstName = firstName)
            )
        }
        appDataStoreRepository.setFirstName(firstName)
    }
    fun changeLastName(lastName: String) = intent {
        reduce {
            state.copy(
                settings = state.settings.copy(lastName = lastName)
            )
        }
        appDataStoreRepository.setLastName(lastName)
    }

    fun authenticate(username: String, password: String) = intent {
        reduce { state.copy(authInProgress = true) }
        userRepository.login(username, password)
            .onSuccess {
                postSideEffect(UserSettingsScreenEffect.Navigate(Screens.Home))
            }
            .onException {
                userRepository.register(username, password)
                    .onSuccess {
                        postSideEffect(UserSettingsScreenEffect.Navigate(Screens.Home))
                    }
                reduce { state.copy(error = true, errorMessage = it) }
            }
        reduce { state.copy(authInProgress = false) }
    }
}
data class UserSettingsState(
    val username: String = "",
    val password: String = "",
    val error: Boolean = false,
    val authInProgress: Boolean = false,
    val errorMessage: String = "",
    val settings: Settings = Settings()
)

data class Settings(
    val darkTheme: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val alwaysSync: Boolean = false,
)

sealed class UserSettingsScreenEffect {
    data class Navigate(val route: Screens) : UserSettingsScreenEffect()
}
