package com.example.ui.screens.admin

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
import com.example.data.models.*
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodels.EducationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    currentLanguage: AppLanguage,
    courses: List<Course>,
    videos: List<VideoLesson>,
    liveClasses: List<LiveClass>,
    pdfs: List<PdfMaterial>,
    tests: List<TestSeries>,
    transactions: List<PaymentTransaction>,
    students: List<StudentAccount>,
    testResults: List<TestResult>,
    onExitAdmin: () -> Unit,
    viewModel: EducationViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { onExitAdmin() }

    var selectedAdminSection by remember { mutableIntStateOf(0) }
    // 0: Stats, 1: Courses & Pricing, 2: Live Scheduler, 3: Video LMS, 4: PDF Notes, 5: Tests & MCQs, 6: Student Accounts, 7: Payments, 8: Broadcast
    val sectionNames = listOf(
        "Overview", "Courses", "Live Classes", "Videos", "PDFs", "Test Series", "Students", "Payments", "Broadcast"
    )

    // Modal dialog states
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showScheduleLiveDialog by remember { mutableStateOf(false) }
    var showRescheduleLiveDialog by remember { mutableStateOf<LiveClass?>(null) }
    var showAddVideoDialog by remember { mutableStateOf(false) }
    var showAddPdfDialog by remember { mutableStateOf(false) }
    var showCreateTestDialog by remember { mutableStateOf(false) }
    var showAddQuestionDialog by remember { mutableStateOf<TestSeries?>(null) }
    var showEditPriceDialog by remember { mutableStateOf<Course?>(null) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    val totalRevenue = transactions.filter { it.status == "SUCCESS" }.sumOf { it.amount.toLong() } + 184500L
    val upcomingLiveCount = liveClasses.count { !it.isCompleted }
    val freeTestsCount = tests.count { it.isFree }
    val paidTestsCount = tests.count { !it.isFree }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Owner Admin Portal",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SaffronGold
                            ) {
                                Text(
                                    text = "SINGLE OWNER",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDark,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = OwnerSecurityConfig.OWNER_EMAIL,
                            fontSize = 11.sp,
                            color = SaffronGold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onExitAdmin) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Admin", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = onExitAdmin,
                        colors = ButtonDefaults.buttonColors(containerColor = RoseError),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp).padding(end = 8.dp)
                    ) {
                        Text(text = "Exit Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                .testTag("admin_dashboard_root")
        ) {
            // Admin Navigation Tabs
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedAdminSection,
                edgePadding = 12.dp
            ) {
                sectionNames.forEachIndexed { idx, name ->
                    Tab(
                        selected = selectedAdminSection == idx,
                        onClick = { selectedAdminSection = idx },
                        text = { Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Tab Content
            when (selectedAdminSection) {
                0 -> { // Overview Stats & Security Authority Status
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Exclusive Authority Badge
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = EmeraldSuccess.copy(alpha = 0.12f)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(EmeraldSuccess, SaffronGold)))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = "Verified Single Owner Authority", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = "Bound to ${OwnerSecurityConfig.OWNER_EMAIL}. Secondary admins and student self-elevation are strictly prohibited.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }

                        // Total Revenue Hero Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = NavyDark)
                            ) {
                                Row(
                                    modifier = Modifier.padding(18.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(SaffronGold),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "₹", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = AppStrings.get("total_revenue", currentLanguage),
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "₹$totalRevenue",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SaffronGold
                                        )
                                        Text(
                                            text = "Verified Razorpay / Cashfree Transactions (${transactions.size})",
                                            fontSize = 10.sp,
                                            color = EmeraldSuccess
                                        )
                                    }
                                }
                            }
                        }

                        // Metric Stat Grid
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    AdminStatBox(title = "Registered Students", value = "${students.size + 4850}", icon = Icons.Default.Groups, modifier = Modifier.weight(1f))
                                    AdminStatBox(title = "Active Today", value = "1,240", icon = Icons.Default.PersonSearch, modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    AdminStatBox(title = "Total Courses", value = "${courses.size}", icon = Icons.Default.MenuBook, modifier = Modifier.weight(1f))
                                    AdminStatBox(title = "Uploaded Videos", value = "${videos.size}", icon = Icons.Default.VideoLibrary, modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    AdminStatBox(title = "PDF Study Notes", value = "${pdfs.size}", icon = Icons.Default.PictureAsPdf, modifier = Modifier.weight(1f))
                                    AdminStatBox(title = "Upcoming Live", value = "$upcomingLiveCount", icon = Icons.Default.LiveTv, modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    AdminStatBox(title = "Free Test Series", value = "$freeTestsCount", icon = Icons.Default.Assignment, modifier = Modifier.weight(1f))
                                    AdminStatBox(title = "Paid Test Series", value = "$paidTestsCount", icon = Icons.Default.WorkspacePremium, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                1 -> { // Manage Courses (Publish/Unpublish, Edit Price, Delete)
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Button(
                                onClick = { showAddCourseDialog = true },
                                modifier = Modifier.fillMaxWidth().testTag("admin_add_course_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create New Course / Batch")
                            }
                        }

                        items(courses) { course ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = course.titleEn, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (course.isPublished) EmeraldSuccess else Color.Gray
                                        ) {
                                            Text(
                                                text = if (course.isPublished) "PUBLISHED" else "UNPUBLISHED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Price: ${if (course.isFree) "FREE" else "₹" + course.price} • ${course.instructor} • ${course.category.name}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilledTonalButton(
                                            onClick = { viewModel.adminToggleCoursePublish(course.id, course.isPublished) },
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = if (course.isPublished) "Unpublish" else "Publish", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { showEditPriceDialog = course },
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = "Change Price", fontSize = 11.sp)
                                        }
                                        IconButton(
                                            onClick = { viewModel.adminDeleteCourse(course) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseError, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> { // Live Classes Scheduler (Reschedule, Start/Stop, Cancel)
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Button(
                                onClick = { showScheduleLiveDialog = true },
                                modifier = Modifier.fillMaxWidth().testTag("admin_schedule_live_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Publish Official Live Timetable")
                            }
                        }

                        items(liveClasses) { live ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = live.titleEn, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (live.isLiveNow) RoseError else if (live.isCompleted) Color.Gray else SaffronGold
                                        ) {
                                            Text(
                                                text = if (live.isLiveNow) "STREAMING" else if (live.isCompleted) "COMPLETED" else "SCHEDULED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Scheduled: ${live.scheduledTime} • Teacher: ${live.teacherName} (${live.subject})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilledTonalButton(
                                            onClick = { viewModel.adminToggleLiveClass(live.id, !live.isLiveNow) },
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = if (live.isLiveNow) "End Stream" else "Start Class", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { showRescheduleLiveDialog = live },
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = "Reschedule", fontSize = 11.sp)
                                        }
                                        IconButton(
                                            onClick = { viewModel.adminDeleteLiveClass(live) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Cancel", tint = RoseError, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> { // Manage Video Classes
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Button(
                                onClick = { showAddVideoDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Educational Video")
                            }
                        }

                        items(videos) { vid ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = IndigoAccent)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = vid.titleEn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${vid.chapter} • ${vid.durationText} • ${if (vid.isFreePreview) "Free Demo" else "Paid Video"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { viewModel.adminDeleteVideo(vid) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseError)
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> { // Manage PDF Notes
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Button(
                                onClick = { showAddPdfDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = NavyDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload PDF Study Notes", color = NavyDark, fontWeight = FontWeight.Bold)
                            }
                        }

                        items(pdfs) { pdf ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = CyanAccent)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = pdf.titleEn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${pdf.subject} • ${pdf.pageCount} Pages • ${pdf.fileSizeMb} MB • ${if (pdf.isFree) "Free" else "Paid"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { viewModel.adminDeletePdf(pdf) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseError)
                                    }
                                }
                            }
                        }
                    }
                }

                5 -> { // Manage Test Series & Questions
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Button(
                                onClick = { showCreateTestDialog = true },
                                modifier = Modifier.fillMaxWidth().testTag("admin_create_test_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create Test Series with Negative Marking")
                            }
                        }

                        items(tests) { test ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = test.titleEn, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (test.isPublished) EmeraldSuccess else Color.Gray
                                        ) {
                                            Text(
                                                text = if (test.isPublished) "ACTIVE" else "DRAFT",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Duration: ${test.durationMinutes}m • Negative: -${test.negativeMarks} • Price: ${if (test.isFree) "FREE" else "₹" + test.price}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilledTonalButton(
                                            onClick = { showAddQuestionDialog = test },
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = "+ Add MCQ", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.adminToggleTestPublish(test.id, test.isPublished) },
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = if (test.isPublished) "Unpublish" else "Publish", fontSize = 11.sp)
                                        }
                                        IconButton(
                                            onClick = { viewModel.adminDeleteTest(test) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseError, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                6 -> { // Student Accounts (Manage Access, Block / Unblock)
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = "Registered Students (${students.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        items(students) { std ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (std.isBlocked) RoseError.copy(alpha = 0.2f) else EmeraldSuccess.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = std.name.take(2).uppercase(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (std.isBlocked) RoseError else EmeraldSuccess
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = std.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${std.email} • ${std.mobileNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = "Target: ${std.targetExam} • Joined: ${std.joinedDate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Button(
                                        onClick = { viewModel.adminToggleStudentBlock(std.id, std.isBlocked) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (std.isBlocked) EmeraldSuccess else RoseError
                                        ),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (std.isBlocked) "Unblock" else "Block",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                7 -> { // Payment Transactions Log
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(transactions) { tx ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = tx.itemTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                        Text(text = "₹${tx.amount}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldSuccess)
                                    }
                                    Text(text = "Ref: ${tx.transactionRef} • ${tx.paymentMethod}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Time: ${tx.dateText} • Receipt: ${tx.receiptId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                8 -> { // Broadcast Announcements
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Button(
                                onClick = { showBroadcastDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronGold)
                            ) {
                                Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = NavyDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Broadcast New Announcement to All Students", fontWeight = FontWeight.Bold, color = NavyDark)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Add Course
    if (showAddCourseDialog) {
        var courseTitleEn by remember { mutableStateOf("") }
        var courseTitleHi by remember { mutableStateOf("") }
        var instructorName by remember { mutableStateOf("Prof. Rakesh Verma") }
        var coursePrice by remember { mutableStateOf("499") }

        AlertDialog(
            onDismissRequest = { showAddCourseDialog = false },
            title = { Text("Create Course Batch", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = courseTitleEn, onValueChange = { courseTitleEn = it }, label = { Text("Title (English)") }, singleLine = true)
                    OutlinedTextField(value = courseTitleHi, onValueChange = { courseTitleHi = it }, label = { Text("Title (Hindi)") }, singleLine = true)
                    OutlinedTextField(value = instructorName, onValueChange = { instructorName = it }, label = { Text("Instructor") }, singleLine = true)
                    OutlinedTextField(value = coursePrice, onValueChange = { coursePrice = it }, label = { Text("Price (₹, 0 for free)") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (courseTitleEn.isNotBlank()) {
                        val p = coursePrice.toIntOrNull() ?: 0
                        viewModel.adminAddCourse(courseTitleEn, courseTitleHi.ifBlank { courseTitleEn }, CategoryType.COMPETITIVE, ExamType.SSC_GD, p, instructorName)
                        showAddCourseDialog = false
                    }
                }) { Text("Save & Publish") }
            },
            dismissButton = { TextButton(onClick = { showAddCourseDialog = false }) { Text("Cancel") } }
        )
    }

    // Modal: Schedule Live Class
    if (showScheduleLiveDialog) {
        var liveTitleEn by remember { mutableStateOf("") }
        var teacher by remember { mutableStateOf("Dr. Surendra Sir") }
        var subject by remember { mutableStateOf("General Science") }
        var timeSchedule by remember { mutableStateOf("Today, 7:00 PM") }

        AlertDialog(
            onDismissRequest = { showScheduleLiveDialog = false },
            title = { Text("Publish Live Timetable", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = liveTitleEn, onValueChange = { liveTitleEn = it }, label = { Text("Class Title") }, singleLine = true)
                    OutlinedTextField(value = teacher, onValueChange = { teacher = it }, label = { Text("Teacher Name") }, singleLine = true)
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, singleLine = true)
                    OutlinedTextField(value = timeSchedule, onValueChange = { timeSchedule = it }, label = { Text("Time Schedule") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (liveTitleEn.isNotBlank()) {
                        viewModel.adminScheduleLiveClass(liveTitleEn, liveTitleEn, subject, teacher, "Selection Batch", timeSchedule)
                        showScheduleLiveDialog = false
                    }
                }) { Text("Publish") }
            },
            dismissButton = { TextButton(onClick = { showScheduleLiveDialog = false }) { Text("Cancel") } }
        )
    }

    // Modal: Reschedule Live Class
    showRescheduleLiveDialog?.let { live ->
        var newTime by remember { mutableStateOf(live.scheduledTime) }
        AlertDialog(
            onDismissRequest = { showRescheduleLiveDialog = null },
            title = { Text("Reschedule Live Class") },
            text = {
                Column {
                    Text(text = live.titleEn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newTime, onValueChange = { newTime = it }, label = { Text("New Scheduled Time") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.adminRescheduleLiveClass(live.id, newTime)
                    showRescheduleLiveDialog = null
                }) { Text("Update Time") }
            },
            dismissButton = { TextButton(onClick = { showRescheduleLiveDialog = null }) { Text("Cancel") } }
        )
    }

    // Modal: Edit Course Price
    showEditPriceDialog?.let { course ->
        var newPriceInput by remember { mutableStateOf(course.price.toString()) }
        AlertDialog(
            onDismissRequest = { showEditPriceDialog = null },
            title = { Text("Update Course Price") },
            text = {
                Column {
                    Text(text = course.titleEn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newPriceInput, onValueChange = { newPriceInput = it }, label = { Text("New Price (₹, 0 for free)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val p = newPriceInput.toIntOrNull() ?: 0
                    viewModel.adminUpdateCoursePrice(course.id, p)
                    showEditPriceDialog = null
                }) { Text("Update Price") }
            },
            dismissButton = { TextButton(onClick = { showEditPriceDialog = null }) { Text("Cancel") } }
        )
    }

    // Modal: Create Test Series with Negative Marking
    if (showCreateTestDialog) {
        var tTitleEn by remember { mutableStateOf("") }
        var tDuration by remember { mutableStateOf("15") }
        var tNegative by remember { mutableStateOf("0.5") }
        var tPrice by remember { mutableStateOf("0") }

        AlertDialog(
            onDismissRequest = { showCreateTestDialog = false },
            title = { Text("Create Test Series") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = tTitleEn, onValueChange = { tTitleEn = it }, label = { Text("Test Title") })
                    OutlinedTextField(value = tDuration, onValueChange = { tDuration = it }, label = { Text("Duration (Mins)") })
                    OutlinedTextField(value = tNegative, onValueChange = { tNegative = it }, label = { Text("Negative Marking (e.g. 0.5)") })
                    OutlinedTextField(value = tPrice, onValueChange = { tPrice = it }, label = { Text("Price (0 for Free)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (tTitleEn.isNotBlank()) {
                        val pr = tPrice.toIntOrNull() ?: 0
                        val dur = tDuration.toIntOrNull() ?: 15
                        val neg = tNegative.toFloatOrNull() ?: 0.5f
                        viewModel.adminCreateTestSeries(tTitleEn, tTitleEn, if (pr == 0) TestType.FREE else TestType.PAID, ExamType.SSC_GD, pr, dur, neg)
                        showCreateTestDialog = false
                    }
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showCreateTestDialog = false }) { Text("Cancel") } }
        )
    }

    // Modal: Add MCQ Question to Test
    showAddQuestionDialog?.let { test ->
        var qText by remember { mutableStateOf("") }
        var optA by remember { mutableStateOf("") }
        var optB by remember { mutableStateOf("") }
        var optC by remember { mutableStateOf("") }
        var optD by remember { mutableStateOf("") }
        var explanation by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddQuestionDialog = null },
            title = { Text("Add MCQ to ${test.titleEn.take(20)}...") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = qText, onValueChange = { qText = it }, label = { Text("Question Text") })
                    OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("Option A (Correct)") })
                    OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("Option B") })
                    OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("Option C") })
                    OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("Option D") })
                    OutlinedTextField(value = explanation, onValueChange = { explanation = it }, label = { Text("Explanation") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (qText.isNotBlank() && optA.isNotBlank()) {
                        viewModel.adminAddQuestionToTest(
                            testId = test.id,
                            qNumber = (test.questionsCount + 1),
                            questionEn = qText,
                            questionHi = qText,
                            optionsEn = listOf(optA, optB, optC, optD),
                            optionsHi = listOf(optA, optB, optC, optD),
                            correctIdx = 0,
                            explanationEn = explanation,
                            explanationHi = explanation
                        )
                        showAddQuestionDialog = null
                    }
                }) { Text("Save MCQ") }
            },
            dismissButton = { TextButton(onClick = { showAddQuestionDialog = null }) { Text("Cancel") } }
        )
    }

    // Modal: Add Video
    if (showAddVideoDialog) {
        var vTitleEn by remember { mutableStateOf("") }
        var vChapter by remember { mutableStateOf("Mathematics") }
        var vDuration by remember { mutableStateOf("45:00") }

        AlertDialog(
            onDismissRequest = { showAddVideoDialog = false },
            title = { Text("Upload Video Lesson") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = vTitleEn, onValueChange = { vTitleEn = it }, label = { Text("Video Title") })
                    OutlinedTextField(value = vChapter, onValueChange = { vChapter = it }, label = { Text("Chapter / Subject") })
                    OutlinedTextField(value = vDuration, onValueChange = { vDuration = it }, label = { Text("Duration (e.g. 45:00)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (vTitleEn.isNotBlank()) {
                        viewModel.adminAddVideo("c_ssc_gd", vTitleEn, vTitleEn, vChapter, vDuration, true)
                        showAddVideoDialog = false
                    }
                }) { Text("Upload") }
            },
            dismissButton = { TextButton(onClick = { showAddVideoDialog = false }) { Text("Cancel") } }
        )
    }

    // Modal: Add PDF
    if (showAddPdfDialog) {
        var pTitleEn by remember { mutableStateOf("") }
        var pSubject by remember { mutableStateOf("Science") }
        var pPages by remember { mutableStateOf("50") }

        AlertDialog(
            onDismissRequest = { showAddPdfDialog = false },
            title = { Text("Upload PDF Notes") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = pTitleEn, onValueChange = { pTitleEn = it }, label = { Text("Notes Title") })
                    OutlinedTextField(value = pSubject, onValueChange = { pSubject = it }, label = { Text("Subject") })
                    OutlinedTextField(value = pPages, onValueChange = { pPages = it }, label = { Text("Page Count") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (pTitleEn.isNotBlank()) {
                        viewModel.adminAddPdf("c_ssc_gd", pTitleEn, pTitleEn, pSubject, "All Chapters", pPages.toIntOrNull() ?: 20, true)
                        showAddPdfDialog = false
                    }
                }) { Text("Save PDF") }
            },
            dismissButton = { TextButton(onClick = { showAddPdfDialog = false }) { Text("Cancel") } }
        )
    }

    // Modal: Broadcast Announcement
    if (showBroadcastDialog) {
        var bTitle by remember { mutableStateOf("") }
        var bMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Broadcast Notification") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = bTitle, onValueChange = { bTitle = it }, label = { Text("Title") })
                    OutlinedTextField(value = bMessage, onValueChange = { bMessage = it }, label = { Text("Message to all students") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (bTitle.isNotBlank() && bMessage.isNotBlank()) {
                        viewModel.adminBroadcastNotification(bTitle, bTitle, bMessage, bMessage, false)
                        showBroadcastDialog = false
                    }
                }) { Text("Send Broadcast") }
            },
            dismissButton = { TextButton(onClick = { showBroadcastDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun AdminStatBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
