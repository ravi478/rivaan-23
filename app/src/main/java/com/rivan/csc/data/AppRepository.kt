package com.rivan.csc.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rivan.csc.model.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AppRepository {
    var isLoggedIn by mutableStateOf(true)
    var currentRole by mutableStateOf("admin") // "admin" or "operator"
    var currentUserId by mutableStateOf("ADMIN-01")
    var currentUserName by mutableStateOf("Ravi Kumar")
    var currentCscId by mutableStateOf("ALL")
    var isDarkTheme by mutableStateOf(false)
    var selectedCscFilter by mutableStateOf("ALL")
    var appName by mutableStateOf("RIVAN CSC Manager")
    var tagline by mutableStateOf("Digital Seva & 100 CSC Network")

    val today: String = LocalDate.now().toString()

    val cscs = mutableStateListOf(
        CscCenter("CSC-001", "Gudhan CSC (Main)", "Gudhan", "Rohtak", "9812011111", "Ravi Kumar", "09:00 - 18:00", "Active"),
        CscCenter("CSC-002", "Kalanaur Digital Kendra", "Kalanaur", "Rohtak", "9812022222", "Sandeep Sharma", "09:00 - 18:00", "Active"),
        CscCenter("CSC-003", "Julana Seva Center", "Julana", "Jind", "9812033333", "Amit Verma", "09:30 - 17:30", "Inactive")
    )

    val operators = mutableStateListOf(
        Operator("OP-001", "Sandeep Sharma", "9812022222", "CSC-002", "2026-01-15", 12000, "Active"),
        Operator("OP-002", "Pooja Rani", "9812044444", "CSC-001", "2026-03-01", 11000, "Active"),
        Operator("OP-003", "Vikram Singh", "9812055555", "CSC-001", "2026-11-01", 10000, "Pending")
    )

    val services = mutableStateListOf(
        CscService("SRV-01", "PM Kisan Registration", "Agriculture", 30, "New farmer registration & eKYC", "Active"),
        CscService("SRV-02", "Ayushman Card", "Health", 30, "PM-JAY Golden Card", "Active"),
        CscService("SRV-03", "PAN Card (New/Correction)", "Identity", 110, "NSDL / UTI PAN application", "Active"),
        CscService("SRV-04", "Income Certificate", "Revenue", 40, "Saral Haryana Income Certificate", "Active"),
        CscService("SRV-05", "Caste / Domicile Certificate", "Revenue", 40, "Haryana Resident & Caste", "Active"),
        CscService("SRV-06", "Parivar Pehchan Patra (PPP)", "State", 30, "Family ID update & correction", "Active"),
        CscService("SRV-07", "e-Shram / Pension", "Welfare", 30, "Old age / Widow pension & e-Shram", "Active"),
        CscService("SRV-08", "Printing / Photo / Lamination", "Retail", 20, "Urgent documents & passport photo", "Active")
    )

    val customers = mutableStateListOf(
        Customer("CUST-1001", "Ramesh Chander", "9813010101", "Gudhan", "Rohtak", "CSC-001", "Regular farmer beneficiary"),
        Customer("CUST-1002", "Sunita Devi", "9813020202", "Kalanaur", "Rohtak", "CSC-002", "Family ID verified")
    )

    val applications = mutableStateListOf(
        CscApplication("RIVAN-2026-0001", "CUST-1001", "Ramesh Chander", "9813010101", "PM Kisan Registration", 30, "UPI", "UPI8849201", "Verified", "Completed", "CSC-001", "Ravi Kumar", today),
        CscApplication("RIVAN-2026-0002", "CUST-1002", "Sunita Devi", "9813020202", "Income Certificate", 40, "Cash", "CASH-02", "Pending", "Processing", "CSC-002", "Sandeep Sharma", today)
    )

    val incomes = mutableStateListOf(
        LedgerEntry("INC-001", "App: RIVAN-2026-0001 (PM Kisan)", "Service Income", 30, "UPI", "CSC-001", today, "Approved")
    )

    val expenses = mutableStateListOf(
        LedgerEntry("EXP-001", "A4 Paper Rim & Ink", "Stationery", 450, "Cash", "CSC-001", today, "Approved")
    )

    val requests = mutableStateListOf(
        RefundRequest("REQ-001", "Refund", "RIVAN-2026-0002", 40, "Duplicate fee paid by customer", "Sandeep Sharma", today, "Pending")
    )

    val attendance = mutableStateListOf(
        AttendanceRecord("ATT-01", "OP-001", "Sandeep Sharma", "CSC-002", today, "08:58", "", "Present"),
        AttendanceRecord("ATT-02", "OP-002", "Pooja Rani", "CSC-001", today, "09:22", "", "Late")
    )

    val leaves = mutableStateListOf(
        LeaveRequest("LV-01", "Pooja Rani", "Casual Leave", "$today to $today", "Family function", "Pending")
    )

    val payrolls = mutableStateListOf(
        PayrollRecord("PAY-01", "OP-001", "Sandeep Sharma", "2026-10", 12000, 500, 1000, 11500, "Paid")
    )

    fun cscName(id: String): String = cscs.find { it.id == id }?.name ?: id

    fun loginAsAdmin() {
        currentRole = "admin"
        currentUserId = "ADMIN-01"
        currentUserName = "Ravi Kumar"
        currentCscId = "ALL"
        isLoggedIn = true
    }

    fun loginAsOperator(op: Operator): Boolean {
        if (op.status != "Active") return false
        currentRole = "operator"
        currentUserId = op.id
        currentUserName = op.name
        currentCscId = op.cscId
        isLoggedIn = true
        return true
    }

    fun scopedApps(): List<CscApplication> = when {
        currentRole == "operator" -> applications.filter { it.cscId == currentCscId }
        selectedCscFilter != "ALL" -> applications.filter { it.cscId == selectedCscFilter }
        else -> applications
    }

    fun scopedCustomers(): List<Customer> = when {
        currentRole == "operator" -> customers.filter { it.cscId == currentCscId }
        selectedCscFilter != "ALL" -> customers.filter { it.cscId == selectedCscFilter }
        else -> customers
    }

    fun scopedIncomes(): List<LedgerEntry> = when {
        currentRole == "operator" -> incomes.filter { it.cscId == currentCscId }
        selectedCscFilter != "ALL" -> incomes.filter { it.cscId == selectedCscFilter }
        else -> incomes
    }

    fun scopedExpenses(): List<LedgerEntry> = when {
        currentRole == "operator" -> expenses.filter { it.cscId == currentCscId }
        selectedCscFilter != "ALL" -> expenses.filter { it.cscId == selectedCscFilter }
        else -> expenses
    }

    fun addApplication(cust: Customer, srv: CscService, amt: Int, mode: String, status: String) {
        val id = "RIVAN-2026-${(applications.size + 1).toString().padStart(4, '0')}"
        val csc = if (currentRole == "operator") currentCscId else cust.cscId
        val app = CscApplication(
            id = id,
            customerId = cust.id,
            customerName = cust.name,
            customerMobile = cust.mobile,
            serviceName = srv.name,
            amount = amt,
            paymentMode = mode,
            paymentRef = "REF-${(1000..9999).random()}",
            paymentStatus = if (mode == "Cash") "Pending" else "Verified",
            status = status,
            cscId = csc,
            operatorName = currentUserName,
            date = today
        )
        applications.add(0, app)
        if (status == "Completed") {
            incomes.add(0, LedgerEntry("INC-${incomes.size + 1}", "App: $id (${srv.name})", "Service Income", amt, mode, csc, today))
        }
    }

    fun updateAppStatus(app: CscApplication, newStatus: String) {
        val idx = applications.indexOf(app)
        if (idx != -1) {
            applications[idx] = app.copy(status = newStatus)
            if (newStatus == "Completed" && incomes.none { it.title.contains(app.id) }) {
                incomes.add(0, LedgerEntry("INC-${incomes.size + 1}", "App: ${app.id} (${app.serviceName})", "Service Income", app.amount, app.paymentMode, app.cscId, today))
            }
        }
    }

    fun checkIn(): String {
        if (attendance.any { it.operatorId == currentUserId && it.date == today }) {
            return "Aaj ka Check-In pehle hi ho chuka hai!"
        }
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        val st = if (time > "09:15") "Late" else "Present"
        attendance.add(0, AttendanceRecord("ATT-${attendance.size + 1}", currentUserId, currentUserName, currentCscId, today, time, "", st))
        return "Checked-In at $time ($st)"
    }

    fun checkOut(): String {
        val idx = attendance.indexOfFirst { it.operatorId == currentUserId && it.date == today }
        if (idx == -1) return "Pehle Check-In karein!"
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        attendance[idx] = attendance[idx].copy(checkOut = time)
        return "Checked-Out at $time"
    }
}
