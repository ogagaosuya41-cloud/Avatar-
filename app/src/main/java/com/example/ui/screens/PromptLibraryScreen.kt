package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.entity.PromptEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun PromptLibraryScreen(
    viewModel: StudioViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val context = LocalContext.current
    val allPrompts by viewModel.allPrompts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Cinematic", "Sci-Fi", "Landscapes", "Action", "Commercial", "YouTube", "Favorites")

    val filteredPrompts = remember(allPrompts, searchQuery, selectedCategory) {
        allPrompts.filter { item ->
            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Favorites" -> item.isFavorite
                else -> item.category.equals(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.promptText.contains(searchQuery, ignoreCase = true) ||
                item.tags.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title Row with "Add Prompt"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Prompt Library",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${filteredPrompts.size} saved prompts in library",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Prompt", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search prompts, tags, or cinematics...", color = TextMuted) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prompt_search_bar"),
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

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        val categoryScroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(categoryScroll),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Prompts List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredPrompts, key = { it.id }) { promptItem ->
                PromptCard(
                    item = promptItem,
                    onCopy = {
                        val textToCopy = promptItem.enhancedText.ifEmpty { promptItem.promptText }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("CineAI Prompt", textToCopy))
                        Toast.makeText(context, "Copied prompt to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onUseInImage = {
                        viewModel.imagePrompt.value = promptItem.enhancedText.ifEmpty { promptItem.promptText }
                        onNavigateToTab("Image")
                    },
                    onUseInVideo = {
                        viewModel.videoPrompt.value = promptItem.enhancedText.ifEmpty { promptItem.promptText }
                        onNavigateToTab("Video")
                    },
                    onDelete = {
                        viewModel.deletePrompt(promptItem)
                    }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreatePromptDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { title, text, cat, tags ->
                viewModel.saveToPromptLibrary(title, text, "", cat)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun PromptCard(
    item: PromptEntity,
    onCopy: () -> Unit,
    onUseInImage: () -> Unit,
    onUseInVideo: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Badge(containerColor = StudioCyan.copy(alpha = 0.2f)) {
                        Text(item.category, color = StudioCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Text preview
            Text(
                text = item.enhancedText.ifEmpty { item.promptText },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 3,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Specs badge
            if (item.cameraAngle.isNotBlank() || item.lighting.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (item.cameraAngle.isNotBlank()) {
                        Text("📷 ${item.cameraAngle}", color = StudioPurple, fontSize = 10.sp)
                    }
                    if (item.lighting.isNotBlank()) {
                        Text("💡 ${item.lighting.take(16)}", color = StudioAmber, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 11.sp)
                }

                Button(
                    onClick = onUseInImage,
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                    modifier = Modifier.weight(1.2f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("To Image", fontSize = 11.sp, color = Color.Black)
                }

                Button(
                    onClick = onUseInVideo,
                    colors = ButtonDefaults.buttonColors(containerColor = StudioPurple),
                    modifier = Modifier.weight(1.2f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("To Video", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CreatePromptDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, prompt: String, category: String, tags: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var promptText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Cinematic") }
    var tags by remember { mutableStateOf("cinematic,4k") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Custom Prompt") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Prompt Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    label = { Text("Prompt Description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Sci-Fi, Cinematic, YouTube)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma separated)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, promptText, category, tags) },
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyan)
            ) {
                Text("Save", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
