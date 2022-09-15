package com.example.randomuselessfacts.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object DailyFact: Screen(
        route = "daily_fact",
        title = "Daily Fact",
        icon = Icons.Default.Home
    )
    object SavedFacts: Screen(
        route = "saved_facts",
        title = "Saved Facts",
        icon = Icons.Default.Favorite
    )
}