package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import com.example.data.entity.ProjectAssetEntity
import com.example.ui.components.VideoTrimmerDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun VideoScreen(
    viewModel: StudioViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val context = LocalContext.current
    val promptText by viewModel.videoPrompt.collectAsState()
    val durationSec by viewModel.videoDurationSec.collectAsState()
    val cameraMovement by viewModel.videoCameraMovement.collectAsState()
    val currentVideo by viewModel.currentGeneratedVideo.collectAsState()
    val isVideoLoading by viewModel.isVideoLoading.collectAsState()
    val activeTrimmingAsset by viewModel.activeTrimmingAsset.collectAsState()
    val trimStartSec by viewModel.trimStart.collectAsState()
    val trimEndSec by viewModel.trimEnd.collectAsState()
    val trimFormat by viewModel.trimExportFormat.collectAsState()
    val projects by viewModel.allProjects.collectAsState()

    var showProjectDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Text-to-Video Engine",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Powered by Seedance 2.5 & Kings 3.0 • Up to 3 minutes (180s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = StudioCyan
                )
            }
        }

        // Cinematic Video Templates Section
        Text(
            text = "Cinematic Video Templates:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        val templateScroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(templateScroll),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.aiEngine.videoTemplates.forEach { template ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StudioCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                    modifier = Modifier
                        .width(180.dp)
                        .padding(vertical = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (template.iconName) {
                                    "Memory" -> Icons.Default.Memory
                                    "Movie" -> Icons.Default.Movie
                                    "Spa" -> Icons.Default.Spa
                                    "Smartphone" -> Icons.Default.Smartphone
                                    "Speed" -> Icons.Default.Speed
                                    else -> Icons.Default.Videocam
                                },
                                contentDescription = null,
                                tint = StudioCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = template.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = template.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 2,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Badge(containerColor = StudioPurple.copy(alpha = 0.3f)) {
                                Text("${template.durationSec}s • ${template.aspectRatio}", color = StudioPurple, fontSize = 10.sp)
                            }
                            TextButton(
                                onClick = { viewModel.applyVideoTemplate(template) },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Use", color = StudioCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Prompt Input
        OutlinedTextField(
            value = promptText,
            onValueChange = { viewModel.videoPrompt.value = it },
            label = { Text("Cinematic Video Scene Prompt") },
            placeholder = { Text("E.g. A majestic Siamese cat on a weathered red-tiled roof at sunset, camera slowly dollies forward...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("video_prompt_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioCyan,
                unfocusedBorderColor = StudioCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = StudioCard,
                unfocusedContainerColor = StudioCard
            ),
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.enhancerBasicIdea.value = promptText
                    onNavigateToTab("Prompt Enhancer")
                }) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Enhance Prompt",
                        tint = StudioAmber
                    )
                }
            },
            minLines = 3,
            maxLines = 6
        )

        // Duration Slider (up to 3 minutes / 180 seconds)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sequence Duration:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Badge(containerColor = StudioCyan) {
                val mins = durationSec / 60
                val secs = durationSec % 60
                val textTime = if (mins > 0) "${mins}m ${secs}s (${durationSec}s)" else "${durationSec} seconds"
                Text(textTime, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Slider(
            value = durationSec.toFloat(),
            onValueChange = { viewModel.videoDurationSec.value = it.toInt() },
            valueRange = 5f..180f,
            steps = 34,
            colors = SliderDefaults.colors(
                thumbColor = StudioCyan,
                activeTrackColor = StudioCyan,
                inactiveTrackColor = StudioCardBorder
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Camera Movement Selector
        Text(
            text = "Camera Movement / Rig:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Slow Dolly Forward", "Crane Up", "Orbit 360", "Lateral Pan", "Dynamic FPV").forEach { move ->
                FilterChip(
                    selected = cameraMovement == move,
                    onClick = { viewModel.videoCameraMovement.value = move },
                    label = { Text(move.take(12), fontSize = 11.sp) }
                )
            }
        }

        // Generate Video Button
        Button(
            onClick = { viewModel.triggerVideoGen() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_video_button"),
            colors = ButtonDefaults.buttonColors(containerColor = StudioPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isVideoLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Rendering Video with Seedance 2.5...", color = Color.White, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Video (${durationSec}s / up to 3m)",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Generated Video Preview Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = StudioCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Video Preview & Timeline",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Badge(containerColor = StudioCyan) {
                        Text(currentVideo?.modelUsed ?: "Seedance 2.5", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentVideo != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(currentVideo!!.thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Video Thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Play Button Overlay
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentVideo!!.videoUrl)).apply {
                                    setDataAndType(Uri.parse(currentVideo!!.videoUrl), "video/mp4")
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Playing video in external player", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Video",
                                tint = StudioCyan,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        // Duration Badge bottom right
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.8f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${currentVideo!!.durationSeconds}s • MP4",
                                style = MaterialTheme.typography.labelSmall,
                                color = StudioAmber
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.MovieCreation, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Text("Ready to generate up to 3-minute video", color = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Bar: Trim & Export, Share, Add to Project
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val dummyAsset = ProjectAssetEntity(
                                id = 999,
                                projectId = 0,
                                type = "VIDEO",
                                title = currentVideo?.title ?: "Generated Video Clip",
                                uriOrContent = currentVideo?.videoUrl ?: "",
                                durationSeconds = currentVideo?.durationSeconds ?: durationSec,
                                trimStartSec = 0,
                                trimEndSec = currentVideo?.durationSeconds ?: durationSec,
                                format = "MP4"
                            )
                            viewModel.openTrimmer(dummyAsset)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCut, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Trim Clip", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.shareVideoClip(
                                context = context,
                                title = currentVideo?.title ?: "CineAI Video",
                                urlOrContent = currentVideo?.videoUrl ?: "",
                                format = "MP4"
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Social", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showProjectDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("To Project", fontSize = 11.sp)
                    }
                }
            }
        }
    }

    // Video Trimmer Dialog
    if (activeTrimmingAsset != null) {
        VideoTrimmerDialog(
            asset = activeTrimmingAsset!!,
            startSec = trimStartSec,
            endSec = trimEndSec,
            selectedFormat = trimFormat,
            onStartChange = { viewModel.trimStart.value = it },
            onEndChange = { viewModel.trimEnd.value = it },
            onFormatChange = { viewModel.trimExportFormat.value = it },
            onSaveTrim = { viewModel.saveTrimChanges() },
            onShare = {
                viewModel.shareVideoClip(
                    context = context,
                    title = activeTrimmingAsset!!.title,
                    urlOrContent = activeTrimmingAsset!!.uriOrContent,
                    format = trimFormat
                )
            },
            onDismiss = { viewModel.closeTrimmer() }
        )
    }

    if (showProjectDialog) {
        AlertDialog(
            onDismissRequest = { showProjectDialog = false },
            title = { Text("Add Video to Project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (projects.isEmpty()) {
                        Text("No projects yet. Create one in the Projects tab.")
                    } else {
                        projects.forEach { proj ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StudioDarkBg,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(proj.title, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    TextButton(onClick = {
                                        viewModel.addAssetToProject(
                                            projectId = proj.id,
                                            type = "VIDEO",
                                            title = currentVideo?.title ?: "Video Clip",
                                            content = currentVideo?.videoUrl ?: "",
                                            duration = currentVideo?.durationSeconds ?: durationSec,
                                            format = "MP4"
                                        )
                                        showProjectDialog = false
                                    }) {
                                        Text("Add", color = StudioCyan)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProjectDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
