package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDeckScreen(
    onNavigateToMedicalCard: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val medicalProfile by LifelineRepository.medicalProfile.collectAsState()
    val emergencyContacts by LifelineRepository.emergencyContacts.collectAsState()
    val selectedLanguage by LifelineRepository.selectedLanguage.collectAsState()

    var isEditMode by remember { mutableStateOf(false) }

    // Forms states
    var editName by remember { mutableStateOf(medicalProfile.name) }
    var editAge by remember { mutableStateOf(medicalProfile.age.toString()) }
    var editBlood by remember { mutableStateOf(medicalProfile.bloodGroup) }
    var editAllergies by remember { mutableStateOf(medicalProfile.allergies) }
    var editConditions by remember { mutableStateOf(medicalProfile.chronicConditions) }
    var editMedications by remember { mutableStateOf(medicalProfile.medications) }
    var editAddress by remember { mutableStateOf(medicalProfile.address) }

    // Contact form state
    var showAddContactDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactRelation by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }

    // Synchronize form values on edit start
    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            editName = medicalProfile.name
            editAge = medicalProfile.age.toString()
            editBlood = medicalProfile.bloodGroup
            editAllergies = medicalProfile.allergies
            editConditions = medicalProfile.chronicConditions
            editMedications = medicalProfile.medications
            editAddress = medicalProfile.address
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("profile_screen_container"),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Card Header Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDADCE0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F0FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = medicalProfile.name.firstOrNull()?.toString()?.uppercase() ?: "R",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B57D0)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = medicalProfile.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )

                    Text(
                        text = "Emergency Medical Class: ${medicalProfile.bloodGroup}",
                        fontSize = 13.sp,
                        color = Color(0xFFD93025),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToMedicalCard,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF1F6)),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("profile_view_card_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.MedicalInformation, contentDescription = null, tint = Color(0xFF0B57D0), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("See Responders Card", color = Color(0xFF0B57D0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (isEditMode) {
                                    // SAVE FORMS
                                    val updatedProfile = medicalProfile.copy(
                                        name = editName,
                                        age = editAge.toIntOrNull() ?: medicalProfile.age,
                                        bloodGroup = editBlood,
                                        allergies = editAllergies,
                                        chronicConditions = editConditions,
                                        medications = editMedications,
                                        address = editAddress
                                    )
                                    LifelineRepository.updateMedicalProfile(updatedProfile)
                                    isEditMode = false
                                } else {
                                    isEditMode = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEditMode) Color(0xFF137333) else Color(0xFF0B57D0)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("profile_edit_toggle_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (isEditMode) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEditMode) "Save Changes" else "Edit Core Info", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = isEditMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "profile_edit_switch"
            ) { editActive ->
                if (editActive) {
                    // Inline Edit Form Panel
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.testTag("profile_edit_form")
                    ) {
                        Text("Core Medical Registration Details", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))

                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = editAge,
                                onValueChange = { editAge = it },
                                label = { Text("Age") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = editBlood,
                                onValueChange = { editBlood = it },
                                label = { Text("Blood Group") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = editAllergies,
                            onValueChange = { editAllergies = it },
                            label = { Text("Known Allergies (comma separated)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = editConditions,
                            onValueChange = { editConditions = it },
                            label = { Text("Chronic Conditions") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = editMedications,
                            onValueChange = { editMedications = it },
                            label = { Text("Current Medications") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = editAddress,
                            onValueChange = { editAddress = it },
                            label = { Text("Permanent Address in India") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                } else {
                    // Static Medical Specification Display
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Medical Specifications ID", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ProfileDisplayRow(label = "Age Profile", value = "${medicalProfile.age} Years")
                            ProfileDisplayRow(label = "Known Allergies", value = medicalProfile.allergies, isAlert = true)
                            ProfileDisplayRow(label = "Chronic States", value = medicalProfile.chronicConditions)
                            ProfileDisplayRow(label = "Medications Ingested", value = medicalProfile.medications)
                            ProfileDisplayRow(label = "Home coordinates address", value = medicalProfile.address)
                            ProfileDisplayRow(
                                label = "Organ Donor Class",
                                value = if (medicalProfile.organDonorStatus) "ACTIVE DONOR (Verified)" else "NOT DECLARED"
                            )
                        }
                    }
                }
            }

            // Emergency Contacts Setup Panel
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SOS Transmit List",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )

                    IconButton(
                        onClick = { showAddContactDialog = true },
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("profile_add_contact_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Rescue Contact", tint = Color(0xFF0B57D0))
                    }
                }

                Text(
                    text = "These relations will receive your live coordinates automatically whenever an SOS is active.",
                    fontSize = 12.sp,
                    color = Color(0xFF5F6368)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emergencyContacts.forEach { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8F9FA))
                                .border(1.dp, Color(0xFFEEF1F6), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFECEB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ContactPhone, contentDescription = null, tint = Color(0xFFD93025), modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(contact.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                                    Text("${contact.relationship} • ${contact.phone}", fontSize = 11.sp, color = Color(0xFF5F6368))
                                }
                            }

                            IconButton(
                                onClick = { LifelineRepository.deleteEmergencyContact(contact.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove contact", tint = Color(0xFFD93025), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // Language Selector Configuration Group
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("App Linguistic Preference", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                Text("Select language for stress guidance and emergency tools:", fontSize = 11.sp, color = Color(0xFF5F6368))
                Box(modifier = Modifier.padding(top = 4.dp)) {
                    LanguageSelectorMini()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Add Emergency Contact Dialog form
        if (showAddContactDialog) {
            AlertDialog(
                onDismissRequest = { showAddContactDialog = false },
                title = { Text("Link SOS Recipient", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = newContactName,
                            onValueChange = { newContactName = it },
                            placeholder = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = newContactRelation,
                            onValueChange = { newContactRelation = it },
                            placeholder = { Text("Relationship (e.g. Spouse)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = newContactPhone,
                            onValueChange = { newContactPhone = it },
                            placeholder = { Text("Mobile (+91 ##########)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newContactName.isNotEmpty() && newContactPhone.isNotEmpty()) {
                                showAddContactDialog = false
                                val item = EmergencyContact(
                                    id = System.currentTimeMillis().toString(),
                                    name = newContactName,
                                    phone = newContactPhone,
                                    relationship = newContactRelation,
                                    isSosRecipient = true
                                )
                                LifelineRepository.addEmergencyContact(item)
                                newContactName = ""
                                newContactRelation = ""
                                newContactPhone = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B57D0))
                    ) {
                        Text("Save Parameter", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddContactDialog = false }) {
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
fun ProfileDisplayRow(
    label: String,
    value: String,
    isAlert: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8F9FA))
            .border(1.dp, Color(0xFFEEF1F6), RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF5F6368), fontWeight = FontWeight.Medium)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAlert) Color(0xFFD93025) else Color(0xFF1F1F1F),
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 200.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalEmergencyCardView(
    onDismiss: () -> Unit
) {
    val profile by LifelineRepository.medicalProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("First-Responder Medical Card", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                actions = {
                    IconButton(onClick = { /* Share PDF simulation */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share PDF", tint = Color(0xFF0B57D0))
                    }
                }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .testTag("medical_card_view_container"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Show this screen to paramedic responders or citizens assisting you.",
                fontSize = 12.sp,
                color = Color(0xFF5F6368),
                textAlign = TextAlign.Center
            )

            // High Contrast Medical CARD element resembling physical ID card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFFD93025), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    // Alert Header Ribbon
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD93025))
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CRITICAL LIFE-LINE SAFETY ID", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }

                            // Indian flag color scheme dots represent national support
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFF9933)))
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF128807)))
                            }
                        }
                    }

                    // Card Body
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(profile.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                                Text("Age: ${profile.age} • Organ Donor: Yes", fontSize = 12.sp, color = Color(0xFF5F6368))
                            }

                            // Big Red Blood Type Indicator Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFCE8E6))
                                    .border(1.dp, Color(0xFFD93025), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(profile.bloodGroup, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD93025))
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEF1F6))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            CardValueRow(label = "KNOWN ALLERGIES", value = profile.allergies, isRed = true)
                            CardValueRow(label = "CHRONIC MEDICAL CONDITIONS", value = profile.chronicConditions)
                            CardValueRow(label = "MEDICATION PRESCRIBED", value = profile.medications)
                            CardValueRow(label = "RESIDENTIAL ADDRESS", value = profile.address)
                            CardValueRow(label = "EMERGENCY GUARDIANS", value = "${profile.emergencyContactName} (${profile.emergencyContactPhone})")
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEF1F6))

                        // QR Code Placeholder and scan metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("DIGITAL ENCRYPTED MATCH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3C4043))
                                Text("Scan QR with responder terminal for verification log keys.", fontSize = 10.sp, color = Color(0xFF5F6368), modifier = Modifier.width(180.dp), lineHeight = 14.sp)
                            }

                            // Draw a beautiful QR Code mock with Canvas
                            Canvas(modifier = Modifier.size(64.dp)) {
                                val size = size.minDimension

                                // Draw boundary box
                                drawRect(Color(0xFF1F1F1F), style = Stroke(width = 2.dp.toPx()))

                                // Draw central pixels simulating qr barcode matrix
                                val strokeWidth = 6.dp.toPx()
                                drawLine(Color(0xFF1F1F1F), Offset(10.dp.toPx(), 10.dp.toPx()), Offset(24.dp.toPx(), 10.dp.toPx()), strokeWidth)
                                drawLine(Color(0xFF1F1F1F), Offset(10.dp.toPx(), 10.dp.toPx()), Offset(10.dp.toPx(), 24.dp.toPx()), strokeWidth)

                                drawLine(Color(0xFF1F1F1F), Offset(40.dp.toPx(), 10.dp.toPx()), Offset(54.dp.toPx(), 10.dp.toPx()), strokeWidth)
                                drawLine(Color(0xFF1F1F1F), Offset(54.dp.toPx(), 10.dp.toPx()), Offset(54.dp.toPx(), 24.dp.toPx()), strokeWidth)

                                drawLine(Color(0xFF1F1F1F), Offset(10.dp.toPx(), 40.dp.toPx()), Offset(10.dp.toPx(), 54.dp.toPx()), strokeWidth)
                                drawLine(Color(0xFF1F1F1F), Offset(10.dp.toPx(), 54.dp.toPx()), Offset(24.dp.toPx(), 54.dp.toPx()), strokeWidth)

                                // Central simulated noise pattern
                                drawRect(Color(0xFF1F1F1F), Offset(18.dp.toPx(), 18.dp.toPx()), androidx.compose.ui.geometry.Size(12.dp.toPx(), 12.dp.toPx()))
                                drawRect(Color(0xFF1F1F1F), Offset(32.dp.toPx(), 32.dp.toPx()), androidx.compose.ui.geometry.Size(16.dp.toPx(), 16.dp.toPx()))
                                drawRect(Color(0xFF1F1F1F), Offset(18.dp.toPx(), 38.dp.toPx()), androidx.compose.ui.geometry.Size(8.dp.toPx(), 8.dp.toPx()))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Informative help tip for physical placement
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD93025))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tip: You can download this digital Card in high resolution and set it as your lock screen wallpaper to keep responders prepared.",
                        fontSize = 11.sp,
                        color = Color(0xFF5F6368),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CardValueRow(
    label: String,
    value: String,
    isRed: Boolean = false
) {
    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5F6368), letterSpacing = 0.5.sp)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isRed) Color(0xFFD93025) else Color(0xFF1F1F1F),
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}
