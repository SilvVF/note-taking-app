@file:OptIn(OrbitExperimental::class)

package io.silv.jikannoto.presentation.screens.user_settings

import androidx.lifecycle.ViewModel
import io.silv.jikannoto.data.AppDataStoreRepository
import io.silv.jikannoto.data.remote.UserRepository
import io.silv.jikannoto.domain.models.User
import io.silv.jikannoto.domain.result.onException
import io.silv.jikannoto.domain.result.onSuccess
import io.silv.jikannoto.presentation.navigation.Screens
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.*
import org.orbitmvi.orbit.viewmodel.container

class UserSettingsViewModel(
    private val userRepository: UserRepository,
    private val appDataStoreRepository: AppDataStoreRepository
) : ViewModel(), ContainerHost<UserSettingsState, UserSettingsScreenEffect> {

    override val container = container<UserSettingsState, UserSettingsScreenEffect>(UserSettingsState()) {
        intent {
            coroutineScope {
                val key = appDataStoreRepository.encryptKeyFlow.first()?.decodeToString() ?: ""
                val initial = appDataStoreRepository.collectAllFlow.first()
                reduce {
                    state.copy(
                        settings = Settings(
                            darkTheme = initial.darkTheme,
                            firstName = initial.firstname,
                            lastName = initial.lastName,
                            alwaysSync = initial.sync,
                            key = key
                        )
                    )
                }
            }
            repeatOnSubscription {
                userRepository.currentUserInfo().collect { user ->
                    reduce { state.copy(currentUser = user) }
                }
            }
        }
    }

    fun sendPasswordReset(email: String) = intent {
        userRepository.sendPasswordReset(email).onSuccess {
            postSideEffect(UserSettingsScreenEffect.PasswordResetSuccess)
        }.onException {
            postSideEffect(UserSettingsScreenEffect.PasswordResetError(it))
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

    fun encryptionKeyTextHandler(key: String) = blockingIntent {
        reduce {
            state.copy(
                settings = state.settings.copy(key = key)
            )
        }
        intent { appDataStoreRepository.setEncryptKey(key.encodeToByteArray()) }
    }

    fun usernameTextHandler(username: String) = blockingIntent {
        reduce { state.copy(username = username) }
    }
    fun passwordTextHandler(password: String) = blockingIntent {
        reduce { state.copy(password = password) }
    }

    fun resetEmailChangeHandler(email: String) = blockingIntent {
        reduce { state.copy(resetEmail = email) }
    }
    fun changeFirstName(firstName: String) = blockingIntent {
        reduce {
            state.copy(
                settings = state.settings.copy(firstName = firstName)
            )
        }
        intent { appDataStoreRepository.setFirstName(firstName) }
    }
    fun changeLastName(lastName: String) = blockingIntent {
        reduce {
            state.copy(
                settings = state.settings.copy(lastName = lastName)
            )
        }
        intent { appDataStoreRepository.setLastName(lastName) }
    }

    fun toggleSync(sync: Boolean) = intent {
        appDataStoreRepository.setSync(sync)
        reduce {
            state.copy(
                settings = state.settings.copy(alwaysSync = sync)
            )
        }
    }
    fun authenticate(username: String, password: String) = intent {
        reduce { state.copy(authInProgress = true) }
        userRepository.login(username, password)
            .onSuccess {
                postSideEffect(UserSettingsScreenEffect.Navigate(Screens.CheckList))
            }
            .onException {
                userRepository.register(username, password)
                    .onSuccess {
                        postSideEffect(UserSettingsScreenEffect.Navigate(Screens.CheckList))
                    }
                reduce { state.copy(error = true, errorMessage = it) }
            }
        reduce { state.copy(authInProgress = false) }
    }

    fun logOut() = intent {
        userRepository.logout().onSuccess {
        }
    }
}
data class UserSettingsState(
    val username: String = "",
    val password: String = "",
    val error: Boolean = false,
    val authInProgress: Boolean = false,
    val errorMessage: String = "",
    val resetEmail: String = "",
    val settings: Settings = Settings(),
    val currentUser: User = User()
)

data class Settings(
    val darkTheme: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val alwaysSync: Boolean = false,
    val key: String = ""
)

sealed class UserSettingsScreenEffect {
    data class Navigate(val route: Screens) : UserSettingsScreenEffect()
    data class PasswordResetError(val e: String) : UserSettingsScreenEffect()
    object PasswordResetSuccess : UserSettingsScreenEffect()

    object LogOutSuccess : UserSettingsScreenEffect()
    object LogOutFailure : UserSettingsScreenEffect()
}
