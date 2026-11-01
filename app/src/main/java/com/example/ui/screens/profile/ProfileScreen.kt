package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AuthUser
import com.example.data.models.OwnerSecurityConfig
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    currentUser: AuthUser,
    isOwnerAdmin: Boolean,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onNavigatePurchases: () -> Unit,
    onNavigateResults: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onVerifyOwner: (String, String) -> Pair<Boolean, String?>,
    onLogoutOwner: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOwnerAuthDialog by remember { mutableStateOf(false) }
    var ownerEmailInput by remember { mutableStateOf(OwnerSecurityConfig.OWNER_EMAIL) }
    var securityTokenInput by remember { mutableStateOf("") }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Identity Header Card (Owner vs Student)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isOwnerAdmin) RoseError else SaffronGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isOwnerAdmin) "👑" else currentUser.name.take(2).uppercase(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyDark
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (isOwnerAdmin) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = SaffronGold) {
                                    Text(
                                        text = "OWNER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = currentUser.email,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isOwnerAdmin) "Sole Permanent Administrator" else "🎯 ${currentUser.targetExam}",
                                fontSize = 11.sp,
                                color = SaffronGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Language Switcher Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleLanguage() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SaffronGold.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = SaffronDark)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "भाषा बदलें (Language)" else "App Language",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "वर्तमान: हिन्दी" else "Current: English",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    FilledTonalButton(onClick = onToggleLanguage, shape = RoundedCornerShape(8.dp)) {
                        Text(text = if (currentLanguage == AppLanguage.HINDI) "English" else "हिन्दी", fontSize = 12.sp)
                    }
                }
            }
        }

        // 3. Learning & Records Menu
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Receipt,
                        title = AppStrings.get("my_purchases", currentLanguage),
                        subtitle = if (currentLanguage == AppLanguage.HINDI) "भुगतान इतिहास व रसीदें" else "Payment history & invoices",
                        onClick = onNavigatePurchases
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuItem(
                        icon = Icons.Default.AssignmentTurnedIn,
                        title = AppStrings.get("my_results", currentLanguage),
                        subtitle = if (currentLanguage == AppLanguage.HINDI) "मॉक टेस्ट स्कोरकार्ड व रैंक" else "Mock test performance & rank card",
                        onClick = onNavigateResults
                    )
                }
            }
        }

        // 4. Owner Administration Gateway
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isOwnerAdmin) onNavigateAdmin() else showOwnerAuthDialog = true
                    }
                    .testTag("admin_portal_button"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(NavyPrimary, SaffronGold)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = NavyPrimary)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isOwnerAdmin) "Open Owner Admin Dashboard" else "Platform Owner Login",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isOwnerAdmin) "Full educational & schedule controller active" else "Strictly restricted to permanent owner: ${OwnerSecurityConfig.OWNER_EMAIL}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // If currently Owner Admin, option to switch back to Student View
        if (isOwnerAdmin) {
            item {
                OutlinedButton(
                    onClick = onLogoutOwner,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError)
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Exit Owner Session (Switch to Student View)")
                }
            }
        }
    }

    // Strict Owner Admin Verification Dialog
    if (showOwnerAuthDialog) {
        AlertDialog(
            onDismissRequest = {
                showOwnerAuthDialog = false
                securityTokenInput = ""
                authErrorMessage = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sole Owner Verification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Notice: Gyanvardhan2.0 operates on a strict Single Owner architecture. No student or secondary admin accounts can access administrative authority.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = ownerEmailInput,
                        onValueChange = {
                            ownerEmailInput = it
                            authErrorMessage = null
                        },
                        label = { Text("Registered Owner Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("owner_email_input")
                    )

                    OutlinedTextField(
                        value = securityTokenInput,
                        onValueChange = {
                            securityTokenInput = it
                            authErrorMessage = null
                        },
                        label = { Text("Owner 2FA Token / Passkey") },
                        placeholder = { Text("e.g. 88219") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("owner_token_input")
                    )

                    if (authErrorMessage != null) {
                        Text(
                            text = authErrorMessage!!,
                            fontSize = 11.sp,
                            color = RoseError,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val (success, error) = onVerifyOwner(ownerEmailInput, securityTokenInput)
                        if (success) {
                            showOwnerAuthDialog = false
                            securityTokenInput = ""
                            authErrorMessage = null
                        } else {
                            authErrorMessage = error ?: "Authentication failed."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
                    modifier = Modifier.testTag("submit_owner_auth")
                ) {
                    Text(text = "Verify & Access", fontWeight = FontWeight.Bold, color = NavyDark)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOwnerAuthDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
    }
}
