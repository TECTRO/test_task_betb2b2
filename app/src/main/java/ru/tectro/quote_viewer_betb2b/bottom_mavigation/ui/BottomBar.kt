package ru.tectro.quote_viewer_betb2b.domain.navigation.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.tectro.quote_viewer_betb2b.bottom_mavigation.entities.NavItem
import ru.tectro.quote_viewer_betb2b.domain.navigation.util.Route

@Composable
fun BottomBar(modifier: Modifier = Modifier, navController: NavController, vararg tab: NavItem, onTabSelected: (Route<*>) -> Unit = {}) {

    BottomNavigation(modifier = modifier, backgroundColor = MaterialTheme.colors.background, elevation = 16.dp) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        tab.forEach { screen ->

            val isItemSelected = currentDestination?.hierarchy?.any { it.route == screen.route.startDest() } == true

            if (isItemSelected)
                onTabSelected(screen.route)

            BottomNavigationItem(
                icon = { screen.icon.asIcon() },
                label = {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = screen.label,
                        overflow = TextOverflow.Visible,
                        fontWeight = FontWeight.SemiBold,
                        softWrap = false,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                },
                selected = isItemSelected,
                onClick = {
                    navController.navigate(screen.route.passRoute()) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
