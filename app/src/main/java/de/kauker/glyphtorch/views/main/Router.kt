package de.kauker.glyphtorch.views.main

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import de.kauker.glyphtorch.models.Animation
import de.kauker.glyphtorch.models.AppViewModel
import de.kauker.glyphtorch.models.Route
import de.kauker.glyphtorch.views.routes.legal.LegalRoute
import de.kauker.glyphtorch.views.routes.main.MainRoute
import de.kauker.glyphtorch.views.routes.requestUnlock.RequestUnlockRoute

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainRouter(vm: AppViewModel) {
    val navHostController = rememberAnimatedNavController()
    vm.navHostController = navHostController

    val routes = listOf(
        Route("main", Animation.SLIDE_VERTICAL) { MainRoute(vm = vm, bse = it) },
        Route("legal", Animation.SLIDE_HORIZONTAL) { LegalRoute(vm = vm, bse = it) },

        Route("requestUnlock", Animation.SLIDE_VERTICAL) { RequestUnlockRoute(vm = vm, bse = it) }
    )

    AnimatedNavHost(
        navHostController,
        startDestination = vm.startDestination
    ) {
        routes.forEach { route ->
            when (route.animation) {
                Animation.SLIDE_HORIZONTAL -> {
                    composable(
                        route = route.route,
                        arguments = route.arguments,
                        deepLinks = route.deepLinks,
                        content = route.content,
                        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500)) },
                        exitTransition = { slideOutHorizontally(targetOffsetX = { - it }, animationSpec = tween(500)) },
                        popEnterTransition = { slideInHorizontally(initialOffsetX = { - it }, animationSpec = tween(500)) },
                        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500)) }
                    )
                }
                Animation.SLIDE_VERTICAL -> {
                    composable(
                        route = route.route,
                        arguments = route.arguments,
                        deepLinks = route.deepLinks,
                        content = route.content,
                        enterTransition = { slideInVertically (initialOffsetY = { it }, animationSpec = tween(500)) },
                        exitTransition = { slideOutVertically (targetOffsetY = { - it }, animationSpec = tween(500)) },
                        popEnterTransition = { slideInVertically (initialOffsetY = { - it }, animationSpec = tween(500)) },
                        popExitTransition = { slideOutVertically (targetOffsetY = { it }, animationSpec = tween(500)) }
                    )
                }
            }
        }
    }
}