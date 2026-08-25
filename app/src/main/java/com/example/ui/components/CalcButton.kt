package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ButtonShapeStyle
import com.example.util.SoundVibratorHelper

enum class CalcButtonType {
    NUMBER,
    OPERATOR,
    ACTION_PRIMARY, // Equals button
    ACTION_SECONDARY, // AC, DEL
    SCIENTIFIC
}

@Composable
fun CalcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: CalcButtonType = CalcButtonType.NUMBER,
    buttonShape: ButtonShapeStyle = ButtonShapeStyle.SQUIRCLE,
    hapticEnabled: Boolean = true,
    soundEnabled: Boolean = false,
    fontSize: Int = 22,
    subText: String? = null,
    testTag: String = "btn_$text"
) {
    val context = LocalContext.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        label = "button_scale"
    )

    val shape = RoundedCornerShape(buttonShape.cornerRadiusDp.dp)

    val (backgroundColor, contentColor) = when (type) {
        CalcButtonType.NUMBER -> {
            MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
        }
        CalcButtonType.OPERATOR -> {
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.primary
        }
        CalcButtonType.ACTION_PRIMARY -> {
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        }
        CalcButtonType.ACTION_SECONDARY -> {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f) to MaterialTheme.colorScheme.error
        }
        CalcButtonType.SCIENTIFIC -> {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) to MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    Surface(
        modifier = modifier
            .scale(scale)
            .heightIn(min = 52.dp)
            .testTag(testTag),
        shape = shape,
        color = backgroundColor,
        shadowElevation = if (isPressed) 1.dp else 3.dp,
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        SoundVibratorHelper.performHaptic(context, view, hapticEnabled)
                        SoundVibratorHelper.playSound(context, soundEnabled)
                        onClick()
                    }
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = fontSize.sp,
                    fontWeight = if (type == CalcButtonType.ACTION_PRIMARY || type == CalcButtonType.NUMBER) FontWeight.SemiBold else FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                if (subText != null) {
                    Text(
                        text = subText,
                        color = contentColor.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
