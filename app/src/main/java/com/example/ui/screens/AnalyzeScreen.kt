package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun AnalyzeScreen(
    viewModel: StudioViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val input by viewModel.analyzeInput.collectAsState()
    val result by viewModel.visualAnalysisResult.collectAsState()
    val isLoading by viewModel.isAnalyzeLoading.collectAsState()

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
                text = "Scene & Visual Analyzer",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Deep cinematography decomposition: composition, lighting, camera rig, and color science",
                style = MaterialTheme.typography.bodySmall,
                color = StudioCyan
            )
        }

        OutlinedTextField(
            value = input,
            onValueChange = { viewModel.analyzeInput.value = it },
            label = { Text("Scene or Frame Description to Analyze") },
            placeholder = { Text("E.g. Slow-motion shot of liquid nitrogen fog cascading down dark titanium supercomputer racks...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analyze_input_field"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioCyan,
                unfocusedBorderColor = StudioCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = StudioCard,
                unfocusedContainerColor = StudioCard
            ),
            minLines = 3,
            maxLines = 6
        )

        Button(
            onClick = { viewModel.triggerAnalyze() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("analyze_button"),
            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
            } else {
                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Deconstruct Cinematography",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        if (result != null) {
            val res = result!!
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StudioCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Cinematographic Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StudioAmber
                    )

                    // Summary
                    Text(res.subjectSummary, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)

                    Divider(color = StudioCardBorder)

                    // Specs grid
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("COMPOSITION", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(res.compositionStyle, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("LIGHTING SETUP", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(res.lightingStyle, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CAMERA & LENS RIG", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(res.cameraRig, style = MaterialTheme.typography.bodySmall, color = StudioCyan)
                        }
                    }

                    // Color Palette
                    Text("COLOR SCIENCE & PALETTE", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        res.colorPalette.forEach { hex ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            try {
                                                Color(android.graphics.Color.parseColor(hex))
                                            } catch (_: Exception) {
                                                StudioCyan
                                            }
                                        )
                                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(hex, fontSize = 9.sp, color = TextSecondary)
                            }
                        }
                    }

                    Divider(color = StudioCardBorder)

                    // Generated Master Prompt
                    Text("RECREATED CINEMATIC PROMPT", style = MaterialTheme.typography.labelSmall, color = StudioGreen)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StudioDarkBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = res.generatedCinematicPrompt,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.videoPrompt.value = res.generatedCinematicPrompt
                                onNavigateToTab("Video")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Test in Video", fontSize = 11.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                viewModel.imagePrompt.value = res.generatedCinematicPrompt
                                onNavigateToTab("Image")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Test in Image", fontSize = 11.sp, color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.saveToPromptLibrary(
                                    title = "Analyzed Scene",
                                    prompt = res.generatedCinematicPrompt
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
