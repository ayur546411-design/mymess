package com.example.mymess.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Calendar
import java.util.UUID

// --- Data Models (Top Level) ---

data class Student(
    val id: String,
    val name: String,
    val mobile: String,
    // Meals Logic -- Split by Type
    var remainingBreakfasts: Int = 0,
    var remainingLunches: Int = 0,
    var remainingDinners: Int = 0,
    var remainingSundayMeals: Int = 4,
    // Daily Stats
    var breakfastCount: Int = 0,
    var lunchCount: Int = 0,
    var dinnerCount: Int = 0,
    // State
    var lastAttendanceDate: Long = 0,
    var lastBreakfastDate: Long = 0,
    var lastLunchDate: Long = 0,
    var lastDinnerDate: Long = 0
)

data class Employee(
    val id: String,
    val name: String,
    val role: String,
    val mobile: String
)

data class PaymentRecord(
    val id: String,
    val studentId: String,
    val amount: Int,
    val date: Long,
    val method: String = "Cash"
)

data class AttendanceLog(
    val studentId: String,
    val timestamp: Long,
    val mealType: String
)
object StudentRepository {
    private const val FILE_NAME = "mess_data.json"
    private var context: android.content.Context? = null

    // State Holders
    private val _students = mutableStateListOf<Student>()
    // This helper property is used by the UI
    val students: List<Student> get() = _students

    private val _employees = mutableStateListOf<Employee>()

    val employees: List<Employee> get() = _employees


    private val _payments = mutableStateListOf<PaymentRecord>()
    val payments: List<PaymentRecord> get() = _payments

    private val _attendanceLogs = mutableStateListOf<AttendanceLog>()
    val attendanceLogs: List<AttendanceLog> get() = _attendanceLogs


    // Archives (Memory only for now, or save if needed)
    private val _archivedStudents = mutableListOf<Student>()

    // --- Menu State ---
    var breakfastMenu by mutableStateOf("Aloo Paratha, Curd, Tea")
        private set
    var lunchMenu by mutableStateOf("Rice, Dal Fry, Seasonal Veg")
        private set
    var dinnerMenu by mutableStateOf("Paneer, Roti, Rice")
        private set

    // --- Initialization ---
    fun init(context: android.content.Context) {
        this.context = context
        loadFromDisk()
    }



    fun getStudent(id: String): Student? {
        return _students.find { it.id == id }
    }

    // --- CRUD Operations ---

    fun addStudent(name: String, mobile: String, plans: List<String>): Result<String> {
        if (name.isBlank()) return Result.failure(Exception("Name cannot be empty"))
        if (mobile.length != 10) return Result.failure(Exception("Mobile must be 10 digits"))
        if (plans.isEmpty()) return Result.failure(Exception("Select at least one plan"))

        if (isMobileDuplicate(mobile)) {
            return Result.failure(Exception("Mobile already exists"))
        }

        // 30 days default for selected plans
        val br = if (plans.contains("Breakfast")) 30 else 0
        val ln = if (plans.contains("Lunch")) 30 else 0
        val dn = if (plans.contains("Dinner")) 30 else 0
        val sun = if (plans.contains("Dinner")) 4 else 0

        val newId = generateUniqueId()
        val newStudent = Student(
            id = newId,
            name = name,
            mobile = mobile,
            remainingBreakfasts = br,
            remainingLunches = ln,
            remainingDinners = dn,
            remainingSundayMeals = sun
        )

        _students.add(newStudent)
        saveToDisk()
        return Result.success("Student Added")
    }

    fun updateStudent(id: String, name: String, mobile: String): Result<String> {
        val index = _students.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(Exception("Student not found"))

        if (name.isBlank()) return Result.failure(Exception("Name cannot be empty"))
        if (mobile.length != 10) return Result.failure(Exception("Invalid Mobile"))

        // Check duplicate but exclude self
        if (isMobileDuplicate(mobile, excludeId = id)) {
            return Result.failure(Exception("Mobile already exists"))
        }

        val current = _students[index]
        _students[index] = current.copy(name = name, mobile = mobile)
        saveToDisk()
        return Result.success("Updated")
    }

    fun removeStudent(studentId: String) {
        val student = _students.find { it.id == studentId }
        if (student != null) {
            _students.remove(student)
            _archivedStudents.add(student) // Optional: Save archive to disk if needed
            saveToDisk()
        }
    }

    // --- Meal & Attendance Logic ---

