package de.kauker.glyphtorch.views.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun VerticalSlider(
    predefinedValue: Float,
    min: Float,
    max: Float,
    step: Float,
    onValueChanged: (value: Float) -> Unit
) {
    val paddingPx = 300f
    val density = LocalDensity.current

    var value by remember { mutableStateOf(-1f) }

    var height by remember { mutableStateOf(0) }
    var totalOffset by remember { mutableStateOf(0f) }

    var percentage by remember { mutableStateOf(0f) }
    var barWidthDp by remember { mutableStateOf(0.dp) }

    var initBlock by remember { mutableStateOf(0L) }

    fun init() {
        if(height == 0) return
        if(value == predefinedValue) return
        if(System.currentTimeMillis() - initBlock < 1500L) return

        value = predefinedValue
        val availablePixels = (height - paddingPx)

        percentage = (value - min) / (max - min)
        if(percentage < 0f) percentage = 0f
        if(percentage > 1f) percentage = 1f

        barWidthDp = with(density) { (paddingPx + (availablePixels * percentage)).toDp() }
    }

    fun update(height: Int) {
        val comparedOffset = (height - totalOffset).absoluteValue

        percentage = (comparedOffset - paddingPx) / (height - paddingPx)
        if(percentage < 0f) percentage = 0f
        if(percentage > 1f) percentage = 1f

        barWidthDp = with(density) { (if(comparedOffset > paddingPx) comparedOffset else paddingPx).toDp() }

        val decimalValue = (max - min) * percentage
        val remainder = decimalValue % step
        value = (decimalValue - remainder) + min

        onValueChanged(value)
    }

    LaunchedEffect(predefinedValue) { init() }
    LaunchedEffect(height) { init() }

    Box(
        Modifier
            .fillMaxHeight(0.6f)
            .width(150.dp)
            .clip(RoundedCornerShape(64.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
            .onGloballyPositioned {
                height = it.size.height
            }
            .pointerInput(Unit) {
                this.detectDragGestures(
                    onDragStart = { offset ->
                        totalOffset = offset.y
                        update(this.size.height)

                        initBlock = System.currentTimeMillis()
                    },
                    onDrag = { _, offset ->
                        totalOffset += offset.y
                        update(this.size.height)

                        initBlock = System.currentTimeMillis()
                    }
                )
            },
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(64.dp))
                .background(if(isSystemInDarkTheme()) Color.White else Color.Black)
                .width(150.dp)
                .height(barWidthDp)
        ) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .height(6.dp)
                    .width(40.dp)
                    .background(if(!isSystemInDarkTheme()) Color.White else Color.Black, RoundedCornerShape(3.dp))
            )
        }
    }
}