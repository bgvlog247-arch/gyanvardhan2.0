package com.example.data.local

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: CourseEntity)

    @Update
    suspend fun update(course: CourseEntity)

    @Delete
    suspend fun delete(course: CourseEntity)

    @Query("UPDATE courses SET enrolled = 1 WHERE id = :courseId")
    suspend fun enrollInCourse(courseId: String)

    @Query("UPDATE courses SET isPublished = :published WHERE id = :courseId")
    suspend fun setPublishedStatus(courseId: String, published: Boolean)

    @Query("UPDATE courses SET price = :price, isFree = (:price == 0) WHERE id = :courseId")
    suspend fun updatePrice(courseId: String, price: Int)
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos WHERE courseId = :courseId")
    fun getVideosForCourse(courseId: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<VideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: VideoEntity)

    @Delete
    suspend fun delete(video: VideoEntity)

    @Query("UPDATE videos SET progressPercent = :progress, isCompleted = :completed WHERE id = :videoId")
    suspend fun updateProgress(videoId: String, progress: Float, completed: Boolean)
}

@Dao
interface LiveClassDao {
    @Query("SELECT * FROM live_classes")
    fun getAllLiveClasses(): Flow<List<LiveClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(classes: List<LiveClassEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(liveClass: LiveClassEntity)

    @Update
    suspend fun update(liveClass: LiveClassEntity)

    @Delete
    suspend fun delete(liveClass: LiveClassEntity)

    @Query("UPDATE live_classes SET isLiveNow = :isLive WHERE id = :classId")
    suspend fun setLiveStatus(classId: String, isLive: Boolean)

    @Query("UPDATE live_classes SET scheduledTime = :newTime WHERE id = :classId")
    suspend fun rescheduleClass(classId: String, newTime: String)
}

@Dao
interface PdfDao {
    @Query("SELECT * FROM pdf_materials")
    fun getAllPdfs(): Flow<List<PdfEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pdfs: List<PdfEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pdf: PdfEntity)

    @Update
    suspend fun update(pdf: PdfEntity)

    @Delete
    suspend fun delete(pdf: PdfEntity)

    @Query("UPDATE pdf_materials SET isDownloaded = 1 WHERE id = :pdfId")
    suspend fun markDownloaded(pdfId: String)
}

@Dao
interface TestDao {
    @Query("SELECT * FROM test_series")
    fun getAllTests(): Flow<List<TestSeriesEntity>>

    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY questionNumber ASC")
    fun getQuestionsForTest(testId: String): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTests(tests: List<TestSeriesEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestSeriesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Update
    suspend fun updateTest(test: TestSeriesEntity)

    @Delete
    suspend fun deleteTest(test: TestSeriesEntity)

    @Query("UPDATE test_series SET isUnlocked = 1 WHERE id = :testId")
    suspend fun unlockTest(testId: String)

    @Query("UPDATE test_series SET isPublished = :published WHERE id = :testId")
    suspend fun setTestPublishedStatus(testId: String, published: Boolean)

    @Query("UPDATE test_series SET durationMinutes = :duration, negativeMarks = :negative WHERE id = :testId")
    suspend fun updateTestSettings(testId: String, duration: Int, negative: Float)

    @Query("SELECT * FROM test_results ORDER BY dateTaken DESC")
    fun getAllTestResults(): Flow<List<TestResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TestResultEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE studentId = :studentId")
    fun getSubscriptionsForStudent(studentId: String): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: SubscriptionEntity)

    @Query("UPDATE subscriptions SET status = :status WHERE id = :subscriptionId")
    suspend fun updateStatus(subscriptionId: String, status: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<StudentAccountEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentAccountEntity)

    @Query("UPDATE students SET isBlocked = :blocked WHERE id = :studentId")
    suspend fun setBlockedStatus(studentId: String, blocked: Boolean)
}

@Database(
    entities = [
        CourseEntity::class,
        VideoEntity::class,
        LiveClassEntity::class,
        PdfEntity::class,
        TestSeriesEntity::class,
        QuestionEntity::class,
        TestResultEntity::class,
        TransactionEntity::class,
        SubscriptionEntity::class,
        NotificationEntity::class,
        StudentAccountEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun videoDao(): VideoDao
    abstract fun liveClassDao(): LiveClassDao
    abstract fun pdfDao(): PdfDao
    abstract fun testDao(): TestDao
    abstract fun transactionDao(): TransactionDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gyanvardhan_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
