package com.example.xauusdlotsizecalculator.ui.calculator.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.theme.TvBuyColor
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvOnBuyColor
import com.example.xauusdlotsizecalculator.theme.TvOnSellColor
import com.example.xauusdlotsizecalculator.theme.TvSellColor
import com.example.xauusdlotsizecalculator.theme.TvSilver

@Composable
fun DirectionSelector(
    selectedDirection: TradeDirection,
    onDirectionSelected: (TradeDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            // BUY Option (Styled in TradingView Darker Royal Purple)
            DirectionTab(
                label = "BUY (Long)",
                direction = TradeDirection.BUY,
                isSelected = selectedDirection == TradeDirection.BUY,
                activeColor = TvBuyColor,
                activeContentColor = TvOnBuyColor,
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                onClick = { onDirectionSelected(TradeDirection.BUY) },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            // SELL Option (Styled in TradingView Light Grey, NOT red)
            DirectionTab(
                label = "SELL (Short)",
                direction = TradeDirection.SELL,
                isSelected = selectedDirection == TradeDirection.SELL,
                activeColor = TvSellColor,
                activeContentColor = TvOnSellColor,
                icon = Icons.AutoMirrored.Filled.TrendingDown,
                onClick = { onDirectionSelected(TradeDirection.SELL) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DirectionTab(
    label: String,
    direction: TradeDirection,
    isSelected: Boolean,
    activeColor: Color,
    activeContentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "bgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) activeContentColor else TvSilver,
        animationSpec = tween(durationMillis = 200),
        label = "contentColor"
    )

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.height(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = contentColor,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
