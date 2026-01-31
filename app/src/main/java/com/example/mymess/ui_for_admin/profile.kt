package com.example.mymess.ui_for_admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

// --- 1. Data Models (The Logic) ---

data class Payment(
    val id: String,
    val date: String,
    val amount: Double,
    val note: String
)

data class Employee(
    val name: String,
    val phone: String,
    val monthlySalary: Double,
    val payments: List<Payment>
) {
    // Logic: Calculate Total Advances automatically
    val totalAdvances: Double
        get() = payments.sumOf { it.amount }

    // Logic: Calculate Net Salary (Monthly - Advances)
    val netSalary: Double
        get() = monthlySalary - totalAdvances

    val initial: String
        get() = name.firstOrNull()?.toString() ?: ""
}

// --- 2. The Main Screen ---

@Composable
fun ProfileScreen(contentPadding: PaddingValues = PaddingValues()) {
    // Sample Data from your image
    val sampleEmployee = Employee(
        name = "Kuldeep Yadav",
        phone = "7061804004",
        monthlySalary = 5000.0,
        payments = listOf(
            Payment("1", "2026-01-09", 2000.0, "Advance me diya"),
            Payment("2", "2026-01-09", 10000.0, "")
        )
    )

    // Removed Scaffold as it's provided by MainAppNavigation
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
            // 1. Profile Header
            ProfileHeaderCard(sampleEmployee)

            // 2. Salary Details (Logic inside)
            SalaryDetailsCard(sampleEmployee)

            // 3. Advance Payments List
            AdvancePaymentsCard(sampleEmployee)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- 3. Component: Profile Header ---

@Composable
fun ProfileHeaderCard(employee: Employee) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF6F00)) // Orange
            ) {
                Text(
                    text = employee.initial,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Name
            Text(
                text = employee.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Mobile
            Text(
                text = "Mobile: ${employee.phone}",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )
        }
    }
}

// --- 4. Component: Salary Details (Logic Display) ---

@Composable
fun SalaryDetailsCard(employee: Employee) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Salary Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )
                Surface(
                    color = Color(0xFFFFE0B2), // Light Orange
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Edit",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFFFF6F00))
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Monthly Salary
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AttachMoney, // Or custom Rupee icon
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Monthly Salary", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                    Text(
                        text = formatCurrency(employee.monthlySalary),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color(0xFF2E7D32), // Green
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Row: Total Advances & Net Salary
            Row(modifier = Modifier.fillMaxWidth()) {
                // Total Advances
                Column(modifier = Modifier.weight(1f)) {
                    Text("Total Advances", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                    Text(
                        text = formatCurrency(employee.totalAdvances),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFFD32F2F), // Red
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Net Salary
                Column(modifier = Modifier.weight(1f)) {
                    Text("Net Salary", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                    Text(
                        text = formatCurrency(employee.netSalary),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF2E7D32), // Green (as per image, even if negative)
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

// --- 5. Component: Advance Payments ---

@Composable
fun AdvancePaymentsCard(employee: Employee) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFFFF6F00),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Advance Payments",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    )
                }

                // Add Button
                FloatingActionButton(
                    onClick = { /* Add Logic */ },
                    containerColor = Color(0xFFFF6F00),
                    contentColor = Color.White,
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List of Payments
            employee.payments.forEach { payment ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically // Center vertically
                ) {
                    Column {
                        Text(
                            text = payment.date,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        )
                        if (payment.note.isNotEmpty()) {
                            Text(
                                text = payment.note,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                            )
                        }
                    }
                    Text(
                        text = formatCurrency(payment.amount),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFFD32F2F), // Red
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Divider(color = Color.LightGray.copy(alpha = 0.2f))
            }
        }
    }
}

// Helper to format currency
fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return format.format(amount).replace("₹", "₹")
    // Note: Java's default IN locale might output "Rs.", simple replacement ensures symbol if needed.
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}