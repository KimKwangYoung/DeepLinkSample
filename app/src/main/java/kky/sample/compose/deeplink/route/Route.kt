package kky.sample.compose.deeplink.route

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data class DeepLink(
        val number: Int
    ) : Route
}