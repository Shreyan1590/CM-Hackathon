package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LifelineRepository {

    // Onboarding and Auth State
    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted = _onboardingCompleted.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    private val _loggedInPhone = MutableStateFlow("")
    val loggedInPhone = _loggedInPhone.asStateFlow()

    private val _isGuestUser = MutableStateFlow(false)
    val isGuestUser = _isGuestUser.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("English") // "English", "Hindi", "Tamil"
    val selectedLanguage = _selectedLanguage.asStateFlow()

    // Offline simulated mode
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode = _isOfflineMode.asStateFlow()

    // SOS State
    private val _isSosAlertActive = MutableStateFlow(false)
    val isSosAlertActive = _isSosAlertActive.asStateFlow()

    private val _sosLiveLocation = MutableStateFlow("Adyar, Chennai (13.0063° N, 80.2574° E)")
    val sosLiveLocation = _sosLiveLocation.asStateFlow()

    // Medical ID State
    private val _medicalProfile = MutableStateFlow(MedicalProfile())
    val medicalProfile = _medicalProfile.asStateFlow()

    // Emergency Contacts State
    private val _emergencyContacts = MutableStateFlow(
        listOf(
            EmergencyContact("1", "Priyah Kumar", "+91 98401 23456", "Spouse (Primary)", true),
            EmergencyContact("2", "Dr. Amit Kumar", "+91 94440 87654", "Family Physician", true),
            EmergencyContact("3", "Vikram Kumar", "+91 91234 56789", "Brother", false)
        )
    )
    val emergencyContacts = _emergencyContacts.asStateFlow()

    // Nearby Help Services
    private val _nearbyServices = MutableStateFlow(
        listOf(
            NearbyService(
                "h1", "Fortis Malar Hospital", ServiceCategory.HOSPITALS, 0.8, "Open 24/7", 98, 4.6,
                "+91 44 4289 2222", "No. 52, 1st Main Rd, Gandhi Nagar, Adyar, Chennai - 600020"
            ),
            NearbyService(
                "h2", "Adayar Cancer Institute", ServiceCategory.HOSPITALS, 1.4, "Open 24/7", 95, 4.7,
                "+91 44 2491 0754", "Corner of Sardar Patel Rd, Adyar, Chennai - 600020"
            ),
            NearbyService(
                "h3", "Apollo Urgent Clinic", ServiceCategory.HOSPITALS, 2.1, "Closes at 10:00 PM", 92, 4.4,
                "+91 44 2442 3311", "No. 32, Sardar Patel Rd, Kasturba Nagar, Adyar, Chennai - 600020"
            ),
            NearbyService(
                "p1", "Apollo Pharmacy 24/7", ServiceCategory.PHARMACY, 0.3, "Open 24/7", 99, 4.5,
                "+91 44 2440 1200", "No. 18, Lattice Bridge Rd, Adyar, Chennai - 600020"
            ),
            NearbyService(
                "p2", "MedPlus Pharmacy", ServiceCategory.PHARMACY, 1.1, "Closes at 11:00 PM", 86, 4.2,
                "+91 44 2441 5566", "No. 45, Shastri Nagar 1st Main Rd, Chennai - 600020"
            ),
            NearbyService(
                "b1", "Rotary Central Blood Bank", ServiceCategory.BLOOD_BANK, 1.9, "Open 24/7", 94, 4.8,
                "+91 44 2811 1133", "No. 115, TTK Road, Alwarpet, Chennai - 600018"
            ),
            NearbyService(
                "po1", "J4 Adyar Police Station", ServiceCategory.POLICE, 0.9, "Open 24/7", 89, 4.1,
                "+91 44 2345 2582", "J4 Station Rd, Adyar, Chennai - 600020"
            ),
            NearbyService(
                "po2", "J13 Taramani Police Station", ServiceCategory.POLICE, 2.8, "Open 24/7", 85, 3.9,
                "+91 44 2345 2596", "Taramani Link Rd, Chennai - 600113"
            ),
            NearbyService(
                "s1", "Chennai Corporation Fire & Rescue", ServiceCategory.SHELTER, 2.3, "Open 24/7", 97, 4.9,
                "101", "Besant Nagar 2nd Ave, Besant Nagar, Chennai - 600090"
            ),
            NearbyService(
                "s2", "YMCA Community Volunteer Center", ServiceCategory.SHELTER, 3.2, "Open 24/7", 90, 4.3,
                "+91 44 2445 1122", "No. 4, Jeypore Colony, Adyar, Chennai - 600020"
            ),
            NearbyService(
                "a1", "LifeLine Emergency Ambulance Unit", ServiceCategory.AMBULANCE, 0.5, "Open 24/7", 100, 4.9,
                "108", "Dedicated Emergency Mobile Ambulance Station, Adyar Link Road"
            )
        )
    )
    val nearbyServices = _nearbyServices.asStateFlow()

    // Community Volunteers Support State
    private val _volunteers = MutableStateFlow(
        listOf(
            CommunityVolunteer(
                "v1", "Amithabh Sen", VolunteerType.MEDICAL_TRAINED,
                "Emergency Care Nurse, CPR and Trauma First Aid Certified",
                "+91 98400 90123", 4.9, true, 99, "Adyar Ground Area, Chennai"
            ),
            CommunityVolunteer(
                "v2", "Devendra Modi", VolunteerType.BLOOD_DONOR,
                "O- Blood donor, last donated 4 months ago. Relicensed donor.",
                "+91 97890 23456", 4.8, true, 97, "Gandhi Nagar, Adyar"
            ),
            CommunityVolunteer(
                "v3", "Ramanathan Swamy", VolunteerType.COORDINATOR,
                "Apartment Emergency Lead, keeps Oxygen concentrators and fire kits.",
                "+91 94441 55998", 4.7, true, 95, "Sardar Patel Mansions, Chennai"
            ),
            CommunityVolunteer(
                "v4", "Arundhati Roy", VolunteerType.GENERAL_SUPPORT,
                "Crisis support helper, fluent in Hindi, Tamil, English.",
                "+91 91234 43210", 4.6, true, 92, "Kasturba Nagar, Adyar"
            ),
            CommunityVolunteer(
                "v5", "Karthik Raja", VolunteerType.BLOOD_DONOR,
                "B+ Blood donor, active community safety advocate.",
                "+91 98844 55667", 4.9, true, 94, "Shastri Nagar, Adyar"
            )
        )
    )
    val volunteers = _volunteers.asStateFlow()

    // Notifications Inbox State
    private val _notifications = MutableStateFlow(
        listOf(
            AlertNotification(
                "n1", "Welcome to LifeLine Connect",
                "Your community emergency deck is set up. Keep your medical parameters updated.",
                getFormattedTime(0), AlertType.SYSTEM, true
            ),
            AlertNotification(
                "n2", "Location Sharing Configured",
                "In high urgency, your real-time coordinates are paired to your selected rescue relations.",
                getFormattedTime(1), AlertType.SYSTEM, true
            ),
            AlertNotification(
                "n3", "Local Blood Banks Synced",
                "Chennai main corporation lists updated. Open hospitals logged nearby.",
                getFormattedTime(2), AlertType.SYSTEM, false
            )
        )
    )
    val notifications = _notifications.asStateFlow()

    // Mock active query selections
    val selectedServiceCategory = MutableStateFlow(ServiceCategory.ALL)

    // State Mutation Methods
    fun completeOnboarding() {
        _onboardingCompleted.value = true
    }

    fun loginUser(phone: String) {
        _loggedInPhone.value = phone
        _isUserLoggedIn.value = true
        _isGuestUser.value = false
        addNotification(
            "Account Verified Successfully",
            "Signed in with phone prefix $phone. Trusted community features activated.",
            AlertType.SUCCESS
        )
    }

    fun loginAsGuest() {
        _isGuestUser.value = true
        _isUserLoggedIn.value = true
        addNotification(
            "Guest Access Enabled",
            "Emergency contacts can still receive local SOS. Sign in later to enable verified volunteer badge.",
            AlertType.SYSTEM
        )
    }

    fun logout() {
        _isUserLoggedIn.value = false
        _isGuestUser.value = false
        _loggedInPhone.value = ""
    }

    fun changeLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun setOfflineMode(offline: Boolean) {
        _isOfflineMode.value = offline
        if (offline) {
            addNotification(
                "Offline Mode Activated",
                "simulated network offline. Medical ID parameters and emergency group parameters cached offline.",
                AlertType.WARNING
            )
        } else {
            addNotification(
                "Connected to LifeLine",
                "Online connection restored. Cached offline logs synced to primary rescuers.",
                AlertType.SUCCESS
            )
        }
    }

    fun addIncidentReport(type: String, location: String, severity: String, description: String) {
        addNotification(
            "⚠️ Road Hazard Reported: $type",
            "A $severity severity hazard ($description) at $location has been broadcasted to rescuers and local drivers.",
            AlertType.WARNING
        )
    }

    fun triggerSOS() {
        _isSosAlertActive.value = true
        addNotification(
            "🚨 CRITICAL SOS ALERT SENT",
            "Your live location has been transmitted to Priyah Kumar and Dr. Amit Kumar.",
            AlertType.EMERGENCY
        )
    }

    fun reportUserSafe() {
        _isSosAlertActive.value = false
        addNotification(
            "✅ Safety Status Clarified",
            "Responders notified. System coordinates returned to standard background pacing.",
            AlertType.SUCCESS
        )
    }

    fun updateMedicalProfile(updated: MedicalProfile) {
        _medicalProfile.value = updated
        addNotification(
            "Medical Profile Updated",
            "Critical health metadata aligned. Responders can parse this on the medical ID card.",
            AlertType.SUCCESS
        )
    }

    fun addEmergencyContact(contact: EmergencyContact) {
        _emergencyContacts.update { it + contact }
        addNotification(
            "Rescue Contact Added",
            "${contact.name} (${contact.relationship}) linked to SOS parameters.",
            AlertType.SUCCESS
        )
    }

    fun deleteEmergencyContact(id: String) {
        _emergencyContacts.update { it.filter { contact -> contact.id != id } }
        addNotification(
            "Contact Parameter Severed",
            "Emergency alerts will no longer map to this parameter.",
            AlertType.WARNING
        )
    }

    fun addNotification(title: String, message: String, type: AlertType) {
        val newAlert = AlertNotification(
            id = "n_" + System.currentTimeMillis(),
            title = title,
            message = message,
            timestamp = getFormattedTime(0),
            type = type,
            isRead = false
        )
        _notifications.update { listOf(newAlert) + it }
    }

    fun markNotificationsAsRead() {
        _notifications.update { currentList ->
            currentList.map { it.copy(isRead = true) }
        }
    }

    private fun getFormattedTime(backMinutes: Int): String {
        val sdf = SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault())
        val date = Date(System.currentTimeMillis() - (backMinutes * 60 * 1000))
        return sdf.format(date)
    }
}
