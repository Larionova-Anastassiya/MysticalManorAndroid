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

val ManorGold = Color(0xFFE6BE73)
val ManorPurple = Color(0xFF211A35)
val ManorSurface = Color(0xFF30263F)

@Composable
fun ManorTitle(title: String, subtitle: String = "") {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            color = ManorGold,
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold
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
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ManorGold,
            contentColor = ManorPurple
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}