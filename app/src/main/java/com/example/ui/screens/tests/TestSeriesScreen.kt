package com.example.ui.screens.tests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.TestSeries
import com.example.data.models.TestType
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@Composable
fun TestSeriesScreen(
    currentLanguage: AppLanguage,
    testSeriesList: List<TestSeries>,
    onTestClick: (TestSeries) -> Unit,
    onBuyTestClick: (TestSeries) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf<TestType?>(null) }

    val filteredTests = testSeriesList.filter {
        selectedType == null || it.type == selectedType
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("test_series_screen")
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = AppStrings.get("tests", currentLanguage),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "ऑल-इंडिया रैंकिंग, नेगेटिव मार्किंग एवं विस्तृत समाधान के साथ परीक्षा-पैटर्न टेस्ट" else "All-India ranking, realistic negative marking & detailed solutions",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Filter chips: All, Free, Daily Quiz, Mock, Paid
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { selectedType = null },
                    label = { Text(AppStrings.get("all", currentLanguage), fontSize = 12.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedType == TestType.FREE,
                    onClick = { selectedType = TestType.FREE },
                    label = { Text(AppStrings.get("free_tests", currentLanguage), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldSuccess, selectedLabelColor = Color.White)
                )
            }
            item {
                FilterChip(
                    selected = selectedType == TestType.DAILY_QUIZ,
                    onClick = { selectedType = TestType.DAILY_QUIZ },
                    label = { Text(AppStrings.get("daily_quiz", currentLanguage), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SaffronGold, selectedLabelColor = NavyDark)
                )
            }
            item {
                FilterChip(
                    selected = selectedType == TestType.MOCK,
                    onClick = { selectedType = TestType.MOCK },
                    label = { Text("Mock Tests", fontSize = 12.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedType == TestType.PAID,
                    onClick = { selectedType = TestType.PAID },
                    label = { Text(AppStrings.get("paid_tests", currentLanguage), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = Color.White)
                )
            }
        }

        // Test Series Cards
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredTests) { test ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (test.isFree || test.isUnlocked) onTestClick(test) else onBuyTestClick(test)
                        }
                        .testTag("test_item_${test.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (test.type) {
                                    TestType.FREE -> EmeraldSuccess
                                    TestType.DAILY_QUIZ -> SaffronGold
                                    TestType.PAID -> NavyDark
                                    else -> IndigoAccent
                                }
                            ) {
                                Text(
                                    text = when (test.type) {
                                        TestType.FREE -> "FREE MOCK"
                                        TestType.DAILY_QUIZ -> "DAILY QUIZ"
                                        TestType.PAID -> "₹${test.price} PREMIUM"
                                        else -> test.type.name
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (test.type == TestType.DAILY_QUIZ) NavyDark else Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = "👥 ${test.attemptsCount} ${if (currentLanguage == AppLanguage.HINDI) "परीक्षार्थी" else "attempted"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) test.titleHi else test.titleEn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Test Parameters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "❓ ${test.questionsCount} ${if (currentLanguage == AppLanguage.HINDI) "प्रश्न" else "Ques"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "⏱️ ${test.durationMinutes} ${if (currentLanguage == AppLanguage.HINDI) "मिनट" else "Mins"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "🎯 ${test.totalMarks} ${if (currentLanguage == AppLanguage.HINDI) "अंक" else "Marks"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "⚠️ -${test.negativeMarks}",
                                fontSize = 11.sp,
                                color = RoseError
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (test.isFree || test.isUnlocked) AppStrings.get("free", currentLanguage) else "₹${test.price}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (test.isFree || test.isUnlocked) EmeraldSuccess else MaterialTheme.colorScheme.primary
                            )

                            if (test.isFree || test.isUnlocked) {
                                Button(
                                    onClick = { onTestClick(test) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = AppStrings.get("attempt_test", currentLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { onBuyTestClick(test) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, tint = NavyDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = AppStrings.get("buy_now", currentLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
