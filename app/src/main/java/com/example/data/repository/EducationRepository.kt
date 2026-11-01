package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EducationRepository(private val database: AppDatabase) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        val currentCourses = database.courseDao().getAllCourses().first()
        if (currentCourses.isEmpty()) {
            database.courseDao().insertAll(SeedData.initialCourses)
            database.videoDao().insertAll(SeedData.initialVideos)
            database.liveClassDao().insertAll(SeedData.initialLiveClasses)
            database.pdfDao().insertAll(SeedData.initialPdfs)
            database.testDao().insertAllTests(SeedData.initialTests)
            database.testDao().insertAllQuestions(SeedData.initialQuestions)
            database.transactionDao().insertAll(SeedData.initialTransactions)
            SeedData.initialSubscriptions.forEach { database.subscriptionDao().insert(it) }
            database.notificationDao().insertAll(SeedData.initialNotifications)
            database.studentDao().insertAll(SeedData.initialStudents)
            SeedData.initialResults.forEach { database.testDao().insertResult(it) }
        }
    }

    // Subscriptions
    val subscriptions: Flow<List<SubscriptionRecord>> = database.subscriptionDao().getAllSubscriptions().map { entities ->
        entities.map { it.toDomain() }
    }

    // Courses
    val courses: Flow<List<Course>> = database.courseDao().getAllCourses().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun enrollInCourse(courseId: String) {
        database.courseDao().enrollInCourse(courseId)
    }

    suspend fun addCourse(course: Course) {
        database.courseDao().insert(course.toEntity())
    }

    suspend fun updateCourse(course: Course) {
        database.courseDao().update(course.toEntity())
    }

    suspend fun deleteCourse(course: Course) {
        database.courseDao().delete(course.toEntity())
    }

    suspend fun toggleCoursePublished(courseId: String, isPublished: Boolean) {
        database.courseDao().setPublishedStatus(courseId, isPublished)
    }

    suspend fun updateCoursePrice(courseId: String, price: Int) {
        database.courseDao().updatePrice(courseId, price)
    }

    // Videos
    fun getVideosForCourse(courseId: String): Flow<List<VideoLesson>> {
        return database.videoDao().getVideosForCourse(courseId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    val allVideos: Flow<List<VideoLesson>> = database.videoDao().getAllVideos().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun addVideo(video: VideoLesson) {
        database.videoDao().insert(video.toEntity())
    }

    suspend fun deleteVideo(video: VideoLesson) {
        database.videoDao().delete(video.toEntity())
    }

    suspend fun updateVideoProgress(videoId: String, progress: Float, isCompleted: Boolean) {
        database.videoDao().updateProgress(videoId, progress, isCompleted)
    }

    // Live Classes
    val liveClasses: Flow<List<LiveClass>> = database.liveClassDao().getAllLiveClasses().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun scheduleLiveClass(liveClass: LiveClass) {
        database.liveClassDao().insert(liveClass.toEntity())
    }

    suspend fun toggleLiveStatus(classId: String, isLive: Boolean) {
        database.liveClassDao().setLiveStatus(classId, isLive)
    }

    suspend fun rescheduleLiveClass(classId: String, newTime: String) {
        database.liveClassDao().rescheduleClass(classId, newTime)
    }

    suspend fun deleteLiveClass(liveClass: LiveClass) {
        database.liveClassDao().delete(liveClass.toEntity())
    }

    // PDF Materials
    val pdfMaterials: Flow<List<PdfMaterial>> = database.pdfDao().getAllPdfs().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun addPdf(pdf: PdfMaterial) {
        database.pdfDao().insert(pdf.toEntity())
    }

    suspend fun markPdfDownloaded(pdfId: String) {
        database.pdfDao().markDownloaded(pdfId)
    }

    suspend fun deletePdf(pdf: PdfMaterial) {
        database.pdfDao().delete(pdf.toEntity())
    }

    // Tests & Questions
    val testSeries: Flow<List<TestSeries>> = database.testDao().getAllTests().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getQuestionsForTest(testId: String): Flow<List<Question>> {
        return database.testDao().getQuestionsForTest(testId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun unlockTest(testId: String) {
        database.testDao().unlockTest(testId)
    }

    suspend fun toggleTestPublished(testId: String, isPublished: Boolean) {
        database.testDao().setTestPublishedStatus(testId, isPublished)
    }

    suspend fun updateTestSettings(testId: String, duration: Int, negativeMarks: Float) {
        database.testDao().updateTestSettings(testId, duration, negativeMarks)
    }

    suspend fun addTestSeries(test: TestSeries, questions: List<Question>) {
        database.testDao().insertTest(test.toEntity())
        questions.forEach { database.testDao().insertQuestion(it.toEntity()) }
    }

    suspend fun addQuestionToTest(question: Question) {
        database.testDao().insertQuestion(question.toEntity())
    }

    suspend fun deleteTest(test: TestSeries) {
        database.testDao().deleteTest(test.toEntity())
    }

    // Results
    val testResults: Flow<List<TestResult>> = database.testDao().getAllTestResults().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun saveTestResult(
        testId: String,
        testTitleEn: String,
        testTitleHi: String,
        score: Float,
        totalMarks: Int,
        correctCount: Int,
        wrongCount: Int,
        unattemptedCount: Int,
        accuracyPercent: Int,
        timeSpentMinutes: Int,
        userAnswers: Map<Int, Int>
    ) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateTaken = dateFormat.format(Date())
        val entity = TestResultEntity(
            id = "res_${System.currentTimeMillis()}",
            testId = testId,
            testTitleEn = testTitleEn,
            testTitleHi = testTitleHi,
            score = score,
            totalMarks = totalMarks,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unattemptedCount = unattemptedCount,
            accuracyPercent = accuracyPercent,
            timeSpentMinutes = timeSpentMinutes,
            rank = (12..45).random(),
            totalParticipants = (1200..3500).random(),
            dateTaken = dateTaken
        )
        database.testDao().insertResult(entity)
    }

    // Students Management (Owner Exclusive)
    val registeredStudents: Flow<List<StudentAccount>> = database.studentDao().getAllStudents().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun setStudentBlocked(studentId: String, isBlocked: Boolean) {
        database.studentDao().setBlockedStatus(studentId, isBlocked)
    }

    // Transactions
    val transactions: Flow<List<PaymentTransaction>> = database.transactionDao().getAllTransactions().map { entities ->
        entities.map { it.toDomain() }
    }

    fun createRazorpayOrder(
        itemId: String,
        itemTitle: String,
        amount: Int,
        purchaseType: PurchaseType
    ): RazorpayOrder {
        return com.example.data.network.RazorpayBackendService.createOrder(
            itemId = itemId,
            itemTitle = itemTitle,
            amountInRupees = amount,
            purchaseType = purchaseType
        )
    }

    suspend fun verifyAndSettlePayment(
        order: RazorpayOrder,
        paymentId: String,
        signature: String,
        paymentMethod: String,
        isCourse: Boolean,
        simulationOutcome: PaymentStatus = PaymentStatus.SUCCESS
    ): com.example.data.network.RazorpayBackendService.ServerPaymentVerificationResult {
        val verification = com.example.data.network.RazorpayBackendService.verifyPaymentSignatureOnServer(
            orderId = order.orderId,
            paymentId = paymentId,
            clientProvidedSignature = signature,
            receiptId = order.receiptId,
            simulationOutcome = simulationOutcome
        )

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateText = dateFormat.format(Date())

        val transactionEntity = TransactionEntity(
            id = "tx_${System.currentTimeMillis()}",
            transactionRef = "TXN_RZP_${(10000..99999).random()}",
            razorpayPaymentId = paymentId,
            razorpayOrderId = order.orderId,
            itemTitle = order.itemTitle,
            amount = (order.amountInPaise / 100).toInt(),
            paymentMethod = paymentMethod,
            status = verification.status.name,
            dateText = dateText,
            receiptId = order.receiptId,
            failureReason = verification.failureReason,
            purchaseType = order.purchaseType.name
        )
        database.transactionDao().insert(transactionEntity)

        // CONTENT UNLOCK: Gated strictly on server verification SUCCESS
        if (verification.isVerified && verification.status == PaymentStatus.SUCCESS) {
            if (isCourse) {
                database.courseDao().enrollInCourse(order.itemId)
                if (order.purchaseType == PurchaseType.MONTHLY_SUBSCRIPTION) {
                    val cal = Calendar.getInstance()
                    val startDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)
                    cal.add(Calendar.DAY_OF_MONTH, 30)
                    val expiryDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)

                    val subEntity = SubscriptionEntity(
                        id = "sub_${System.currentTimeMillis()}",
                        courseId = order.itemId,
                        courseTitle = order.itemTitle,
                        studentId = "std_me",
                        startDate = startDate,
                        expiryDate = expiryDate,
                        status = "ACTIVE",
                        monthlyPrice = (order.amountInPaise / 100).toInt()
                    )
                    database.subscriptionDao().insert(subEntity)
                }
            } else {
                database.testDao().unlockTest(order.itemId)
            }

            val notif = NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                titleEn = "Payment Verified: ${order.itemTitle}",
                titleHi = "भुगतान सत्यापित: ${order.itemTitle}",
                messageEn = "Payment of ₹${order.amountInPaise / 100} verified via Razorpay ($paymentMethod). Access has been unlocked!",
                messageHi = "रेज़रपे ($paymentMethod) द्वारा ₹${order.amountInPaise / 100} का भुगतान सत्यापित। सामग्री अनलॉक कर दी गई है!",
                timeAgo = "Just now",
                isLiveAlert = false,
                isRead = false
            )
            database.notificationDao().insert(notif)
        }

        return verification
    }

    // Notifications
    val notifications: Flow<List<NotificationMessage>> = database.notificationDao().getAllNotifications().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun sendNotification(titleEn: String, titleHi: String, bodyEn: String, bodyHi: String, isLive: Boolean) {
        val entity = NotificationEntity(
            id = "notif_${System.currentTimeMillis()}",
            titleEn = titleEn,
            titleHi = titleHi,
            messageEn = bodyEn,
            messageHi = bodyHi,
            timeAgo = "Just now",
            isLiveAlert = isLive,
            isRead = false
        )
        database.notificationDao().insert(entity)
    }

    suspend fun markNotificationRead(id: String) {
        database.notificationDao().markAsRead(id)
    }

    companion object {
        @Volatile
        private var INSTANCE: EducationRepository? = null

        fun getInstance(context: Context): EducationRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val instance = EducationRepository(db)
                INSTANCE = instance
                instance
            }
        }
    }
}