    fun hasTakenMealOnDate(studentId: String, date: Long, mealType: String): Boolean {
        // Simple check: check if any log exists for this student, this date, this meal type
        // Note: 'date' passed here is usually start of day or similar. 
        // We need to check if the timestamp in log falls on the same calendar day.
        
        val callDate = Calendar.getInstance().apply { timeInMillis = date }
        val callDay = callDate.get(Calendar.DAY_OF_YEAR)
        val callYear = callDate.get(Calendar.YEAR)

        return _attendanceLogs.any { log ->
            if (log.studentId != studentId || log.mealType != mealType) return@any false
            
            val logDate = Calendar.getInstance().apply { timeInMillis = log.timestamp }
            logDate.get(Calendar.DAY_OF_YEAR) == callDay && logDate.get(Calendar.YEAR) == callYear
        }
    }

    fun getAttendanceForDate(dateInMillis: Long): List<AttendanceLog> {
        val callDate = Calendar.getInstance().apply { timeInMillis = dateInMillis }
        val callDay = callDate.get(Calendar.DAY_OF_YEAR)
        val callYear = callDate.get(Calendar.YEAR)

        return _attendanceLogs.filter { log ->
            val logDate = Calendar.getInstance().apply { timeInMillis = log.timestamp }
            logDate.get(Calendar.DAY_OF_YEAR) == callDay && logDate.get(Calendar.YEAR) == callYear
        }
    }

    fun markAttendance(studentId: String, mealType: String): Result<String> {
        val student = _students.find { it.id == studentId } ?: return Result.failure(Exception("Student not found"))
        
        // 1. Check if already taken today
        if (hasTakenMealOnDate(studentId, System.currentTimeMillis(), mealType)) {
             return Result.failure(Exception("Already marked for $mealType today"))
        }

        // 2. Check Balance & Sunday Rules
        val calendar = Calendar.getInstance()
        val isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY

        // Logic split by meal type
        var s = student.copy()
        var successMessage = "Attendance Marked for $mealType"

        when(mealType) {
            "Breakfast" -> {
                if (s.remainingBreakfasts <= 0) return Result.failure(Exception("No Breakfast credits left"))
                s.remainingBreakfasts--
                s.breakfastCount++
                s.lastBreakfastDate = System.currentTimeMillis()
            }
            "Lunch" -> {
                if (s.remainingLunches <= 0) return Result.failure(Exception("No Lunch credits left"))
                s.remainingLunches--
                s.lunchCount++
                s.lastLunchDate = System.currentTimeMillis()
            }
            "Dinner" -> {
                 if (isSunday) {
                     if (s.remainingSundayMeals > 0) {
                         s.remainingSundayMeals--
                         successMessage += " (Sunday Special)"
                     } else {
                         // Fallback to regular dinner if Sunday runs out? Or block?
                         // Usually Sunday is separate. Let's strict block if implied.
                         // Or if user wants to use regular dinner? 
                         // Requirement said "Dinner plan includes 4 Sunday Specials".
                         // Let's assume strict Sunday counter for Sunday Dinner.
                         return Result.failure(Exception("No Sunday Special credits left"))
                     }
                 }
                 // Regular dinner logic (for non-Sundays, OR if we treat Sunday decrements separately from main Dinner?)
                 // Re-reading: "Dinner plan includes 4 Sunday Specials".
                 // It implies Sunday Dinner consumes 'remainingSundayMeals', NOT 'remainingDinners'.
                 if (!isSunday) {
                     if (s.remainingDinners <= 0) return Result.failure(Exception("No Dinner credits left"))
                     s.remainingDinners--
                 }
                 
                 s.dinnerCount++
                 s.lastDinnerDate = System.currentTimeMillis()
            }
        }

        s.lastAttendanceDate = System.currentTimeMillis()
        
        // Update List
        val index = _students.indexOfFirst { it.id == studentId }
        _students[index] = s
        
        // Add Log
        _attendanceLogs.add(AttendanceLog(studentId, System.currentTimeMillis(), mealType))
        
        saveToDisk()
        return Result.success(successMessage)
    }

    // --- Renewal & Financials ---

