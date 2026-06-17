package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LifelineRepository
import kotlinx.coroutines.delay

sealed class Screen {
    object Splash : Screen()
    object Onboarding : Screen()
    object Welcome : Screen()
    data class PhoneInput(val dummy: String = "") : Screen()
    data class OTPInput(val phoneVal: String) : Screen()
    object MainAppHub : Screen()
    object NotificationsLog : Screen()
    object MedicalIDCardFullScreen : Screen()
}

enum class HomeTab {
    HOME, EMERGENCY, NEARBY, COMMUNITY, PROFILE
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LifeLineAppContainer() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    val onboardingDone by LifelineRepository.onboardingCompleted.collectAsState()
    val isUserLoggedIn by LifelineRepository.isUserLoggedIn.collectAsState()

    // Splash Timeout Coordinator
    if (currentScreen is Screen.Splash) {
        LaunchedEffect(Unit) {
            delay(1600) // Reassuring 1.6s brand exposure
            currentScreen = when {
                !onboardingDone -> Screen.Onboarding
                !isUserLoggedIn -> Screen.Welcome
                else -> Screen.MainAppHub
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn() togetherWith slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeOut()
            },
            label = "screen_navigation"
        ) { screen ->
            when (screen) {
                is Screen.Splash -> LifeLineSplashScreen()
                is Screen.Onboarding -> OnboardingScreen(
                    onNavigateToWelcome = { currentScreen = Screen.Welcome }
                )
                is Screen.Welcome -> WelcomeScreen(
                    onNavigateToPhoneInput = { currentScreen = Screen.PhoneInput() },
                    onNavigateAsGuest = { currentScreen = Screen.MainAppHub }
                )
                is Screen.PhoneInput -> PhoneInputScreen(
                    onNavigateToOTP = { phone -> currentScreen = Screen.OTPInput(phone) },
                    onBack = { currentScreen = Screen.Welcome }
                )
                is Screen.OTPInput -> OTPVerificationScreen(
                    paramPhoneString = screen.phoneVal,
                    onVerifySuccess = { currentScreen = Screen.MainAppHub },
                    onBack = { currentScreen = Screen.PhoneInput() }
                )
                is Screen.MainAppHub -> MainAppScrollerHub(
                    onNavigateToNotifications = { currentScreen = Screen.NotificationsLog },
                    onNavigateToMedicalID = { currentScreen = Screen.MedicalIDCardFullScreen },
                    onLogoutTrigger = {
                        LifelineRepository.logout()
                        currentScreen = Screen.Welcome
                    }
                )
                is Screen.NotificationsLog -> NotificationInboxScreen(
                    onNavigateBack = { currentScreen = Screen.MainAppHub }
                )
                is Screen.MedicalIDCardFullScreen -> MedicalEmergencyCardView(
                    onDismiss = { currentScreen = Screen.MainAppHub }
                )
            }
        }
    }
}

@Composable
fun LifeLineSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("splash_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Heartbeat medical shield logo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F0FE)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_launcher_foreground),
                    contentDescription = "Shield Logo",
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LifeLine Connect",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F),
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Help is close. Communities connect.",
                fontSize = 13.sp,
                color = Color(0xFF5F6368),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainAppScrollerHub(
    onNavigateToNotifications: () -> Unit,
    onNavigateToMedicalID: () -> Unit,
    onLogoutTrigger: () -> Unit
) {
    var activeTab by remember { mutableStateOf(HomeTab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav")
            ) {
                // HOME TAB
                NavigationBarItem(
                    selected = activeTab == HomeTab.HOME,
                    onClick = { activeTab = HomeTab.HOME },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == HomeTab.HOME) Icons.Default.Home else Icons.Outlined.Home,
                            contentDescription = "Home Dashboard"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0B57D0),
                        unselectedIconColor = Color(0xFF5F6368),
                        selectedTextColor = Color(0xFF0B57D0),
                        unselectedTextColor = Color(0xFF5F6368),
                        indicatorColor = Color(0xFFE8F0FE)
                    )
                )

                // EMERGENCY TAB
                NavigationBarItem(
                    selected = activeTab == HomeTab.EMERGENCY,
                    onClick = { activeTab = HomeTab.EMERGENCY },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == HomeTab.EMERGENCY) Icons.Default.Security else Icons.Outlined.Security,
                            contentDescription = "SOS Hub"
                        )
                    },
                    label = { Text("Emergency", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFD93025),
                        unselectedIconColor = Color(0xFF5F6368),
                        selectedTextColor = Color(0xFFD93025),
                        unselectedTextColor = Color(0xFF5F6368),
                        indicatorColor = Color(0xFFFFECEB)
                    )
                )

                // NEARBY HELP TAB
                NavigationBarItem(
                    selected = activeTab == HomeTab.NEARBY,
                    onClick = { activeTab = HomeTab.NEARBY },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == HomeTab.NEARBY) Icons.Default.Map else Icons.Outlined.Map,
                            contentDescription = "Nearby Matrix"
                        )
                    },
                    label = { Text("Nearby Help", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF007A87),
                        unselectedIconColor = Color(0xFF5F6368),
                        selectedTextColor = Color(0xFF007A87),
                        unselectedTextColor = Color(0xFF5F6368),
                        indicatorColor = Color(0xFFE0F7FA)
                    )
                )

                // COMMUNITY TAB
                NavigationBarItem(
                    selected = activeTab == HomeTab.COMMUNITY,
                    onClick = { activeTab = HomeTab.COMMUNITY },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == HomeTab.COMMUNITY) Icons.Default.Groups else Icons.Outlined.Groups,
                            contentDescription = "Community Grid"
                        )
                    },
                    label = { Text("Community", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF137333),
                        unselectedIconColor = Color(0xFF5F6368),
                        selectedTextColor = Color(0xFF137333),
                        unselectedTextColor = Color(0xFF5F6368),
                        indicatorColor = Color(0xFFE6F4EA)
                    )
                )

                // PROFILE TAB
                NavigationBarItem(
                    selected = activeTab == HomeTab.PROFILE,
                    onClick = { activeTab = HomeTab.PROFILE },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == HomeTab.PROFILE) Icons.Default.Person else Icons.Outlined.Person,
                            contentDescription = "Personal Panel"
                        )
                    },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0B57D0),
                        unselectedIconColor = Color(0xFF5F6368),
                        selectedTextColor = Color(0xFF0B57D0),
                        unselectedTextColor = Color(0xFF5F6368),
                        indicatorColor = Color(0xFFE8F0FE)
                    )
                )
            }
        },
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                HomeTab.HOME -> DashboardScreen(
                    onNavigateToSOSMenu = { activeTab = HomeTab.EMERGENCY },
                    onNavigateToNearbyCategory = { activeTab = HomeTab.NEARBY },
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToMedicalID = onNavigateToMedicalID,
                    onNavigateToProfile = { activeTab = HomeTab.PROFILE }
                )
                HomeTab.EMERGENCY -> EmergencyCoreScreen(
                    onNavigateBack = { activeTab = HomeTab.HOME },
                    onNavigateToMedicalCard = onNavigateToMedicalID
                )
                HomeTab.NEARBY -> NearbyHelpListScreen(
                    onNavigateBack = { activeTab = HomeTab.HOME }
                )
                HomeTab.COMMUNITY -> CommunityDeckScreen(
                    onNavigateBack = { activeTab = HomeTab.HOME }
                )
                HomeTab.PROFILE -> ProfileDeckScreen(
                    onNavigateToMedicalCard = onNavigateToMedicalID,
                    onNavigateBack = { activeTab = HomeTab.HOME }
                )
            }
        }
    }
}
