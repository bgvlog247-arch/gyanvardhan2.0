package com.example.ui.screens.live

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.models.LiveClass
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@Composable
fun LiveClassesScreen(
    currentLanguage: AppLanguage,
    liveClasses: List<LiveClass>,
    onJoinLiveClass: (LiveClass) -> Unit,
    modifier: Modifier = Modifier
) {
    val liveNowList = liveClasses.filter { it.isLiveNow }
    val upcomingList = liveClasses.filter { !it.isLiveNow && !it.isCompleted }
    val pastRecordedList = liveClasses.filter { it.isCompleted }

    var selectedLiveForStream by remember { mutableStateOf<LiveClass?>(null) }
    var chatMessage by remember { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                "Rohit" to "Namaste Sir! Sound & video are crystal clear.",
                "Pooja" to "Sir please solve option C once again.",
                "Admin" to "Welcome all! Please note down formula #4 in your notebook."
            )
        )
    }

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

    if (selectedLiveForStream != null) {
        // Active Live Streaming Room
        val stream = selectedLiveForStream!!
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Live Stream Video Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(NavyDark)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = null,
                        tint = RoseError,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) stream.titleHi else stream.titleEn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${stream.teacherName} • ${stream.subject}",
                        fontSize = 12.sp,
                        color = SaffronGold
                    )
                }

                // Top Badge: LIVE NOW + Close Room Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RoseError
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE • ${stream.viewersCount} ${if (currentLanguage == AppLanguage.HINDI) "छात्र" else "online"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = { selectedLiveForStream = null },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Exit Stream", tint = Color.White)
                    }
                }
            }

            // Live Chat Section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp)
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "लाइव चैट (Live Discussion)" else "Live Chat & Q&A",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { (user, msg) ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "$user: ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user == "Admin") RoseError else SaffronGold
                            )
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatMessage,
                        onValueChange = { chatMessage = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(if (currentLanguage == AppLanguage.HINDI) "लाइव चैट में लिखें..." else "Send message...", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (chatMessage.isNotBlank()) {
                                chatMessages = chatMessages + ("You" to chatMessage)
                                chatMessage = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(RoseError)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    } else {
        // Scheduled Timetable & Live Classes View
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("live_classes_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "आधिकारिक लाइव क्लास समय-सारणी" else "Official Live Class Timetable",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "प्रशासन द्वारा निर्धारित कार्यक्रम के अनुसार लाइव कक्षाएं संचालित होती हैं।" else "Classes follow the verified admin schedule with interactive Q&A.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // 1. Live Now Section
            if (liveNowList.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(RoseError.copy(alpha = alphaAnim))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("live_now", currentLanguage),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoseError
                        )
                    }
                }

                items(liveNowList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLiveForStream = item },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoseError, SaffronGold)))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(shape = RoundedCornerShape(6.dp), color = RoseError) {
                                    Text(
                                        text = "LIVE NOW",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "👥 ${item.viewersCount} ${if (currentLanguage == AppLanguage.HINDI) "छात्र" else "viewers"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) item.titleHi else item.titleEn,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "👨‍🏫 ${item.teacherName} • ${item.subject} (${item.batchName})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { selectedLiveForStream = item },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("join_live", currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // 2. Upcoming Schedule Section
            item {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "आगामी कक्षाएं (Upcoming Schedule)" else "Upcoming Classes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(upcomingList) { item ->
                var isReminderSet by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SaffronGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = item.scheduledTime,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) item.titleHi else item.titleEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${item.teacherName} (${item.subject})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { isReminderSet = !isReminderSet }) {
                            Icon(
                                imageVector = if (isReminderSet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                contentDescription = "Reminder",
                                tint = if (isReminderSet) SaffronGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. Past Recorded Live Classes Section
            item {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "पूर्व में रिकॉर्डेड लाइव कक्षाएं" else "Past Recorded Live Classes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(pastRecordedList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) item.titleHi else item.titleEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${item.teacherName} • ${item.durationMinutes} ${if (currentLanguage == AppLanguage.HINDI) "मिनट" else "Mins"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FilledTonalButton(
                            onClick = { selectedLiveForStream = item },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(text = AppStrings.get("watch_now", currentLanguage), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
