package com.example.mob23location.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable object Home: Screen()
    @Serializable object Register: Screen()
    @Serializable object Login: Screen()
}