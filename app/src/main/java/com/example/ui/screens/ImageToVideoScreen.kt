package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ImageToVideoScreen(
    viewModel: StudioViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val context = LocalContext.current
    val sourceUrl by viewModel.img2VidSourceUrl.collectAsState()
    val motionPrompt by viewModel.img2VidMotionPrompt.collectAsState()
    val motionStrength by viewModel.img2VidMotionStrength.collectAsState()
    val duration by viewModel.img2VidDuration.collectAsState()
    val isVideoLoading by viewModel.isVideoLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "Image-to-Video Animator",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Breathe cinematic life into static images with camera motion vectors",
                style = MaterialTheme.typography.bodySmall,
                color = StudioCyan
            )
        }

        // Image Preview & Source Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = StudioCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Source Image Keyframe",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (sourceUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(sourceUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Source Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("No source image selected", color = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Image URL input or sample selector
                OutlinedTextField(
                    value = sourceUrl,
                    onValueChange = { viewModel.img2VidSourceUrl.value = it },
                    label = { Text("Source Image URL / Path") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioCyan,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = {
                            viewModel.img2VidSourceUrl.value = "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=1200&q=80"
                            viewModel.img2VidMotionPrompt.value = "Slow dolly into quantum supercomputer core with swirling cyan mist"
                        },
                        label = { Text("Supercomputer Sample", fontSize = 11.sp) }
                    )
                    AssistChip(
                        onClick = {
                            viewModel.img2VidSourceUrl.value = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=1200&q=80"
                            viewModel.img2VidMotionPrompt.value = "Gentle head turn towards camera at sunset, whiskers catching golden light"
                        },
                        label = { Text("Cat at Sunset Sample", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Motion Guidance Prompt
        OutlinedTextField(
            value = motionPrompt,
            onValueChange = { viewModel.img2VidMotionPrompt.value = it },
            label = { Text("Camera & Object Motion Direction") },
            placeholder = { Text("E.g. Camera slowly pushes in while background foliage sways gently...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("img2vid_motion_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioCyan,
                unfocusedBorderColor = StudioCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = StudioCard,
                unfocusedContainerColor = StudioCard
            ),
            minLines = 2,
            maxLines = 4
        )

        // Motion Strength Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Motion Dynamic Strength:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Text(
                text = "${String.format("%.1f", motionStrength)}x",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = StudioAmber
            )
        }
        Slider(
            value = motionStrength,
            onValueChange = { viewModel.img2VidMotionStrength.value = it },
            valueRange = 0.5f..2.5f,
            colors = SliderDefaults.colors(
                thumbColor = StudioAmber,
                activeTrackColor = StudioAmber
            )
        )

        // Target Duration
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Video Duration (up to 3 min):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Text(
                text = "${duration}s",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = StudioCyan
            )
        }
        Slider(
            value = duration.toFloat(),
            onValueChange = { viewModel.img2VidDuration.value = it.toInt() },
            valueRange = 10f..180f,
            colors = SliderDefaults.colors(
                thumbColor = StudioCyan,
                activeTrackColor = StudioCyan
            )
        )

        // Generate Button
        Button(
            onClick = { viewModel.triggerImageToVideo() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_img2vid_button"),
            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isVideoLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Default.MovieFilter, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Animate Image to Video (${duration}s)",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}
