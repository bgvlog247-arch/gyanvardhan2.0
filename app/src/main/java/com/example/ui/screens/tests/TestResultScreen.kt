package com.example.ui.screens.tests

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Question
import com.example.data.models.TestResult
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultScreen(
    result: TestResult,
    questions: List<Question>,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    var filterType by remember { mutableStateOf("ALL") } // ALL, CORRECT, WRONG, UNATTEMPTED
    var solutionLang by remember { mutableStateOf(currentLanguage) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "परीक्षण परिणाम एवं विश्लेषण" else "Test Analysis & Results",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        solutionLang = if (solutionLang == AppLanguage.HINDI) AppLanguage.ENGLISH else AppLanguage.HINDI
                    }) {
                        Icon(imageVector = Icons.Default.Translate, contentDescription = "Language", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("test_result_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Overall Scorecard Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) result.testTitleHi else result.testTitleEn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Score Circle Display
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(SaffronGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${result.score}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyDark
                                )
                                Text(
                                    text = "/ ${result.totalMarks}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metrics Grid: Rank, Accuracy, Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ScoreMetric(
                                label = AppStrings.get("rank", currentLanguage),
                                value = "#${result.rank} / ${result.totalParticipants}",
                                color = SaffronGold
                            )
                            ScoreMetric(
                                label = AppStrings.get("accuracy", currentLanguage),
                                value = "${result.accuracyPercent}%",
                                color = EmeraldSuccess
                            )
                            ScoreMetric(
                                label = AppStrings.get("time_spent", currentLanguage),
                                value = "${result.timeSpentMinutes} min",
                                color = CyanAccent
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Color.White.copy(alpha = 0.15f))

                        // Correct / Wrong / Unattempted counters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ResultPill(count = result.correctCount, label = "Correct", color = EmeraldSuccess)
                            ResultPill(count = result.wrongCount, label = "Wrong", color = RoseError)
                            ResultPill(count = result.unattemptedCount, label = "Skipped", color = Color.Gray)
                        }
                    }
                }
            }

            // 2. Solutions Title & Filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "विस्तृत उत्तर एवं समाधान" else "Detailed Question Solutions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${questions.size} Questions",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3. Question Reviews
            items(questions) { q ->
                val userAnswer = result.userAnswers[q.questionNumber]
                val isCorrect = userAnswer == q.correctOptionIndex
                val isUnattempted = userAnswer == null

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
                            Text(
                                text = "Q.${q.questionNumber}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when {
                                    isUnattempted -> Color.Gray.copy(alpha = 0.2f)
                                    isCorrect -> EmeraldSuccess.copy(alpha = 0.2f)
                                    else -> RoseError.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = when {
                                        isUnattempted -> if (currentLanguage == AppLanguage.HINDI) "छोड़ा गया" else "Not Attempted"
                                        isCorrect -> if (currentLanguage == AppLanguage.HINDI) "सही उत्तर (+${q.marks})" else "Correct (+${q.marks})"
                                        else -> if (currentLanguage == AppLanguage.HINDI) "गलत उत्तर" else "Incorrect"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isUnattempted -> Color.Gray
                                        isCorrect -> EmeraldSuccess
                                        else -> RoseError
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Question
                        Text(
                            text = if (solutionLang == AppLanguage.HINDI) q.questionHi else q.questionEn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Options
                        val options = if (solutionLang == AppLanguage.HINDI) q.optionsHi else q.optionsEn
                        val letters = listOf("A", "B", "C", "D")

                        options.forEachIndexed { idx, opt ->
                            val isCorrectOpt = idx == q.correctOptionIndex
                            val isUserPicked = userAnswer == idx

                            val optBg = when {
                                isCorrectOpt -> EmeraldSuccess.copy(alpha = 0.15f)
                                isUserPicked -> RoseError.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(optBg)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${letters[idx]}. $opt",
                                    fontSize = 12.sp,
                                    fontWeight = if (isCorrectOpt || isUserPicked) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isCorrectOpt -> EmeraldSuccess
                                        isUserPicked -> RoseError
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                if (isCorrectOpt) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Correct", tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                                } else if (isUserPicked) {
                                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "Wrong", tint = RoseError, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Detailed Explanation Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AmberLight.copy(alpha = 0.35f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "💡 " + AppStrings.get("explanation", currentLanguage) + ":",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (solutionLang == AppLanguage.HINDI) q.explanationHi else q.explanationEn,
                                    fontSize = 12.sp,
                                    color = Color.Black.copy(alpha = 0.85f),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
fun ResultPill(count: Int, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "$count $label", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}
