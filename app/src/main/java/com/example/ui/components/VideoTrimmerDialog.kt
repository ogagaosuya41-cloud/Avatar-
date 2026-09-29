package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.ProjectAssetEntity
import com.example.ui.theme.*

@Composable
fun VideoTrimmerDialog(
    asset: ProjectAssetEntity,
    startSec: Int,
    endSec: Int,
    selectedFormat: String,
    onStartChange: (Int) -> Unit,
    onEndChange: (Int) -> Unit,
    onFormatChange: (String) -> Unit,
    onSaveTrim: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val totalDuration = if (asset.durationSeconds > 0) asset.durationSeconds else 60
    val maxSlider = totalDuration.toFloat().coerceAtLeast(10f)

    var currentStart by remember(startSec) { mutableFloatStateOf(startSec.toFloat()) }
    var currentEnd by remember(endSec, totalDuration) {
        val safeEnd = if (endSec > startSec) endSec.toFloat() else totalDuration.toFloat()
        mutableFloatStateOf(safeEnd)
    }
    var selectedAspect by remember { mutableStateOf("16:9") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = StudioCard,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Trim Clip",
                            tint = StudioCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Trim & Export Clip",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Clip Info
                Text(
                    text = asset.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudioCyan,
                    maxLines = 1
                )
                Text(
                    text = "Original Duration: ${totalDuration}s (Up to 3 min supported)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Timeline Visual Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F0D1C))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Trim Range: ${currentStart.toInt()}s  ➔  ${currentEnd.toInt()}s",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = StudioAmber
                        )
                        Text(
                            text = "Export Length: ${(currentEnd - currentStart).toInt()}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Start Time Slider
                Text(
                    text = "Start Trim: ${currentStart.toInt()}s",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Slider(
                    value = currentStart,
                    onValueChange = {
                        val safe = it.coerceAtMost(currentEnd - 2f)
                        currentStart = safe
                        onStartChange(safe.toInt())
                    },
                    valueRange = 0f..maxSlider,
                    colors = SliderDefaults.colors(
                        thumbColor = StudioCyan,
                        activeTrackColor = StudioCyan
                    )
                )

                // End Time Slider
                Text(
                    text = "End Trim: ${currentEnd.toInt()}s",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Slider(
                    value = currentEnd,
                    onValueChange = {
                        val safe = it.coerceAtLeast(currentStart + 2f)
                        currentEnd = safe
                        onEndChange(safe.toInt())
                    },
                    valueRange = 0f..maxSlider,
                    colors = SliderDefaults.colors(
                        thumbColor = StudioPurple,
                        activeTrackColor = StudioPurple
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Aspect Ratio Selector
                Text(
                    text = "Target Social Aspect Ratio:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("16:9 (Landscape)", "9:16 (Shorts/TikTok)", "1:1 (Square)").forEach { aspect ->
                        val isSelected = selectedAspect == aspect.take(4) || selectedAspect == aspect
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedAspect = aspect.take(4).trim() },
                            label = { Text(aspect.take(10), style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Format Selector (MP4, WEBM, GIF)
                Text(
                    text = "Export Format:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("MP4", "WEBM", "GIF").forEach { fmt ->
                        FilterChip(
                            selected = selectedFormat == fmt,
                            onClick = { onFormatChange(fmt) },
                            label = { Text(fmt) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = onSaveTrim,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Apply Trim", color = Color.Black)
                    }
                }
            }
        }
    }
}
