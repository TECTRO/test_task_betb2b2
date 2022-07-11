package ru.tectro.quote_viewer_betb2b.domain.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.tectro.quote_viewer_betb2b.domain.navigation.MainNavGraphRoute
import ru.tectro.quote_viewer_betb2b.domain.navigation.mainNavGraph
import ru.tectro.quote_viewer_betb2b.domain.ui.theme.Test_task_betb2bTheme

@AndroidEntryPoint
class RootActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Test_task_betb2bTheme {
                val controller = rememberNavController()

                NavHost(
                    navController = controller,
                    startDestination = MainNavGraphRoute.startDest()
                ) {
                    mainNavGraph()
                }
            }
        }
    }
}