package com.innovatex.auracast.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

import com.innovatex.auracast.data.SampleData
import com.innovatex.auracast.ui.screens.AccessibilityScreen
import com.innovatex.auracast.ui.screens.ArrivedScreen
import com.innovatex.auracast.ui.screens.JourneyRoute
import com.innovatex.auracast.ui.screens.RouteScreen
import com.innovatex.auracast.ui.screens.SetupCheckScreen

@Serializable object SetupCheck

@Serializable object RouteSelect

@Serializable object Arrived

@Serializable object Accessibility

/** The real journey — scanner running, state driven by MatchingEngine. */
@Serializable data class Journey(val routeId: String)

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = RouteSelect,
        modifier = modifier
    ) {
        composable<SetupCheck> {
            SetupCheckScreen(
                onContinue = { navController.navigate(RouteSelect) }
            )
        }

        composable<RouteSelect> {
            RouteScreen(
                onContinue = { route ->
                    navController.navigate(Journey(route.id))
                }
            )
        }

        composable<Journey> { backStackEntry ->
            val args = backStackEntry.toRoute<Journey>()
            JourneyRoute(
                route = SampleData.routeById(args.routeId),
                onEndJourney = {
                    navController.navigate(Arrived) { popUpTo<SetupCheck>() }
                },
                onOpenAccessibility = { navController.navigate(Accessibility) }
            )
        }

        composable<Arrived> {
            ArrivedScreen(
                onPlanAnother = {
                    navController.navigate(SetupCheck) {
                        popUpTo<SetupCheck> { inclusive = true }
                    }
                }
            )
        }

        composable<Accessibility> {
            AccessibilityScreen(
                onDone = { navController.popBackStack() }
            )
        }
    }
}