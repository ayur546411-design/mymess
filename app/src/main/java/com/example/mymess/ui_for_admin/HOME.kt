package com.example.mymess.ui_for_admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Main Screen Composable ---

@Composable
fun HomeScreen() {
    // --- State Management (The Logic) ---
    // These variables hold the live data. Updating them updates the UI automatically.
    var breakfastCount by remember { mutableIntStateOf(3) }
    var lunchCount by remember { mutableIntStateOf(2) }
    var dinnerCount by remember { mutableIntStateOf(3) }
    var totalStudents by remember { mutableIntStateOf(5) }

    var studentNumberInput by remember { mutableStateOf("") }

    // Logic to mark attendance
    fun markAttendance(type: String) {
        if (studentNumberInput.isNotEmpty()) {
            when (type) {
                "Breakfast" -> breakfastCount++
                "Lunch" -> lunchCount++
                "Dinner" -> dinnerCount++
            }
            // Clear input after marking (optional UX choice)
            studentNumberInput = ""
        }
    }

    Scaffold(
        bottomBar = { HomeBottomNavBar() },
        containerColor = Color(0xFFFFF8F1) // Creamy background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Menu Card (Partially visible in your image)
            MenuCard()

            // 2. Attendance Summary Card (Live Updates)
            AttendanceSummaryCard(breakfastCount, lunchCount, dinnerCount)

            // 3. Quick Attendance Card (Input & Actions)
            QuickAttendanceCard(
                inputValue = studentNumberInput,
                onValueChange = { studentNumberInput = it },
                onMarkAttendance = { type -> markAttendance(type) }
            )

            // 4. Total Students Banner
            TotalStudentsBanner(totalStudents)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- Component 1: Menu Card (Top) ---
@Composable
fun MenuCard() {
    Column() {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Placeholder
                Surface(
                    color = Color(0xFFFFE0B2),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.RestaurantMenu,
                        contentDescription = null,
                        tint = Color(0xFFFF6F00),
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Breakafast",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Text(
                        text = "रोटी, सब्जी, दाल", // Hindi Text
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Placeholder
                Surface(
                    color = Color(0xFFFFE0B2),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.RestaurantMenu,
                        contentDescription = null,
                        tint = Color(0xFFFF6F00),
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Lunch",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Text(
                        text = "रोटी, सब्जी, दाल", // Hindi Text
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Placeholder
            Surface(
                color = Color(0xFFFFE0B2),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.RestaurantMenu,
                    contentDescription = null,
                    tint = Color(0xFFFF6F00),
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Dinner",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
                Text(
                    text = "रोटी, सब्जी, दाल", // Hindi Text
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
    }
}

// --- Component 2: Attendance Summary ---
@Composable
fun AttendanceSummaryCard(breakfast: Int, lunch: Int, dinner: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "आज की Attendance", // Hindi Mixed
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AttendanceStatItem(count = breakfast, label = "Breakfast")
                AttendanceStatItem(count = lunch, label = "Lunch")
                AttendanceStatItem(count = dinner, label = "Dinner")
            }
        }
    }
}

@Composable
fun AttendanceStatItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5722) // Orange color for numbers
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        )
    }
}

// --- Component 3: Quick Attendance (Input & Buttons) ---
@Composable
fun QuickAttendanceCard(
    inputValue: String,
    onValueChange: (String) -> Unit,
    onMarkAttendance: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Quick Attendance",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )
            Text(
                text = "Student Number डालें",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input Field
            OutlinedTextField(
                value = inputValue,
                onValueChange = onValueChange,
                placeholder = { Text("Student Number", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F6F8), RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFFFF5722),
                    focusedContainerColor = Color(0xFFF5F6F8),
                    unfocusedContainerColor = Color(0xFFF5F6F8)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            AttendanceButton(
                text = "Breakfast",
                icon = Icons.Outlined.Coffee,
                color = Color(0xFFFF5722), // Orange
                onClick = { onMarkAttendance("Breakfast") }
            )
            Spacer(modifier = Modifier.height(12.dp))

            AttendanceButton(
                text = "Lunch",
                icon = Icons.Outlined.WbSunny,
                color = Color(0xFFFFA000), // Amber/Yellow
                onClick = { onMarkAttendance("Lunch") }
            )
            Spacer(modifier = Modifier.height(12.dp))

            AttendanceButton(
                text = "Dinner",
                icon = Icons.Outlined.Bedtime, // Moon icon
                color = Color(0xFF8E24AA), // Purple
                onClick = { onMarkAttendance("Dinner") }
            )
        }
    }
}

@Composable
fun AttendanceButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        )
    }
}

// --- Component 4: Total Students Banner ---
@Composable
fun TotalStudentsBanner(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)) // Clip right side
            .background(Color(0xFFE0F2F1)), // Light Greenish Teal
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dark Green Bar on Left
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(50.dp)
                .background(Color(0xFF26A69A))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "कुल Students: $count", // Hindi Mix
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color(0xFF00695C),
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

// --- Component 5: Bottom Navigation ---
@Composable
fun HomeBottomNavBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFF5722),
                selectedTextColor = Color(0xFFFF5722),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Students") },
            label = { Text("Students") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.DateRange, contentDescription = "Records") },
            label = { Text("Records") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.WorkOutline, contentDescription = "Employees") },
            label = { Text("Employees") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}