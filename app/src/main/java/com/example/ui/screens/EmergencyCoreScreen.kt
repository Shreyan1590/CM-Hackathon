package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Campaign
import android.media.ToneGenerator
import android.media.AudioManager
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LifelineRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyCoreScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMedicalCard: () -> Unit
) {
    val isSosActive by LifelineRepository.isSosAlertActive.collectAsState()
    val medicalProfile by LifelineRepository.medicalProfile.collectAsState()
    val contacts by LifelineRepository.emergencyContacts.collectAsState()
    val locationText by LifelineRepository.sosLiveLocation.collectAsState()

    var showSosManualDialog by remember { mutableStateOf(false) }
    var scaleButtonActive by remember { mutableStateOf(false) }

    var isSirenActive by remember { mutableStateOf(false) }
    var strobeColorState by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    // Audio Siren Beep Loop
    LaunchedEffect(isSirenActive) {
        if (isSirenActive) {
            val toneGenerator = try {
                ToneGenerator(AudioManager.STREAM_ALARM, 100)
            } catch (e: Exception) {
                null
            }
            try {
                while (true) {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 350)
                    delay(500)
                }
            } catch (e: Exception) {
                // handle safely
            } finally {
                toneGenerator?.release()
            }
        }
    }

    // Screen Brightness Strobe & Background Color Trigger
    LaunchedEffect(isSirenActive) {
        val window = activity?.window
        val layoutParams = window?.attributes
        val originalBrightness = layoutParams?.screenBrightness ?: -1f

        try {
            if (isSirenActive) {
                while (true) {
                    strobeColorState = !strobeColorState

                    // Strobe actual device screen brightness
                    layoutParams?.screenBrightness = if (strobeColorState) 1.0f else 0.1f
                    window?.attributes = layoutParams

                    delay(200)
                }
            } else {
                // Reset standard brightness
                layoutParams?.screenBrightness = originalBrightness
                window?.attributes = layoutParams
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    val scaffoldContainerColor = if (isSirenActive) {
        if (strobeColorState) Color(0xFFFFD5D5) else Color(0xFFFF8585)
    } else {
        Color.White
    }

    val coroutineScope = rememberCoroutineScope()

    // Breathing pulse animations for holding prompt
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_red")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (isSosActive) 1.0f else 0.95f,
        targetValue = if (isSosActive) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Emergency Hub",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F),
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = scaffoldContainerColor)
            )
        },
        containerColor = scaffoldContainerColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .testTag("emergency_screen_container"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Status message
            Text(
                text = if (isSosActive) "SOS Alert Broadcast Active" else "Help is just one tap away",
                fontSize = 14.sp,
                color = if (isSosActive) Color(0xFFD93025) else Color(0xFF5F6368),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Massive red SOS Button layout
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(12.dp)
                    .testTag("emergency_sos_button_container"),
                contentAlignment = Alignment.Center
            ) {
                // Background pulsing wave halo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            if (isSosActive) Color(0xFFD93025).copy(alpha = 0.15f)
                            else Color(0xFFFFDAD6).copy(alpha = 0.4f)
                        )
                )

                // The Central Button
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSosActive) Color(0xFFB31412) else Color(0xFFD93025)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    LifelineRepository.triggerSOS()
                                },
                                onTap = {
                                    // Gentle safety warning prompting dialog
                                    showSosManualDialog = true
                                }
                            )
                        }
                        .testTag("emergency_trigger_sos_tap"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SOS",
                            color = Color.White,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSosActive) "Broadcasting" else "Press-and-Hold",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Quick state verification / rescue trigger summary
            AnimatedVisibility(
                visible = isSosActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { LifelineRepository.reportUserSafe() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF137333)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("emergency_report_safe")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("I AM SAFE - Resolve SOS", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Notifies your guardians that the danger has ceased.",
                        fontSize = 11.sp,
                        color = Color(0xFF5F6368)
                    )
                }
            }

            // Rescue Live Coordinates Dispatch Monitor
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDADCE0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LOCATION BROADCAST",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSosActive) Color(0xFFD93025) else Color(0xFF5F6368)
                        )
                        if (isSosActive) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFCE8E6))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "LIVE FEED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD93025)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = locationText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F1F1F)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Transmitted via encrypted SMS protocols to family contacts even when data pipelines lag.",
                        fontSize = 11.sp,
                        color = Color(0xFF5F6368),
                        lineHeight = 16.sp
                    )
                }
            }

            // Quick Hotlines Row
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDADCE0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "National Emergency Hotlines",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // National helpline integration
                        Button(
                            onClick = { /* Call 112 */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF1F6))
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF0B57D0), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("112 Center", color = Color(0xFF0B57D0), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { /* Call 108 */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF1F6))
                        ) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFFD93025), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("108 Ambulance", color = Color(0xFFD93025), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Emergency Siren Card (Loud Audio distress & screen strobe)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("emergency_siren_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSirenActive) Color(0xFFFFDAD6) else Color(0xFFFBEBEB)
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isSirenActive) Color(0xFFD93025) else Color(0xFFF1C0C0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isSirenActive) Color(0xFFD93025) else Color(0xFFFCE8E6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Siren Symbol",
                            tint = if (isSirenActive) Color.White else Color(0xFFD93025)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DISTRESS BEACON SIREN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD93025),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Loud Alarm & Screen Strobe",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F)
                        )
                        Text(
                            text = "Emits repetitive warning beeps and flashes backdrops to draw rescuers in crowded areas.",
                            fontSize = 11.sp,
                            color = Color(0xFF5F6368),
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = isSirenActive,
                        onCheckedChange = { isSirenActive = it },
                        modifier = Modifier.testTag("emergency_siren_toggle"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFD93025),
                            uncheckedThumbColor = Color(0xFF5F6368),
                            uncheckedTrackColor = Color(0xFFEEF1F6)
                        )
                    )
                }
            }

            // Medical ID Shortcut Peek Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToMedicalCard() }
                    .testTag("emergency_medical_card_shortcut"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF1F6))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = Color(0xFF0B57D0)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "First-Responder Medical ID",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F)
                        )
                        Text(
                            text = "Tap to show allergies, blood variables, and rescue QR",
                            fontSize = 12.sp,
                            color = Color(0xFF5F6368)
                        )
                    }

                    Icon(Icons.Default.VolumeUp, contentDescription = "Siren/Guide hint", tint = Color(0xFF5F6368))
                }
            }

            // Immediate SOS Trigger safety manual dialogue
            if (showSosManualDialog) {
                AlertDialog(
                    onDismissRequest = { showSosManualDialog = false },
                    icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD93025)) },
                    title = {
                        Text(
                            text = "Trigger Emergency SOS?",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Text(
                            text = "This will immediately simulate dispatching coordinates to Priyah Kumar, Dr. Amit Kumar and nearby community volunteers. Please confirm your intent.",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color(0xFF5F6368)
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSosManualDialog = false
                                LifelineRepository.triggerSOS()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD93025))
                        ) {
                            Text("Yes, Send Alert", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSosManualDialog = false }) {
                            Text("Cancel", color = Color(0xFF5F6368))
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = Color.White
                )
            }
        }
    }
}
