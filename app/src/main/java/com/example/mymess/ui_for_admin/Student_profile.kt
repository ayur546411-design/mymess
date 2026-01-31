package com.example.mymess.ui_for_admin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudentProfileScreen(
    studentId: String,
    onBackClick: () -> Unit = {}
) {
    // Lookup student
    val student = sampleStudents.find { it.id == studentId }

    if (student == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Student not found")
        }
        return
    }

    val totalMeals = student.breakfastCount + student.lunchCount + student.dinnerCount

    // Removed Scaffold as it's provided by MainAppNavigation
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Profile Header Card
            ProfileDetailCard(student)

            // 2. Meal Summary Card
            SummaryCard(student, totalMeals)

            // 3. Attendance History Card
            HistoryCard()
        }
    }
}

// --- Helper Components ---

@Composable
fun ProfileDetailCard(student: Student) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5722)) // Orange
            ) {
                Text(
                    text = student.name.firstOrNull()?.toString() ?: "",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Name
            Text(
                text = student.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Details
            Text(
                text = "Student Number: ${student.studentNumber}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )
            Text(
                text = "Mobile: ${student.mobile}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )
        }
    }
}

@Composable
fun SummaryCard(student: Student, totalMeals: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "इस महीने का Summary", // Hindi/English mix
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(icon = Icons.Outlined.Coffee, count = student.breakfastCount, label = "Breakfast", color = Color(0xFFFF5722))
                StatItem(icon = Icons.Outlined.WbSunny, count = student.lunchCount, label = "Lunch", color = Color(0xFFFFA000))
                StatItem(icon = Icons.Outlined.Bedtime, count = student.dinnerCount, label = "Dinner", color = Color(0xFF8E24AA))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "कुल Meals:", // Hindi/English mix
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )
                Text(
                    text = totalMeals.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32) // Green
                    )
                )
            }
        }
    }
}

@Composable
fun StatItem(icon: ImageVector, count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )
    }
}

@Composable
fun HistoryCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DateRange, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Attendance History",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Static History Item (Example)
            Column {
                Text("2026-01-09", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, fontWeight = FontWeight.SemiBold))
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HistoryChip(Icons.Outlined.Coffee, "Breakfast")
                    HistoryChip(Icons.Outlined.WbSunny, "Lunch")
                    HistoryChip(Icons.Outlined.Bedtime, "Dinner")
                }
            }
        }
    }
}

@Composable
fun HistoryChip(icon: ImageVector, text: String) {
    Surface(
        color = Color(0xFFF5F6F8),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, style = MaterialTheme.typography.bodySmall.copy(color = Color.Black))
        }
    }
}

