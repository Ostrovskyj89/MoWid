package com.eleks.mowid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eleks.mowid.base.ui.EVENTS_KEY
import com.eleks.mowid.ui.feature.main.MainEvent
import com.eleks.mowid.ui.feature.main.MainViewModel
import com.eleks.mowid.ui.navigation.Navigation.Args.GROUP_ID
import com.eleks.mowid.ui.navigation.Navigation.Args.QUOTE_ID
import kotlinx.coroutines.flow.onEach

@Composable
fun AppNavigation(activityViewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.Route.Home.route
    ) {
        composable(
            route = Navigation.Route.Home.route
        ) {
            HomeScreenDestination(
                onNavigateToQuotes = { groupId ->
                    navController.navigate(route = Navigation.Route.Quotes.createRoute(groupId))
                },
                onNavigateToSettings = { navController.navigate(Navigation.Route.Settings.route) }
            )
        }
        composable(
            route = Navigation.Route.Quotes.route,
            arguments = listOf(
                navArgument(name = GROUP_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            QuotesScreenDestination(
                groupName = "",
                onBackClicked = { navController.navigateUp() },
                onNavigateToSettings = { navController.navigate(Navigation.Route.Settings.route) }
            )
        }

        composable(
            route = Navigation.Route.Settings.route
        ) {
            SettingsScreenDestination(activityViewModel) { navController.navigateUp() }
        }
    }

    LaunchedEffect(EVENTS_KEY) {
        activityViewModel.event.onEach { event ->
            when (event) {
                is MainEvent.NavigateToQuote -> {
                    navController.navigate(
                        route = Navigation.Route.Quote.createRoute(event.groupId, event.quoteId)
                    )
                }

                MainEvent.SignIn,
                MainEvent.SignOut,
                -> {
                }
            }
        }
    }

}

object Navigation {
    object Args {

        const val GROUP_ID = "group_id"
        const val GROUP_NAME = "group_name"
        const val QUOTE_ID = "quote_id"
    }

    sealed class Route(val route: String) {
        object Home : Route("Home")
        object Quotes : Route("Quotes/{$GROUP_ID}") {

            fun createRoute(groupId: String) = "Quotes/$groupId"
        }

        object Quote : Route("Quotes/{$GROUP_ID}/{$QUOTE_ID}") {

            fun createRoute(groupId: String, quoteId: String) = "Quotes/$groupId/$quoteId"
        }

        object Settings : Route("Settings")
    }
}
