package com.anastassiya.mysticalmanor.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.animation.animateContentSize

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.sin

val ManorGold = Color(0xFFE6BE73)
val ManorPurple = Color(0xFF211A35)
val ManorSurface = Color(0xFF30263F)

@Composable
fun ManorTitle(
    title: String,
    subtitle: String = ""
) {
    val transition = rememberInfiniteTransition(
        label = "Title magic"
    )

    val glow by transition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Title glow"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            color = ManorGold,
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(glow)
        )

        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                color = Color(0xFFCAC0D5),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ManorCard(
    title: String,
    description: String,
    icon: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = ManorSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(icon, fontSize = 32.sp)
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) ManorGold else Color.Gray
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    description,
                    color = Color(0xFFD5CDDE),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ManorSurface
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 10.dp
            )
        ) {
            Text(label, color = Color.LightGray, fontSize = 11.sp)
            Text(
                value,
                color = ManorGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ManorButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val transition = rememberInfiniteTransition(
        label = "Magical button glow"
    )

    val glow by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Gold shimmer"
    )

    val animatedGold = androidx.compose.ui.graphics.lerp(
        Color(0xFFE6BE73),
        Color(0xFFFFE8A6),
        glow
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedGold,
            contentColor = ManorPurple,
            disabledContainerColor = ManorSurface,
            disabledContentColor = Color.Gray
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = (3f + glow * 4f).dp,
            pressedElevation = 1.dp
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MagicalParticles(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(
        label = "Floating magical particles"
    )

    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000),
            repeatMode = RepeatMode.Restart
        ),
        label = "Particle movement"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val particles = listOf(
            0.08f to 0.15f,
            0.78f to 0.10f,
            0.42f to 0.28f,
            0.91f to 0.40f,
            0.13f to 0.56f,
            0.65f to 0.68f,
            0.28f to 0.84f,
            0.84f to 0.91f,
            0.51f to 0.47f
        )

        particles.forEachIndexed { index, position ->

            val phase = progress * 2f * PI.toFloat()

            val movement = sin(
                phase + index * 0.9f
            ) * 12f

            val twinkle = (
                    0.20f + 0.35f *
                            ((sin(phase * 2f + index) + 1f) / 2f)
                    ).coerceIn(0f, 1f)

            val x = size.width * position.first

            val y = size.height * position.second +
                    movement

            drawCircle(
                color = ManorGold.copy(alpha = twinkle),
                radius = if (index % 3 == 0) 3.5f else 2f,
                center = Offset(x, y)
            )
        }
    }
}