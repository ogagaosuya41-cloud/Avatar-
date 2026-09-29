package com.example.ui.screens

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
fun ImageScreen(
    viewModel: StudioViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val context = LocalContext.current
    val promptText by viewModel.imagePrompt.collectAsState()
    val selectedStyle by viewModel.imageStyle.collectAsState()
    val selectedAspect by viewModel.imageAspect.collectAsState()
    val imageUrl by viewModel.currentGeneratedImageUrl.collectAsState()
    val isLoading by viewModel.isImageLoading.collectAsState()
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
        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Text-to-Image Studio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "High-definition FLUX / SDXL open generation • No subscription",
                    style = MaterialTheme.typography.bodySmall,
                    color = StudioCyan
                )
            }
        }

        // Quick Preset Inspiration Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "Supercomputer" to "Quantum supercomputer mainframe glowing in server cathedral, cyan cryo-fluid tubes",
                "Siamese Cat" to "A majestic Siamese cat on a weathered red-tiled roof at sunset, golden hour rim light",
                "Rain Drift" to "Hypercar midnight drift through rain-slicked Tokyo, neon reflections"
            ).forEach { (label, preset) ->
                AssistChip(
                    onClick = { viewModel.imagePrompt.value = preset },
                    label = { Text(label, fontSize = 11.sp) }
                )
            }
        }

        // Prompt Input
        OutlinedTextField(
            value = promptText,
            onValueChange = { viewModel.imagePrompt.value = it },
            label = { Text("Visual Prompt Description") },
            placeholder = { Text("Describe the visual elements, lighting, lens, and subject...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("image_prompt_input"),
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
                        contentDescription = "Enhance in Prompt Enhancer",
                        tint = StudioAmber
                    )
                }
            },
            minLines = 3,
            maxLines = 6
        )

        // Style Selector
        Text(
            text = "Artistic Style:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Cinematic", "Cyberpunk", "Anime", "Photorealistic", "Fantasy", "3D Render").take(4).forEach { style ->
                FilterChip(
                    selected = selectedStyle == style,
                    onClick = { viewModel.imageStyle.value = style },
                    label = { Text(style, fontSize = 11.sp) }
                )
            }
        }

        // Aspect Ratio Selector
        Text(
            text = "Aspect Ratio:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("16:9", "9:16", "1:1", "21:9").forEach { aspect ->
                FilterChip(
                    selected = selectedAspect == aspect,
                    onClick = { viewModel.imageAspect.value = aspect },
                    label = { Text(aspect) }
                )
            }
        }

        // Generate Button
        Button(
            onClick = { viewModel.triggerImageGen() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("generate_image_button"),
            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
            } else {
                Icon(Icons.Default.Brush, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Artwork (Free Open Model)",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Image Output Card
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
                        text = "Generated Canvas",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Badge(containerColor = StudioPurple) {
                        Text("FLUX 8K", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Generated AI Art",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Text("Ready to render", color = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Bar for the generated image
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.saveToPromptLibrary(
                                title = "Image: ${promptText.take(24)}",
                                prompt = promptText,
                                category = selectedStyle
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Prompt", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showProjectDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to Project", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.img2VidSourceUrl.value = imageUrl
                            viewModel.img2VidMotionPrompt.value = promptText
                            onNavigateToTab("Image-to-Video")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioPurple),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.SlowMotionVideo, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Animate", fontSize = 11.sp)
                    }
                }
            }
        }
    }

    if (showProjectDialog) {
        AlertDialog(
            onDismissRequest = { showProjectDialog = false },
            title = { Text("Select Project") },
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
                                            type = "IMAGE",
                                            title = "Art: ${promptText.take(20)}",
                                            content = imageUrl,
                                            format = "PNG"
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
