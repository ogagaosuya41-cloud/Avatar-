package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun YouTubeToPromptScreen(
    viewModel: StudioViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val context = LocalContext.current
    val urlInput by viewModel.youtubeUrlInput.collectAsState()
    val notesInput by viewModel.youtubeNotesInput.collectAsState()
    val analysis by viewModel.youtubeAnalysis.collectAsState()
    val isLoading by viewModel.isYouTubeLoading.collectAsState()

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
                text = "YouTube to Prompt",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Extracts camera movement, shot types & color grading into copyright-clean generative prompts",
                style = MaterialTheme.typography.bodySmall,
                color = StudioCyan
            )
        }

        // Quick Preset Links for immediate testing
        Text(
            text = "Try Sample Video Styles:",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = {
                    viewModel.youtubeUrlInput.value = "https://www.youtube.com/watch?v=nature-macro-dew"
                    viewModel.youtubeNotesInput.value = "Slow-motion macro dew drops on leaf, diffused dawn light, gentle panning"
                },
                label = { Text("Nature Doc Macro", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = {
                    viewModel.youtubeUrlInput.value = "https://www.youtube.com/watch?v=supercomputer-quantum-core"
                    viewModel.youtubeNotesInput.value = "Corridor of glowing quantum servers, cyan tubes, low floor dolly"
                },
                label = { Text("Supercomputer Core", fontSize = 11.sp) }
            )
            AssistChip(
                onClick = {
                    viewModel.youtubeUrlInput.value = "https://www.youtube.com/watch?v=tokyo-night-rain-drift"
                    viewModel.youtubeNotesInput.value = "High-speed chase car, rain reflections, neon signs, wide 24mm tracking"
                },
                label = { Text("Night Drift", fontSize = 11.sp) }
            )
        }

        // URL Input Field
        OutlinedTextField(
            value = urlInput,
            onValueChange = { viewModel.youtubeUrlInput.value = it },
            label = { Text("YouTube Video URL") },
            placeholder = { Text("https://www.youtube.com/watch?v=...") },
            leadingIcon = {
                Icon(Icons.Default.SmartDisplay, contentDescription = null, tint = StudioPink)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("youtube_url_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioCyan,
                unfocusedBorderColor = StudioCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = StudioCard,
                unfocusedContainerColor = StudioCard
            ),
            singleLine = true
        )

        // Scene Notes Input
        OutlinedTextField(
            value = notesInput,
            onValueChange = { viewModel.youtubeNotesInput.value = it },
            label = { Text("Visual Target Notes (Optional)") },
            placeholder = { Text("Specific timestamps or focus (e.g. 00:30 macro scene, anamorphic flare, lighting)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioCyan,
                unfocusedBorderColor = StudioCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = StudioCard,
                unfocusedContainerColor = StudioCard
            ),
            minLines = 2,
            maxLines = 3
        )

        // Analyze Button
        Button(
            onClick = { viewModel.triggerYouTubeAnalysis() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("analyze_youtube_button"),
            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Analyzing Video Cinematography...", color = Color.Black, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.MovieFilter, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Deconstruct Video & Create Prompt",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Analysis Results Card
        if (analysis != null) {
            val res = analysis!!
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StudioCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = res.videoTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StudioCyan
                        )
                        Badge(containerColor = StudioGreen.copy(alpha = 0.2f)) {
                            Text("Copyright Safe", color = StudioGreen, fontSize = 10.sp)
                        }
                    }

                    // Key Scenes
                    Text("IDENTIFIED SCENES & TIMELINE", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    res.identifiedScenes.forEach { scene ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FiberManualRecord, contentDescription = null, tint = StudioAmber, modifier = Modifier.size(8.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(scene, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }

                    Divider(color = StudioCardBorder)

                    // Camera Movements & Shot Types
                    Text("CAMERA MOVEMENTS & CHOREOGRAPHY", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    res.cameraMovements.forEach { move ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(move, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }

                    // Shot Types
                    Text("SHOT TYPES & LENSES", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        res.shotTypes.forEach { shot ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(shot, fontSize = 10.sp) }
                            )
                        }
                    }

                    // Color Grading & Aesthetic
                    Text("COLOR GRADING & MOOD", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(res.colorGrading, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    Text("Aesthetic: ${res.visualAesthetic}", style = MaterialTheme.typography.bodySmall, color = StudioPurple)

                    Divider(color = StudioCardBorder)

                    // Non-Copyrighted Generated Cinematic Prompt
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ORIGINAL CINEMATIC PROMPT", style = MaterialTheme.typography.labelSmall, color = StudioAmber)
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("CineAI YouTube Prompt", res.nonCopyrightedPrompt))
                                Toast.makeText(context, "Prompt copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StudioDarkBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCyan.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = res.nonCopyrightedPrompt,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.videoPrompt.value = res.nonCopyrightedPrompt
                                viewModel.videoCameraMovement.value = res.recommendedMotion
                                onNavigateToTab("Video")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Generate Video", fontSize = 11.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                viewModel.imagePrompt.value = res.nonCopyrightedPrompt
                                onNavigateToTab("Image")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Generate Image", fontSize = 11.sp, color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.saveToPromptLibrary(
                                    title = "YT Style: ${res.videoTitle}",
                                    prompt = res.nonCopyrightedPrompt,
                                    category = "YouTube"
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
