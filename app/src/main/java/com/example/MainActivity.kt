package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ai.AiModels
import com.example.ui.components.StudioHeader
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioPurple
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: StudioViewModel = viewModel()
                CineAiStudioApp(viewModel)
            }
        }
    }
}

@Composable
fun CineAiStudioApp(viewModel: StudioViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedModelId by viewModel.selectedModel.collectAsState()
    val selectedProject by viewModel.selectedProject.collectAsState()

    val modelName = remember(selectedModelId) {
        AiModels.AVAILABLE_MODELS.find { it.id == selectedModelId }?.name ?: "Seedance 2.5"
    }

    // BackHandler: if inside project detail, navigate back to project list; otherwise if not on Chat, go back to Chat
    BackHandler(enabled = selectedProject != null || currentTab != "Chat") {
        if (selectedProject != null) {
            viewModel.selectedProject.value = null
        } else {
            viewModel.currentTab.value = "Chat"
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg),
        topBar = {
            StudioHeader(
                activeTab = currentTab,
                selectedModelName = modelName,
                onTabSelected = { viewModel.currentTab.value = it },
                onSettingsClick = { viewModel.currentTab.value = "Settings" }
            )
        },
        bottomBar = {
            // Quick Mobile Thumbs Navigation Bar for primary workflow hubs
            NavigationBar(
                containerColor = StudioCard,
                tonalElevation = 8.dp
            ) {
                val primaryTabs = listOf(
                    Triple("Chat", Icons.Default.Chat, "Chat"),
                    Triple("Image", Icons.Default.Image, "Image"),
                    Triple("Video", Icons.Default.Videocam, "Video"),
                    Triple("Prompt Enhancer", Icons.Default.AutoAwesome, "Enhance"),
                    Triple("Projects", Icons.Default.FolderSpecial, "Projects")
                )

                primaryTabs.forEach { (tabId, icon, label) ->
                    val isSelected = currentTab == tabId
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.currentTab.value = tabId },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) StudioCyan else Color(0xFFAFA9C7)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                color = if (isSelected) StudioCyan else Color(0xFFAFA9C7),
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = StudioCyan.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(StudioDarkBg)
        ) {
            when (currentTab) {
                "Chat" -> ChatScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
                "Image" -> ImageScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
                "Video" -> VideoScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
                "Image-to-Video" -> ImageToVideoScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
                "Analyze" -> AnalyzeScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
                "YouTube-to-Prompt" -> YouTubeToPromptScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
                "Prompt Enhancer" -> PromptEnhancerScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
                "Prompt Library" -> PromptLibraryScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
                "Projects" -> ProjectsScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
                "Settings" -> SettingsScreen(viewModel = viewModel)
                else -> ChatScreen(viewModel = viewModel, onNavigateToTab = { viewModel.currentTab.value = it })
            }
        }
    }
}