// Entity to Domain mappers
fun CourseEntity.toDomain() = Course(
    id = id,
    titleEn = titleEn,
    titleHi = titleHi,
    category = runCatching { CategoryType.valueOf(category) }.getOrDefault(CategoryType.COMPETITIVE),
    examType = runCatching { ExamType.valueOf(examType) }.getOrDefault(ExamType.ALL),
    descriptionEn = descriptionEn,
    descriptionHi = descriptionHi,
    instructor = instructor,
    rating = rating,
    studentsCount = studentsCount,
    price = price,
    originalPrice = originalPrice,
    isFree = isFree,
    enrolled = enrolled,
    isPublished = isPublished,
    monthlySubscriptionPrice = monthlySubscriptionPrice,
    totalVideos = totalVideos,
    totalPdfs = totalPdfs,
    totalTests = totalTests
)

fun Course.toEntity() = CourseEntity(
    id = id,
    titleEn = titleEn,
    titleHi = titleHi,
    category = category.name,
    examType = examType.name,
    descriptionEn = descriptionEn,
    descriptionHi = descriptionHi,
    instructor = instructor,
    rating = rating,
    studentsCount = studentsCount,
    price = price,
    originalPrice = originalPrice,
    isFree = isFree,
    enrolled = enrolled,
    isPublished = isPublished,
    monthlySubscriptionPrice = monthlySubscriptionPrice,
    totalVideos = totalVideos,
    totalPdfs = totalPdfs,
    totalTests = totalTests
)

