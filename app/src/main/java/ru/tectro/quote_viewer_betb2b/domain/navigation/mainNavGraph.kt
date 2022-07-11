package ru.tectro.quote_viewer_betb2b.domain.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.tectro.quote_viewer_betb2b.bottom_mavigation.entities.IconResource
import ru.tectro.quote_viewer_betb2b.bottom_mavigation.entities.NavItem
import ru.tectro.quote_viewer_betb2b.bottom_mavigation.navigation.BottomNavigationRoute
import ru.tectro.quote_viewer_betb2b.domain.navigation.components.BottomNavScreen
import ru.tectro.quote_viewer_betb2b.main_screen.ui.MainScreen
import ru.tectro.quote_viewer_betb2b.favourites_screen.navigation.FavoriteScreenRoute
import ru.tectro.quote_viewer_betb2b.main_screen.navigation.MainScreenRoute

fun NavGraphBuilder.mainNavGraph() {
    navigation(
        route = MainNavGraphRoute.passRoute(),
        startDestination = BottomNavigationRoute.startDest()
    ) {
        composable(
            route = BottomNavigationRoute.passRoute()
        ) {
            BottomNavScreen(
                navItems = arrayOf(
                    NavItem(
                        route = MainScreenRoute,
                        badges = 0,
                        icon = IconResource.VectorResource(Icons.Rounded.Home),
                        label = "Главная"
                    ),
                    NavItem(
                        route = FavoriteScreenRoute,
                        badges = 0,
                        icon = IconResource.VectorResource(Icons.Rounded.Star),
                        label = "Избранное"
                    )
                ),
                startDestination = MainScreenRoute,
                contentNavGraph = { padding, _ ->
                    composable(
                        route = MainScreenRoute.passRoute()
                    ) {
                        Box(modifier = Modifier.padding(padding)){
                            MainScreen()
                        }
                    }

                    composable(
                        route = FavoriteScreenRoute.passRoute()
                    ) {
                        Box(modifier = Modifier.padding(padding)){

                        }
                    }
                }
            )
        }
    }
}