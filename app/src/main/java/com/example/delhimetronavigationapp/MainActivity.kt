package com.example.delhimetronavigationapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.delhimetronavigationapp.ui.theme.DelhiMetroNavigationAppTheme
import com.example.delhimetronavigationapp.MainScreen
import com.example.delhimetronavigationapp.SplashScreen
import com.example.delhimetronavigationapp.createMetroData
import com.example.delhimetronavigationapp.currentmap
import com.example.delhimetronavigationapp.upcomingmap
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.delhimetronavigationapp.ui.theme.RouteDisplayScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            DelhiMetroNavigationAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

// In your Composable function
    val context = LocalContext.current

    NavHost(navController, startDestination = "splashScreen") {
        composable("splashScreen") { SplashScreen(navController) }
        composable("mainscreen") { MainScreen(navController) }
        composable("currentmap") { currentmap() }
        composable("upcomingmap") { upcomingmap() }
        composable("home") {
            MetroRouteFinder(navController)
        }

        // Route display screen
        composable(
            route = "routeDisplay/{sourceId}/{destId}",
            arguments = listOf(
                navArgument("sourceId") { type = NavType.StringType },
                navArgument("destId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sourceId = backStackEntry.arguments?.getString("sourceId") ?: ""
            val destId = backStackEntry.arguments?.getString("destId") ?: ""
            RouteDisplayScreen(sourceId, destId, navController)
        }

    }
}