package com.example.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.Course
import com.example.data.models.LiveClass
import com.example.data.models.TestSeries
import com.example.data.models.VideoLesson
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    currentLanguage: AppLanguage,
    courses: List<Course>,
    liveClasses: List<LiveClass>,
    videos: List<VideoLesson>,
    testSeries: List<TestSeries>,
    onCourseClick: (Course) -> Unit,
    onLiveClassClick: (LiveClass) -> Unit,
    onVideoClick: (VideoLesson) -> Unit,
    onTestClick: (TestSeries) -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToLive: () -> Unit,
    onNavigateToTests: () -> Unit,
    onNavigateToPdfs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val liveNowClass = liveClasses.firstOrNull { it.isLiveNow }
    val featuredCourses = courses.take(4)
    val dailyQuiz = testSeries.firstOrNull { it.type.name == "DAILY_QUIZ" } ?: testSeries.firstOrNull()
    val resumeVideo = videos.firstOrNull { it.progressPercent > 0f && !it.isCompleted } ?: videos.firstOrNull()

    // Pulsing animation for Live indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Hero Educational Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(185.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.edtech_banner),
                        contentDescription = "Gyanvardhan Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        NavyDark.copy(alpha = 0.7f),
                                        NavyDark.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )
                    // Text over banner
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SaffronGold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "मिशन 2026: सरकारी नौकरी व बोर्ड टॉपर" else "Mission 2026: Selection Batch",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "आज सीखें, कल नेतृत्व करें!" else "Learn Today, Lead Tomorrow!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "SSC GD, रेलवे, बोर्ड परीक्षा 10वीं/12वीं की सर्वश्रेष्ठ तैयारी" else "India's Best Faculty for SSC, Railway & Board Exams",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // 2. Live Now Alert Strip (if class is live)
        if (liveNowClass != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onLiveClassClick(liveNowClass) }
                        .testTag("live_now_banner"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoseError, SaffronGold)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pulsing red live badge
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(RoseError.copy(alpha = alphaAnim))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = AppStrings.get("live_now", currentLanguage),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoseError
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• ${liveNowClass.viewersCount} ${if (currentLanguage == AppLanguage.HINDI) "छात्र जुड़े हैं" else "watching"}",
                                    fontSize = 11.sp,
                                    color = DarkTextSecondary
                                )
                            }
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) liveNowClass.titleHi else liveNowClass.titleEn,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${liveNowClass.teacherName} (${liveNowClass.subject})",
                                fontSize = 12.sp,
                                color = SaffronGold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onLiveClassClick(liveNowClass) },
                            colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = AppStrings.get("join_live", currentLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 3. Quick Action Grid (6 Key Modules)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "शीघ्र पहुंच (Quick Access)" else "Explore Learning",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = AppStrings.get("daily_quiz", currentLanguage),
                        icon = Icons.Default.Quiz,
                        accentColor = SaffronGold,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (dailyQuiz != null) onTestClick(dailyQuiz) else onNavigateToTests()
                        }
                    )
                    QuickActionCard(
                        title = AppStrings.get("live_classes", currentLanguage),
                        icon = Icons.Default.LiveTv,
                        accentColor = RoseError,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToLive
                    )
                    QuickActionCard(
                        title = AppStrings.get("recorded_videos", currentLanguage),
                        icon = Icons.Default.PlayCircle,
                        accentColor = IndigoAccent,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val v = videos.firstOrNull()
                            if (v != null) onVideoClick(v) else onNavigateToCourses()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = AppStrings.get("pdf_notes", currentLanguage),
                        icon = Icons.Default.MenuBook,
                        accentColor = CyanAccent,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPdfs
                    )
                    QuickActionCard(
                        title = AppStrings.get("free_tests", currentLanguage),
                        icon = Icons.Default.Assignment,
                        accentColor = EmeraldSuccess,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToTests
                    )
                    QuickActionCard(
                        title = AppStrings.get("all", currentLanguage) + " " + AppStrings.get("courses", currentLanguage),
                        icon = Icons.Default.School,
                        accentColor = SaffronDark,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCourses
                    )
                }
            }
        }

        // 4. Resume Learning Card (if available)
        if (resumeVideo != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onVideoClick(resumeVideo) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "पढ़ाई जारी रखें (Resume Learning)" else "Continue Watching",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) resumeVideo.titleHi else resumeVideo.titleEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            LinearProgressIndicator(
                                progress = { resumeVideo.progressPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = SaffronGold,
                                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }

        // 5. Featured Courses Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "लोकप्रिय पाठ्यक्रम (Popular Courses)" else "Popular Courses",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onNavigateToCourses) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "सभी देखें" else "View All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(featuredCourses) { course ->
                        HomeCourseCard(
                            course = course,
                            currentLanguage = currentLanguage,
                            onClick = { onCourseClick(course) }
                        )
                    }
                }
            }
        }

        // 6. Today's Daily Practice Challenge
        if (dailyQuiz != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = NavyDark
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SaffronGold
                            ) {
                                Text(
                                    text = AppStrings.get("daily_quiz", currentLanguage).uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "⏱️ ${dailyQuiz.durationMinutes} ${if (currentLanguage == AppLanguage.HINDI) "मिनट" else "Mins"} • ${dailyQuiz.totalMarks} ${if (currentLanguage == AppLanguage.HINDI) "अंक" else "Marks"}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) dailyQuiz.titleHi else dailyQuiz.titleEn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "आज के चुनिंदा 5 प्रश्न हल करें और अपनी ऑल-इंडिया रैंक जांचें।" else "Attempt today's top 5 questions to test your speed and accuracy.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Button(
                            onClick = { onTestClick(dailyQuiz) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = NavyDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("attempt_test", currentLanguage),
                                fontSize = 13.sp,
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

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(88.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HomeCourseCard(
    course: Course,
    currentLanguage: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Header Color Strip with Category Tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(NavyPrimary, NavyDark)
                        )
                    )
                    .padding(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (course.isFree) EmeraldSuccess else SaffronGold,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = if (course.isFree) AppStrings.get("free", currentLanguage) else "₹${course.price}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = SaffronGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${course.rating} (${course.studentsCount}+ ${if (currentLanguage == AppLanguage.HINDI) "छात्र" else "students"})",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Body
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) course.titleHi else course.titleEn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = course.instructor,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎥 ${course.totalVideos} • 📄 ${course.totalPdfs}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (course.enrolled) AppStrings.get("enrolled", currentLanguage) else AppStrings.get("view_details", currentLanguage),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
