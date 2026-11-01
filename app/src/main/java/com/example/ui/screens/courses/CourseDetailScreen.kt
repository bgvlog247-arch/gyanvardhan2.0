package com.example.ui.screens.courses

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Course
import com.example.data.models.PdfMaterial
import com.example.data.models.TestSeries
import com.example.data.models.VideoLesson
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    course: Course,
    currentLanguage: AppLanguage,
    videos: List<VideoLesson>,
    pdfs: List<PdfMaterial>,
    tests: List<TestSeries>,
    onBackClick: () -> Unit,
    onVideoClick: (VideoLesson) -> Unit,
    onPdfClick: (PdfMaterial) -> Unit,
    onTestClick: (TestSeries) -> Unit,
    onEnrollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = if (currentLanguage == AppLanguage.HINDI) {
        listOf("पाठ (Videos)", "नोट्स (PDF)", "टेस्ट सीरीज़ (Tests)")
    } else {
        listOf("Lessons", "PDF Notes", "Test Series")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) course.titleHi else course.titleEn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (course.isFree) {
                            Text(
                                text = AppStrings.get("free", currentLanguage),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldSuccess
                            )
                        } else {
                            Text(
                                text = "₹${course.price}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "or ₹${course.monthlySubscriptionPrice}/month",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronGold
                            )
                        }
                        Text(
                            text = if (course.enrolled) AppStrings.get("enrolled", currentLanguage) else "Full Access Guarantee",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (course.enrolled) {
                        Button(
                            onClick = {
                                val firstVideo = videos.firstOrNull()
                                if (firstVideo != null) onVideoClick(firstVideo)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("watch_now", currentLanguage),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = onEnrollClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (course.isFree) EmeraldSuccess else SaffronGold
                            ),
                            modifier = Modifier.testTag("course_detail_enroll_button")
                        ) {
                            Text(
                                text = if (course.isFree) AppStrings.get("enroll_now", currentLanguage) else AppStrings.get("buy_now", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Course Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) course.titleHi else course.titleEn,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) course.descriptionHi else course.descriptionEn,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = course.instructor, fontSize = 12.sp, color = Color.White)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${course.rating} (${course.studentsCount}+ ${if (currentLanguage == AppLanguage.HINDI) "नामांकित" else "enrolled"})", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Tab Switcher
            item {
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> { // Video lessons
                    if (videos.isEmpty()) {
                        item {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "जल्द ही वीडियो कक्षाएं जोड़ी जाएंगी।" else "Videos will be updated soon.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(videos) { video ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (course.enrolled || video.isFreePreview) onVideoClick(video) else onEnrollClick()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (video.isFreePreview) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (video.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = if (video.isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) video.titleHi else video.titleEn,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${video.chapter} • ${video.durationText}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (video.isFreePreview && !course.enrolled) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldSuccess
                                        ) {
                                            Text(
                                                text = "FREE DEMO",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else if (!course.enrolled) {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> { // PDF Notes
                    if (pdfs.isEmpty()) {
                        item {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "कोई PDF नोट्स उपलब्ध नहीं हैं।" else "No PDF notes available.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(pdfs) { pdf ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (course.enrolled || pdf.isFree) onPdfClick(pdf) else onEnrollClick()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(CyanAccent.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = CyanAccent)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) pdf.titleHi else pdf.titleEn,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${pdf.pageCount} ${if (currentLanguage == AppLanguage.HINDI) "पृष्ठ" else "Pages"} • ${pdf.fileSizeMb} MB",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (!pdf.isFree && !course.enrolled) {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    } else {
                                        IconButton(onClick = { onPdfClick(pdf) }) {
                                            Icon(imageVector = Icons.Default.Visibility, contentDescription = "Read", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> { // Test Series
                    if (tests.isEmpty()) {
                        item {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "इस कोर्स के लिए टेस्ट सीरीज़ जल्द जोड़ी जाएगी।" else "Test series will be added soon.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(tests) { test ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (course.enrolled || test.isFree || test.isUnlocked) onTestClick(test) else onEnrollClick()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldSuccess.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Assignment, contentDescription = null, tint = EmeraldSuccess)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) test.titleHi else test.titleEn,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${test.questionsCount} ${if (currentLanguage == AppLanguage.HINDI) "प्रश्न" else "Questions"} • ${test.durationMinutes} ${if (currentLanguage == AppLanguage.HINDI) "मिनट" else "Mins"}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            if (course.enrolled || test.isFree || test.isUnlocked) onTestClick(test) else onEnrollClick()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(text = AppStrings.get("attempt_test", currentLanguage), fontSize = 11.sp)
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
