package com.example.mymess

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mymess.navigation.Screen
import com.example.mymess.ui.auth.LoginScreen
import com.example.mymess.ui_for_admin.AdminDashboardScreen
import com.example.mymess.ui_for_admin.NameOfStudentScreen
import com.example.mymess.ui_for_user.UserHomeScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.mymess.ui.auth.LoginScreen
import com.example.mymess.ui_for_admin.*
import com.example.mymess.ui_for_user.UserHomeScreen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


enum class ScreenName{


}





@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()

    // Determine if we should show the bottom bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route != Screen.Login.route

    // Define Navigation Items for Roles
    val adminNavItems = listOf(
        Screen.AdminDashboard,
        Screen.StudentList,
        Screen.Records,
        Screen.EmployeeProfile
    )
    
    // For User, currently we only have Dashboard. 
    // We can add more user-specific screens later (e.g., Profile, Complaints).
    val userNavItems = listOf(
        Screen.UserDashboard
        // Screen.UserProfile // Example for future
    )

    // Determine which items to show based on current route
    // We can infer the role based on the current screen. 
    // If the user is on a User screen, show User Nav. If Admin, show Admin Nav.
    val isUserRoute = currentDestination?.route == Screen.UserDashboard.route 
    // Add other user routes here using || comparison if needed

    val navItems = if (isUserRoute) userNavItems else adminNavItems

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { 
                                screen.icon?.let { 
                                    Icon(it, contentDescription = screen.title) 
                                } 
                            },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFF5722),
                                selectedTextColor = Color(0xFFFF5722),
                                indicatorColor = Color.Transparent,
                                unselectedIconColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(navController)
            }
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(navController)
            }
            composable(Screen.UserDashboard.route) {
                UserHomeScreen(navController)
            }
            composable(Screen.StudentList.route) {
                // Pass a callback to navigate to profile
                NameOfStudentScreen(
                    onStudentClick = { studentId ->
                       navController.navigate(Screen.StudentProfile.createRoute(studentId))
                    }
                )
            }
            composable(
                route = Screen.StudentProfile.route,
                arguments = listOf(navArgument("studentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString("studentId")
                // We'll update StudentProfile to take ID or load logic
                // For now assuming StudentProfileScreen exists and can handle it
                // We might need to refactor StudentProfileScreen signature.
                // Assuming we pass the ID to it.
                if (studentId != null) {
                    StudentProfileScreen(studentId = studentId)
                }
            }
            composable(Screen.EmployeeProfile.route) {
                ProfileScreen()
            }
             composable(Screen.Records.route) {
                // Placeholder for Records
                Text("Records Screen Placeholder")
            }
            composable("user_dashboard") {
                UserHomeScreen(navController)
            }
        }
    }
}