fun VideoEntity.toDomain() = VideoLesson(
    id = id,
    courseId = courseId,
    titleEn = titleEn,
    titleHi = titleHi,
    chapter = chapter,
    durationText = durationText,
    isFreePreview = isFreePreview,
    progressPercent = progressPercent,
    videoUrl = videoUrl,
    isCompleted = isCompleted,
    instructor = instructor
)

fun VideoLesson.toEntity() = VideoEntity(
    id = id,
    courseId = courseId,
    titleEn = titleEn,
    titleHi = titleHi,
    chapter = chapter,
    durationText = durationText,
    isFreePreview = isFreePreview,
    progressPercent = progressPercent,
    videoUrl = videoUrl,
    isCompleted = isCompleted,
    instructor = instructor
)

fun LiveClassEntity.toDomain() = LiveClass(
    id = id,
    courseId = courseId,
    titleEn = titleEn,
    titleHi = titleHi,
    subject = subject,
    teacherName = teacherName,
    batchName = batchName,
    scheduledTime = scheduledTime,
    durationMinutes = durationMinutes,
    isLiveNow = isLiveNow,
    isCompleted = isCompleted,
    viewersCount = viewersCount
)

fun LiveClass.toEntity() = LiveClassEntity(
    id = id,
    courseId = courseId,
    titleEn = titleEn,
    titleHi = titleHi,
    subject = subject,
    teacherName = teacherName,
    batchName = batchName,
    scheduledTime = scheduledTime,
    durationMinutes = durationMinutes,
    isLiveNow = isLiveNow,
    isCompleted = isCompleted,
    viewersCount = viewersCount
)

