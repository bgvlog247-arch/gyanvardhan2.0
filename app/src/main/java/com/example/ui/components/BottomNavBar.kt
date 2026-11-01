package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SaffronGold
import com.example.ui.viewmodels.MainTab

@Composable
fun AppBottomNavBar(
    currentTab: MainTab,
    currentLanguage: AppLanguage,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Home
        NavigationBarItem(
            selected = currentTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = AppStrings.get("home", currentLanguage)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("home", currentLanguage),
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDark,
                indicatorColor = SaffronGold,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Courses
        NavigationBarItem(
            selected = currentTab == MainTab.COURSES,
            onClick = { onTabSelected(MainTab.COURSES) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.COURSES) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                    contentDescription = AppStrings.get("courses", currentLanguage)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("courses", currentLanguage),
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDark,
                indicatorColor = SaffronGold,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Tests
        NavigationBarItem(
            selected = currentTab == MainTab.TESTS,
            onClick = { onTabSelected(MainTab.TESTS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.TESTS) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                    contentDescription = AppStrings.get("tests", currentLanguage)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("tests", currentLanguage),
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDark,
                indicatorColor = SaffronGold,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Live Classes
        NavigationBarItem(
            selected = currentTab == MainTab.LIVE,
            onClick = { onTabSelected(MainTab.LIVE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.LIVE) Icons.Filled.LiveTv else Icons.Outlined.LiveTv,
                    contentDescription = AppStrings.get("live_classes", currentLanguage)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("live_classes", currentLanguage),
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDark,
                indicatorColor = SaffronGold,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Profile
        NavigationBarItem(
            selected = currentTab == MainTab.PROFILE,
            onClick = { onTabSelected(MainTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = AppStrings.get("profile", currentLanguage)
                )
            },
            label = {
                Text(
                    text = AppStrings.get("profile", currentLanguage),
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDark,
                indicatorColor = SaffronGold,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
