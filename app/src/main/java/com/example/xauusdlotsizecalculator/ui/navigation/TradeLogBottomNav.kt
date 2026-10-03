package com.example.xauusdlotsizecalculator.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.tactileClickable

enum class TradeLogTab(
    val title: String,
    val icon: ImageVector
) {
    CALCULATOR("Terminal", Icons.Default.Calculate),
    JOURNAL("Journal", Icons.AutoMirrored.Filled.MenuBook),
    ANALYTICS("Analytics", Icons.Default.QueryStats),
    ACCOUNT("Accounts", Icons.Default.Shield)
}

@Composable
fun TradeLogBottomNav(
    selectedTab: TradeLogTab,
    onTabSelected: (TradeLogTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                // Top Specular Glass Line
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            TvPurpleGlow.copy(alpha = 0.35f),
                            Color(0x80C084FC),
                            TvPurpleGlow.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.2f
                )
            },
        color = Color(0xF208060E),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TradeLogTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) TvPurpleGlow else TvSilver.copy(alpha = 0.7f),
                    animationSpec = tween(180),
                    label = "iconColor"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else TvSilver.copy(alpha = 0.7f),
                    animationSpec = tween(180),
                    label = "textColor"
                )

                val containerBg by animateColorAsState(
                    targetValue = if (isSelected) TvPlumContainer.copy(alpha = 0.9f) else Color.Transparent,
                    animationSpec = tween(180),
                    label = "containerBg"
                )

                val borderStroke = if (isSelected) BorderStroke(1.dp, TvPurplePrimary.copy(alpha = 0.6f)) else null

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(containerBg)
                        .then(if (borderStroke != null) Modifier.drawBehind {
                            drawRoundRect(
                                color = TvPurplePrimary.copy(alpha = 0.4f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                            )
                        } else Modifier)
                        .tactileClickable(onClick = { onTabSelected(tab) })
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.2.sp,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}
