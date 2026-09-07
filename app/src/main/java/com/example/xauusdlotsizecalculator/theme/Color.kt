package com.example.xauusdlotsizecalculator.theme

import androidx.compose.ui.graphics.Color

// TradingView Chart Reference Palette (Refined for darker, premium polished look)
// - Bullish / Signature Purple: Deep Royal Violet (#7E22CE / #86198F)
// - Bearish / Neutral: Crisp Light Grey (#E5E7EB / #D1D5DB)
// - Target Box Plum: Deep Rich Plum (#240827 / #330B38)
// - Chart Background: Pure OLED Black (#000000)

// Polished Darker Purple Palette
val TvPurplePrimary = Color(0xFF7E22CE) // Deep Royal Purple
val TvPurpleDark = Color(0xFF581C87)    // Dark Plum / Indigo
val TvPurpleGlow = Color(0xFFC084FC)    // Soft Lilac for reading
val TvPurpleNumber = Color(0xFFD8B4FE)  // Crisp readable violet
val TvPlumContainer = Color(0xFF230826) // Deep velvet plum
val TvPlumContainerBorder = Color(0xFF45104B) // Subtle plum stroke
val TvOnPlum = Color(0xFFF3E8FF)

// TradingView Light Grey / Silver Palette (For SELL & Bearish styling)
val TvLightGrey = Color(0xFFE5E7EB)      // Crisp light grey for active SELL
val TvLightGreyHover = Color(0xFFF3F4F6)
val TvOnLightGrey = Color(0xFF111827)    // Dark charcoal on light grey
val TvSilver = Color(0xFF9CA3AF)         // Neutral secondary text
val TvSilverBright = Color(0xFFE5E7EB)   // Crisp silver text

// Charcoal and Borders
val TvCharcoalBox = Color(0xFF374151)
val TvCharcoalDark = Color(0xFF1F242C)

// Trade Direction Active Colors
val TvBuyColor = Color(0xFF7E22CE)       // Polished Royal Purple for BUY
val TvOnBuyColor = Color(0xFFFFFFFF)
val TvSellColor = Color(0xFFE5E7EB)      // Light Grey for SELL (matching chart)
val TvOnSellColor = Color(0xFF111827)    // Dark text on Light Grey SELL tab

// Surfaces & Canvas (Pure OLED Black & Deep Obsidian)
val TvBlackBackground = Color(0xFF000000)
val TvDarkSurface = Color(0xFF0A090F)
val TvDarkSurfaceElevated = Color(0xFF121019)
val TvDarkSurfaceBorder = Color(0xFF241C2E)

// Text Hierarchy
val TvTextPrimary = Color(0xFFF9FAFB)
val TvTextSecondary = Color(0xFF9CA3AF)
val TvTextMuted = Color(0xFF6B7280)

// Light fallback
val TvLightBackground = Color(0xFFF8FAFC)
val TvLightSurface = Color(0xFFFFFFFF)
val TvLightSurfaceElevated = Color(0xFFF3E8FF)
val TvLightSurfaceBorder = Color(0xFFE9D5FF)
val TvLightTextPrimary = Color(0xFF1E1B4B)
val TvLightTextSecondary = Color(0xFF6B21A8)
