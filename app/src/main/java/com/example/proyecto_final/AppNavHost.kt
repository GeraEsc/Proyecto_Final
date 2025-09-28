package com.example.proyecto_final

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.core_data.viewmodel.GestorViewModel
import com.example.proyecto_final.navigation.Routes
import com.example.proyecto_final.navigation.Routes.Splash
import com.example.ratings.screens.RatingsScreen
import com.example.registration.Screens.LoginRegisterScreen
import com.example.registration.Screens.ProfileScreen
import com.example.registration.Screens.SettingsScreen
import com.example.services.screens.ActDetailScreen
import com.example.services.screens.ActEditorScreen
import com.example.services.screens.ActsScreen
import com.google.androidgamesdk.gametextinput.Settings
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AppNavHost(
    viewModel: GestorViewModel,
    navController: NavHostController,
    startDestination: String,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    isUserLoggedIn: Boolean,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
) {
    NavHost(navController = navController, startDestination = startDestination) {

        // Rutas de Perfil y Configuracion
        composable(Routes.Settings) {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
                onBack = { navController.popBackStack() }
            )
        }


        // Rutas de Ratings
        composable("act_detail/{actId}") { backStackEntry ->
            val actId = backStackEntry.arguments?.getString("actId") ?: return@composable

            ActDetailScreen(
                actId = actId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEditLocal = { navController.navigate("act_editor?actId=$actId") },
                onSeeReviews = { navController.navigate("ratings/$actId") } // <--- aquí navegas a Ratings
            )
        }

        composable(
            route = "ratings/{actId}",
            arguments = listOf(navArgument("actId") { type = NavType.StringType })
        ) { backStackEntry ->
            val actId = backStackEntry.arguments?.getString("actId") ?: return@composable
            RatingsScreen(
                actId = actId,
                onBack = { navController.popBackStack() }
            )
        }




        // Autenticacion
        composable("auth/login") {
            LoginRegisterScreen(
                onLogin = onLogin,
                onRegister = onRegister,
                onGoogleSignIn = onGoogleSignIn
            )
        }

        // Lista
        composable("ActsList") {
            ActsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Detalle
        composable(
            route = "act_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id")!!
            ActDetailScreen(
                actId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEditLocal = { navController.navigate("act_editor/$id") },
                onSeeReviews = { navController.navigate("ratings/$id") } //Navega a Ratings con el actId
            )
        }

        // Crear
        composable("act_editor") {
            ActEditorScreen(
                viewModel = viewModel,
                onClose = { navController.popBackStack() }
            )
        }

        // Editar (local)
        composable(
            route = "act_editor/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id")
            ActEditorScreen(
                viewModel = viewModel,
                actId = id,
                onClose = { navController.popBackStack() }
            )
        }

        composable(Routes.Splash) {
            SplashScreen(navController = navController,
                onFinished = {
                    if (isUserLoggedIn) {
                        navController.navigate(Routes.ActsList) {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.AuthLogin) {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    }
                }
                )
        }

        // dentro de NavHost(...)
        composable("profile") {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    //Cierra FirebaseAuth
                    FirebaseAuth.getInstance().signOut()


                    //Navega al login
                    navController.navigate("auth/login") {
                        popUpTo(0)
                    }
                }
            )
        }

        // Configuracion
        composable("settings") {
            SettingsScreen(
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
                onBack = { navController.popBackStack() }
            )
        }


    }
}
