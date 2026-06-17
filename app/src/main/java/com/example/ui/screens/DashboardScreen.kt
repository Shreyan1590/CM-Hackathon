package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.EmergencyShare
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AlertNotification
import com.example.data.AlertType
import com.example.data.EmergencyContact
import com.example.data.LifelineRepository
import com.example.data.ServiceCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToSOSMenu: () -> Unit,
    onNavigateToNearbyCategory: (ServiceCategory) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToMedicalID: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val medicalProfile by LifelineRepository.medicalProfile.collectAsState()
    val emergencyContacts by LifelineRepository.emergencyContacts.collectAsState()
    val isSosAlertActive by LifelineRepository.isSosAlertActive.collectAsState()
    val liveLocation by LifelineRepository.sosLiveLocation.collectAsState()
    val isOfflineMode by LifelineRepository.isOfflineMode.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showIncidentReportDialog by remember { mutableStateOf(false) }

    // Fetch unread count for badges
    val notifications by LifelineRepository.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            DashboardHeader(
                userName = medicalProfile.name,
                bloodGroup = medicalProfile.bloodGroup,
                unreadNotificationsCount = unreadCount,
                onNotificationClick = onNavigateToNotifications,
                onProfileClick = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showIncidentReportDialog = true },
                containerColor = Color(0xFFD93025),
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_quick_incident_report")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAlert,
                        contentDescription = "Report Incident"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report Hazard", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("dashboard_scroll_container"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Simulated Connectivity Control Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("offline_simulator_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOfflineMode) Color(0xFFFFF3CD) else Color(0xFFE8F0FE)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isOfflineMode) Color(0xFFFFC107) else Color(0xFFADCCF6))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isOfflineMode) Color(0xFFD93025) else Color(0xFF137333))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isOfflineMode) "No Network - Offline Mode" else "Connected to LifeLine Net",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOfflineMode) Color(0xFF664D03) else Color(0xFF0B57D0)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Simulate Loss: ",
                                fontSize = 11.sp,
                                color = Color(0xFF5F6368)
                            )
                            Switch(
                                checked = isOfflineMode,
                                onCheckedChange = { LifelineRepository.setOfflineMode(it) },
                                modifier = Modifier.scale(0.75f).testTag("simulation_offline_toggle")
                            )
                        }
                    }
                }
            }

            // Offline Mode State local cache display
            if (isOfflineMode) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF9E6))
                            .border(2.dp, Color(0xFFFFC107), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                            .testTag("offline_profile_deck_container"),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = Color(0xFFD93025),
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "OFFLINE SAFETY DECK PROTOCOL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD93025),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Cached Health & Rescue ID",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }

                        Text(
                            text = "Network connection is offline. Critical health and rescue contacts are secured offline from cached storage for instant first-responder review.",
                            fontSize = 12.sp,
                            color = Color(0xFF5F6368),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFFFE082)))

                        // Offline medical attributes
                        Text(
                            text = "PRE-SAVED MEDICAL PROFILE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5F6368)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Full Name", fontSize = 10.sp, color = Color(0xFF5F6368))
                                    Text(medicalProfile.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFCE8E6))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("BLOOD: ${medicalProfile.bloodGroup}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD93025))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Severe Allergies", fontSize = 10.sp, color = Color(0xFF5F6368))
                                    Text(medicalProfile.allergies, fontSize = 13.sp, color = Color(0xFFD93025), fontWeight = FontWeight.SemiBold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Age", fontSize = 10.sp, color = Color(0xFF5F6368))
                                    Text("${medicalProfile.age} yrs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                                }
                            }

                            Column {
                                Text("Chronic Conditions", fontSize = 10.sp, color = Color(0xFF5F6368))
                                Text(medicalProfile.chronicConditions, fontSize = 13.sp, color = Color(0xFF1F1F1F))
                            }

                            Column {
                                Text("Active Medications", fontSize = 10.sp, color = Color(0xFF5F6368))
                                Text(medicalProfile.medications, fontSize = 13.sp, color = Color(0xFF1F1F1F))
                            }
                        }

                        Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFFFE082)))

                        // Offline guardians
                        Text(
                            text = "CACHED RESCUE RELATIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5F6368)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            emergencyContacts.forEach { contact ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF1F3F4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            contact.name.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1F1F1F)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(contact.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                                        Text("${contact.relationship} • ${contact.phone}", fontSize = 11.sp, color = Color(0xFF5F6368))
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFECEB))
                                            .clickable { /* dial contact simulated card */ }
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = "Contact Rescue Call",
                                            tint = Color(0xFFD93025),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Interactive Search Help
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Find hospitals, pharmacies, blood banks...", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_search_bar"),
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = Color(0xFF5F6368)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0B57D0),
                        unfocusedBorderColor = Color(0xFFDADCE0),
                        focusedContainerColor = Color(0xFFF8F9FA),
                        unfocusedContainerColor = Color(0xFFF8F9FA)
                    ),
                    singleLine = true
                )
            }

            // High-Stress Visual SOS Card
            item {
                HomeSOSBanner(
                    isSosAlertActive = isSosAlertActive,
                    onClick = onNavigateToSOSMenu
                )
            }

            // Live Location Status Panel
            item {
                LiveCoordinatesCard(
                    locationString = liveLocation,
                    isSosActive = isSosAlertActive
                )
            }

            // Quick Service Categories Grid Title
            item {
                Text(
                    text = "Quick Directory",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F)
                )
            }

            // 3x2 Grid items for category shortcuts
            item {
                QuickDirectoryGrid(
                    onCategoryClick = { category ->
                        LifelineRepository.selectedServiceCategory.value = category
                        onNavigateToNearbyCategory(category)
                    }
                )
            }

            // Emergency Medical Profile Quick Peek Card
            item {
                MedicalIDQuickPeekCard(
                    bloodGroup = medicalProfile.bloodGroup,
                    allergies = medicalProfile.allergies,
                    conditions = medicalProfile.chronicConditions,
                    onIdClick = onNavigateToMedicalID
                )
            }

            // Offline / Actionable Family Contacts Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SOS Rescue Contacts",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                    Text(
                        text = "Manage in Profile",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0B57D0),
                        modifier = Modifier.clickable { onNavigateToProfile() }
                    )
                }
            }

            item {
                FamilyContactsShortcutRow(
                    contacts = emergencyContacts
                )
            }

            // Emergency Preparedness First Aid Tips Carousel
            item {
                Text(
                    text = "Stress-Friendly Guidance",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F)
                )
            }

            item {
                FirstAidInstructionsRow()
            }
        }
    }

    if (showIncidentReportDialog) {
        var incidentType by remember { mutableStateOf("Road Accident") }
        var incidentLocation by remember { mutableStateOf("") }
        var severityLevel by remember { mutableStateOf("Medium") }
        var hazardDescription by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showIncidentReportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AddAlert,
                        contentDescription = null,
                        tint = Color(0xFFD93025),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report Road Hazard", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Report road blockages, collisions, or infrastructure hazards. Broadcasts instantly to nearby connections.",
                        fontSize = 12.sp,
                        color = Color(0xFF5F6368)
                    )

                    // Incident Type selection Row
                    Text("Incident Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Road Accident", "Road Hazard").forEach { type ->
                            val selected = incidentType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) Color(0xFFFFECEB) else Color(0xFFF1F3F4))
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) Color(0xFFD93025) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { incidentType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color(0xFFD93025) else Color(0xFF5F6368)
                                )
                            }
                        }
                    }

                    // Location text field
                    Text("Location description", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                    OutlinedTextField(
                        value = incidentLocation,
                        onValueChange = { incidentLocation = it },
                        placeholder = { Text("e.g. Near Fortis Hospital Signal", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("incident_location_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD93025),
                            unfocusedBorderColor = Color(0xFFDADCE0)
                        ),
                        singleLine = true
                    )

                    // Severity levels selection Row
                    Text("Severity Level", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Low", "Medium", "High").forEach { sev ->
                            val selected = severityLevel == sev
                            val toneColor = when (sev) {
                                "Low" -> Color(0xFFE6F4EA)
                                "Medium" -> Color(0xFFFEF7E0)
                                else -> Color(0xFFFFECEB)
                            }
                            val textColor = when (sev) {
                                "Low" -> Color(0xFF137333)
                                "Medium" -> Color(0xFFB06000)
                                else -> Color(0xFFD93025)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) toneColor else Color(0xFFF1F3F4))
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) textColor else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { severityLevel = sev }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sev,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) textColor else Color(0xFF5F6368)
                                )
                            }
                        }
                    }

                    // Hazard details/description
                    Text("hazard details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                    OutlinedTextField(
                        value = hazardDescription,
                        onValueChange = { hazardDescription = it },
                        placeholder = { Text("e.g. Oil spill on asphalt, cars sliding", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("incident_description_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD93025),
                            unfocusedBorderColor = Color(0xFFDADCE0)
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val loc = if (incidentLocation.isBlank()) "Unknown coordinates" else incidentLocation
                        val desc = if (hazardDescription.isBlank()) "No additional details" else hazardDescription
                        LifelineRepository.addIncidentReport(
                            type = incidentType,
                            location = loc,
                            severity = severityLevel,
                            description = desc
                        )
                        showIncidentReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD93025)),
                    modifier = Modifier.testTag("submit_incident_report_button")
                ) {
                    Text("Broadcast Report", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showIncidentReportDialog = false }) {
                    Text("Cancel", color = Color(0xFF5F6368))
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun DashboardHeader(
    userName: String,
    bloodGroup: String,
    unreadNotificationsCount: Int,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rounded Avatar or Initials block
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F0FE))
                    .clickable { onProfileClick() }
                    .testTag("header_avatar"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.toString()?.uppercase() ?: "R",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B57D0)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE6F4EA))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "A+", // Simulating a verified community badge
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF137333)
                        )
                    }
                }
                Text(
                    text = "Medical Class: $bloodGroup",
                    fontSize = 12.sp,
                    color = Color(0xFF5F6368)
                )
            }
        }

        // Notification Action Icon with badge indicator
        Box {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.testTag("header_notifications_button")
            ) {
                Icon(
                    imageVector = if (unreadNotificationsCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                    contentDescription = "Alert notifications",
                    tint = if (unreadNotificationsCount > 0) Color(0xFFD93025) else Color(0xFF1F1F1F),
                    modifier = Modifier.size(26.dp)
                )
            }
            if (unreadNotificationsCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD93025)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadNotificationsCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HomeSOSBanner(
    isSosAlertActive: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSosAlertActive) Color(0xFFFCE8E6) else Color(0xFFFFECEB),
        animationSpec = tween(1000)
    )

    val borderStrokeColor by animateColorAsState(
        targetValue = if (isSosAlertActive) Color(0xFFD93025) else Color(0xFFFFDAD6),
        animationSpec = tween(1000)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .border(
                width = if (isSosAlertActive) 2.dp else 1.dp,
                color = borderStrokeColor,
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("home_sos_banner"),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left custom code-drawn red alert signal orb
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFDAD6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSosAlertActive) Icons.Default.AddAlert else Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFFD93025),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isSosAlertActive) "🚨 SOS ALERT BROADCAST ACTIVE" else "SOS EMERGENCY CENTER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD93025),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isSosAlertActive) "Transmitting coordinates to your network. Tap to update or stop." else "Help is just one tap away",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isSosAlertActive) "Tap here to access your active safety status panel" else "Alert saved family group contacts instantly.",
                    fontSize = 12.sp,
                    color = Color(0xFF5F6368)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFD93025)
            )
        }
    }
}

