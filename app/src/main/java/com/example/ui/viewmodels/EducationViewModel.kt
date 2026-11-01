package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.*
import com.example.data.repository.EducationRepository
import com.example.data.security.AuthResult
import com.example.data.security.OwnerAuthService
import com.example.ui.locale.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    COURSES,
    TESTS,
    LIVE,
    PROFILE
}

enum class ActiveScreen {
    MAIN,
    COURSE_DETAIL,
    VIDEO_PLAYER,
    PDF_VIEWER,
    TEST_TAKING,
    TEST_RESULT,
    ADMIN_PANEL,
    MY_PURCHASES,
    MY_RESULTS
}

class EducationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EducationRepository.getInstance(application)

    // Language
    private val _currentLanguage = MutableStateFlow(AppLanguage.HINDI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.HINDI) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.HINDI
        }
    }

    // Security & Single Owner Admin Session
    private val _currentAuthUser = MutableStateFlow(OwnerAuthService.getCurrentUser())
    val currentAuthUser: StateFlow<AuthUser> = _currentAuthUser.asStateFlow()

    private val _isOwnerAdmin = MutableStateFlow(OwnerAuthService.isCurrentOwnerAdmin())
    val isOwnerAdmin: StateFlow<Boolean> = _isOwnerAdmin.asStateFlow()

    fun authenticateOwner(email: String, securityToken: String): Pair<Boolean, String?> {
        val result = OwnerAuthService.authenticateOwnerAdmin(email, securityToken)
        return when (result) {
            is AuthResult.Success -> {
                _currentAuthUser.value = result.user
                _isOwnerAdmin.value = true
                _activeScreen.value = ActiveScreen.ADMIN_PANEL
                Pair(true, null)
            }
            is AuthResult.Denied -> {
                Pair(false, result.message)
            }
        }
    }

    fun exitAdminMode() {
        OwnerAuthService.switchBackToStudent()
        _currentAuthUser.value = OwnerAuthService.getCurrentUser()
        _isOwnerAdmin.value = false
        _activeScreen.value = ActiveScreen.MAIN
        _currentTab.value = MainTab.PROFILE
    }

    // Navigation
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _activeScreen = MutableStateFlow(ActiveScreen.MAIN)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
        _activeScreen.value = ActiveScreen.MAIN
    }

    fun navigateTo(screen: ActiveScreen) {
        // Enforce Rule 9: Students must NEVER access admin functions
        if (screen == ActiveScreen.ADMIN_PANEL && !_isOwnerAdmin.value) {
            return
        }
        _activeScreen.value = screen
    }

    fun navigateBack() {
        when (_activeScreen.value) {
            ActiveScreen.MAIN -> {}
            ActiveScreen.TEST_TAKING -> {
                _activeScreen.value = ActiveScreen.MAIN
                _currentTab.value = MainTab.TESTS
            }
            ActiveScreen.TEST_RESULT -> {
                _activeScreen.value = ActiveScreen.MAIN
                _currentTab.value = MainTab.TESTS
            }
            ActiveScreen.COURSE_DETAIL,
            ActiveScreen.VIDEO_PLAYER,
            ActiveScreen.PDF_VIEWER -> {
                _activeScreen.value = ActiveScreen.MAIN
            }
            ActiveScreen.ADMIN_PANEL,
            ActiveScreen.MY_PURCHASES,
            ActiveScreen.MY_RESULTS -> {
                _activeScreen.value = ActiveScreen.MAIN
                _currentTab.value = MainTab.PROFILE
            }
        }
    }

    // Data streams from repository
    val courses: StateFlow<List<Course>> = repository.courses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVideos: StateFlow<List<VideoLesson>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveClasses: StateFlow<List<LiveClass>> = repository.liveClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pdfMaterials: StateFlow<List<PdfMaterial>> = repository.pdfMaterials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val testSeries: StateFlow<List<TestSeries>> = repository.testSeries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val testResults: StateFlow<List<TestResult>> = repository.testResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<PaymentTransaction>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<SubscriptionRecord>> = repository.subscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verifiedRevenue: StateFlow<Long> = repository.transactions.map { list ->
        list.filter { it.status == "SUCCESS" }.fold(0L) { acc, tx -> acc + tx.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val notifications: StateFlow<List<NotificationMessage>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val registeredStudents: StateFlow<List<StudentAccount>> = repository.registeredStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _selectedCategory = MutableStateFlow(CategoryType.ALL)
    val selectedCategory: StateFlow<CategoryType> = _selectedCategory.asStateFlow()

    fun selectCategory(category: CategoryType) {
        _selectedCategory.value = category
    }

    // Selected items for detail views
    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()

    fun openCourseDetails(course: Course) {
        _selectedCourse.value = course
        _activeScreen.value = ActiveScreen.COURSE_DETAIL
    }

    private val _selectedVideo = MutableStateFlow<VideoLesson?>(null)
    val selectedVideo: StateFlow<VideoLesson?> = _selectedVideo.asStateFlow()

    fun openVideoPlayer(video: VideoLesson) {
        _selectedVideo.value = video
        _activeScreen.value = ActiveScreen.VIDEO_PLAYER
    }

    fun updateVideoProgress(videoId: String, progress: Float) {
        viewModelScope.launch {
            repository.updateVideoProgress(videoId, progress, progress >= 0.95f)
        }
    }

    private val _selectedPdf = MutableStateFlow<PdfMaterial?>(null)
    val selectedPdf: StateFlow<PdfMaterial?> = _selectedPdf.asStateFlow()

    fun openPdfViewer(pdf: PdfMaterial) {
        _selectedPdf.value = pdf
        _activeScreen.value = ActiveScreen.PDF_VIEWER
    }

    fun downloadPdf(pdfId: String) {
        viewModelScope.launch {
            repository.markPdfDownloaded(pdfId)
        }
    }

    // Interactive Test Engine State
    private val _activeTest = MutableStateFlow<TestSeries?>(null)
    val activeTest: StateFlow<TestSeries?> = _activeTest.asStateFlow()

    private val _testQuestions = MutableStateFlow<List<Question>>(emptyList())
    val testQuestions: StateFlow<List<Question>> = _testQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val userAnswers: StateFlow<Map<Int, Int>> = _userAnswers.asStateFlow()

    private val _markedForReview = MutableStateFlow<Set<Int>>(emptySet())
    val markedForReview: StateFlow<Set<Int>> = _markedForReview.asStateFlow()

    private val _testTimeRemainingSeconds = MutableStateFlow(600)
    val testTimeRemainingSeconds: StateFlow<Int> = _testTimeRemainingSeconds.asStateFlow()

    private var timerJob: Job? = null

    private val _latestResult = MutableStateFlow<TestResult?>(null)
    val latestResult: StateFlow<TestResult?> = _latestResult.asStateFlow()

    fun startTest(test: TestSeries) {
        _activeTest.value = test
        _currentQuestionIndex.value = 0
        _userAnswers.value = emptyMap()
        _markedForReview.value = emptySet()
        _testTimeRemainingSeconds.value = test.durationMinutes * 60

        viewModelScope.launch {
            repository.getQuestionsForTest(test.id).collect { questions ->
                _testQuestions.value = questions
            }
        }

        startTimer()
        _activeScreen.value = ActiveScreen.TEST_TAKING
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_testTimeRemainingSeconds.value > 0) {
                delay(1000)
                _testTimeRemainingSeconds.value -= 1
            }
            submitTest()
        }
    }

    fun selectOption(questionNumber: Int, optionIndex: Int) {
        _userAnswers.value = _userAnswers.value.toMutableMap().apply {
            put(questionNumber, optionIndex)
        }
    }

    fun clearAnswer(questionNumber: Int) {
        _userAnswers.value = _userAnswers.value.toMutableMap().apply {
            remove(questionNumber)
        }
    }

    fun toggleMarkForReview(questionNumber: Int) {
        _markedForReview.value = if (_markedForReview.value.contains(questionNumber)) {
            _markedForReview.value - questionNumber
        } else {
            _markedForReview.value + questionNumber
        }
    }

    fun goToQuestion(index: Int) {
        if (index in 0 until _testQuestions.value.size) {
            _currentQuestionIndex.value = index
        }
    }

    fun submitTest() {
        timerJob?.cancel()
        val test = _activeTest.value ?: return
        val questions = _testQuestions.value
        val answers = _userAnswers.value

        var correct = 0
        var wrong = 0
        var unattempted = 0
        var totalScore = 0f

        questions.forEach { q ->
            val selected = answers[q.questionNumber]
            if (selected == null) {
                unattempted++
            } else if (selected == q.correctOptionIndex) {
                correct++
                totalScore += q.marks
            } else {
                wrong++
                totalScore -= test.negativeMarks
            }
        }

        val accuracy = if (correct + wrong > 0) ((correct.toFloat() / (correct + wrong)) * 100).toInt() else 0
        val timeSpent = (test.durationMinutes * 60 - _testTimeRemainingSeconds.value) / 60

        val result = TestResult(
            id = "res_${System.currentTimeMillis()}",
            testId = test.id,
            testTitleEn = test.titleEn,
            testTitleHi = test.titleHi,
            score = totalScore.coerceAtLeast(0f),
            totalMarks = test.totalMarks,
            correctCount = correct,
            wrongCount = wrong,
            unattemptedCount = unattempted,
            accuracyPercent = accuracy,
            timeSpentMinutes = timeSpent.coerceAtLeast(1),
            rank = (10..35).random(),
            totalParticipants = test.attemptsCount + 1,
            dateTaken = "Today",
            userAnswers = answers
        )

        _latestResult.value = result

        viewModelScope.launch {
            repository.saveTestResult(
                testId = test.id,
                testTitleEn = test.titleEn,
                testTitleHi = test.titleHi,
                score = result.score,
                totalMarks = test.totalMarks,
                correctCount = correct,
                wrongCount = wrong,
                unattemptedCount = unattempted,
                accuracyPercent = accuracy,
                timeSpentMinutes = result.timeSpentMinutes,
                userAnswers = answers
            )
        }

        _activeScreen.value = ActiveScreen.TEST_RESULT
    }

    fun viewResult(result: TestResult) {
        _latestResult.value = result
        viewModelScope.launch {
            repository.getQuestionsForTest(result.testId).collect { questions ->
                _testQuestions.value = questions
            }
        }
        _activeScreen.value = ActiveScreen.TEST_RESULT
    }

    // Payment Checkout State
    data class CheckoutItem(
        val id: String,
        val titleEn: String,
        val titleHi: String,
        val amount: Int,
        val originalPrice: Int,
        val isCourse: Boolean,
        val purchaseType: PurchaseType = PurchaseType.ONE_TIME,
        val monthlyPrice: Int = 199
    )

    sealed class PaymentFlowState {
        object Idle : PaymentFlowState()
        data class OrderReady(val order: RazorpayOrder, val item: CheckoutItem) : PaymentFlowState()
        data class Verifying(val orderId: String, val paymentMethod: String) : PaymentFlowState()
        data class Success(val order: RazorpayOrder, val paymentId: String, val receiptId: String) : PaymentFlowState()
        data class Failed(val order: RazorpayOrder?, val paymentId: String?, val reason: String) : PaymentFlowState()
        data class Pending(val order: RazorpayOrder, val paymentId: String, val message: String) : PaymentFlowState()
    }

    private val _checkoutItem = MutableStateFlow<CheckoutItem?>(null)
    val checkoutItem: StateFlow<CheckoutItem?> = _checkoutItem.asStateFlow()

    private val _activeOrder = MutableStateFlow<RazorpayOrder?>(null)
    val activeOrder: StateFlow<RazorpayOrder?> = _activeOrder.asStateFlow()

    private val _paymentFlowState = MutableStateFlow<PaymentFlowState>(PaymentFlowState.Idle)
    val paymentFlowState: StateFlow<PaymentFlowState> = _paymentFlowState.asStateFlow()

    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

    private val _paymentSuccessReceipt = MutableStateFlow<String?>(null)
    val paymentSuccessReceipt: StateFlow<String?> = _paymentSuccessReceipt.asStateFlow()

    fun initiateCheckoutForCourse(course: Course, purchaseType: PurchaseType = PurchaseType.ONE_TIME) {
        val amount = if (purchaseType == PurchaseType.MONTHLY_SUBSCRIPTION) course.monthlySubscriptionPrice else course.price
        val item = CheckoutItem(
            id = course.id,
            titleEn = course.titleEn,
            titleHi = course.titleHi,
            amount = amount,
            originalPrice = course.originalPrice,
            isCourse = true,
            purchaseType = purchaseType,
            monthlyPrice = course.monthlySubscriptionPrice
        )
        _checkoutItem.value = item
        val order = repository.createRazorpayOrder(
            itemId = item.id,
            itemTitle = item.titleEn,
            amount = item.amount,
            purchaseType = item.purchaseType
        )
        _activeOrder.value = order
        _paymentFlowState.value = PaymentFlowState.OrderReady(order, item)
    }

    fun selectPurchaseTypeForCourse(purchaseType: PurchaseType) {
        val current = _checkoutItem.value ?: return
        if (!current.isCourse) return
        val newAmount = if (purchaseType == PurchaseType.MONTHLY_SUBSCRIPTION) current.monthlyPrice else current.amount
        val updatedItem = current.copy(purchaseType = purchaseType, amount = newAmount)
        _checkoutItem.value = updatedItem
        val newOrder = repository.createRazorpayOrder(
            itemId = updatedItem.id,
            itemTitle = updatedItem.titleEn,
            amount = updatedItem.amount,
            purchaseType = updatedItem.purchaseType
        )
        _activeOrder.value = newOrder
        _paymentFlowState.value = PaymentFlowState.OrderReady(newOrder, updatedItem)
    }

    fun initiateCheckoutForTest(test: TestSeries) {
        val item = CheckoutItem(
            id = test.id,
            titleEn = test.titleEn,
            titleHi = test.titleHi,
            amount = test.price,
            originalPrice = test.price + 200,
            isCourse = false,
            purchaseType = PurchaseType.ONE_TIME,
            monthlyPrice = 0
        )
        _checkoutItem.value = item
        val order = repository.createRazorpayOrder(
            itemId = item.id,
            itemTitle = item.titleEn,
            amount = item.amount,
            purchaseType = item.purchaseType
        )
        _activeOrder.value = order
        _paymentFlowState.value = PaymentFlowState.OrderReady(order, item)
    }

    fun dismissCheckout() {
        _checkoutItem.value = null
        _activeOrder.value = null
        _paymentSuccessReceipt.value = null
        _isProcessingPayment.value = false
        _paymentFlowState.value = PaymentFlowState.Idle
    }

    fun processPayment(paymentMethod: String) {
        processRazorpayPayment(paymentMethod, PaymentStatus.SUCCESS)
    }

    fun processRazorpayPayment(
        paymentMethod: String,
        simulationOutcome: PaymentStatus = PaymentStatus.SUCCESS
    ) {
        val item = _checkoutItem.value ?: return
        val order = _activeOrder.value ?: repository.createRazorpayOrder(
            itemId = item.id,
            itemTitle = item.titleEn,
            amount = item.amount,
            purchaseType = item.purchaseType
        )

        _isProcessingPayment.value = true
        _paymentFlowState.value = PaymentFlowState.Verifying(order.orderId, paymentMethod)

        viewModelScope.launch {
            // Generates client callback payload (simulates Razorpay Checkout onSuccess)
            val (paymentId, signature) = com.example.data.network.RazorpayBackendService.generateClientPaymentPayload(order.orderId)

            val result = repository.verifyAndSettlePayment(
                order = order,
                paymentId = paymentId,
                signature = signature,
                paymentMethod = paymentMethod,
                isCourse = item.isCourse,
                simulationOutcome = simulationOutcome
            )

            _isProcessingPayment.value = false
            when (result.status) {
                PaymentStatus.SUCCESS -> {
                    _paymentSuccessReceipt.value = result.receiptId
                    _paymentFlowState.value = PaymentFlowState.Success(order, result.paymentId, result.receiptId)
                }
                PaymentStatus.FAILED -> {
                    _paymentFlowState.value = PaymentFlowState.Failed(
                        order = order,
                        paymentId = result.paymentId,
                        reason = result.failureReason ?: "Transaction was declined by issuing bank."
                    )
                }
                PaymentStatus.PENDING -> {
                    _paymentFlowState.value = PaymentFlowState.Pending(
                        order = order,
                        paymentId = result.paymentId,
                        message = "Payment is currently processing by the bank gateway. Your course will unlock as soon as settlement completes."
                    )
                }
            }
        }
    }

    fun retryFailedPayment() {
        val current = _checkoutItem.value ?: return
        val order = repository.createRazorpayOrder(
            itemId = current.id,
            itemTitle = current.titleEn,
            amount = current.amount,
            purchaseType = current.purchaseType
        )
        _activeOrder.value = order
        _paymentFlowState.value = PaymentFlowState.OrderReady(order, current)
    }

    // OWNER ADMIN EXCLUSIVE OPERATIONS (Strictly single owner gated)
    private fun verifyOwnerAuthority() {
        if (!_isOwnerAdmin.value || !OwnerSecurityConfig.isPermanentOwner(_currentAuthUser.value.email)) {
            throw SecurityException("Access Denied: Unprivileged operation rejected.")
        }
    }

    fun adminAddCourse(titleEn: String, titleHi: String, category: CategoryType, examType: ExamType, price: Int, instructor: String) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            val course = Course(
                id = "c_${System.currentTimeMillis()}",
                titleEn = titleEn,
                titleHi = titleHi,
                category = category,
                examType = examType,
                descriptionEn = "Comprehensive syllabus curated by $instructor for top results.",
                descriptionHi = "$instructor द्वारा तैयार किया गया विशेष पाठ्यक्रम।",
                instructor = instructor,
                price = price,
                originalPrice = if (price > 0) price + 500 else 0,
                isFree = price == 0,
                enrolled = false,
                isPublished = true
            )
            repository.addCourse(course)
        }
    }

    fun adminToggleCoursePublish(courseId: String, currentPublished: Boolean) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.toggleCoursePublished(courseId, !currentPublished)
        }
    }

    fun adminUpdateCoursePrice(courseId: String, newPrice: Int) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.updateCoursePrice(courseId, newPrice)
        }
    }

    fun adminDeleteCourse(course: Course) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.deleteCourse(course)
        }
    }

    fun adminScheduleLiveClass(titleEn: String, titleHi: String, subject: String, teacher: String, batch: String, time: String) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            val liveClass = LiveClass(
                id = "live_${System.currentTimeMillis()}",
                courseId = "c_general",
                titleEn = titleEn,
                titleHi = titleHi,
                subject = subject,
                teacherName = teacher,
                batchName = batch,
                scheduledTime = time,
                isLiveNow = false,
                isCompleted = false
            )
            repository.scheduleLiveClass(liveClass)
        }
    }

    fun adminToggleLiveClass(classId: String, isLive: Boolean) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.toggleLiveStatus(classId, isLive)
        }
    }

    fun adminRescheduleLiveClass(classId: String, newTime: String) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.rescheduleLiveClass(classId, newTime)
        }
    }

    fun adminDeleteLiveClass(liveClass: LiveClass) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.deleteLiveClass(liveClass)
        }
    }

    fun adminAddVideo(courseId: String, titleEn: String, titleHi: String, chapter: String, duration: String, isFree: Boolean) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            val video = VideoLesson(
                id = "v_${System.currentTimeMillis()}",
                courseId = courseId,
                titleEn = titleEn,
                titleHi = titleHi,
                chapter = chapter,
                durationText = duration,
                isFreePreview = isFree
            )
            repository.addVideo(video)
        }
    }

    fun adminDeleteVideo(video: VideoLesson) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.deleteVideo(video)
        }
    }

    fun adminAddPdf(courseId: String, titleEn: String, titleHi: String, subject: String, chapter: String, pages: Int, isFree: Boolean) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            val pdf = PdfMaterial(
                id = "pdf_${System.currentTimeMillis()}",
                courseId = courseId,
                titleEn = titleEn,
                titleHi = titleHi,
                subject = subject,
                chapter = chapter,
                pageCount = pages,
                fileSizeMb = 6.5f,
                isFree = isFree
            )
            repository.addPdf(pdf)
        }
    }

    fun adminDeletePdf(pdf: PdfMaterial) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.deletePdf(pdf)
        }
    }

    fun adminCreateTestSeries(titleEn: String, titleHi: String, type: TestType, examType: ExamType, price: Int, duration: Int, negativeMarks: Float) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            val testId = "test_${System.currentTimeMillis()}"
            val test = TestSeries(
                id = testId,
                courseId = "c_general",
                titleEn = titleEn,
                titleHi = titleHi,
                type = type,
                examType = examType,
                questionsCount = 5,
                durationMinutes = duration,
                totalMarks = 10,
                negativeMarks = negativeMarks,
                price = price,
                isFree = price == 0,
                isUnlocked = price == 0,
                isPublished = true
            )

            val questions = (1..5).map { idx ->
                Question(
                    id = "q_${testId}_$idx",
                    testId = testId,
                    questionNumber = idx,
                    questionEn = "Exam Practice Question #$idx for $titleEn?",
                    questionHi = "$titleHi हेतु अभ्यास प्रश्न #$idx क्या है?",
                    optionsEn = listOf("Correct Option A", "Option B", "Option C", "Option D"),
                    optionsHi = listOf("सही विकल्प A", "विकल्प B", "विकल्प C", "विकल्प D"),
                    correctOptionIndex = 0,
                    explanationEn = "Conceptual explanation for Question #$idx.",
                    explanationHi = "प्रश्न #$idx हेतु विस्तृत विश्लेषण।",
                    marks = 2
                )
            }

            repository.addTestSeries(test, questions)
        }
    }

    fun adminToggleTestPublish(testId: String, currentPublished: Boolean) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.toggleTestPublished(testId, !currentPublished)
        }
    }

    fun adminUpdateTestSettings(testId: String, duration: Int, negativeMarks: Float) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.updateTestSettings(testId, duration, negativeMarks)
        }
    }

    fun adminAddQuestionToTest(
        testId: String,
        qNumber: Int,
        questionEn: String,
        questionHi: String,
        optionsEn: List<String>,
        optionsHi: List<String>,
        correctIdx: Int,
        explanationEn: String,
        explanationHi: String,
        marks: Int = 2
    ) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            val q = Question(
                id = "q_${testId}_$qNumber",
                testId = testId,
                questionNumber = qNumber,
                questionEn = questionEn,
                questionHi = questionHi,
                optionsEn = optionsEn,
                optionsHi = optionsHi,
                correctOptionIndex = correctIdx,
                explanationEn = explanationEn,
                explanationHi = explanationHi,
                marks = marks
            )
            repository.addQuestionToTest(q)
        }
    }

    fun adminDeleteTest(test: TestSeries) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.deleteTest(test)
        }
    }

    fun adminToggleStudentBlock(studentId: String, currentBlocked: Boolean) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.setStudentBlocked(studentId, !currentBlocked)
        }
    }

    fun adminBroadcastNotification(titleEn: String, titleHi: String, messageEn: String, messageHi: String, isLive: Boolean) {
        verifyOwnerAuthority()
        viewModelScope.launch {
            repository.sendNotification(titleEn, titleHi, messageEn, messageHi, isLive)
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }
}
