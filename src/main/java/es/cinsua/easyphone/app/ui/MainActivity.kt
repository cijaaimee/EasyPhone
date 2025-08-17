package es.cinsua.easyphone.app.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.cinsua.easyphone.app.ui.components.EasyPhoneScaffold
import es.cinsua.easyphone.app.ui.dialer.DialerScreen
import es.cinsua.easyphone.app.ui.home.HomeScreen
import es.cinsua.easyphone.app.ui.launcher.LauncherScreen

class MainActivity : EasyActivity() {

  private lateinit var navController: NavHostController
  private var returningToActivity = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      navController = rememberNavController()
      EasyPhoneScaffold { paddingValues ->
        MainNavigation(modifier = Modifier.padding(paddingValues), navController = navController)
      }
    }
  }

  override fun onStart() {
    super.onStart()
    if (returningToActivity) {
      if (navController.currentDestination?.id != navController.graph.startDestinationId) {
        navController.navigate(NavRoutes.HOME)
      }
    }
    returningToActivity = true
  }
}

object NavRoutes {
  const val HOME = "home"
  const val DIALER = "dialer"
  const val LAUNCHER = "phone"
}

@Composable
private fun MainNavigation(modifier: Modifier, navController: NavHostController) {
  NavHost(
      modifier = modifier,
      navController = navController,
      startDestination = NavRoutes.HOME,
      enterTransition = { EnterTransition.None },
      exitTransition = { ExitTransition.None }) {
        screen(route = NavRoutes.HOME) { HomeScreen { route -> navController.navigate(route) } }

        screen(route = NavRoutes.DIALER) { DialerScreen { navController.navigate(NavRoutes.HOME) } }

        screen(route = NavRoutes.LAUNCHER) {
          LauncherScreen { navController.navigate(NavRoutes.HOME) }
        }
      }
}

/**
 * Wrapper around composable to add a background to avoid both screens being visible for a short
 * period of time while navigating.
 */
fun NavGraphBuilder.screen(
    route: String,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
  composable(route) { backStackEntry ->
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
      content(backStackEntry)
    }
  }
}
