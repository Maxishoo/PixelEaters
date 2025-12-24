package com.example.pixeleaters.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pixeleaters.ui.screens.ContactsScreen
import com.example.pixeleaters.ui.screens.RemindersScreen

sealed class Screen(val route: String) {
    data object Contacts : Screen("contacts")
    data object Reminders : Screen("reminders")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Contacts.route
    ) {
        composable(
            route = Screen.Contacts.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(180))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(220))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(180))
            }
        ) {
            ContactsScreen(
                onNavigateToReminders = {
                    navController.navigate(Screen.Reminders.route)
                }
            )
        }

        composable(
            route = Screen.Reminders.route,
            enterTransition = {
                fadeIn(animationSpec = tween(240)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(240)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(200)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(240)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(240)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(240)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(240)
                        )
            }
        ) {
            RemindersScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
