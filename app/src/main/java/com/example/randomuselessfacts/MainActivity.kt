package com.example.randomuselessfacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.randomuselessfacts.ui.DailyFactPage
import com.example.randomuselessfacts.ui.MainViewModel
import com.example.randomuselessfacts.ui.SavedFactsPage
import com.example.randomuselessfacts.ui.theme.RandomUselessFactsTheme
import com.example.randomuselessfacts.util.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RandomUselessFactsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun Navigation(
    viewModel: MainViewModel
) {

    LaunchedEffect(true) {
        viewModel.initialiseSavedFacts()
        viewModel.getDailyFact()
    }

    val navController = rememberNavController()
    val items = listOf(Screen.DailyFact, Screen.SavedFacts)

    Scaffold(
        topBar = {
            TopAppBar {
                Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.h6)
            }
        },
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, null) },
                        label = { Text(text = screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.DailyFact.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.DailyFact.route) {
                DailyFactPage(viewModel = viewModel)
            }
            composable(Screen.SavedFacts.route) {
                SavedFactsPage(viewModel = viewModel)
            }
        }
    }
}
