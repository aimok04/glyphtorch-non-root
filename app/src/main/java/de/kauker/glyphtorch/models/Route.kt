package de.kauker.glyphtorch.models

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink

enum class Animation {
    SLIDE_HORIZONTAL,
    SLIDE_VERTICAL
}

class Route(
    route: String,
    animation: Animation,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {

    val route: String
    val animation: Animation
    val arguments: List<NamedNavArgument>
    val deepLinks: List<NavDeepLink>
    val content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit

    init {
        this.route = route
        this.animation = animation
        this.arguments = arguments
        this.deepLinks = deepLinks
        this.content = content
    }

}