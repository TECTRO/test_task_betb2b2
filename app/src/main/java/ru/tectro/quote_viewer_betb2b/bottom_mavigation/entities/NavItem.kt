package ru.tectro.quote_viewer_betb2b.bottom_mavigation.entities

import ru.tectro.quote_viewer_betb2b.domain.navigation.util.Route

data class NavItem(
    val icon: IconResource,
    val label: String,
    val badges: Int = 0,
    val route: Route<*>
)
