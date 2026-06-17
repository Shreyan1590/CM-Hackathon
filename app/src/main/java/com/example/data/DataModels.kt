package com.example.data

import java.io.Serializable

data class EmergencyContact(
    val id: String,
    val name: String,
    val phone: String,
    val relationship: String,
    val isSosRecipient: Boolean = true
) : Serializable

data class MedicalProfile(
    val name: String = "Rajesh Kumar",
    val age: Int = 42,
    val bloodGroup: String = "O+",
    val allergies: String = "Penicillin, Dust mites",
    val chronicConditions: String = "Type 2 Diabetes, Mild Hypertension",
    val medications: String = "Metformin 500mg, Amlodipine 5mg",
    val organDonorStatus: Boolean = true,
    val emergencyContactName: String = "Priyah Kumar",
    val emergencyContactPhone: String = "+91 98401 23456",
    val address: String = "No. 14, 2nd Main Road, Adyar, Chennai - 600020",
    val insuranceProvider: String = "Star Health Insurance",
    val insurancePolicyNum: String = "SH-9082-A31"
) : Serializable

enum class ServiceCategory {
    ALL, HOSPITALS, PHARMACY, BLOOD_BANK, POLICE, SHELTER, AMBULANCE
}

data class NearbyService(
    val id: String,
    val name: String,
    val category: ServiceCategory,
    val distanceKm: Double,
    val statusText: String, // "Open 24/7", "Closes at 10 PM"
    val trustScore: Int, // 1-100 rating scale representing user-verified confirmations
    val rating: Double,
    val contactPhone: String,
    val address: String,
    val latOffset: Double = 0.0,
    val lngOffset: Double = 0.0
) : Serializable

enum class VolunteerType {
    GENERAL_SUPPORT, BLOOD_DONOR, MEDICAL_TRAINED, COORDINATOR
}

data class CommunityVolunteer(
    val id: String,
    val name: String,
    val type: VolunteerType,
    val details: String, // "O- Blood, Available for urgent donation", "CPR Certified, Resident near Mylapore"
    val contact: String,
    val rating: Double,
    val isVerified: Boolean = true,
    val trustScore: Int = 98,
    val cityRegion: String = "Adyar, Chennai"
) : Serializable

enum class AlertType {
    EMERGENCY, WARNING, SYSTEM, SUCCESS
}

data class AlertNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: AlertType,
    val isRead: Boolean = false
) : Serializable
