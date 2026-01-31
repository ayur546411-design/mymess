package com.example.mymess.ui_for_admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// --- Main Screen Composable ---

@Composable
fun AdminDashboardScreen(navController: androidx.navigation.NavController) {
    // --- State Management (The Logic) ---
    // These variables hold the live data. Updating them updates the UI automatically.
    var breakfastCount by remember { mutableIntStateOf(3) }
    var lunchCount by remember { mutableIntStateOf(2) }
    var dinnerCount by remember { mutableIntStateOf(3) }
    var totalStudents by remember { mutableIntStateOf(5) }

    var studentNumberInput by remember { mutableStateOf("") }

    // Menu State
    var breakfastMenu by remember { mutableStateOf("रोटी, सब्जी, दाल") }
    var lunchMenu by remember { mutableStateOf("रोटी, सब्जी, दाल") }
    var dinnerMenu by remember { mutableStateOf("रोटी, सब्जी, दाल") }

    // Dialog State
    var showEditDialog by remember { mutableStateOf(false) }
    var editingMealType by remember { mutableStateOf("") }
    var editingMenuValue by remember { mutableStateOf("") }

    // Attendance Tracking & Reset Logic
    val breakfastStudents = remember { mutableStateListOf<String>() }
    val lunchStudents = remember { mutableStateListOf<String>() }
    val dinnerStudents = remember { mutableStateListOf<String>() }
    
    // Using current system time for simplistic day check. 
    // In production, might persist this key to SharedPreferences/DataStore.
    var lastResetDate by remember { mutableStateOf(java.time.LocalDate.now()) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun checkAndReset() {
        val today = java.time.LocalDate.now()
        if (!today.isEqual(lastResetDate)) {
            // New Day - Reset Everything
            breakfastCount = 0
            lunchCount = 0
            dinnerCount = 0
            breakfastStudents.clear()
            lunchStudents.clear()
            dinnerStudents.clear()
            lastResetDate = today
            // Show reset message
             CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                snackbarHostState.showSnackbar("New Day: Attendance Reset")
            }
        }
    }

    // Logic to mark attendance
    fun markAttendance(type: String) {
        checkAndReset() // Ensure it's the correct day first

        if (studentNumberInput.isNotEmpty()) {
            val studentId = studentNumberInput.trim()
            var isDuplicate = false
            
            when (type) {
                "Breakfast" -> {
                    if (breakfastStudents.contains(studentId)) isDuplicate = true
                    else {
                        breakfastStudents.add(studentId)
                        breakfastCount++
                    }
                }
                "Lunch" -> {
                     if (lunchStudents.contains(studentId)) isDuplicate = true
                     else {
                         lunchStudents.add(studentId)
                         lunchCount++
                     }
                }
                "Dinner" -> {
                     if (dinnerStudents.contains(studentId)) isDuplicate = true
                     else {
                         dinnerStudents.add(studentId)
                         dinnerCount++
                     }
                }
            }

            scope.launch {
                if (isDuplicate) {
                    snackbarHostState.showSnackbar("Student $studentId already marked for $type")
                } else {
                    snackbarHostState.showSnackbar("Marked $type for Student $studentId")
                    // Clear input after marking (optional UX choice)
                    studentNumberInput = ""
                }
            }
        }
    }

    fun openEditDialog(type: String) {
        editingMealType = type
        editingMenuValue = when(type) {
            "Breakfast" -> breakfastMenu
            "Lunch" -> lunchMenu
            "Dinner" -> dinnerMenu
            else -> ""
        }
        showEditDialog = true
    }
    
    // Check reset on initial composition
    LaunchedEffect(Unit) {
        checkAndReset()
    }

    // Main Content - Removed Scaffold as it's provided by MainAppNavigation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F1)) // Creamy background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Menu Card (Partially visible in your image)
            MenuCard(
                breakfast = breakfastMenu,
                lunch = lunchMenu,
                dinner = dinnerMenu,
                onEditClick = { type -> openEditDialog(type) }
            )

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

        if (showEditDialog) {
            MenuEditDialog(
                mealType = editingMealType,
                initialValue = editingMenuValue,
                onDismiss = { showEditDialog = false },
                onSave = { newValue ->
                    when(editingMealType) {
                        "Breakfast" -> breakfastMenu = newValue
                        "Lunch" -> lunchMenu = newValue
                        "Dinner" -> dinnerMenu = newValue
                    }
                    showEditDialog = false
                }
            )
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MenuEditDialog(
    mealType: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit $mealType Menu") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Menu Items") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// --- Component 1: Menu Card (Top) ---
@Composable
fun MenuCard(
    breakfast: String,
    lunch: String,
    dinner: String,
    onEditClick: (String) -> Unit
) {
    Column() {
        MenuResCard("Breakfast", breakfast) { onEditClick("Breakfast") }
        Spacer(modifier = Modifier.height(10.dp))
        MenuResCard("Lunch", lunch) { onEditClick("Lunch") }
        Spacer(modifier = Modifier.height(10.dp))
        MenuResCard("Dinner", dinner) { onEditClick("Dinner") }
    }
}

@Composable
fun MenuResCard(title: String, menu: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
                Text(
                    text = menu, // Dynamic Text
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color.Gray)
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

// Removed HomeBottomNavBar as it is now in MainAppNavigation
