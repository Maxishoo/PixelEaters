package com.example.dz4composekotlin.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dz4composekotlin.ui.contacts.ContactsScreen
import com.example.dz4composekotlin.data.repository.ContactsRepository
import com.example.dz4composekotlin.ui.contacts.ContactsViewModel
import com.example.dz4composekotlin.ui.contant_details.ContactDetailsScreen
import com.example.dz4composekotlin.ui.contant_details.ContactDetailsViewModelFactory
import com.example.dz4composekotlin.data.source.ContactsDataSource
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

@Composable
fun AppNavGraph(
    navController: NavHostController, // теперь приходит снаружи
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "contacts"
    ) {
        composable("contacts") {
            ContactsScreen(
                paddingValues = paddingValues,
                repository = ContactsRepository(ContactsDataSource(LocalContext.current)),
                onContactClick = { contactId ->
                    navController.navigate("details/$contactId")
                }
            )
        }

        composable(
            route = "details/{contactId}",
            arguments = listOf(navArgument("contactId") { type = NavType.LongType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId") ?: 0L
            ContactDetailsScreen(
                paddingValues = paddingValues,
                contactId = contactId,
                repository = ContactsRepository(ContactsDataSource(LocalContext.current))
            )
        }
    }
}