fun PdfEntity.toDomain() = PdfMaterial(
    id = id,
    courseId = courseId,
    titleEn = titleEn,
    titleHi = titleHi,
    subject = subject,
    chapter = chapter,
    pageCount = pageCount,
    fileSizeMb = fileSizeMb,
    isFree = isFree,
    isDownloaded = isDownloaded,
    previewSummaryEn = previewSummaryEn,
    previewSummaryHi = previewSummaryHi
)

fun PdfMaterial.toEntity() = PdfEntity(
    id = id,
    courseId = courseId,
    titleEn = titleEn,
    titleHi = titleHi,
    subject = subject,
    chapter = chapter,
    pageCount = pageCount,
    fileSizeMb = fileSizeMb,
    isFree = isFree,
    isDownloaded = isDownloaded,
    previewSummaryEn = previewSummaryEn,
    previewSummaryHi = previewSummaryHi
)

fun TestSeriesEntity.toDomain() = TestSeries(
    id = id,
    courseId = courseId,
    titleEn = titleEn,
    titleHi = titleHi,
    type = runCatching { TestType.valueOf(type) }.getOrDefault(TestType.FREE),
    examType = runCatching { ExamType.valueOf(examType) }.getOrDefault(ExamType.ALL),
    questionsCount = questionsCount,
    durationMinutes = durationMinutes,
    totalMarks = totalMarks,
    negativeMarks = negativeMarks,
    price = price,
    isFree = isFree,
    isUnlocked = isUnlocked,
    isPublished = isPublished,
    attemptsCount = attemptsCount
)

