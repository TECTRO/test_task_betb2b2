package ru.tectro.quote_viewer_betb2b.domain.navigation.util

import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

abstract class Route<T>(route: String? = null, private val navArgs: Map<T, NavType<*>> = emptyMap()) {
    private val provideRoute: String = route ?: this::class.java.name

    fun passRoute(): String = provideRoute + navArgs.map { "${it.key}={${it.key}}" }.joinToString(separator = "&", prefix = if (navArgs.any()) "?" else "")
    fun passArguments() = navArgs.map { navArgument(it.key.toString()) { type = it.value; nullable = true; defaultValue = null } }

    fun navigate(args: (key: T) -> Any? = { null }): String = provideRoute + navArgs.mapNotNull { args(it.key)?.let { arg -> "${it.key}=$arg" } }.let { it.joinToString(separator = "&", prefix = if (it.any()) "?" else "") }
    fun popTo() = provideRoute
    fun startDest() = provideRoute
}
