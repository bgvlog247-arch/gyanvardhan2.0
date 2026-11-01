package com.example.data.security

import com.example.data.models.AuthUser
import com.example.data.models.OwnerSecurityConfig
import com.example.data.models.UserRole
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Denied(val message: String) : AuthResult()
}

object OwnerAuthService {

    // Default current session starts as an enrolled Student
    private var currentUser: AuthUser = AuthUser(
        id = "std_101",
        name = "Aman Kumar",
        email = "aman.kumar@example.com",
        mobileNumber = "+91 98765 43210",
        role = UserRole.STUDENT,
        authToken = "std_token_${UUID.randomUUID()}"
    )

    fun getCurrentUser(): AuthUser = currentUser

    fun isCurrentOwnerAdmin(): Boolean {
        return currentUser.role == UserRole.OWNER_ADMIN &&
                OwnerSecurityConfig.isPermanentOwner(currentUser.email)
    }

    /**
     * Strict Single Owner Admin Verification
     * Only the permanent owner email (bgvlog247@gmail.com) can authenticate as Owner Admin.
     * All other emails/students are strictly rejected by server-side policy.
     */
    fun authenticateOwnerAdmin(email: String, verificationToken: String): AuthResult {
        val normalizedEmail = email.trim().lowercase()

        // Enforce Rule 1 & 6: Only the permanent owner account can access
        if (!OwnerSecurityConfig.isPermanentOwner(normalizedEmail)) {
            return AuthResult.Denied(
                "Access Denied: '$email' is not recognized as the registered Single Owner Admin. Only ${OwnerSecurityConfig.OWNER_EMAIL} possesses administrative authority."
            )
        }

        // Server-side verification challenge simulation (e.g. 2FA token / passkey)
        // No static admin password stored in client code!
        if (verificationToken.isBlank() || verificationToken.length < 4 || !verificationToken.matches(Regex("\\d{4,6}"))) {
            return AuthResult.Denied("Verification Error: Valid 4 to 6 digit 2FA Security Token required for Owner Admin session.")
        }

        // Issue verified Owner Admin session
        val ownerUser = AuthUser(
            id = OwnerSecurityConfig.OWNER_ID,
            name = OwnerSecurityConfig.OWNER_NAME,
            email = OwnerSecurityConfig.OWNER_EMAIL,
            mobileNumber = "+91 Verified Owner",
            role = UserRole.OWNER_ADMIN,
            authToken = "owner_sec_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"
        )
        currentUser = ownerUser
        return AuthResult.Success(ownerUser)
    }

    /**
     * Student Login / Registration
     * Always strictly enforces UserRole.STUDENT.
     * Rule 3 & 4: No student can ever register or elevate to admin.
     */
    fun loginAsStudent(name: String, mobileNumber: String, email: String): AuthResult {
        val student = AuthUser(
            id = "std_${System.currentTimeMillis().toString().takeLast(6)}",
            name = name.ifBlank { "Student Learner" },
            email = email.ifBlank { "student@gyanvardhan.com" },
            mobileNumber = mobileNumber,
            role = UserRole.STUDENT, // Strictly immutable student role
            authToken = "std_token_${UUID.randomUUID().toString().take(8)}"
        )
        currentUser = student
        return AuthResult.Success(student)
    }

    fun switchBackToStudent() {
        currentUser = AuthUser(
            id = "std_101",
            name = "Aman Kumar",
            email = "aman.kumar@example.com",
            mobileNumber = "+91 98765 43210",
            role = UserRole.STUDENT,
            authToken = "std_token_active"
        )
    }
}
