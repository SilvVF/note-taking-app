package io.silv.jikannoto.presentation.navigation

sealed class Screens(val route: String) {
    object Home : Screens("Home")
    object UserSettings : Screens("Auth")

    object CheckList : Screens("Auth")
    object OnBoarding : Screens("On_Boarding")
}
