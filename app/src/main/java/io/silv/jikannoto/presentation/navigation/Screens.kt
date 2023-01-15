package io.silv.jikannoto.presentation.navigation

sealed class Screens(val route: String) {
    object Noto : Screens("Noto")
    object UserSettings : Screens("UserSettings")

    object CheckList : Screens("CheckList")
}
