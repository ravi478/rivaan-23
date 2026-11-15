package com.rivan.csc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rivan.csc.data.AppRepository
import com.rivan.csc.model.*
import com.rivan.csc.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val repo = remember { AppRepository() }
            RivanCscTheme(darkTheme = repo.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!repo.isLoggedIn) {
                        LoginScreen(repo)
                    } else {
                        MainAppShell(repo)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(repo: AppRepository) {
    var roleTab by remember { mutableStateOf("admin") }
    var selectedOp by remember { mutableStateOf(repo.operators.first()) }
    var opExpanded by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(PrimaryDarkGreen, PrimaryGreen)))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PrimaryGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text(repo.appName, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(repo.tagline, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(4.dp)
                ) {
                    listOf("admin" to "Admin Login", "operator" to "Operator Login").forEach { (r, label) ->
                        val active = roleTab == r
                        Button(
                            onClick = { roleTab = r },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (active) MaterialTheme.colorScheme.surface else Color.Transparent,
                                contentColor = if (active) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            elevation = if (active) ButtonDefaults.buttonElevation(2.dp) else ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (roleTab == "admin") {
                    OutlinedTextField(value = "ADMIN-01", onValueChange = {}, label = { Text("Admin ID") }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(value = "••••••••", onValueChange = {}, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = {
                            repo.loginAsAdmin()
                            Toast.makeText(ctx, "Welcome Super Admin Ravi Kumar!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Rounded.AdminPanelSettings, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Login as Super Admin", fontWeight = FontWeight.Bold)
                    }
                } else {
                    ExposedDropdownMenuBox(expanded = opExpanded, onExpandedChange = { opExpanded = it }) {
                        OutlinedTextField(
                            value = "${selectedOp.id} — ${selectedOp.name} (${selectedOp.status})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Operator") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = opExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = opExpanded, onDismissRequest = { opExpanded = false }) {
                            repo.operators.forEach { op ->
                                DropdownMenuItem(
                                    text = { Text("${op.id} — ${op.name} (${op.status})") },
                                    onClick = { selectedOp = op; opExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = {
                            if (repo.loginAsOperator(selectedOp)) {
                                Toast.makeText(ctx, "Namaste ${selectedOp.name}!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(ctx, "Access Blocked: Status is ${selectedOp.status}", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Rounded.Badge, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Login as Operator", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class NavItem(val id: String, val label: String, val icon: ImageVector, val badge: Int = 0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(repo: AppRepository) {
    var currentScreen by remember { mutableStateOf("dashboard") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showNewAppDialog by remember { mutableStateOf(false) }
    var showNewCustDialog by remember { mutableStateOf(false) }

    val pendingReqCount = repo.requests.count { it.status == "Pending" }
    val pendingLeaveCount = repo.leaves.count { it.status == "Pending" }

    val menuItems = remember(repo.currentRole, pendingReqCount, pendingLeaveCount) {
        if (repo.currentRole == "admin") {
            listOf(
                NavItem("dashboard", "Admin Dashboard", Icons.Rounded.Dashboard),
                NavItem("csc_operators", "Centers & Operators", Icons.Rounded.Hub),
                NavItem("customers", "Customers", Icons.Rounded.Group),
                NavItem("applications", "Applications", Icons.Rounded.Description),
                NavItem("services", "Services & Rates", Icons.Rounded.DesignServices),
                NavItem("finance", "Income, Expense & Profit", Icons.Rounded.AccountBalanceWallet),
                NavItem("requests", "Refunds & Cancels", Icons.Rounded.SyncAlt, pendingReqCount),
                NavItem("attendance", "Attendance & Leave", Icons.Rounded.CameraAlt, pendingLeaveCount),
                NavItem("payroll", "Salary / Payroll", Icons.Rounded.Payments),
                NavItem("reports", "Reports & Summary", Icons.Rounded.BarChart),
                NavItem("settings", "Branding & Settings", Icons.Rounded.Settings)
            )
        } else {
            listOf(
                NavItem("dashboard", "My Dashboard", Icons.Rounded.Dashboard),
                NavItem("customers", "Customers", Icons.Rounded.Group),
                NavItem("applications", "My Applications", Icons.Rounded.Description),
                NavItem("services", "Services & Rates", Icons.Rounded.DesignServices),
                NavItem("requests", "Refund / Cancel Req", Icons.Rounded.SyncAlt),
                NavItem("attendance", "Attendance & Leave", Icons.Rounded.CameraAlt),
                NavItem("reports", "My Reports", Icons.Rounded.BarChart),
                NavItem("profile", "My Profile", Icons.Rounded.Person)
            )
        }
    }

    val currentItem = menuItems.find { it.id == currentScreen } ?: menuItems.first()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = PrimaryDarkGreen,
                drawerContentColor = Color.White,
                modifier = Modifier.width(280.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Storefront, contentDescription = null, tint = PrimaryGreen)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(repo.appName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text(
                            if (repo.currentRole == "admin") "Super Admin Panel" else repo.cscName(repo.currentCscId),
                            fontSize = 11.sp,
                            color = Color(0xFFA7D7C0)
                        )
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                LazyColumn(modifier = Modifier.weight(1f).padding(12.dp)) {
                    items(menuItems) { item ->
                        val active = currentScreen == item.id
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.label, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium) },
                            badge = if (item.badge > 0) {
                                { Badge(containerColor = DangerRed) { Text("${item.badge}", color = Color.White) } }
                            } else null,
                            selected = active,
                            onClick = {
                                currentScreen = item.id
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = PrimaryGreen,
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color(0xFFD9EEE3),
                                unselectedTextColor = Color(0xFFD9EEE3)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.Logout, contentDescription = null, tint = Color(0xFFFFB4B4)) },
                    label = { Text("Logout / Switch Role", color = Color(0xFFFFB4B4), fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = { repo.isLoggedIn = false },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(currentItem.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(currentItem.label, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                        }
                    },
                    actions = {
                        IconButton(onClick = { repo.isDarkTheme = !repo.isDarkTheme }) {
                            Icon(
                                if (repo.isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                                contentDescription = "Theme"
                            )
                        }
                        Surface(
                            onClick = { repo.isLoggedIn = false },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(if (repo.currentRole == "admin") "Admin" else "Op", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    val bottomItems = listOf(
                        NavItem("dashboard", "Home", Icons.Rounded.Dashboard),
                        NavItem("customers", "Customers", Icons.Rounded.Group),
                        NavItem("applications", "Apps", Icons.Rounded.Description),
                        NavItem("reports", "Reports", Icons.Rounded.BarChart)
                    )
                    bottomItems.forEachIndexed { idx, item ->
                        if (idx == 2) {
                            FloatingActionButton(
                                onClick = { showNewAppDialog = true },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White,
                                shape = CircleShape,
                                modifier = Modifier.size(50.dp)
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "New App", modifier = Modifier.size(28.dp))
                            }
                        }
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            selected = currentScreen == item.id,
                            onClick = { currentScreen = item.id }
                        )
                    }
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) + slideInVertically { it / 25 } togetherWith
                            fadeOut(animationSpec = tween(200))
                },
                label = "ScreenAnimation",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { screen ->
                when (screen) {
                    "dashboard" -> DashboardScreen(repo, onNavigate = { currentScreen = it }, onNewCust = { showNewCustDialog = true }, onNewApp = { showNewAppDialog = true })
                    "csc_operators" -> CscOperatorsScreen(repo)
                    "customers" -> CustomersScreen(repo, onNewCust = { showNewCustDialog = true })
                    "applications" -> ApplicationsScreen(repo, onNewApp = { showNewAppDialog = true })
                    "services" -> ServicesScreen(repo, onBook = { showNewAppDialog = true })
                    "finance" -> FinanceScreen(repo)
                    "requests" -> RequestsScreen(repo)
                    "attendance" -> AttendanceScreen(repo)
                    "payroll" -> PayrollScreen(repo)
                    "reports" -> ReportsScreen(repo)
                    "settings" -> SettingsScreen(repo)
                    "profile" -> ProfileScreen(repo)
                    else -> DashboardScreen(repo, onNavigate = { currentScreen = it }, onNewCust = { showNewCustDialog = true }, onNewApp = { showNewAppDialog = true })
                }
            }
        }
    }

    if (showNewCustDialog) {
        NewCustomerDialog(repo = repo, onDismiss = { showNewCustDialog = false })
    }
    if (showNewAppDialog) {
        NewApplicationDialog(repo = repo, onDismiss = { showNewAppDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    repo: AppRepository,
    onNavigate: (String) -> Unit,
    onNewCust: () -> Unit,
    onNewApp: () -> Unit
) {
    val apps = repo.scopedApps()
    val custs = repo.scopedCustomers()
    val incTotal = repo.scopedIncomes().sumOf { it.amount }
    val expTotal = repo.scopedExpenses().filter { it.status == "Approved" }.sumOf { it.amount }
    val profit = incTotal - expTotal
    val isAdmin = repo.currentRole == "admin"

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(PrimaryDarkGreen, PrimaryGreen)))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Namaste, ${repo.currentUserName} 👋", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        if (isAdmin) "Super Admin • 100 CSC Network" else "Operator Desk • ${repo.cscName(repo.currentCscId)}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onNewCust,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PrimaryGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Customer", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onNewApp,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.NoteAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Application", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // KPI Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isAdmin) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatCard("CSC Centers", "${repo.cscs.size}", "${repo.cscs.count { it.status == "Active" }} Active", InfoBlue, Modifier.weight(1f))
                        StatCard("Operators", "${repo.operators.count { it.status == "Active" }}/${repo.operators.size}", "${repo.operators.count { it.status == "Pending" }} Pending", InfoBlue, Modifier.weight(1f))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Customers", "${custs.size}", "Registered", MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
                    StatCard("Applications", "${apps.size}", "${apps.count { it.status == "Pending" }} Pending", WarningOrange, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Total Income", "₹$incTotal", "Verified & Cash", PrimaryGreen, Modifier.weight(1f))
                    if (isAdmin) {
                        StatCard("Net Profit", "₹$profit", "After Expenses", if (profit >= 0) PrimaryGreen else DangerRed, Modifier.weight(1f))
                    } else {
                        StatCard("Completed", "${apps.count { it.status == "Completed" }}", "Applications", PrimaryGreen, Modifier.weight(1f))
                    }
                }
            }
        }

        // Quick Actions
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = WarningOrange)
                        Spacer(Modifier.width(6.dp))
                        Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickActionBox("Attendance", "Check-In / Out", Icons.Rounded.CameraAlt, WarningOrange, Modifier.weight(1f)) { onNavigate("attendance") }
                        QuickActionBox("Reports", "Summary & Stats", Icons.Rounded.BarChart, InfoBlue, Modifier.weight(1f)) { onNavigate("reports") }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (isAdmin) {
                            QuickActionBox("Centers & Ops", "Merge Panel", Icons.Rounded.Hub, PrimaryGreen, Modifier.weight(1f)) { onNavigate("csc_operators") }
                            QuickActionBox("Approvals", "Refund & Leave", Icons.Rounded.Rule, DangerRed, Modifier.weight(1f)) { onNavigate("requests") }
                        } else {
                            QuickActionBox("Services", "Rate List", Icons.Rounded.DesignServices, PrimaryGreen, Modifier.weight(1f)) { onNavigate("services") }
                            QuickActionBox("Profile", "ID & Salary", Icons.Rounded.AccountCircle, InfoBlue, Modifier.weight(1f)) { onNavigate("profile") }
                        }
                    }
                }
            }
        }

        // Recent Applications
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Recent Applications", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                TextButton(onClick = { onNavigate("applications") }) { Text("View All →") }
            }
        }
        items(apps.take(4)) { app ->
            ApplicationCard(app = app, repo = repo)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, sub: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
            Text(sub, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun QuickActionBox(title: String, sub: String, icon: ImageVector, tint: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            Text(sub, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ==================== MERGED CENTERS & OPERATORS SCREEN ====================
@Composable
fun CscOperatorsScreen(repo: AppRepository) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddCsc by remember { mutableStateOf(false) }
    var showAddOp by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("CSC Centers (${repo.cscs.size})", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Operators (${repo.operators.size})", fontWeight = FontWeight.Bold) })
        }
        Spacer(Modifier.height(12.dp))

        if (selectedTab == 0) {
            Button(onClick = { showAddCsc = true }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.AddBusiness, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add New CSC Center")
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(repo.cscs) { csc ->
                    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(csc.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("${csc.id} • ${csc.village}, ${csc.district}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                StatusBadge(csc.status)
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Manager: ${csc.manager}", fontSize = 13.sp)
                                OutlinedButton(
                                    onClick = {
                                        val idx = repo.cscs.indexOf(csc)
                                        repo.cscs[idx] = csc.copy(status = if (csc.status == "Active") "Inactive" else "Active")
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Toggle Status", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Button(onClick = { showAddOp = true }, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.PersonAdd, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add New Operator")
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(repo.operators) { op ->
                    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(op.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("${op.id} • ${repo.cscName(op.cscId)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                StatusBadge(op.status)
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Salary: ₹${op.basicSalary}/mo", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Button(
                                    onClick = {
                                        val idx = repo.operators.indexOf(op)
                                        repo.operators[idx] = op.copy(status = if (op.status == "Active") "Inactive" else "Active")
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(if (op.status == "Active") "Deactivate" else "Approve", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCsc) {
        var name by remember { mutableStateOf("") }
        var village by remember { mutableStateOf("") }
        var manager by remember { mutableStateOf("") }
        var mobile by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCsc = false },
            title = { Text("Add CSC Center") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Center Name") })
                    OutlinedTextField(value = village, onValueChange = { village = it }, label = { Text("Village / Town") })
                    OutlinedTextField(value = manager, onValueChange = { manager = it }, label = { Text("Manager Name") })
                    OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        repo.cscs.add(CscCenter("CSC-${(repo.cscs.size + 1).toString().padStart(3, '0')}", name, village, "Rohtak", mobile, manager))
                        showAddCsc = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAddCsc = false }) { Text("Cancel") } }
        )
    }

    if (showAddOp) {
        var name by remember { mutableStateOf("") }
        var mobile by remember { mutableStateOf("") }
        var salary by remember { mutableStateOf("10000") }
        AlertDialog(
            onDismissRequest = { showAddOp = false },
            title = { Text("Add Operator") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Operator Name") })
                    OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") })
                    OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("Basic Salary (₹)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        repo.operators.add(Operator("OP-${(repo.operators.size + 1).toString().padStart(3, '0')}", name, mobile, repo.cscs.first().id, repo.today, salary.toIntOrNull() ?: 10000, "Active"))
                        showAddOp = false
                    }
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showAddOp = false }) { Text("Cancel") } }
        )
    }
}

// ==================== CUSTOMERS SCREEN ====================
@Composable
fun CustomersScreen(repo: AppRepository, onNewCust: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val filtered = repo.scopedCustomers().filter {
        it.name.contains(query, true) || it.mobile.contains(query) || it.village.contains(query, true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search name, mobile, village...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
            Button(onClick = onNewCust, shape = RoundedCornerShape(14.dp), modifier = Modifier.height(56.dp)) {
                Icon(Icons.Rounded.PersonAdd, contentDescription = null)
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered) { c ->
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(c.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("📞 ${c.mobile} • ${c.village}, ${c.district}", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(repo.cscName(c.cscId), fontSize = 11.5.sp, color = PrimaryGreen, fontWeight = FontWeight.SemiBold)
                        }
                        StatusBadge("${repo.applications.count { it.customerId == c.id }} Apps")
                    }
                }
            }
        }
    }
}

// ==================== APPLICATIONS SCREEN ====================
@Composable
fun ApplicationsScreen(repo: AppRepository, onNewApp: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val filtered = repo.scopedApps().filter {
        it.id.contains(query, true) || it.customerName.contains(query, true) || it.serviceName.contains(query, true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search App ID, Customer, Service...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
            Button(onClick = onNewApp, shape = RoundedCornerShape(14.dp), modifier = Modifier.height(56.dp)) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered) { app ->
                ApplicationCard(app = app, repo = repo)
            }
        }
    }
}

@Composable
fun ApplicationCard(app: CscApplication, repo: AppRepository) {
    val ctx = LocalContext.current
    var showReceipt by remember { mutableStateOf(false) }

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(app.id, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = PrimaryGreen)
                    Text(app.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                StatusBadge(app.status)
            }
            Spacer(Modifier.height(6.dp))
            Text("${app.serviceName} • ₹${app.amount} (${app.paymentMode})", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (app.status != "Completed") {
                    Button(
                        onClick = { repo.updateAppStatus(app, "Completed") },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Complete", fontSize = 12.sp)
                    }
                }
                OutlinedButton(
                    onClick = { showReceipt = true },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Rounded.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Receipt", fontSize = 12.sp)
                }
            }
        }
    }

    if (showReceipt) {
        AlertDialog(
            onDismissRequest = { showReceipt = false },
            title = { Text("🧾 Digital Receipt") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(repo.appName, fontWeight = FontWeight.Bold, color = PrimaryGreen, fontSize = 16.sp)
                    HorizontalDivider()
                    Text("Receipt ID: ${app.id}", fontWeight = FontWeight.Bold)
                    Text("Date: ${app.date}")
                    Text("Customer: ${app.customerName} (${app.customerMobile})")
                    Text("Service: ${app.serviceName}")
                    Text("Payment: ₹${app.amount} (${app.paymentMode})")
                    Text("Status: ${app.status}", fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(onClick = {
                    val text = "*RIVAN CSC RECEIPT*
ID: ${app.id}
Customer: ${app.customerName}
Service: ${app.serviceName}
Amount: ₹${app.amount}
Status: ${app.status}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/91${app.customerMobile}?text=${Uri.encode(text)}"))
                    ctx.startActivity(intent)
                }) {
                    Text("WhatsApp Share")
                }
            },
            dismissButton = { TextButton(onClick = { showReceipt = false }) { Text("Close") } }
        )
    }
}

// ==================== SERVICES SCREEN ====================
@Composable
fun ServicesScreen(repo: AppRepository, onBook: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(repo.services) { srv ->
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(srv.category.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = InfoBlue)
                        Text(srv.name, fontWeight = FontWeight.Bold, fontSize = 15.5.sp)
                        Text(srv.desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("₹${srv.price}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PrimaryGreen)
                        Spacer(Modifier.height(4.dp))
                        Button(onClick = onBook, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)) {
                            Text("Book", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==================== FINANCE SCREEN ====================
@Composable
fun FinanceScreen(repo: AppRepository) {
    val totalInc = repo.scopedIncomes().sumOf { it.amount }
    val totalExp = repo.scopedExpenses().filter { it.status == "Approved" }.sumOf { it.amount }
    var showAddExp by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Total Income", "₹$totalInc", "Service & Manual", PrimaryGreen, Modifier.weight(1f))
                StatCard("Total Expense", "₹$totalExp", "Approved Bills", DangerRed, Modifier.weight(1f))
            }
        }
        item {
            Button(onClick = { showAddExp = true }, colors = ButtonDefaults.buttonColors(containerColor = DangerRed), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.RemoveCircleOutline, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Expense Entry")
            }
        }
        item { Text("Recent Income Entries", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        items(repo.scopedIncomes()) { inc ->
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(inc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${inc.date} • ${inc.mode}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("+₹${inc.amount}", fontWeight = FontWeight.ExtraBold, color = PrimaryGreen)
                }
            }
        }
        item { Text("Expense Ledger", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        items(repo.scopedExpenses()) { exp ->
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(exp.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${exp.date} • ${exp.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("-₹${exp.amount}", fontWeight = FontWeight.ExtraBold, color = DangerRed)
                }
            }
        }
    }

    if (showAddExp) {
        var title by remember { mutableStateOf("") }
        var amt by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddExp = false },
            title = { Text("Add Expense") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Expense Detail") })
                    OutlinedTextField(value = amt, onValueChange = { amt = it }, label = { Text("Amount (₹)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank() && amt.isNotBlank()) {
                        repo.expenses.add(0, LedgerEntry("EXP-${repo.expenses.size + 1}", title, "Operational", amt.toIntOrNull() ?: 0, "Cash", "CSC-001", repo.today))
                        showAddExp = false
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showAddExp = false }) { Text("Cancel") } }
        )
    }
}

// ==================== REQUESTS SCREEN ====================
@Composable
fun RequestsScreen(repo: AppRepository) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(repo.requests) { req ->
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${req.type} • ${req.appId}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        StatusBadge(req.status)
                    }
                    Text("Amount: ₹${req.amount} • By: ${req.by}", fontSize = 13.sp)
                    Text("Reason: ${req.reason}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (repo.currentRole == "admin" && req.status == "Pending") {
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                val idx = repo.requests.indexOf(req)
                                repo.requests[idx] = req.copy(status = "Approved")
                            }, shape = RoundedCornerShape(10.dp)) { Text("Approve") }
                            OutlinedButton(onClick = {
                                val idx = repo.requests.indexOf(req)
                                repo.requests[idx] = req.copy(status = "Rejected")
                            }, shape = RoundedCornerShape(10.dp)) { Text("Reject") }
                        }
                    }
                }
            }
        }
    }
}

// ==================== ATTENDANCE SCREEN ====================
@Composable
fun AttendanceScreen(repo: AppRepository) {
    val ctx = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (repo.currentRole == "operator") {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { Toast.makeText(ctx, repo.checkIn(), Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Login, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Check-In")
                    }
                    OutlinedButton(
                        onClick = { Toast.makeText(ctx, repo.checkOut(), Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Logout, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Check-Out")
                    }
                }
            }
        }
        items(repo.attendance) { att ->
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(att.operatorName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${att.date} • In: ${att.checkIn} | Out: ${att.checkOut.ifEmpty { "-" }}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    StatusBadge(att.status)
                }
            }
        }
    }
}

// ==================== PAYROLL SCREEN ====================
@Composable
fun PayrollScreen(repo: AppRepository) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(repo.payrolls) { pay ->
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("${pay.operatorName} (${pay.month})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Basic: ₹${pay.basic} | Bonus: +₹${pay.bonus} | Ded: -₹${pay.deduction}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("₹${pay.net}", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = PrimaryGreen)
                        StatusBadge(pay.status)
                    }
                }
            }
        }
    }
}

// ==================== REPORTS SCREEN ====================
@Composable
fun ReportsScreen(repo: AppRepository) {
    val apps = repo.scopedApps()
    val totalInc = repo.scopedIncomes().sumOf { it.amount }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📊 Live Network Analytics", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                HorizontalDivider()
                Text("• Total Registered Customers: ${repo.scopedCustomers().size}")
                Text("• Total Applications: ${apps.size}")
                Text("• Completed Applications: ${apps.count { it.status == "Completed" }}")
                Text("• Pending / Processing: ${apps.count { it.status != "Completed" }}")
                Text("• Total Revenue Generated: ₹$totalInc", fontWeight = FontWeight.Bold, color = PrimaryGreen)
            }
        }
    }
}

// ==================== SETTINGS SCREEN ====================
@Composable
fun SettingsScreen(repo: AppRepository) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🎨 App Branding & Theme", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(value = repo.appName, onValueChange = { repo.appName = it }, label = { Text("App Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = repo.tagline, onValueChange = { repo.tagline = it }, label = { Text("Tagline") }, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Dark Mode Theme", fontWeight = FontWeight.SemiBold)
                    Switch(checked = repo.isDarkTheme, onCheckedChange = { repo.isDarkTheme = it })
                }
            }
        }
    }
}

