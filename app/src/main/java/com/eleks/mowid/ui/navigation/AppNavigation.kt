package com.eleks.mowid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eleks.mowid.ui.feature.main.MainViewModel

@Composable
fun AppNavigation(activityViewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.Routes.HOME
    ) {
        composable(
            route = Navigation.Routes.HOME
        ) {
            HomeScreenDestination(navController, activityViewModel)
        }

//        composable(
//            route = Navigation.Routes.PHRASES,
//            arguments = listOf(navArgument(name = GROUP_ID) {
//                type = NavType.StringType
//            })
//        ) { backStackEntry ->
//            val groupId = requireNotNull(backStackEntry.arguments?.getString(GROUP_ID)) { "Group id is required as an argument" }
//            PhrasesScreenDestination(
//                groupId = groupId,
//                navController = navController,
//            )
//        }
    }
}

object Navigation {
    object Args {
        const val GROUP_ID = "group_id"
    }

    object Routes {
        const val HOME = "home"
        const val PHRASES = "phrases"
    }
}
