package com.example.xauusdlotsizecalculator.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvSilver

enum class TradeLogTab(
    val title: String,
    val icon: ImageVector
) {
    CALCULATOR("Calculator", Icons.Default.Calculate),
    JOURNAL("Journal", Icons.AutoMirrored.Filled.MenuBook),
    ANALYTICS("Analytics", Icons.Default.QueryStats),
    ACCOUNT("Account", Icons.Default.Shield)
}

@Composable
fun TradeLogBottomNav(
    selectedTab: TradeLogTab,
    onTabSelected: (TradeLogTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        TradeLogTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TvPurpleGlow,
                    selectedTextColor = TvPurpleGlow,
                    indicatorColor = TvPlumContainer,
                    unselectedIconColor = TvSilver,
                    unselectedTextColor = TvSilver
                )
            )
        }
    }
}
