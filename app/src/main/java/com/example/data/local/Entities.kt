package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleHi: String,
    val category: String,
    val examType: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val instructor: String,
    val rating: Float,
    val studentsCount: Int,
    val price: Int,
    val originalPrice: Int,
    val isFree: Boolean,
    val enrolled: Boolean,
    val isPublished: Boolean = true,
    val monthlySubscriptionPrice: Int = 199,
    val totalVideos: Int,
    val totalPdfs: Int,
    val totalTests: Int
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val titleEn: String,
    val titleHi: String,
    val chapter: String,
    val durationText: String,
    val isFreePreview: Boolean,
    val progressPercent: Float,
    val videoUrl: String,
    val isCompleted: Boolean,
    val instructor: String
)

@Entity(tableName = "live_classes")
data class LiveClassEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val titleEn: String,
    val titleHi: String,
    val subject: String,
    val teacherName: String,
    val batchName: String,
    val scheduledTime: String,
    val durationMinutes: Int,
    val isLiveNow: Boolean,
    val isCompleted: Boolean,
    val viewersCount: Int
)

@Entity(tableName = "pdf_materials")
data class PdfEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val titleEn: String,
    val titleHi: String,
    val subject: String,
    val chapter: String,
    val pageCount: Int,
    val fileSizeMb: Float,
    val isFree: Boolean,
    val isDownloaded: Boolean,
    val previewSummaryEn: String,
    val previewSummaryHi: String
)

@Entity(tableName = "test_series")
data class TestSeriesEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val titleEn: String,
    val titleHi: String,
    val type: String,
    val examType: String,
    val questionsCount: Int,
    val durationMinutes: Int,
    val totalMarks: Int,
    val negativeMarks: Float,
    val price: Int,
    val isFree: Boolean,
    val isUnlocked: Boolean,
    val isPublished: Boolean = true,
    val attemptsCount: Int
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val questionNumber: Int,
    val questionEn: String,
    val questionHi: String,
    val optionAEn: String,
    val optionBEn: String,
    val optionCEn: String,
    val optionDEn: String,
    val optionAHi: String,
    val optionBHi: String,
    val optionCHi: String,
    val optionDHi: String,
    val correctOptionIndex: Int,
    val explanationEn: String,
    val explanationHi: String,
    val marks: Int
)

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey val id: String,
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
    val rank: Int,
    val totalParticipants: Int,
    val dateTaken: String
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val transactionRef: String,
    val razorpayPaymentId: String = "",
    val razorpayOrderId: String = "",
    val itemTitle: String,
    val amount: Int,
    val paymentMethod: String,
    val status: String,
    val dateText: String,
    val receiptId: String,
    val failureReason: String? = null,
    val purchaseType: String = "ONE_TIME"
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val courseTitle: String,
    val studentId: String,
    val startDate: String,
    val expiryDate: String,
    val status: String, // "ACTIVE", "EXPIRED", "CANCELLED"
    val monthlyPrice: Int
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleHi: String,
    val messageEn: String,
    val messageHi: String,
    val timeAgo: String,
    val isLiveAlert: Boolean,
    val isRead: Boolean
)

@Entity(tableName = "students")
data class StudentAccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val mobileNumber: String,
    val targetExam: String,
    val joinedDate: String,
    val isBlocked: Boolean = false,
    val enrolledCoursesCount: Int = 1,
    val testAttemptsCount: Int = 2
)
