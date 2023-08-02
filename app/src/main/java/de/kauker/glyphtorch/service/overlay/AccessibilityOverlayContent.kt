package de.kauker.glyphtorch.service.overlay

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.kauker.glyphtorch.R
import de.kauker.glyphtorch.ui.theme.GlyphTorchNonRootTheme
import de.kauker.glyphtorch.ui.theme.Typography
import de.kauker.glyphtorch.views.common.VerticalSlider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AccessibilityOverlayContent(
    show: Boolean,
    defaultBrightnessValue: Int,
    onBrightnessValueChange: (value: Int) -> Unit,
    onClose: () -> Unit,
    onDestroy: () -> Unit
) {
    val density = LocalDensity.current

    val coroutineScope = rememberCoroutineScope()
    val animation = remember { Animatable(1f) }

    var brightnessValue by remember { mutableStateOf(256) }

    LaunchedEffect(defaultBrightnessValue) {
        brightnessValue = defaultBrightnessValue
    }

    LaunchedEffect(show) {
        coroutineScope.launch {
            if(show) {
                delay(100)
                animation.animateTo(0f, tween(300))
            }else{
                delay(600)
                animation.animateTo(1f, tween(500))

                onDestroy()
            }
        }
    }

    var height by remember { mutableStateOf(0) }
    var heightDp by remember { mutableStateOf(0.dp) }
    LaunchedEffect(height) { heightDp = with(density) { height.toDp() } }

    GlyphTorchNonRootTheme {
        val shape = RoundedCornerShape(48.dp, 48.dp)

        Box(
            Modifier
                .alpha(if(heightDp == 0.dp) 0f else 1f)
                .offset(y = (heightDp * animation.value))
                .shadow(64.dp, shape)
                .clip(shape)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .fillMaxHeight()
                .onGloballyPositioned { height = it.size.height },
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                onClick = { onClose() }
            ) {
                Icon(Icons.Rounded.Close, stringResource(id = R.string.common_close), tint = MaterialTheme.colorScheme.onBackground)
            }

            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.function_name),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = Typography.headlineLarge
                )

                Spacer(Modifier.height(16.dp))

                VerticalSlider(
                    predefinedValue = brightnessValue.toFloat(),
                    min = 0f,
                    max = 256f,
                    step = 1f,
                    onValueChanged = { value ->
                        brightnessValue = value.toInt()
                        onBrightnessValueChange(value.toInt())
                    }
                )
            }
        }
    }
}