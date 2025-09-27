package com.example.services.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.services.data.ProjectRepository
import com.example.services.screens.*


sealed class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Projects : BottomNavItem("projects", "Proyectos", Icons.Default.Home)
    object Profile : BottomNavItem("profile", "Perfil", Icons.Default.AccountCircle)
    object Settings : BottomNavItem("settings", "Ajustes", Icons.Default.Settings)
}

@Composable
fun AppNavGraph(startDestination: String = "splash") {
    val navController = rememberNavController()
    val items = listOf(BottomNavItem.Projects, BottomNavItem.Profile, BottomNavItem.Settings)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute != "splash" && currentRoute != "login") {
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo("projects") { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {

            // Splash
            composable("splash") {
                SplashScreen {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }

            // Login
            composable("login") {
                LoginScreen(
                    onLogin = { _, _ ->
                        navController.navigate("projects") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onRegister = { navController.navigate("profile") }
                )
            }

            // Secciones principales con BottomBar
            composable("projects") {
                ProjectsListScreen(
                    onProjectClick = { id -> navController.navigate("projectDetail/$id") },
                    onAddProjectClick = { navController.navigate("createProject") }
                )
            }

            composable("profile") {
                UserProfileScreen(
                    userName = "Voluntario Demo",
                    email = "demo@email.com",
                    joinedProjects = 3,
                    onUpdate = { /* luego irá lógica */ }
                )
            }

            composable("settings") {
                SettingsScreen(
                    darkModeEnabled = false,
                    language = "ES",
                    onToggleDarkMode = { },
                    onChangeLanguage = {}
                )
            }

            // Detalle Proyecto
            composable("projectDetail/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toInt() ?: return@composable
                val project = ProjectRepository.getProjectById(id)
                if (project != null) {
                    ProjectDetailScreen(
                        projectName = project.title,
                        description = project.description,
                        onJoinClick = { /* simulado */ },
                        onViewComments = { navController.navigate("comments") }
                    )
                }
            }

            // Crear/editar proyecto
            composable("createProject") {
                CreateEditProjectScreen(
                    projectId = null,
                    onSave = { navController.popBackStack() }
                )
            }

            // Comentarios
            composable("comments") {
                CommentsScreen(
                    comments = listOf("Excelente experiencia", "Muy organizado", "Quiero repetir")
                )
            }

            composable("addComment") {
                AddCommentScreen {
                    navController.popBackStack()
                }
            }
        }
    }
}