package com.eleks.mowid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eleks.mowid.ui.navigation.Navigation.Args.GROUP_ID
import com.eleks.mowid.ui.navigation.Navigation.Args.GROUP_NAME

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.Route.HOME.route
    ) {
        composable(
            route = Navigation.Route.HOME.route
        ) {
            HomeScreenDestination { groupId, groupName ->
                navController.navigate(Navigation.Route.Quotes.createRoute(groupId, groupName))
            }
        }

        composable(
            route = Navigation.Route.Quotes.route,
            arguments = listOf(
                navArgument(name = GROUP_ID) {
                    type = NavType.StringType
                },
                navArgument(name = GROUP_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val groupName = requireNotNull(backStackEntry.arguments?.getString(GROUP_NAME))
            QuotesScreenDestination(groupName = groupName) {
                navController.navigateUp()
            }
        }
    }
}

object Navigation {
    object Args {
        const val GROUP_ID = "group_id"
        const val GROUP_NAME = "group_name"
    }

    sealed class Route(val route: String) {
        object HOME : Route("Home")
        object Quotes : Route("Quotes/{$GROUP_ID}/{$GROUP_NAME}") {
            fun createRoute(groupId: String, groupName: String) = "Quotes/$groupId/$groupName"
        }
    }
}
