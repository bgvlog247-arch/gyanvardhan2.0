package com.example.data.models

enum class CategoryType {
    ALL,
    SCHOOL,
    COMPETITIVE
}

enum class ExamType {
    ALL,
    SSC_GD,
    RAILWAY_D,
    CLASS_10,
    CLASS_12,
    GENERAL_STUDIES,
    CURRENT_AFFAIRS
}

enum class UserRole {
    STUDENT,
    OWNER_ADMIN
}

enum class PurchaseType {
    ONE_TIME,
    MONTHLY_SUBSCRIPTION
}

enum class PaymentStatus {
    SUCCESS,
    PENDING,
    FAILED
}

object OwnerSecurityConfig {
    const val OWNER_EMAIL = "bgvlog247@gmail.com"
    const val OWNER_NAME = "Gyanvardhan Owner Admin"
    const val OWNER_ID = "owner_permanent_master_01"

    fun isPermanentOwner(email: String): Boolean {
        return email.trim().equals(OWNER_EMAIL, ignoreCase = true)
    }
}

data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val mobileNumber: String,
    val role: UserRole,
    val authToken: String,
    val targetExam: String = "SSC GD & Railway Group D",
    val isBlocked: Boolean = false
)

data class Course(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val category: CategoryType,
    val examType: ExamType,
    val descriptionEn: String,
    val descriptionHi: String,
    val instructor: String,
    val rating: Float = 4.8f,
    val studentsCount: Int = 1250,
    val price: Int = 0,
    val originalPrice: Int = 0,
    val isFree: Boolean = true,
    val enrolled: Boolean = false,
    val isPublished: Boolean = true,
    val monthlySubscriptionPrice: Int = 199,
    val totalVideos: Int = 24,
    val totalPdfs: Int = 12,
    val totalTests: Int = 6
)

data class VideoLesson(
    val id: String,
    val courseId: String,
    val titleEn: String,
    val titleHi: String,
    val chapter: String,
    val durationText: String,
    val isFreePreview: Boolean = false,
    val progressPercent: Float = 0f,
    val videoUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    val isCompleted: Boolean = false,
    val instructor: String = "Prof. R. K. Sharma"
)

data class LiveClass(
    val id: String,
    val courseId: String,
    val titleEn: String,
    val titleHi: String,
    val subject: String,
    val teacherName: String,
    val batchName: String,
    val scheduledTime: String,
    val durationMinutes: Int = 60,
    val isLiveNow: Boolean = false,
    val isCompleted: Boolean = false,
    val viewersCount: Int = 420
)

data class PdfMaterial(
    val id: String,
    val courseId: String,
    val titleEn: String,
    val titleHi: String,
    val subject: String,
    val chapter: String,
    val pageCount: Int,
    val fileSizeMb: Float,
    val isFree: Boolean = true,
    val isDownloaded: Boolean = false,
    val previewSummaryEn: String = "",
    val previewSummaryHi: String = ""
)

enum class TestType {
    FREE,
    PAID,
    DAILY_QUIZ,
    MOCK,
    PREVIOUS_YEAR
}

data class TestSeries(
    val id: String,
    val courseId: String,
    val titleEn: String,
    val titleHi: String,
    val type: TestType,
    val examType: ExamType,
    val questionsCount: Int,
    val durationMinutes: Int,
    val totalMarks: Int,
    val negativeMarks: Float = 0.5f,
    val price: Int = 0,
    val isFree: Boolean = true,
    val isUnlocked: Boolean = true,
    val isPublished: Boolean = true,
    val attemptsCount: Int = 890
)

data class Question(
    val id: String,
    val testId: String,
    val questionNumber: Int,
    val questionEn: String,
    val questionHi: String,
    val optionsEn: List<String>,
    val optionsHi: List<String>,
    val correctOptionIndex: Int,
    val explanationEn: String,
    val explanationHi: String,
    val marks: Int = 2
)

data class TestResult(
    val id: String,
    val testId: String,
    val testTitleEn: String,
    val testTitleHi: String,
    val score: Float,
    val totalMarks: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unattemptedCount: Int,
    val accuracyPercent: Int,
    val timeSpentMinutes: Int,
    val rank: Int = 14,
    val totalParticipants: Int = 340,
    val dateTaken: String,
    val userAnswers: Map<Int, Int> = emptyMap()
)

data class RazorpayOrder(
    val orderId: String,
    val amountInPaise: Long,
    val currency: String = "INR",
    val receiptId: String,
    val itemId: String,
    val itemTitle: String,
    val purchaseType: PurchaseType,
    val keyId: String = "rzp_test_gyanvardhan2026"
)

data class PaymentTransaction(
    val id: String,
    val transactionRef: String,
    val razorpayPaymentId: String = "",
    val razorpayOrderId: String = "",
    val itemTitle: String,
    val amount: Int,
    val paymentMethod: String,
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val dateText: String,
    val receiptId: String,
    val failureReason: String? = null,
    val purchaseType: String = "ONE_TIME"
)

data class SubscriptionRecord(
    val id: String,
    val courseId: String,
    val courseTitle: String,
    val studentId: String,
    val startDate: String,
    val expiryDate: String,
    val status: String, // "ACTIVE", "EXPIRED", "CANCELLED"
    val monthlyPrice: Int
)

data class NotificationMessage(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val messageEn: String,
    val messageHi: String,
    val timeAgo: String,
    val isLiveAlert: Boolean = false,
    val isRead: Boolean = false
)

data class StudentAccount(
    val id: String,
    val name: String,
    val email: String,
    val mobileNumber: String,
    val targetExam: String,
    val joinedDate: String,
    val isBlocked: Boolean = false,
    val enrolledCoursesCount: Int = 1,
    val testAttemptsCount: Int = 2
)

data class AdminOverviewStats(
    val totalStudents: Int = 4850,
    val activeStudentsToday: Int = 1240,
    val totalCourses: Int = 18,
    val totalVideos: Int = 142,
    val totalPdfs: Int = 85,
    val totalFreeTests: Int = 24,
    val totalPaidTests: Int = 38,
    val upcomingLiveClasses: Int = 6,
    val totalRevenue: Long = 184500L,
    val totalTransactions: Int = 312
)
