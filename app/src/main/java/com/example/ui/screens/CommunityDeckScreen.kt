package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDeckScreen(
    onNavigateBack: () -> Unit
) {
    val volunteers by LifelineRepository.volunteers.collectAsState()
    var selectedFilterType by remember { mutableStateOf<VolunteerType?>(null) }
    var showRequestHelpDialog by remember { mutableStateOf(false) }
    var showOfferHelpDialog by remember { mutableStateOf(false) }

    // Request help states
    var requestTitle by remember { mutableStateOf("") }
    var requestDescription by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("First Aid") }

    // Offer help states
    var offerName by remember { mutableStateOf("") }
    var offerPhone by remember { mutableStateOf("") }
    var offerType by remember { mutableStateOf(VolunteerType.GENERAL_SUPPORT) }
    var offerDetails by remember { mutableStateOf("") }

    // Filter volunteers List
    val filteredVolunteers = if (selectedFilterType == null) volunteers
    else volunteers.filter { it.type == selectedFilterType }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("community_screen_container")
        ) {
            // Header Hero Deck
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COMMUNITY COHESION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B57D0),
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF137333), modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Verified in Chennai", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF137333))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Verified Local Support",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Connect with local residents who volunteered to provide urgent support. This is a secure community watch interface.",
                        fontSize = 12.sp,
                        color = Color(0xFF5F6368),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showRequestHelpDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B57D0)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("community_request_help_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Request Help", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = { showOfferHelpDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDADCE0)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("community_offer_help_btn")
                        ) {
                            Icon(Icons.Default.Handshake, contentDescription = null, tint = Color(0xFF0B57D0), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Volunteer Self", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B57D0))
                        }
                    }
                }
            }

            // Quick Category Toggles Row
            Text(
                text = "Filter Verified Helpers",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            CommunityFiltersRow(
                selected = selectedFilterType,
                onSelectFilter = { selectedFilterType = it }
            )

            // Dynamic community listings
            if (filteredVolunteers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFFDADCE0), modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("No assistants available matching filter", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("volunteer_listings_scroll")
                ) {
                    items(filteredVolunteers) { volunteer ->
                        VolunteerRowCard(volunteer = volunteer)
                    }
                }
            }
        }

        // DIALOG 1: Request Help Modal Form
        if (showRequestHelpDialog) {
            AlertDialog(
                onDismissRequest = { showRequestHelpDialog = false },
                title = { Text("Submit Dispatch Request", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Describe your immediate community request so verified coordinators can read and respond.",
                            fontSize = 12.sp,
                            color = Color(0xFF5F6368)
                        )

                        // Action type selector
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("First Aid", "O+ Blood", "Elder Support").forEach { cat ->
                                val isSelected = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF0B57D0) else Color(0xFFF1F3F4))
                                        .clickable { selectedCategory = cat }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(cat, color = if (isSelected) Color.White else Color(0xFF5F6368), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = requestTitle,
                            onValueChange = { requestTitle = it },
                            placeholder = { Text("What is needed? (e.g. Oxygen pack)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = requestDescription,
                            onValueChange = { requestDescription = it },
                            placeholder = { Text("Describe location context and instructions...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (requestTitle.isNotEmpty()) {
                                showRequestHelpDialog = false
                                LifelineRepository.addNotification(
                                    "🚨 Urgent Help Request Brocasted",
                                    "Your request for '$requestTitle' ($selectedCategory) is live in Shastri Nagar.",
                                    AlertType.EMERGENCY
                                )
                                requestTitle = ""
                                requestDescription = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD93025))
                    ) {
                        Text("Send Broadcast", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRequestHelpDialog = false }) {
                        Text("Cancel", color = Color(0xFF5F6368))
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }

        // DIALOG 2: Offer Help Modal Form
        if (showOfferHelpDialog) {
            AlertDialog(
                onDismissRequest = { showOfferHelpDialog = false },
                title = { Text("Enroll as LifeLine Assistant", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Thank you for enrolling in Chennai emergency grids. Provide your coordinates so local citizens can discover you.",
                            fontSize = 12.sp,
                            color = Color(0xFF5F6368)
                        )

                        OutlinedTextField(
                            value = offerName,
                            onValueChange = { offerName = it },
                            placeholder = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = offerPhone,
                            onValueChange = { offerPhone = it },
                            placeholder = { Text("Contact Phone (+91 ##########)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = offerDetails,
                            onValueChange = { offerDetails = it },
                            placeholder = { Text("Specialty details (e.g. CPR nurse, O- donor)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (offerName.isNotEmpty() && offerPhone.isNotEmpty()) {
                                showOfferHelpDialog = false
                                LifelineRepository.addNotification(
                                    "Volunteer Registration Transmitted",
                                    "Successfully registered as certified responder. Welcome, $offerName!",
                                    AlertType.SUCCESS
                                )
                                // Demo simulate item addition to state:
                                // To keep it direct, we notify user on UI thread.
                                offerName = ""
                                offerPhone = ""
                                offerDetails = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137333))
                    ) {
                        Text("Complete Enrollment", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showOfferHelpDialog = false }) {
                        Text("Cancel", color = Color(0xFF5F6368))
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun CommunityFiltersRow(
    selected: VolunteerType?,
    onSelectFilter: (VolunteerType?) -> Unit
) {
    val filters = listOf(
        null to "All Helpers",
        VolunteerType.MEDICAL_TRAINED to "Medically Certified",
        VolunteerType.BLOOD_DONOR to "Blood Donors",
        VolunteerType.COORDINATOR to "Coordinators",
        VolunteerType.GENERAL_SUPPORT to "General Volunteers"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.testTag("community_filters_row")
    ) {
        items(filters) { (type, label) ->
            val isSelected = selected == type
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) Color(0xFF007A87) else Color(0xFFF1F3F4))
                    .clickable { onSelectFilter(type) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else Color(0xFF5F6368),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun VolunteerRowCard(volunteer: CommunityVolunteer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("volunteer_card_${volunteer.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEF1F6))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (iconVector, headerBg, pinColor) = getVolunteerStyling(volunteer.type)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(headerBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = volunteer.type.name,
                            tint = pinColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = volunteer.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F)
                            )
                            if (volunteer.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Identity",
                                    tint = Color(0xFF0B57D0),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = volunteer.cityRegion,
                            fontSize = 12.sp,
                            color = Color(0xFF5F6368)
                        )
                    }
                }

                // Reliability Index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE6F4EA))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Score ${volunteer.trustScore}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF137333)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actionable details description block
            Text(
                text = volunteer.details,
                fontSize = 13.sp,
                color = Color(0xFF3C4043),
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive contact panel footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8F9FA))
                    .clickable { /* Dialer triggered */ }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Dial Helper",
                    tint = Color(0xFF0B57D0),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Request Help dispatch via Mobile",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B57D0)
                )
            }
        }
    }
}

fun getVolunteerStyling(type: VolunteerType): Triple<ImageVector, Color, Color> {
    return when (type) {
        VolunteerType.MEDICAL_TRAINED -> Triple(Icons.Default.HealthAndSafety, Color(0xFFFCE8E6), Color(0xFFD93025))
        VolunteerType.BLOOD_DONOR -> Triple(Icons.Default.HeartBroken, Color(0xFFFFECEB), Color(0xFFD93025))
        VolunteerType.COORDINATOR -> Triple(Icons.Default.HomeWork, Color(0xFFE8F0FE), Color(0xFF0B57D0))
        VolunteerType.GENERAL_SUPPORT -> Triple(Icons.Default.Handshake, Color(0xFFE6F4EA), Color(0xFF137333))
    }
}
