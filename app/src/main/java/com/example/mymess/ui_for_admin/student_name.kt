package com.example.mymess.ui_for_admin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- 1. Shared Data Model ---
data class Student(
    val id: String,
    val name: String,
    val studentNumber: String,
    val mobile: String,
    // Daily Stats
    val breakfastCount: Int = 0,
    val lunchCount: Int = 0,
    val dinnerCount: Int = 0
)

// Sample Data
val sampleStudents = listOf(
    Student("1", "Gulshan Kumar", "103", "7061804004", 2, 1, 3),
    Student("2", "Iraj Kumar", "101", "8651629553", 1, 1, 1),
    Student("3", "Ayush Kumar", "102", "8651629553", 5, 4, 5),
    Student("4", "Chandan Mahamya", "104", "7061804004", 0, 0, 0),
    Student("5", "Kuldeep Yadav", "105", "7061804004", 3, 3, 3)
)

// --- 2. Main Navigation Host (Entry Point) ---
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "name_of_student") {

        // Route 1: List of Students
        composable("name_of_student") {
            NameOfStudentScreen(
                students = sampleStudents,
                onStudentClick = { studentId ->
                    navController.navigate("student_profile/$studentId")
                }
            )
        }

        // Route 2: Student Profile (Defined in StudentProfile.kt)
        composable("student_profile/{studentId}") { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId")
            val student = sampleStudents.find { it.id == studentId }

            if (student != null) {
                // We call the screen from the other file here
                StudentProfileScreen(
                    student = student,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

// --- 3. The List Screen Composable ---
@Composable
fun NameOfStudentScreen(
    students: List<Student>,
    onStudentClick: (String) -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFFFF8F1), // Cream Background
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add Student Logic */ },
                containerColor = Color(0xFFFF5722), // Orange
                shape = CircleShape
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add", tint = Color.White)
            }
        },
        bottomBar = {
            // Simple placeholder bottom bar
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Outlined.Person, null) }, label = { Text("Students") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFFF5722), selectedTextColor = Color(0xFFFF5722), indicatorColor = Color.Transparent))
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.DateRange, null) }, label = { Text("Records") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.WorkOutline, null) }, label = { Text("Employees") })
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Students",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Total: ${students.size}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                }
                // User Profile Icon (Top Right)
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFF5722),
                    modifier = Modifier.size(40.dp).clickable { }
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(students) { student ->
                    StudentListItem(student, onClick = { onStudentClick(student.id) })
                }
            }
        }
    }
}

@Composable
fun StudentListItem(student: Student, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Number: ${student.studentNumber}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                Text(
                    text = "Mobile: ${student.mobile}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                )
            }

            // Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color(0xFFE57373), // Reddish
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}