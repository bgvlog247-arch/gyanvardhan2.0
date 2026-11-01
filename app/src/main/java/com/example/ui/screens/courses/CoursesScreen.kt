package com.example.ui.screens.courses

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CategoryType
import com.example.data.models.Course
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*

@Composable
fun CoursesScreen(
    currentLanguage: AppLanguage,
    courses: List<Course>,
    selectedCategory: CategoryType,
    searchQuery: String,
    onCategorySelected: (CategoryType) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onCourseClick: (Course) -> Unit,
    onEnrollClick: (Course) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterOnlyFree by remember { mutableStateOf(false) }

    val filteredCourses = courses.filter { course ->
        val matchesCategory = selectedCategory == CategoryType.ALL || course.category == selectedCategory
        val matchesFree = !filterOnlyFree || course.isFree
        val matchesSearch = searchQuery.isBlank() ||
                course.titleEn.contains(searchQuery, ignoreCase = true) ||
                course.titleHi.contains(searchQuery, ignoreCase = true) ||
                course.instructor.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesFree && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("courses_screen")
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("course_search_bar"),
            placeholder = {
                Text(
                    text = AppStrings.get("search_hint", currentLanguage),
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == CategoryType.ALL,
                    onClick = { onCategorySelected(CategoryType.ALL) },
                    label = { Text(AppStrings.get("all", currentLanguage), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedCategory == CategoryType.COMPETITIVE,
                    onClick = { onCategorySelected(CategoryType.COMPETITIVE) },
                    label = { Text(AppStrings.get("competitive", currentLanguage), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedCategory == CategoryType.SCHOOL,
                    onClick = { onCategorySelected(CategoryType.SCHOOL) },
                    label = { Text(AppStrings.get("school_edu", currentLanguage), fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = filterOnlyFree,
                    onClick = { filterOnlyFree = !filterOnlyFree },
                    label = { Text(AppStrings.get("free", currentLanguage) + " ⭐", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldSuccess,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Course List
        if (filteredCourses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "कोई कोर्स नहीं मिला" else "No courses found",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCourses) { course ->
                    CourseListItem(
                        course = course,
                        currentLanguage = currentLanguage,
                        onClick = { onCourseClick(course) },
                        onEnrollClick = { onEnrollClick(course) }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseListItem(
    course: Course,
    currentLanguage: AppLanguage,
    onClick: () -> Unit,
    onEnrollClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("course_item_${course.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category & Rating header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (course.category == CategoryType.COMPETITIVE) NavyDark else IndigoAccent
                ) {
                    Text(
                        text = if (course.category == CategoryType.COMPETITIVE) "COMPETITIVE EXAM" else "BOARD EXAM",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${course.rating}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = if (currentLanguage == AppLanguage.HINDI) course.titleHi else course.titleEn,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Instructor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = course.instructor,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats row (Videos, PDFs, Tests)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "🎥 ${course.totalVideos} ${if (currentLanguage == AppLanguage.HINDI) "वीडियो" else "Videos"}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "📄 ${course.totalPdfs} ${if (currentLanguage == AppLanguage.HINDI) "नोट्स" else "PDFs"}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "📝 ${course.totalTests} ${if (currentLanguage == AppLanguage.HINDI) "टेस्ट" else "Tests"}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Price & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (course.isFree) {
                        Text(
                            text = AppStrings.get("free", currentLanguage),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldSuccess
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${course.price}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (course.originalPrice > course.price) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "₹${course.originalPrice}",
                                    fontSize = 12.sp,
                                    textDecoration = TextDecoration.LineThrough,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (course.enrolled) {
                    FilledTonalButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = EmeraldSuccess.copy(alpha = 0.15f),
                            contentColor = EmeraldSuccess
                        ),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = AppStrings.get("enrolled", currentLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onEnrollClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (course.isFree) EmeraldSuccess else SaffronGold
                        ),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(
                            text = if (course.isFree) AppStrings.get("enroll_now", currentLanguage) else AppStrings.get("buy_now", currentLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    }
                }
            }
        }
    }
}
