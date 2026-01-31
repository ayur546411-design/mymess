package com.example.mymess.ui_for_admin
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- 1. Shared Data Model ---
// Make sure this is only defined once. If it's used elsewhere, consider moving to a separate file.
// For now, I'll keep it here but we should be careful about duplication if it was in Student_profile.kt too.
// I see Student_profile.kt uses this class, so it needs to be accessible. 
// Ideally this moves to a model file, but for this refactor I will leave it or ensure it's public.
// Assuming Student is defined here and imported elsewhere.

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

// --- 3. The List Screen Composable ---
// Renamed arguments to default for easier usage if needed, but keeping signature for caller
@Composable
fun NameOfStudentScreen(
    students: List<Student> = sampleStudents,
    onStudentClick: (String) -> Unit
) {
    // Scaffold removed, using Box for structure if needed, or just Column
    // FloatingActionButton was in Scaffold, we can put it in a Box with alignment
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFFFF8F1))
    ) {
        Column(
            modifier = Modifier
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
        
        // FAB
        FloatingActionButton(
            onClick = { /* Add Student Logic */ },
            containerColor = Color(0xFFFF5722), // Orange
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = "Add", tint = Color.White)
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

@Preview(showBackground = true)
@Composable
fun PreviewNameOfStudentScreen() {
    NameOfStudentScreen(students = sampleStudents, onStudentClick = {})
}