@Composable
fun LiveCoordinatesCard(
    locationString: String,
    isSosActive: Boolean
) {
    // Blinking live indicator animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDADCE0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulse circle canvas drawing
            Canvas(
                modifier = Modifier
                    .size(16.dp)
                    .scale(alphaAnim)
            ) {
                drawCircle(
                    color = if (isSosActive) Color(0xFFD93025) else Color(0xFF137333),
                    radius = size.minDimension / 2
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (isSosActive) "SOS Live Telemetry (Broadcasting)" else "Active Location Mode (Secured)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSosActive) Color(0xFFD93025) else Color(0xFF137333)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = Color(0xFF5F6368),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = locationString,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F1F1F),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

data class DirectoryItem(
    val category: ServiceCategory,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val bgColor: Color
)

@Composable
fun QuickDirectoryGrid(
    onCategoryClick: (ServiceCategory) -> Unit
) {
    val items = listOf(
        DirectoryItem(ServiceCategory.HOSPITALS, "Hospitals", Icons.Default.LocalHospital, Color(0xFF0B57D0), Color(0xFFE8F0FE)),
        DirectoryItem(ServiceCategory.PHARMACY, "Pharmacies", Icons.Default.LocalPharmacy, Color(0xFF007A87), Color(0xFFE0F7FA)),
        DirectoryItem(ServiceCategory.BLOOD_BANK, "Blood Banks", Icons.Default.HealthAndSafety, Color(0xFFD93025), Color(0xFFFCE8E6)),
        DirectoryItem(ServiceCategory.POLICE, "Police", Icons.Default.LocalPolice, Color(0xFF3C4043), Color(0xFFF1F3F4)),
        DirectoryItem(ServiceCategory.SHELTER, "Rescues", Icons.Default.VerifiedUser, Color(0xFF137333), Color(0xFFE6F4EA)),
        DirectoryItem(ServiceCategory.AMBULANCE, "Ambulance", Icons.Default.MedicalServices, Color(0xFF0B57D0), Color(0xFFE8F0FE))
    )

    // Modern flat panel grid using elegant custom row wrappers for perfect measure constraints
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (i in 0 until items.size step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items[i].let { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF8F9FA))
                            .border(1.dp, Color(0xFFEEF1F6), RoundedCornerShape(16.dp))
                            .clickable { onCategoryClick(item.category) }
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(item.bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.name,
                                    tint = item.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = item.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F)
                            )
                        }
                    }
                }
                if (i + 1 < items.size) {
                    items[i + 1].let { item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF8F9FA))
                                .border(1.dp, Color(0xFFEEF1F6), RoundedCornerShape(16.dp))
                                .clickable { onCategoryClick(item.category) }
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(item.bgColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.name,
                                        tint = item.color,
                                        modifier = Modifier.size(20.dp)
                                )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = item.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicalIDQuickPeekCard(
    bloodGroup: String,
    allergies: String,
    conditions: String,
    onIdClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onIdClick() }
            .testTag("medical_id_quick_peek"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDADCE0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalInformation,
                        contentDescription = "Medical files",
                        tint = Color(0xFF0D57D0),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Emergency Medical Card",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                }
                Text(
                    text = "View Card",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B57D0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grid items mapping indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F3F4))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .weight(1f)
                ) {
                    Column {
                        Text("Blood Donor", fontSize = 10.sp, color = Color(0xFF5F6368))
                        Text(bloodGroup, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD93025))
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F3F4))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .weight(2f)
                ) {
                    Column {
                        Text("Critical Allergies", fontSize = 10.sp, color = Color(0xFF5F6368))
                        Text(
                            text = allergies,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyContactsShortcutRow(
    contacts: List<EmergencyContact>
) {
    if (contacts.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Text(
                text = "No emergency contacts defined yet.",
                modifier = Modifier.padding(16.dp),
                fontSize = 13.sp,
                color = Color(0xFF5F6368)
            )
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp)
        ) {
            items(contacts) { contact ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .border(1.dp, Color(0xFFEEF1F6), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F0FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contact.name.firstOrNull()?.toString()?.uppercase() ?: "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B57D0)
                                )
                            }

                            if (contact.isSosRecipient) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFFFCE8E6))
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Receives SOS",
                                        tint = Color(0xFFD93025),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = contact.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color(0xFF1F1F1F)
                            )
                            Text(
                                text = contact.relationship,
                                fontSize = 11.sp,
                                color = Color(0xFF5F6368)
                            )
                        }

                        // Call Action Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F3F4))
                                .clickable { /* Dial trigger */ }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = Color(0xFF0B57D0),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Call Mobile",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0B57D0)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class FirstAidTip(
    val title: String,
    val rescueActions: String,
    val iconBg: Color,
    val iconColor: Color
)

@Composable
fun FirstAidInstructionsRow() {
    val tips = listOf(
        FirstAidTip(
            "Bleeding control",
            "1. Apply direct pressure with sterile pad.\n2. Keep limbs elevated above head level if possible.\n3. Do not release pressure until clinic wrap.",
            Color(0xFFFCE8E6), Color(0xFFD93025)
        ),
        FirstAidTip(
            "CPR Guidelines (Adult)",
            "1. Deliver 30 quick chest compressions.\n2. Press 2 inches deep in center (100 times/min).\n3. Allow perfect recoil after every recoil compression.",
            Color(0xFFE8F0FE), Color(0xFF0B57D0)
        ),
        FirstAidTip(
            "Heatstroke First-Aid",
            "1. Move immediately to heavily shaded cool space.\n2. Mist or moisten skin. Apply cool ice packs.\n3. Keep legs slightly raised.",
            Color(0xFFE0F7FA), Color(0xFF007A87)
        )
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(tips) { tip ->
            Card(
                modifier = Modifier
                    .width(260.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEF1F6))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(tip.iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = tip.iconColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tip.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = tip.rescueActions,
                        fontSize = 11.sp,
                        color = Color(0xFF5F6368),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
