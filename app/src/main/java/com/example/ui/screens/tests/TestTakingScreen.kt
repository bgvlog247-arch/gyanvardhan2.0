package com.example.ui.screens.tests

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Question
import com.example.data.models.TestSeries
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestTakingScreen(
    test: TestSeries,
    questions: List<Question>,
    currentIndex: Int,
    userAnswers: Map<Int, Int>,
    markedForReview: Set<Int>,
    timeRemainingSeconds: Int,
    currentLanguage: AppLanguage,
    onOptionSelected: (Int, Int) -> Unit,
    onClearAnswer: (Int) -> Unit,
    onToggleMarkReview: (Int) -> Unit,
    onNavigateQuestion: (Int) -> Unit,
    onSubmitTest: () -> Unit,
    onExitWithoutSaving: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPaletteSheet by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var questionLang by remember { mutableStateOf(currentLanguage) }

    // Intercept back button to confirm exit
    BackHandler {
        showSubmitDialog = true
    }

    val currentQ = questions.getOrNull(currentIndex)
    val isTimerLow = timeRemainingSeconds <= 120
    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NavyDark,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) test.titleHi else test.titleEn,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "${AppStrings.get("question", currentLanguage)} ${currentIndex + 1} / ${questions.size}",
                            fontSize = 11.sp,
                            color = SaffronGold
                        )
                    }

                    // Timer Display
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isTimerLow) RoseError else Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.padding(horizontal = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = if (isTimerLow) Color.White else SaffronGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timerText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Question Palette Toggle
                    IconButton(
                        onClick = { showPaletteSheet = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(imageVector = Icons.Default.GridView, contentDescription = "Palette", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous
                        OutlinedButton(
                            onClick = { if (currentIndex > 0) onNavigateQuestion(currentIndex - 1) },
                            enabled = currentIndex > 0,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(AppStrings.get("previous", currentLanguage), fontSize = 11.sp)
                        }

                        // Mark for Review & Next
                        OutlinedButton(
                            onClick = {
                                if (currentQ != null) {
                                    onToggleMarkReview(currentQ.questionNumber)
                                    if (currentIndex < questions.size - 1) onNavigateQuestion(currentIndex + 1)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = IndigoAccent
                            ),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            val isMarked = currentQ != null && markedForReview.contains(currentQ.questionNumber)
                            Text(
                                text = if (isMarked) "Unmark" else AppStrings.get("mark_review", currentLanguage),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Save & Next
                        Button(
                            onClick = {
                                if (currentIndex < questions.size - 1) {
                                    onNavigateQuestion(currentIndex + 1)
                                } else {
                                    showSubmitDialog = true
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(
                                text = if (currentIndex == questions.size - 1) AppStrings.get("submit_test", currentLanguage) else AppStrings.get("save_next", currentLanguage),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Clear Response & Submit Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { if (currentQ != null) onClearAnswer(currentQ.questionNumber) }
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = AppStrings.get("clear_response", currentLanguage), fontSize = 11.sp)
                        }

                        Button(
                            onClick = { showSubmitDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(text = AppStrings.get("submit_test", currentLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (currentQ == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val selectedOption = userAnswers[currentQ.questionNumber]
            val isMarked = markedForReview.contains(currentQ.questionNumber)

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .testTag("test_question_card")
            ) {
                // Question Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Q.${currentQ.questionNumber}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        if (isMarked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = IndigoAccent) {
                                Text(
                                    text = "REVIEW",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Marks info & Language Switcher for this question
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(6.dp), color = EmeraldSuccess.copy(alpha = 0.15f)) {
                            Text(
                                text = "+${currentQ.marks}  -${test.negativeMarks}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // Switch Question Language
                        IconButton(
                            onClick = {
                                questionLang = if (questionLang == AppLanguage.HINDI) AppLanguage.ENGLISH else AppLanguage.HINDI
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Switch Lang",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Text
                Text(
                    text = if (questionLang == AppLanguage.HINDI) currentQ.questionHi else currentQ.questionEn,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Options (A, B, C, D)
                val options = if (questionLang == AppLanguage.HINDI) currentQ.optionsHi else currentQ.optionsEn
                val optionLetters = listOf("A", "B", "C", "D")

                options.forEachIndexed { index, optText ->
                    val isSelected = selectedOption == index
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onOptionSelected(currentQ.questionNumber, index) }
                            .testTag("option_${index}_q_${currentQ.questionNumber}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary))) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optionLetters[index],
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = optText,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = { onOptionSelected(currentQ.questionNumber, index) },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }
    }

    // Question Palette Bottom Sheet
    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "प्रश्न पैलेट (Question Palette)" else "Question Palette",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    LegendItem(color = EmeraldSuccess, label = "Answered")
                    LegendItem(color = RoseError, label = "Unanswered")
                    LegendItem(color = IndigoAccent, label = "Review")
                    LegendItem(color = Color.LightGray, label = "Not Visited")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 240.dp)
                ) {
                    items(questions) { q ->
                        val isAnswered = userAnswers.containsKey(q.questionNumber)
                        val isReview = markedForReview.contains(q.questionNumber)
                        val isCurrent = q.questionNumber == (currentIndex + 1)

                        val bgColor = when {
                            isReview -> IndigoAccent
                            isAnswered -> EmeraldSuccess
                            q.questionNumber <= currentIndex + 1 -> RoseError
                            else -> Color.LightGray
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .border(
                                    width = if (isCurrent) 2.dp else 0.dp,
                                    color = if (isCurrent) SaffronGold else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    onNavigateQuestion(q.questionNumber - 1)
                                    showPaletteSheet = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${q.questionNumber}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        showPaletteSheet = false
                        showSubmitDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text(AppStrings.get("submit_test", currentLanguage), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        val answeredCount = userAnswers.size
        val unansweredCount = questions.size - answeredCount
        val reviewCount = markedForReview.size

        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "टेस्ट सबमिट करें?" else "Submit Test?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "क्या आप निश्चित रूप से टेस्ट सबमिट करना चाहते हैं? आपका स्कोर तुरंत विश्लेषित किया जाएगा।" else "Are you sure you want to submit? Your score and rankings will be generated immediately.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "✅ Answered: $answeredCount", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EmeraldSuccess)
                    Text(text = "❌ Unanswered: $unansweredCount", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = RoseError)
                    Text(text = "🟣 Marked for Review: $reviewCount", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = IndigoAccent)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        onSubmitTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text(if (currentLanguage == AppLanguage.HINDI) "हाँ, सबमिट करें" else "Yes, Submit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text(if (currentLanguage == AppLanguage.HINDI) "जारी रखें" else "Keep Attempting")
                }
            }
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