    fun renewStudent(studentId: String, plans: List<String>) {
         val index = _students.indexOfFirst { it.id == studentId }
         if (index == -1) return
         
         val student = _students[index]
         val s = student.copy()
         
         // Add 30 to selected plans (Reset or Add? User said "Renew", usually means setting to full pack or adding.
         // "Reset meal counters to 30" was in the summary. Let's logic: Add 30 to existing? Or set to 30?
         // Previous conversation implied "Initializes... 30". "Reset... to 30".
         // Let's Add 30 so they don't lose previous remainder? 
         // Actually "Reset" usually implies new month. Let's ADD 30 for safety/logic.
         
         if (plans.contains("Breakfast")) s.remainingBreakfasts += 30
         if (plans.contains("Lunch")) s.remainingLunches += 30
         if (plans.contains("Dinner")) {
             s.remainingDinners += 30
             s.remainingSundayMeals += 4
         }
         
         _students[index] = s
         
         // Record Payment (Mock logic for price)
         // Assuming some rate map. For now just generic record.
         val amount = plans.size * 1500 // Dummy calculation
         val payment = PaymentRecord(UUID.randomUUID().toString(), studentId, amount, System.currentTimeMillis(), "Renewal")
         _payments.add(payment)
         
         saveToDisk()
    }
    
    fun getPaymentHistory(studentId: String): List<PaymentRecord> {
        return _payments.filter { it.studentId == studentId }.sortedByDescending { it.date }
    }

    // --- Admin / Employee functions ---

    fun updateMenu(mealType: String, newMenu: String) {
        when(mealType) {
            "Breakfast" -> breakfastMenu = newMenu
            "Lunch" -> lunchMenu = newMenu
            "Dinner" -> dinnerMenu = newMenu
        }
        saveToDisk()
    }

    fun addEmployee(name: String, role: String, mobile: String): Result<String> {
        if (name.isBlank() || role.isBlank()) return Result.failure(Exception("Invalid details"))
        val newEmployee = Employee(UUID.randomUUID().toString(), name, role, mobile)
        _employees.add(newEmployee)
        // saveToDisk() // If employees need saving
        return Result.success("Employee Added")
    }

    // --- Helpers ---

    private fun generateUniqueId(): String {
        var id: String
        do {
            id = kotlin.random.Random.nextInt(1000, 9999).toString()
        } while (_students.any { it.id == id })
        return id
    }

    fun isMobileDuplicate(mobile: String, excludeId: String? = null): Boolean {
        return _students.any { it.mobile == mobile && it.id != excludeId }
    }

    // --- Persistence (JSON) ---

    private fun saveToDisk() {
        val ctx = context ?: return
        try {
            val root = JSONObject()

            // 1. Menu
            val menuObj = JSONObject()
            menuObj.put("breakfast", breakfastMenu)
            menuObj.put("lunch", lunchMenu)
            menuObj.put("dinner", dinnerMenu)
            root.put("menu", menuObj)

            // 2. Students
            val studentsArray = JSONArray()
            _students.forEach { s ->
                val obj = JSONObject()
                obj.put("id", s.id)
                obj.put("name", s.name)
                obj.put("mobile", s.mobile)
                obj.put("rb", s.remainingBreakfasts)
                obj.put("rl", s.remainingLunches)
                obj.put("rd", s.remainingDinners)
                obj.put("rs", s.remainingSundayMeals)
                // Add logic for saving counts/dates if needed
                studentsArray.put(obj)
            }
            root.put("students", studentsArray)

            // Write
            ctx.openFileOutput(FILE_NAME, android.content.Context.MODE_PRIVATE).use {
                it.write(root.toString().toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadFromDisk() {
        val ctx = context ?: return
        val file = File(ctx.filesDir, FILE_NAME)
        if (!file.exists()) return

        try {
            val jsonString = ctx.openFileInput(FILE_NAME).bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            // 1. Menu
            val menuObj = root.optJSONObject("menu")
            if (menuObj != null) {
                breakfastMenu = menuObj.optString("breakfast", breakfastMenu)
                lunchMenu = menuObj.optString("lunch", lunchMenu)
                dinnerMenu = menuObj.optString("dinner", dinnerMenu)
            }

            // 2. Students
            _students.clear()
            val studentsArray = root.optJSONArray("students")
            if (studentsArray != null) {
                for (i in 0 until studentsArray.length()) {
                    val obj = studentsArray.getJSONObject(i)
                    _students.add(Student(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        mobile = obj.optString("mobile"),
                        remainingBreakfasts = obj.optInt("rb", 0),
                        remainingLunches = obj.optInt("rl", 0),
                        remainingDinners = obj.optInt("rd", 0),
                        remainingSundayMeals = obj.optInt("rs", 0)
                    ))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}