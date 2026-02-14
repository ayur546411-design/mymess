package com.example.mymess.ui_for_admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mymess.data.Employee
import com.example.mymess.data.StudentRepository

@Composable
fun EmployeeListScreen(
    onEmployeeClick: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val employees = StudentRepository.employees

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Manage Employees",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(employees, key = { it.id }) { employee ->
                    EmployeeListItem(employee, onClick = { onEmployeeClick(employee.id) })
                }
            }
        }
        
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = Color(0xFFFF5722),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }
    
    if (showAddDialog) {
        AddEmployeeDialog(onDismiss = { showAddDialog = false })
    }
}

@Composable
fun EmployeeListItem(employee: Employee, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFF2196F3))
            ) {
                Text(
                    text = employee.name.firstOrNull()?.toString() ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(employee.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(employee.role, style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun AddEmployeeDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Employee") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (e.g. Cook)") })
                OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            }
        },
        confirmButton = {
            Button(onClick = {
                if(name.isNotBlank() && role.isNotBlank()) {
                    StudentRepository.addEmployee(name, role, mobile)
                    onDismiss()
                }
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EmployeeProfileScreen(employeeId: String) {
    val employee = StudentRepository.employees.find { it.id == employeeId }
    
    if (employee == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Employee Not Found") }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFFF8F1)).padding(16.dp)) {
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF2196F3))
                ) {
                    Text(
                        text = employee.name.firstOrNull()?.toString() ?: "",
                        color = Color.White,
                        fontSize = 32.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(employee.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                Text(employee.role, style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF2196F3)))
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Mobile", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
                    Text(employee.mobile, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ID", style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
                    Text(employee.id, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
