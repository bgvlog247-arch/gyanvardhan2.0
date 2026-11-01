package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.OwnerSecurityConfig
import com.example.data.models.TestSeries
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.NotificationBottomSheet
import com.example.ui.locale.AppLanguage
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.courses.CourseDetailScreen
import com.example.ui.screens.courses.CoursesScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.live.LiveClassesScreen
import com.example.ui.screens.notes.PdfReaderScreen
import com.example.ui.screens.payment.PaymentCheckoutDialog
import com.example.ui.screens.profile.MyPurchasesScreen
import com.example.ui.screens.profile.MyResultsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.tests.TestResultScreen
import com.example.ui.screens.tests.TestSeriesScreen
import com.example.ui.screens.tests.TestTakingScreen
import com.example.ui.screens.video.VideoPlayerScreen
import com.example.ui.theme.GyanvardhanTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.RoseError
import com.example.ui.theme.SaffronGold
import com.example.ui.viewmodels.ActiveScreen
import com.example.ui.viewmodels.EducationViewModel
import com.example.ui.viewmodels.MainTab

class MainActivity : ComponentActivity() {

    private val viewModel: EducationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GyanvardhanTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: EducationViewModel) {
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()

    val currentAuthUser by viewModel.currentAuthUser.collectAsStateWithLifecycle()
    val isOwnerAdmin by viewModel.isOwnerAdmin.collectAsStateWithLifecycle()

    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val allVideos by viewModel.allVideos.collectAsStateWithLifecycle()
    val liveClasses by viewModel.liveClasses.collectAsStateWithLifecycle()
    val pdfMaterials by viewModel.pdfMaterials.collectAsStateWithLifecycle()
    val testSeries by viewModel.testSeries.collectAsStateWithLifecycle()
    val testResults by viewModel.testResults.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val registeredStudents by viewModel.registeredStudents.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
    val selectedVideo by viewModel.selectedVideo.collectAsStateWithLifecycle()
    val selectedPdf by viewModel.selectedPdf.collectAsStateWithLifecycle()

    // Test Taking states
    val activeTest by viewModel.activeTest.collectAsStateWithLifecycle()
    val testQuestions by viewModel.testQuestions.collectAsStateWithLifecycle()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val userAnswers by viewModel.userAnswers.collectAsStateWithLifecycle()
    val markedForReview by viewModel.markedForReview.collectAsStateWithLifecycle()
    val timeRemainingSeconds by viewModel.testTimeRemainingSeconds.collectAsStateWithLifecycle()
    val latestResult by viewModel.latestResult.collectAsStateWithLifecycle()

    // Payment states
    val checkoutItem by viewModel.checkoutItem.collectAsStateWithLifecycle()
    val activeOrder by viewModel.activeOrder.collectAsStateWithLifecycle()
    val paymentFlowState by viewModel.paymentFlowState.collectAsStateWithLifecycle()
    val isProcessingPayment by viewModel.isProcessingPayment.collectAsStateWithLifecycle()
    val paymentSuccessReceipt by viewModel.paymentSuccessReceipt.collectAsStateWithLifecycle()
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()

    // Dialogs
    var showNotificationSheet by remember { mutableStateOf(false) }
    var showOwnerAuthPrompt by remember { mutableStateOf(false) }
    var ownerEmailPrompt by remember { mutableStateOf(OwnerSecurityConfig.OWNER_EMAIL) }
    var ownerTokenPrompt by remember { mutableStateOf("") }
    var ownerPromptError by remember { mutableStateOf<String?>(null) }

    val unreadNotifCount = notifications.count { !it.isRead }

    // Filter content for students (students see only published content, admin sees all)
    val displayCourses = courses.filter { isOwnerAdmin || it.isPublished }
    val displayTests = testSeries.filter { isOwnerAdmin || it.isPublished }

    // Show BottomNav only on MAIN screens
    val isMainScreen = activeScreen == ActiveScreen.MAIN

    Scaffold(
        topBar = {
            if (isMainScreen) {
                AppTopBar(
                    currentLanguage = currentLanguage,
                    unreadNotifCount = unreadNotifCount,
                    isOwnerAdmin = isOwnerAdmin,
                    onLanguageToggle = { viewModel.toggleLanguage() },
                    onNotificationClick = { showNotificationSheet = true },
                    onAdminClick = {
                        if (isOwnerAdmin) {
                            viewModel.navigateTo(ActiveScreen.ADMIN_PANEL)
                        } else {
                            showOwnerAuthPrompt = true
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isMainScreen) {
                AppBottomNavBar(
                    currentTab = currentTab,
                    currentLanguage = currentLanguage,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (activeScreen) {
                ActiveScreen.MAIN -> {
                    when (currentTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                currentLanguage = currentLanguage,
                                courses = displayCourses,
                                liveClasses = liveClasses,
                                videos = allVideos,
                                testSeries = displayTests,
                                onCourseClick = { viewModel.openCourseDetails(it) },
                                onLiveClassClick = { viewModel.selectTab(MainTab.LIVE) },
                                onVideoClick = { viewModel.openVideoPlayer(it) },
                                onTestClick = { test ->
                                    if (test.isFree || test.isUnlocked) {
                                        viewModel.startTest(test)
                                    } else {
                                        viewModel.initiateCheckoutForTest(test)
                                    }
                                },
                                onNavigateToCourses = { viewModel.selectTab(MainTab.COURSES) },
                                onNavigateToLive = { viewModel.selectTab(MainTab.LIVE) },
                                onNavigateToTests = { viewModel.selectTab(MainTab.TESTS) },
                                onNavigateToPdfs = {
                                    val firstPdf = pdfMaterials.firstOrNull()
                                    if (firstPdf != null) viewModel.openPdfViewer(firstPdf)
                                }
                            )
                        }
                        MainTab.COURSES -> {
                            CoursesScreen(
                                currentLanguage = currentLanguage,
                                courses = displayCourses,
                                selectedCategory = selectedCategory,
                                searchQuery = searchQuery,
                                onCategorySelected = { viewModel.selectCategory(it) },
                                onSearchQueryChanged = { viewModel.updateSearchQuery(it) },
                                onCourseClick = { viewModel.openCourseDetails(it) },
                                onEnrollClick = { course ->
                                    if (course.isFree) {
                                        viewModel.openCourseDetails(course)
                                    } else {
                                        viewModel.initiateCheckoutForCourse(course)
                                    }
                                }
                            )
                        }
                        MainTab.TESTS -> {
                            TestSeriesScreen(
                                currentLanguage = currentLanguage,
                                testSeriesList = displayTests,
                                onTestClick = { test ->
                                    if (test.isFree || test.isUnlocked) {
                                        viewModel.startTest(test)
                                    } else {
                                        viewModel.initiateCheckoutForTest(test)
                                    }
                                },
                                onBuyTestClick = { test ->
                                    viewModel.initiateCheckoutForTest(test)
                                }
                            )
                        }
                        MainTab.LIVE -> {
                            LiveClassesScreen(
                                currentLanguage = currentLanguage,
                                liveClasses = liveClasses,
                                onJoinLiveClass = { /* Handled in screen */ }
                            )
                        }
                        MainTab.PROFILE -> {
                            ProfileScreen(
                                currentUser = currentAuthUser,
                                isOwnerAdmin = isOwnerAdmin,
                                currentLanguage = currentLanguage,
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onNavigatePurchases = { viewModel.navigateTo(ActiveScreen.MY_PURCHASES) },
                                onNavigateResults = { viewModel.navigateTo(ActiveScreen.MY_RESULTS) },
                                onNavigateAdmin = { viewModel.navigateTo(ActiveScreen.ADMIN_PANEL) },
                                onVerifyOwner = { email, token -> viewModel.authenticateOwner(email, token) },
                                onLogoutOwner = { viewModel.exitAdminMode() }
                            )
                        }
                    }
                }
                ActiveScreen.COURSE_DETAIL -> {
                    selectedCourse?.let { course ->
                        CourseDetailScreen(
                            course = course,
                            currentLanguage = currentLanguage,
                            videos = allVideos.filter { it.courseId == course.id },
                            pdfs = pdfMaterials.filter { it.courseId == course.id },
                            tests = displayTests.filter { it.courseId == course.id },
                            onBackClick = { viewModel.navigateBack() },
                            onVideoClick = { viewModel.openVideoPlayer(it) },
                            onPdfClick = { viewModel.openPdfViewer(it) },
                            onTestClick = { viewModel.startTest(it) },
                            onEnrollClick = { viewModel.initiateCheckoutForCourse(course) }
                        )
                    }
                }
                ActiveScreen.VIDEO_PLAYER -> {
                    selectedVideo?.let { video ->
                        VideoPlayerScreen(
                            video = video,
                            allVideos = allVideos,
                            currentLanguage = currentLanguage,
                            onBackClick = { viewModel.navigateBack() },
                            onSelectVideo = { viewModel.openVideoPlayer(it) },
                            onProgressUpdate = { id, prog -> viewModel.updateVideoProgress(id, prog) }
                        )
                    }
                }
                ActiveScreen.PDF_VIEWER -> {
                    selectedPdf?.let { pdf ->
                        PdfReaderScreen(
                            pdf = pdf,
                            currentLanguage = currentLanguage,
                            onBackClick = { viewModel.navigateBack() },
                            onDownloadClick = { viewModel.downloadPdf(pdf.id) }
                        )
                    }
                }
                ActiveScreen.TEST_TAKING -> {
                    activeTest?.let { test ->
                        TestTakingScreen(
                            test = test,
                            questions = testQuestions,
                            currentIndex = currentQuestionIndex,
                            userAnswers = userAnswers,
                            markedForReview = markedForReview,
                            timeRemainingSeconds = timeRemainingSeconds,
                            currentLanguage = currentLanguage,
                            onOptionSelected = { qNum, optIdx -> viewModel.selectOption(qNum, optIdx) },
                            onClearAnswer = { qNum -> viewModel.clearAnswer(qNum) },
                            onToggleMarkReview = { qNum -> viewModel.toggleMarkForReview(qNum) },
                            onNavigateQuestion = { idx -> viewModel.goToQuestion(idx) },
                            onSubmitTest = { viewModel.submitTest() },
                            onExitWithoutSaving = { viewModel.navigateBack() }
                        )
                    }
                }
                ActiveScreen.TEST_RESULT -> {
                    latestResult?.let { result ->
                        TestResultScreen(
                            result = result,
                            questions = testQuestions,
                            currentLanguage = currentLanguage,
                            onBackClick = { viewModel.navigateBack() }
                        )
                    }
                }
                ActiveScreen.ADMIN_PANEL -> {
                    AdminDashboardScreen(
                        currentLanguage = currentLanguage,
                        courses = courses,
                        videos = allVideos,
                        liveClasses = liveClasses,
                        pdfs = pdfMaterials,
                        tests = testSeries,
                        transactions = transactions,
                        students = registeredStudents,
                        testResults = testResults,
                        onExitAdmin = { viewModel.exitAdminMode() },
                        viewModel = viewModel
                    )
                }
                ActiveScreen.MY_PURCHASES -> {
                    MyPurchasesScreen(
                        transactions = transactions,
                        subscriptions = subscriptions,
                        courses = displayCourses,
                        tests = displayTests,
                        currentLanguage = currentLanguage,
                        onBackClick = { viewModel.navigateBack() },
                        onCourseClick = { viewModel.openCourseDetails(it) },
                        onTestClick = { viewModel.startTest(it) }
                    )
                }
                ActiveScreen.MY_RESULTS -> {
                    MyResultsScreen(
                        results = testResults,
                        currentLanguage = currentLanguage,
                        onBackClick = { viewModel.navigateBack() },
                        onViewResult = { viewModel.viewResult(it) }
                    )
                }
            }

            // Indian Payment Gateway Razorpay Checkout BottomSheet
            checkoutItem?.let { item ->
                PaymentCheckoutDialog(
                    item = item,
                    activeOrder = activeOrder,
                    flowState = paymentFlowState,
                    isProcessing = isProcessingPayment,
                    currentLanguage = currentLanguage,
                    onDismiss = { viewModel.dismissCheckout() },
                    onSelectPurchaseType = { type -> viewModel.selectPurchaseTypeForCourse(type) },
                    onPay = { method, simMode -> viewModel.processRazorpayPayment(method, simMode) },
                    onRetry = { viewModel.retryFailedPayment() }
                )
            }

            // Notification Bottom Sheet
            if (showNotificationSheet) {
                NotificationBottomSheet(
                    notifications = notifications,
                    currentLanguage = currentLanguage,
                    onDismiss = { showNotificationSheet = false },
                    onNotificationClick = { notif ->
                        viewModel.markNotificationRead(notif.id)
                        if (notif.isLiveAlert) {
                            viewModel.selectTab(MainTab.LIVE)
                            showNotificationSheet = false
                        }
                    }
                )
            }

            // Strict Owner Admin Verification Dialog
            if (showOwnerAuthPrompt) {
                AlertDialog(
                    onDismissRequest = {
                        showOwnerAuthPrompt = false
                        ownerTokenPrompt = ""
                        ownerPromptError = null
                    },
                    title = {
                        Text(text = "🔒 Sole Owner Admin Verification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    },
                    text = {
                        Column {
                            Text(
                                text = "Strict Single Owner Architecture: Only ${OwnerSecurityConfig.OWNER_EMAIL} possesses administrative authority. Public or student admin registration is strictly disabled.",
                                fontSize = 12.sp,
                                color = NavyDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = ownerEmailPrompt,
                                onValueChange = {
                                    ownerEmailPrompt = it
                                    ownerPromptError = null
                                },
                                label = { Text("Owner Email") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("topbar_owner_email")
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = ownerTokenPrompt,
                                onValueChange = {
                                    ownerTokenPrompt = it
                                    ownerPromptError = null
                                },
                                label = { Text("Owner Security Token / Passkey") },
                                placeholder = { Text("e.g. 88219") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("topbar_owner_token")
                            )
                            if (ownerPromptError != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ownerPromptError!!,
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
                                val (success, err) = viewModel.authenticateOwner(ownerEmailPrompt, ownerTokenPrompt)
                                if (success) {
                                    showOwnerAuthPrompt = false
                                    ownerTokenPrompt = ""
                                    ownerPromptError = null
                                } else {
                                    ownerPromptError = err ?: "Access denied."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
                            modifier = Modifier.testTag("verify_topbar_owner")
                        ) {
                            Text("Verify Owner", fontWeight = FontWeight.Bold, color = NavyDark)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showOwnerAuthPrompt = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
