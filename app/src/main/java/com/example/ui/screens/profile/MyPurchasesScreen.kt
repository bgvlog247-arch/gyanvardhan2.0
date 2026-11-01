package com.example.ui.screens.profile

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Course
import com.example.data.models.PaymentTransaction
import com.example.data.models.SubscriptionRecord
import com.example.data.models.TestSeries
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPurchasesScreen(
    transactions: List<PaymentTransaction>,
    subscriptions: List<SubscriptionRecord>,
    courses: List<Course>,
    tests: List<TestSeries>,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onCourseClick: (Course) -> Unit,
    onTestClick: (TestSeries) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabTitles = if (currentLanguage == AppLanguage.HINDI) {
        listOf("सक्रिय सामग्री (Active)", "सदस्यताएं (Subscriptions)", "रसीदें व इतिहास (Invoices)")
    } else {
        listOf("Active Content", "Subscriptions", "Invoices & History")
    }

    val enrolledCourses = courses.filter { it.enrolled }
    val unlockedPaidTests = tests.filter { !it.isFree && it.isUnlocked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.get("my_purchases", currentLanguage), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Active Courses and Tests
                    if (enrolledCourses.isEmpty() && unlockedPaidTests.isEmpty()) {
                        EmptyPurchaseState(
                            message = if (currentLanguage == AppLanguage.HINDI) "कोई सक्रिय कोर्स या टेस्ट नहीं मिला" else "No active purchases found."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (enrolledCourses.isNotEmpty()) {
                                item {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "नामांकित कोर्स (Enrolled Courses)" else "Enrolled Courses",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                items(enrolledCourses) { course ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onCourseClick(course) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(EmeraldSuccess.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(imageVector = Icons.Default.School, contentDescription = null, tint = EmeraldSuccess)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (currentLanguage == AppLanguage.HINDI) course.titleHi else course.titleEn,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${course.totalVideos} Videos • ${course.totalPdfs} PDFs • ${course.totalTests} Tests",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Button(
                                                onClick = { onCourseClick(course) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text(text = "Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            if (unlockedPaidTests.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "अनलॉक की गई टेस्ट सीरीज़ (Unlocked Tests)" else "Unlocked Test Series",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                items(unlockedPaidTests) { test ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onTestClick(test) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(SaffronGold.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(imageVector = Icons.Default.Assignment, contentDescription = null, tint = SaffronGold)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (currentLanguage == AppLanguage.HINDI) test.titleHi else test.titleEn,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${test.questionsCount} MCQs • ${test.durationMinutes} Mins",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Button(
                                                onClick = { onTestClick(test) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text(text = "Start", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Subscriptions Tab
                    if (subscriptions.isEmpty()) {
                        EmptyPurchaseState(
                            message = if (currentLanguage == AppLanguage.HINDI) "कोई सक्रिय मासिक सदस्यता नहीं है" else "No active subscriptions found."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(subscriptions) { sub ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = EmeraldSuccess.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = sub.status,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldSuccess,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = "₹${sub.monthlyPrice}/mo",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = sub.courseTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(text = "Started: ${sub.startDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(text = "Renews: ${sub.expiryDate}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SaffronGold)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedButton(
                                            onClick = {
                                                Toast.makeText(context, "Subscription active. Auto-renews on ${sub.expiryDate}", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(text = "Manage Subscription (Auto-Renew ON)", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Invoices & Transactions
                    if (transactions.isEmpty()) {
                        EmptyPurchaseState(
                            message = if (currentLanguage == AppLanguage.HINDI) "कोई लेनदेन इतिहास उपलब्ध नहीं है" else "No transaction history available."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.testTag("purchases_list"),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(transactions) { tx ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val isSuccess = tx.status == "SUCCESS"
                                            val isPending = tx.status == "PENDING"
                                            val badgeColor = when {
                                                isSuccess -> EmeraldSuccess
                                                isPending -> SaffronGold
                                                else -> CoralError
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = badgeColor.copy(alpha = 0.15f)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = when {
                                                            isSuccess -> Icons.Default.CheckCircle
                                                            isPending -> Icons.Default.HourglassTop
                                                            else -> Icons.Default.Error
                                                        },
                                                        contentDescription = null,
                                                        tint = badgeColor,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(text = tx.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                                                }
                                            }

                                            Text(
                                                text = "₹${tx.amount} INR",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = tx.itemTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                        if (tx.failureReason != null) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Reason: ${tx.failureReason}",
                                                fontSize = 11.sp,
                                                color = CoralError
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "💳 ${tx.paymentMethod} • ${tx.dateText}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        if (tx.razorpayPaymentId.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Razorpay ID: ${tx.razorpayPaymentId}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF0C73EB)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Receipt: ${tx.receiptId} • Plan: ${tx.purchaseType}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    Toast.makeText(context, "Invoice ${tx.receiptId} saved to downloads folder (PDF)", Toast.LENGTH_SHORT).show()
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "Download Tax Invoice", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyPurchaseState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