// ==================== PROFILE SCREEN ====================
@Composable
fun ProfileScreen(repo: AppRepository) {
    val op = repo.operators.find { it.id == repo.currentUserId } ?: repo.operators.first()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.AccountCircle, contentDescription = null, modifier = Modifier.size(72.dp), tint = PrimaryGreen)
                Spacer(Modifier.height(8.dp))
                Text(op.name, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text("${op.id} • ${repo.cscName(op.cscId)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                Text("Mobile: ${op.mobile}")
                Text("Joining Date: ${op.joining}")
                Text("Basic Salary: ₹${op.basicSalary}/month", fontWeight = FontWeight.Bold, color = PrimaryGreen)
                Spacer(Modifier.height(20.dp))
                Button(onClick = { repo.isLoggedIn = false }, colors = ButtonDefaults.buttonColors(containerColor = DangerRed), modifier = Modifier.fillMaxWidth()) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status.lowercase()) {
        "completed", "active", "present", "paid", "approved" -> Color(0xFFDFF6E8) to PrimaryGreen
        "pending", "late" -> Color(0xFFFFF1D5) to WarningOrange
        "processing" -> Color(0xFFE0F0FF) to InfoBlue
        else -> Color(0xFFFDE8E8) to DangerRed
    }
    Surface(color = bg, shape = CircleShape) {
        Text(
            text = status,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ==================== DIALOGS ====================
@Composable
fun NewCustomerDialog(repo: AppRepository, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("👤 Add New Customer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Customer Name *") })
                OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile Number *") })
                OutlinedTextField(value = village, onValueChange = { village = it }, label = { Text("Village / Address") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && mobile.isNotBlank()) {
                    val csc = if (repo.currentRole == "operator") repo.currentCscId else repo.cscs.first().id
                    repo.customers.add(0, Customer("CUST-${1001 + repo.customers.size}", name, mobile, village.ifBlank { "Rohtak" }, "Rohtak", csc))
                    onDismiss()
                }
            }) { Text("Save Customer") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewApplicationDialog(repo: AppRepository, onDismiss: () -> Unit) {
    val custs = repo.scopedCustomers()
    val srvs = repo.services
    var selectedCust by remember { mutableStateOf(custs.firstOrNull()) }
    var selectedSrv by remember { mutableStateOf(srvs.first()) }
    var amount by remember { mutableStateOf(srvs.first().price.toString()) }
    var mode by remember { mutableStateOf("Cash") }
    var status by remember { mutableStateOf("Pending") }
    var custExp by remember { mutableStateOf(false) }
    var srvExp by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📄 New Application") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selectedCust != null) {
                    ExposedDropdownMenuBox(expanded = custExp, onExpandedChange = { custExp = it }) {
                        OutlinedTextField(
                            value = "${selectedCust!!.name} (${selectedCust!!.mobile})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Customer") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = custExp) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = custExp, onDismissRequest = { custExp = false }) {
                            custs.forEach { c ->
                                DropdownMenuItem(text = { Text("${c.name} (${c.mobile})") }, onClick = { selectedCust = c; custExp = false })
                            }
                        }
                    }
                }
                ExposedDropdownMenuBox(expanded = srvExp, onExpandedChange = { srvExp = it }) {
                    OutlinedTextField(
                        value = "${selectedSrv.name} — ₹${selectedSrv.price}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Service") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = srvExp) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = srvExp, onDismissRequest = { srvExp = false }) {
                        srvs.forEach { s ->
                            DropdownMenuItem(text = { Text("${s.name} — ₹${s.price}") }, onClick = { selectedSrv = s; amount = s.price.toString(); srvExp = false })
                        }
                    }
                }
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Fee (₹)") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Cash", "UPI").forEach { m ->
                        FilterChip(selected = mode == m, onClick = { mode = m }, label = { Text(m) })
                    }
                    listOf("Pending", "Completed").forEach { st ->
                        FilterChip(selected = status == st, onClick = { status = st }, label = { Text(st) })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (selectedCust != null) {
                    repo.addApplication(selectedCust!!, selectedSrv, amount.toIntOrNull() ?: selectedSrv.price, mode, status)
                    onDismiss()
                }
            }) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
