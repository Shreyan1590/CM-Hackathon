package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Directions
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LifelineRepository
import com.example.data.NearbyService
import com.example.data.ServiceCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyHelpListScreen(
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedCategory by LifelineRepository.selectedServiceCategory.collectAsState()
    val allServices by LifelineRepository.nearbyServices.collectAsState()

    // Filter points of interest list
    val filteredServices = allServices.filter { service ->
        val matchesCategory = selectedCategory == ServiceCategory.ALL || service.category == selectedCategory
        val matchesSearch = service.name.contains(searchQuery, ignoreCase = true) ||
                service.address.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("nearby_screen_container")
        ) {
            // Visual Vector Map Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFEEF2F6))
                    .testTag("nearby_map_container")
            ) {
                // Code-drawn map mesh
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid streets drawing
                    val streetColor = Color.White
                    val strokeWidth = 8.dp.toPx()

                    // Horizontal streets
                    drawLine(streetColor, Offset(0f, height * 0.25f), Offset(width, height * 0.25f), strokeWidth)
                    drawLine(streetColor, Offset(0f, height * 0.7f), Offset(width, height * 0.7f), strokeWidth)

                    // Vertical streets
                    drawLine(streetColor, Offset(width * 0.35f, 0f), Offset(width * 0.35f, height), strokeWidth)
                    drawLine(streetColor, Offset(width * 0.75f, 0f), Offset(width * 0.75f, height), strokeWidth)

                    // Secondary diagonal lane
                    drawLine(streetColor, Offset(0f, 0f), Offset(width, height), strokeWidth / 2)
                }

                // Pulsating user live pointer pin
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-20).dp, y = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B57D0).copy(alpha = 0.3f))
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B57D0))
                            .align(Alignment.Center)
                    )
                }

                // Hospital pin overlays in different sector coordinates
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 60.dp, y = 30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PinDrop,
                        contentDescription = null,
                        tint = Color(0xFFD93025),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-80).dp, y = (-40).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PinDrop,
                        contentDescription = null,
                        tint = Color(0xFF007A87),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Interactive Map control HUD overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Navigation,
                                contentDescription = null,
                                tint = Color(0xFF0B57D0),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Adyar (Accuracy 5m)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { /* Locate current */ }
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.PinDrop,
                            contentDescription = "Recenter",
                            tint = Color(0xFF1F1F1F),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Interactive query bar mapping
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter medical facilities near you...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nearby_search_field"),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
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

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F3F4))
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filters",
                        tint = Color(0xFF3C4043),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Toggle category horizontal chips
            ServiceCategoryPillsRow(
                selected = selectedCategory,
                onCategorySelect = { cat ->
                    LifelineRepository.selectedServiceCategory.value = cat
                }
            )

            // points of interest list
            if (filteredServices.isEmpty()) {
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
                        Icon(
                            Icons.Default.Map,
                            contentDescription = null,
                            tint = Color(0xFFDADCE0),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            "No matched emergency facilities found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Try choosing a different directory filter or refining search keyword.",
                            fontSize = 12.sp,
                            color = Color(0xFF5F6368),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("nearby_scroll_items"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredServices) { service ->
                        NearbyServiceListCard(service = service)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCategoryPillsRow(
    selected: ServiceCategory,
    onCategorySelect: (ServiceCategory) -> Unit
) {
    val categories = listOf(
        ServiceCategory.ALL to "All Point",
        ServiceCategory.HOSPITALS to "Hospitals",
        ServiceCategory.PHARMACY to "Pharmacies",
        ServiceCategory.BLOOD_BANK to "Blood Banks",
        ServiceCategory.POLICE to "Police Dept",
        ServiceCategory.SHELTER to "Rescues",
        ServiceCategory.AMBULANCE to "Ambulances"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.testTag("category_pills_row")
    ) {
        items(categories) { (cat, label) ->
            val isSelected = selected == cat
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) Color(0xFF0B57D0) else Color(0xFFF1F3F4)
                    )
                    .clickable { onCategorySelect(cat) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else Color(0xFF5F6368),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NearbyServiceListCard(service: NearbyService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("service_card_${service.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEF1F6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category Icon & Trust Index Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (icon, color) = getCategoryStyling(service.category)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = service.category.name,
                            tint = color,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${service.distanceKm} km away",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B57D0)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Status",
                        tint = Color(0xFF137333),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${service.trustScore}% Trust Score",
                        fontSize = 11.sp,
                        color = Color(0xFF137333),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Name
            Text(
                text = service.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )

            // Address
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = service.address,
                fontSize = 12.sp,
                color = Color(0xFF5F6368),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Open status tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (service.statusText.contains("24/7", ignoreCase = true)) Color(0xFF137333) else Color(0xFFE37400)
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = service.statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5F6368)
                    )
                }

                // Interactive Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { /* Simulated dial action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF1F6)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Dial Clinic",
                            tint = Color(0xFF0B57D0),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", color = Color(0xFF0B57D0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { /* Simulated navigation route */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B57D0)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            Icons.Default.Directions,
                            contentDescription = "Navigate to Clinic",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Navigate", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun getCategoryStyling(category: ServiceCategory): Pair<ImageVector, Color> {
    return when (category) {
        ServiceCategory.HOSPITALS -> Icons.Default.LocalHospital to Color(0xFFD93025)
        ServiceCategory.PHARMACY -> Icons.Default.LocalPharmacy to Color(0xFF007A87)
        ServiceCategory.BLOOD_BANK -> Icons.Default.HealthAndSafety to Color(0xFFD93025)
        ServiceCategory.POLICE -> Icons.Default.LocalPolice to Color(0xFF3C4043)
        ServiceCategory.SHELTER -> Icons.Default.Verified to Color(0xFF137333)
        ServiceCategory.AMBULANCE -> Icons.Default.Directions to Color(0xFF0B57D0)
        else -> Icons.Default.Map to Color(0xFF5F6368)
    }
}
