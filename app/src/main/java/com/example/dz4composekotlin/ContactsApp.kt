package com.example.dz4composekotlin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.dz4composekotlin.ui.navigation.AppNavGraph
import com.example.dz4composekotlin.ui.common.ExpandableFab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsApp() {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_logout)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.cd_logout)
                        )
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.title_contacts),
                            modifier = Modifier.clickable {
                                if (currentRoute?.startsWith("details") == true) {
                                    navController.popBackStack("contacts", false)
                                }
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.cd_menu)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = stringResource(R.string.cd_search)
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = stringResource(R.string.cd_notifications)
                            )
                        }
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = stringResource(R.string.cd_profile)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                ExpandableFab(
                    modifier = Modifier
                        .navigationBarsPadding()
                )
            }
        ) { innerPadding: PaddingValues ->
            AppNavGraph(
                paddingValues = innerPadding,
                navController = navController
            )
        }
    }
}