fun TestSeries.toEntity() = TestSeriesEntity(
    id = id,
    courseId = courseId,
    titleEn = titleEn,
    titleHi = titleHi,
    type = type.name,
    examType = examType.name,
    questionsCount = questionsCount,
    durationMinutes = durationMinutes,
    totalMarks = totalMarks,
    negativeMarks = negativeMarks,
    price = price,
    isFree = isFree,
    isUnlocked = isUnlocked,
    isPublished = isPublished,
    attemptsCount = attemptsCount
)

fun QuestionEntity.toDomain() = Question(
    id = id,
    testId = testId,
    questionNumber = questionNumber,
    questionEn = questionEn,
    questionHi = questionHi,
    optionsEn = listOf(optionAEn, optionBEn, optionCEn, optionDEn),
    optionsHi = listOf(optionAHi, optionBHi, optionCHi, optionDHi),
    correctOptionIndex = correctOptionIndex,
    explanationEn = explanationEn,
    explanationHi = explanationHi,
    marks = marks
)

fun Question.toEntity() = QuestionEntity(
    id = id,
    testId = testId,
    questionNumber = questionNumber,
    questionEn = questionEn,
    questionHi = questionHi,
    optionAEn = optionsEn.getOrElse(0) { "" },
    optionBEn = optionsEn.getOrElse(1) { "" },
    optionCEn = optionsEn.getOrElse(2) { "" },
    optionDEn = optionsEn.getOrElse(3) { "" },
    optionAHi = optionsHi.getOrElse(0) { "" },
    optionBHi = optionsHi.getOrElse(1) { "" },
    optionCHi = optionsHi.getOrElse(2) { "" },
    optionDHi = optionsHi.getOrElse(3) { "" },
    correctOptionIndex = correctOptionIndex,
    explanationEn = explanationEn,
    explanationHi = explanationHi,
    marks = marks
)

fun TestResultEntity.toDomain() = TestResult(
    id = id,
    testId = testId,
    testTitleEn = testTitleEn,
    testTitleHi = testTitleHi,
    score = score,
    totalMarks = totalMarks,
    correctCount = correctCount,
    wrongCount = wrongCount,
    unattemptedCount = unattemptedCount,
    accuracyPercent = accuracyPercent,
    timeSpentMinutes = timeSpentMinutes,
    rank = rank,
    totalParticipants = totalParticipants,
    dateTaken = dateTaken
)

fun TransactionEntity.toDomain() = PaymentTransaction(
    id = id,
    transactionRef = transactionRef,
    razorpayPaymentId = razorpayPaymentId,
    razorpayOrderId = razorpayOrderId,
    itemTitle = itemTitle,
    amount = amount,
    paymentMethod = paymentMethod,
    status = status,
    dateText = dateText,
    receiptId = receiptId,
    failureReason = failureReason,
    purchaseType = purchaseType
)

fun SubscriptionEntity.toDomain() = SubscriptionRecord(
    id = id,
    courseId = courseId,
    courseTitle = courseTitle,
    studentId = studentId,
    startDate = startDate,
    expiryDate = expiryDate,
    status = status,
    monthlyPrice = monthlyPrice
)

fun SubscriptionRecord.toEntity() = SubscriptionEntity(
    id = id,
    courseId = courseId,
    courseTitle = courseTitle,
    studentId = studentId,
    startDate = startDate,
    expiryDate = expiryDate,
    status = status,
    monthlyPrice = monthlyPrice
)

fun NotificationEntity.toDomain() = NotificationMessage(
    id = id,
    titleEn = titleEn,
    titleHi = titleHi,
    messageEn = messageEn,
    messageHi = messageHi,
    timeAgo = timeAgo,
    isLiveAlert = isLiveAlert,
    isRead = isRead
)

fun StudentAccountEntity.toDomain() = StudentAccount(
    id = id,
    name = name,
    email = email,
    mobileNumber = mobileNumber,
    targetExam = targetExam,
    joinedDate = joinedDate,
    isBlocked = isBlocked,
    enrolledCoursesCount = enrolledCoursesCount,
    testAttemptsCount = testAttemptsCount
)
