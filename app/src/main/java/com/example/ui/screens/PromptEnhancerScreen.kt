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
fun PromptEnhancerScreen(
    viewModel: StudioViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val context = LocalContext.current
    val basicIdea by viewModel.enhancerBasicIdea.collectAsState()
    val selectedStyle by viewModel.enhancerStyle.collectAsState()
    val selectedCamera by viewModel.enhancerCamera.collectAsState()
    val selectedLighting by viewModel.enhancerLighting.collectAsState()
    val selectedMood by viewModel.enhancerMood.collectAsState()
    val enhancedResult by viewModel.enhancedResult.collectAsState()
    val isEnhancing by viewModel.isEnhancing.collectAsState()

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
                text = "Cinematic Prompt Enhancer",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Turns short concepts into Hollywood-grade detailed prompts for Seedance 2.5, FLUX & Veo",
                style = MaterialTheme.typography.bodySmall,
                color = StudioCyan
            )
        }

        // Quick Preset Inspiration Chips
        Text(
            text = "Quick Idea Starters:",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "a cat on a roof" to "A cat on a roof",
                "super computer" to "Super computer",
                "car in rain" to "Sports car drifting in Tokyo rain",
                "leaf dew" to "Dew on leaf documentary"
            ).forEach { (label, full) ->
                AssistChip(
                    onClick = {
                        viewModel.enhancerBasicIdea.value = full
                        viewModel.triggerEnhance()
                    },
                    label = { Text(label, fontSize = 11.sp) }
                )
            }
        }

        // Input Field
        OutlinedTextField(
            value = basicIdea,
            onValueChange = { viewModel.enhancerBasicIdea.value = it },
            label = { Text("Your Basic Concept or Idea") },
            placeholder = { Text("E.g. a cat on a roof, super computer, spaceship docking...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("enhancer_idea_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioCyan,
                unfocusedBorderColor = StudioCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = StudioCard,
                unfocusedContainerColor = StudioCard
            ),
            singleLine = false,
            minLines = 2,
            maxLines = 4
        )

        // Customization Selectors
        Text(
            text = "Cinematography Variables:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )

        // Style
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Cinematic", "Photorealistic", "Cyberpunk", "Documentary").forEach { style ->
                FilterChip(
                    selected = selectedStyle == style,
                    onClick = { viewModel.enhancerStyle.value = style },
                    label = { Text(style, fontSize = 11.sp) }
                )
            }
        }

        // Camera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Low Angle, 24mm", "Slow Dolly 35mm", "Macro 100mm", "Drone Aerial").forEach { cam ->
                FilterChip(
                    selected = selectedCamera.contains(cam.take(6)),
                    onClick = { viewModel.enhancerCamera.value = cam },
                    label = { Text(cam, fontSize = 11.sp) }
                )
            }
        }

        // Lighting
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Golden Hour Rim Light", "Neon Volumetric", "Soft Diffused Daylight", "High-Contrast Noir").forEach { light ->
                FilterChip(
                    selected = selectedLighting.contains(light.take(6)),
                    onClick = { viewModel.enhancerLighting.value = light },
                    label = { Text(light.take(12), fontSize = 11.sp) }
                )
            }
        }

        // Enhance Button
        Button(
            onClick = { viewModel.triggerEnhance() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("enhance_prompt_button"),
            colors = ButtonDefaults.buttonColors(containerColor = StudioAmber),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isEnhancing) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Synthesizing Master Prompt...", color = Color.Black, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Expand to Cinematic Master Prompt",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Result Card
        if (enhancedResult != null) {
            val res = enhancedResult!!
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StudioCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioAmber.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = res.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StudioAmber
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Enhanced Cinematic Prompt", res.masterPrompt))
                                Toast.makeText(context, "Copied Master Prompt!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = StudioCyan)
                        }
                    }

                    // Master Prompt Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StudioDarkBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = res.masterPrompt,
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    }

                    // Specs Breakdown
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row {
                            Text("Camera & Lens: ", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 12.sp)
                            Text(res.cameraAngle, color = StudioCyan, fontSize = 12.sp)
                        }
                        Row {
                            Text("Lighting Setup: ", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 12.sp)
                            Text(res.lighting, color = TextPrimary, fontSize = 12.sp)
                        }
                        Row {
                            Text("Mood & Atmosphere: ", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 12.sp)
                            Text(res.mood, color = StudioPurple, fontSize = 12.sp)
                        }
                        Row {
                            Text("Technical Rig: ", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 12.sp)
                            Text(res.technicalDetails, color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    // Visual Elements Tags
                    Text("Guiding Visual Elements:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        res.visualElements.take(3).forEach { elem ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(elem, fontSize = 10.sp) }
                            )
                        }
                    }

                    Divider(color = StudioCardBorder)

                    // One-tap Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.videoPrompt.value = res.masterPrompt
                                viewModel.videoCameraMovement.value = res.cameraAngle
                                onNavigateToTab("Video")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("To Video", fontSize = 11.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                viewModel.imagePrompt.value = res.masterPrompt
                                onNavigateToTab("Image")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("To Image", fontSize = 11.sp, color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.saveToPromptLibrary(
                                    title = res.title,
                                    prompt = basicIdea,
                                    enhanced = res.masterPrompt,
                                    category = selectedStyle
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Library", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
