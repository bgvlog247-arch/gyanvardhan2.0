package com.example.ui.screens.video

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.VideoLesson
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    video: VideoLesson,
    allVideos: List<VideoLesson>,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onSelectVideo: (VideoLesson) -> Unit,
    onProgressUpdate: (String, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    var isPlaying by remember { mutableStateOf(true) }
    var currentProgress by remember(video.id) { mutableFloatStateOf(video.progressPercent.coerceAtLeast(0.05f)) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var showControls by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Playlist, 1: Lesson Notes, 2: Doubts
    var doubtInput by remember { mutableStateOf("") }
    var doubtsList by remember {
        mutableStateOf(
            listOf(
                "Aman: Sir, can you please explain divisibility by 7 again?" to "Teacher: In the next 2 mins, we apply the 2x last-digit subtraction formula!",
                "Priya: Notes downloaded, thank you sir!" to "Teacher: Excellent! Keep revising daily."
            )
        )
    }

    // Playback progress ticker
    LaunchedEffect(isPlaying, video.id) {
        while (isPlaying) {
            delay(1000)
            if (currentProgress < 1.0f) {
                currentProgress = (currentProgress + 0.005f * playbackSpeed).coerceAtMost(1.0f)
                onProgressUpdate(video.id, currentProgress)
            } else {
                isPlaying = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) video.titleHi else video.titleEn,
                        fontSize = 15.sp,
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
                    containerColor = NavyDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Video Player Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(Color.Black)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showControls = !showControls
                    }
                    .testTag("video_player_box")
            ) {
                // Background video placeholder pattern
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.OndemandVideo,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "HD 1080p Streaming • Gyanvardhan LMS",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }

                // Controls Overlay
                if (showControls) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        // Center Playback Buttons (Seek -10s, Play/Pause, Seek +10s)
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            IconButton(
                                onClick = { currentProgress = (currentProgress - 0.05f).coerceAtLeast(0f) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay10,
                                    contentDescription = "-10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(SaffronGold)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Play",
                                    tint = NavyDark,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            IconButton(
                                onClick = { currentProgress = (currentProgress + 0.05f).coerceAtMost(1f) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forward10,
                                    contentDescription = "+10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        // Top Quality & Speed Pill
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                onClick = {
                                    playbackSpeed = when (playbackSpeed) {
                                        1.0f -> 1.25f
                                        1.25f -> 1.5f
                                        1.5f -> 2.0f
                                        else -> 1.0f
                                    }
                                }
                            ) {
                                Text(
                                    text = "${playbackSpeed}x",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SaffronGold
                            ) {
                                Text(
                                    text = "1080p HD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Bottom Slider & Time Text
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val currentMin = (currentProgress * 48).toInt()
                                val currentSec = ((currentProgress * 48 * 60) % 60).toInt()
                                Text(
                                    text = String.format("%02d:%02d", currentMin, currentSec),
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = video.durationText,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            Slider(
                                value = currentProgress,
                                onValueChange = {
                                    currentProgress = it
                                    onProgressUpdate(video.id, it)
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = SaffronGold,
                                    activeTrackColor = SaffronGold,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }
            }

            // 2. Video Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(14.dp)
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) video.titleHi else video.titleEn,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${video.instructor} • ${video.chapter}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3. Tab Switcher (Playlist, Notes, Doubts)
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (currentLanguage == AppLanguage.HINDI) "अध्याय सूची" else "Playlist", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (currentLanguage == AppLanguage.HINDI) "पाठ नोट्स" else "Notes", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(if (currentLanguage == AppLanguage.HINDI) "संदेह पूछें" else "Ask Doubt", fontSize = 12.sp) }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> { // Video Playlist
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allVideos) { item ->
                            val isCurrent = item.id == video.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectVideo(item) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrent) SaffronGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isCurrent) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SaffronGold, SaffronGold))) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isCurrent) Icons.Default.PlayCircle else if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = if (isCurrent) SaffronDark else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) item.titleHi else item.titleEn,
                                            fontSize = 12.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${item.chapter} • ${item.durationText}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> { // Notes
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "कक्षा सारांश एवं मुख्य बिंदु:" else "Class Key Takeaways & Summary:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "1. संख्या पद्धति में विभाज्यता नियम (Divisibility rules by 3, 7, 11, 13).\n2. इकाई अंक (Unit digit calculation) के नियम व चक्रीयता (Cyclicity of 4).\n3. शेषफल प्रमेय (Remainder Theorem) आधारित शॉर्ट ट्रिक्स।"
                                    else
                                        "1. Divisibility rules for numbers 3, 7, 11, and 13.\n2. Unit digit calculations using power cyclicity method.\n3. Wilson's and Fermat's Little Theorem speed tricks for competitive exams.",
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
                2 -> { // Doubt Discussion
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(doubtsList) { (q, a) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "❓ $q", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "✅ $a", fontSize = 11.sp, color = EmeraldSuccess)
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = doubtInput,
                                onValueChange = { doubtInput = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text(if (currentLanguage == AppLanguage.HINDI) "अपना प्रश्न यहाँ टाइप करें..." else "Type your doubt here...", fontSize = 12.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (doubtInput.isNotBlank()) {
                                        doubtsList = doubtsList + ("You: $doubtInput" to "Teacher: Thank you! We will explain this in the live Q&A session.")
                                        doubtInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
