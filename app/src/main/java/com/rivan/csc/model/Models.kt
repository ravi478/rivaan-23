package com.rivan.csc.model

data class CscCenter(
    val id: String,
    val name: String,
    val village: String,
    val district: String,
    val mobile: String,
    val manager: String,
    val timing: String = "09:00 - 18:00",
    var status: String = "Active"
)

data class Operator(
    val id: String,
    val name: String,
    val mobile: String,
    val cscId: String,
    val joining: String,
    val basicSalary: Int,
    var status: String = "Active"
)

data class CscService(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    val desc: String,
    var status: String = "Active"
)

data class Customer(
    val id: String,
    val name: String,
    val mobile: String,
    val village: String,
    val district: String,
    val cscId: String,
    val notes: String = ""
)

data class CscApplication(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerMobile: String,
    val serviceName: String,
    val amount: Int,
    val paymentMode: String,
    val paymentRef: String,
    var paymentStatus: String,
    var status: String,
    val cscId: String,
    val operatorName: String,
    val date: String
)

data class LedgerEntry(
    val id: String,
    val title: String,
    val category: String,
    val amount: Int,
    val mode: String,
    val cscId: String,
    val date: String,
    var status: String = "Approved"
)

data class RefundRequest(
    val id: String,
    val type: String,
    val appId: String,
    val amount: Int,
    val reason: String,
    val by: String,
    val date: String,
    var status: String = "Pending"
)

data class AttendanceRecord(
    val id: String,
    val operatorId: String,
    val operatorName: String,
    val cscId: String,
    val date: String,
    val checkIn: String,
    var checkOut: String,
    val status: String
)

data class LeaveRequest(
    val id: String,
    val operatorName: String,
    val type: String,
    val dates: String,
    val reason: String,
    var status: String = "Pending"
)

data class PayrollRecord(
    val id: String,
    val operatorId: String,
    val operatorName: String,
    val month: String,
    val basic: Int,
    val bonus: Int,
    val deduction: Int,
    val net: Int,
    val status: String = "Paid"
)
