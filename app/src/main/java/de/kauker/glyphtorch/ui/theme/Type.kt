package de.kauker.glyphtorch.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import de.kauker.glyphtorch.R

val NDot = FontFamily(
    Font(R.font.ndot, FontWeight.Normal)
)

val defaultTypography = Typography()

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = defaultTypography.headlineLarge.copy(
        fontFamily = NDot
    ),
    headlineMedium = defaultTypography.headlineLarge.copy(
        fontFamily = NDot
    ),
    headlineSmall = defaultTypography.headlineLarge.copy(
        fontFamily = NDot
    ),
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = NDot
    ),
    titleMedium = defaultTypography.titleLarge.copy(
        fontFamily = NDot
    ),
    titleSmall = defaultTypography.titleLarge.copy(
        fontFamily = NDot
    )
)