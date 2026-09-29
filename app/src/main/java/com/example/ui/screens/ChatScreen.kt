package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ChatMessageEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ChatScreen(
    viewModel: StudioViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    val inputText by viewModel.chatInput.collectAsState()
    val selectedPersona by viewModel.chatPersona.collectAsState()
    val isLoading by viewModel.isChatLoading.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
    ) {
        // Persona Selector Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Persona:",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            listOf("Cinematographer", "Screenwriter", "Director", "VFX Supervisor").forEach { persona ->
                val isSelected = selectedPersona == persona
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.chatPersona.value = persona },
                    label = { Text(persona, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StudioPurple,
                        containerColor = StudioCard
                    )
                )
            }
        }

        Divider(color = StudioCardBorder, thickness = 1.dp)

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("CineAI Chat", msg.content))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onSendToImage = {
                        viewModel.imagePrompt.value = msg.content.take(200)
                        onNavigateToTab("Image")
                    },
                    onSendToVideo = {
                        viewModel.videoPrompt.value = msg.content.take(200)
                        onNavigateToTab("Video")
                    },
                    onSendToLibrary = {
                        viewModel.saveToPromptLibrary(
                            title = "Chat Concept",
                            prompt = msg.content
                        )
                    }
                )
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = StudioCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "$selectedPersona is formulating cinematic ideas...",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Quick Suggestions Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SuggestionChip(
                onClick = { viewModel.chatInput.value = "How do I pace a 3-minute quantum supercomputer teaser?" },
                label = { Text("Supercomputer teaser", fontSize = 11.sp) }
            )
            SuggestionChip(
                onClick = { viewModel.chatInput.value = "Give me camera directions for a golden hour roof scene." },
                label = { Text("Roof scene camera", fontSize = 11.sp) }
            )
        }

        // Input Bar
        Surface(
            color = StudioCard,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.chatInput.value = it },
                    placeholder = { Text("Ask $selectedPersona anything...", color = TextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioCyan,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.sendChatMessage() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(StudioCyan)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessageEntity,
    onCopy: () -> Unit,
    onSendToImage: () -> Unit,
    onSendToVideo: () -> Unit,
    onSendToLibrary: () -> Unit
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Icon(
                imageVector = if (isUser) Icons.Default.Person else Icons.Default.SmartToy,
                contentDescription = null,
                tint = if (isUser) StudioCyan else StudioPurple,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isUser) "You" else message.modelUsed,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) StudioCyan.copy(alpha = 0.15f) else StudioCard,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) StudioCyan.copy(alpha = 0.4f) else StudioCardBorder
            ),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onSendToImage, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Image, contentDescription = "To Image", tint = StudioCyan, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onSendToVideo, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Videocam, contentDescription = "To Video", tint = StudioPurple, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onSendToLibrary, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = "To Library", tint = StudioAmber, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
