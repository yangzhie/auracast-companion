package com.innovatex.auracast.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

import com.innovatex.auracast.bluetooth.JourneyViewModel
import com.innovatex.auracast.data.SampleData
import com.innovatex.auracast.ui.components.ActionBar
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
    /* Activity-scoped on purpose. Scoped to the Journey back stack entry,
     * the action bar — which lives above the NavHost — could not reach it
     * to end a journey in progress.
     */
    val journeyViewModel: JourneyViewModel = viewModel()

    Column(modifier = modifier.fillMaxSize()) {

        /* Above the NavHost rather than floating over it, so no screen has
         * to leave room for it and nothing is ever covered.
         */
        ActionBar(
            onRoutes = {
                journeyViewModel.endJourney()
                navController.navigate(RouteSelect) {
                    popUpTo<RouteSelect> { inclusive = true }
                    launchSingleTop = true
                }
            },
            onAccessibility = {
                journeyViewModel.endJourney()
                navController.navigate(Accessibility) { launchSingleTop = true }
            }
        )

        NavHost(
            navController = navController,
            startDestination = SetupCheck,
            modifier = Modifier.fillMaxSize()
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
                    viewModel = journeyViewModel,
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
}