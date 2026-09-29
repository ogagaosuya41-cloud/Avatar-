package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiModels
import com.example.data.ai.EnhancedPromptResult
import com.example.data.ai.GeneratedVideoResult
import com.example.data.ai.YouTubeAnalysisResult
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.ProjectAssetEntity
import com.example.data.entity.ProjectEntity
import com.example.data.entity.PromptEntity
import com.example.data.repository.StudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    val repository = StudioRepository(application)
    val aiEngine = repository.aiEngine

    // Active Navigation Tab
    // Tabs: Chat, Image, Video, Image-to-Video, Analyze, YouTube-to-Prompt, Prompt Enhancer, Prompt Library, Projects, Settings
    val currentTab = MutableStateFlow("Chat")

    // Preferences & Models
    val selectedModel = repository.selectedModel
    val apiKey = repository.apiKey
    val backendUrl = repository.backendUrl

    // Data Streams from Room
    val allPrompts: StateFlow<List<PromptEntity>> = repository.getAllPrompts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<ProjectEntity>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.getChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Selected Project for detailed asset view
    val selectedProject = MutableStateFlow<ProjectEntity?>(null)
    val projectAssets = MutableStateFlow<List<ProjectAssetEntity>>(emptyList())

    // Chat State
    val chatInput = MutableStateFlow("")
    val chatPersona = MutableStateFlow("Cinematographer") // Cinematographer, Screenwriter, Director, VFX Supervisor
    val isChatLoading = MutableStateFlow(false)

    // Image Gen State
    val imagePrompt = MutableStateFlow("Quantum supercomputer mainframe glowing in server cathedral, cyan cryo-fluid tubes, volumetric lighting")
    val imageStyle = MutableStateFlow("Cinematic")
    val imageAspect = MutableStateFlow("16:9")
    val currentGeneratedImageUrl = MutableStateFlow("")
    val isImageLoading = MutableStateFlow(false)

    // Video Gen State (Up to 3 minutes / 180s)
    val videoPrompt = MutableStateFlow("A majestic Siamese cat perches on a weathered red-tiled roof at sunset, golden hour rim lighting, slow low-angle dolly")
    val videoDurationSec = MutableStateFlow(45) // slider 5s to 180s (3 minutes)
    val videoCameraMovement = MutableStateFlow("Slow Dolly Forward")
    val videoFps = MutableStateFlow(30)
    val currentGeneratedVideo = MutableStateFlow<GeneratedVideoResult?>(null)
    val isVideoLoading = MutableStateFlow(false)

    // Image-to-Video State
    val img2VidSourceUrl = MutableStateFlow("https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=1200&q=80")
    val img2VidMotionPrompt = MutableStateFlow("Camera dollies into glowing quantum server core with floating cyan particle dust")
    val img2VidMotionStrength = MutableStateFlow(1.2f)
    val img2VidDuration = MutableStateFlow(60)

    // Analyze State
    val analyzeInput = MutableStateFlow("Slow-motion shot of liquid nitrogen fog cascading down dark titanium supercomputer racks")
    val visualAnalysisResult = MutableStateFlow<com.example.data.ai.VisualAnalysisResult?>(null)
    val isAnalyzeLoading = MutableStateFlow(false)

    // YouTube to Prompt State
    val youtubeUrlInput = MutableStateFlow("https://www.youtube.com/watch?v=sci-fi-nature-cinematics")
    val youtubeNotesInput = MutableStateFlow("Notice the shallow depth of field on dew drops, anamorphic 35mm flare, and slow 120fps macro movement")
    val youtubeAnalysis = MutableStateFlow<YouTubeAnalysisResult?>(null)
    val isYouTubeLoading = MutableStateFlow(false)

    // Prompt Enhancer State
    val enhancerBasicIdea = MutableStateFlow("a cat on a roof")
    val enhancerStyle = MutableStateFlow("Cinematic")
    val enhancerCamera = MutableStateFlow("Low Angle, Wide-angle 24mm")
    val enhancerLighting = MutableStateFlow("Golden Hour Rim Light")
    val enhancerMood = MutableStateFlow("Serene & Mysterious")
    val enhancedResult = MutableStateFlow<EnhancedPromptResult?>(null)
    val isEnhancing = MutableStateFlow(false)

    // Video Trimmer & Social Share Dialog
    val activeTrimmingAsset = MutableStateFlow<ProjectAssetEntity?>(null)
    val trimStart = MutableStateFlow(0)
    val trimEnd = MutableStateFlow(30)
    val trimExportFormat = MutableStateFlow("MP4") // MP4, WEBM, GIF
    val trimExportAspect = MutableStateFlow("16:9") // 16:9, 9:16 (Shorts/Reels), 1:1

    init {
        // Generate initial image preview
        triggerImageGen()
    }

    // Chat actions
    fun sendChatMessage() {
        val text = chatInput.value.trim()
        if (text.isEmpty()) return
        chatInput.value = ""
        viewModelScope.launch {
            repository.insertChatMessage(
                ChatMessageEntity(
                    role = "user",
                    content = text,
                    modelUsed = selectedModel.value
                )
            )
            isChatLoading.value = true
            val reply = aiEngine.generateChatResponse(
                history = chatMessages.value,
                userMessage = text,
                persona = chatPersona.value,
                modelId = selectedModel.value,
                apiKey = apiKey.value
            )
            repository.insertChatMessage(
                ChatMessageEntity(
                    role = "assistant",
                    content = reply,
                    modelUsed = selectedModel.value
                )
            )
            isChatLoading.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // Image Gen
    fun triggerImageGen() {
        val prompt = imagePrompt.value.trim()
        if (prompt.isEmpty()) return
        isImageLoading.value = true
        val seed = kotlin.math.abs((prompt.hashCode() + System.currentTimeMillis().toInt()) % 10000)
        currentGeneratedImageUrl.value = aiEngine.buildImageUrl(prompt, imageStyle.value, imageAspect.value, seed)
        isImageLoading.value = false
    }

    // Video Gen (up to 3 minutes)
    fun triggerVideoGen() {
        val prompt = videoPrompt.value.trim()
        if (prompt.isEmpty()) return
        isVideoLoading.value = true
        viewModelScope.launch {
            val result = aiEngine.generateVideo(
                prompt = prompt,
                cameraMotion = videoCameraMovement.value,
                durationSec = videoDurationSec.value,
                fps = videoFps.value,
                modelId = selectedModel.value
            )
            currentGeneratedVideo.value = result
            isVideoLoading.value = false
        }
    }

    // Image-to-Video Gen
    fun triggerImageToVideo() {
        val prompt = img2VidMotionPrompt.value.trim()
        isVideoLoading.value = true
        viewModelScope.launch {
            val result = aiEngine.generateVideo(
                prompt = prompt,
                cameraMotion = "Image-Guided Motion (${img2VidMotionStrength.value}x)",
                durationSec = img2VidDuration.value,
                fps = 30,
                sourceImageUri = img2VidSourceUrl.value,
                modelId = selectedModel.value
            )
            currentGeneratedVideo.value = result
            isVideoLoading.value = false
            currentTab.value = "Video"
        }
    }

    // Analyze
    fun triggerAnalyze() {
        val input = analyzeInput.value.trim()
        isAnalyzeLoading.value = true
        viewModelScope.launch {
            val res = aiEngine.analyzeVisual(input)
            visualAnalysisResult.value = res
            isAnalyzeLoading.value = false
        }
    }

    // YouTube to Prompt
    fun triggerYouTubeAnalysis() {
        val url = youtubeUrlInput.value.trim()
        isYouTubeLoading.value = true
        viewModelScope.launch {
            val res = aiEngine.analyzeYouTubeToPrompt(url, youtubeNotesInput.value, selectedModel.value, apiKey.value)
            youtubeAnalysis.value = res
            isYouTubeLoading.value = false
        }
    }

    // Prompt Enhancer
    fun triggerEnhance() {
        val idea = enhancerBasicIdea.value.trim()
        isEnhancing.value = true
        viewModelScope.launch {
            val res = aiEngine.enhancePrompt(
                idea = idea,
                style = enhancerStyle.value,
                cameraAngle = enhancerCamera.value,
                lighting = enhancerLighting.value,
                mood = enhancerMood.value,
                modelId = selectedModel.value,
                apiKey = apiKey.value
            )
            enhancedResult.value = res
            isEnhancing.value = false
        }
    }

    // Apply template to video generator
    fun applyVideoTemplate(template: com.example.data.ai.VideoTemplate) {
        videoPrompt.value = template.promptStarter
        videoDurationSec.value = template.durationSec
        videoCameraMovement.value = template.cameraMovement
        currentTab.value = "Video"
        Toast.makeText(getApplication(), "Loaded Template: ${template.name}", Toast.LENGTH_SHORT).show()
    }

    // Save prompt to Library
    fun saveToPromptLibrary(title: String, prompt: String, enhanced: String = "", category: String = "Cinematic") {
        viewModelScope.launch {
            repository.insertPrompt(
                PromptEntity(
                    title = title.ifBlank { "Cinematic Prompt" },
                    promptText = prompt,
                    enhancedText = enhanced,
                    category = category,
                    tags = "cinematic,seedance,open-source",
                    model = selectedModel.value
                )
            )
            Toast.makeText(getApplication(), "Saved to Prompt Library!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deletePrompt(prompt: PromptEntity) {
        viewModelScope.launch {
            repository.deletePrompt(prompt)
        }
    }

    // Projects Management
    fun selectProject(project: ProjectEntity) {
        selectedProject.value = project
        viewModelScope.launch {
            repository.getAssetsForProject(project.id).collect { assets ->
                projectAssets.value = assets
            }
        }
    }

    fun createProject(name: String, desc: String, category: String, duration: Int) {
        viewModelScope.launch {
            val id = repository.insertProject(
                ProjectEntity(
                    title = name,
                    description = desc,
                    category = category,
                    targetDurationSeconds = duration
                )
            )
            Toast.makeText(getApplication(), "Project '$name' created!", Toast.LENGTH_SHORT).show()
        }
    }

    fun addAssetToProject(projectId: Long, type: String, title: String, content: String, duration: Int = 0, format: String = "MP4") {
        viewModelScope.launch {
            repository.insertAsset(
                ProjectAssetEntity(
                    projectId = projectId,
                    type = type,
                    title = title,
                    uriOrContent = content,
                    durationSeconds = duration,
                    trimEndSec = duration,
                    format = format
                )
            )
            Toast.makeText(getApplication(), "Asset added to project!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteAsset(asset: ProjectAssetEntity) {
        viewModelScope.launch {
            repository.deleteAsset(asset)
        }
    }

    // Video Trimming & Social Share
    fun openTrimmer(asset: ProjectAssetEntity) {
        activeTrimmingAsset.value = asset
        trimStart.value = asset.trimStartSec
        trimEnd.value = if (asset.trimEndSec > 0) asset.trimEndSec else asset.durationSeconds
        trimExportFormat.value = asset.format
    }

    fun closeTrimmer() {
        activeTrimmingAsset.value = null
    }

    fun saveTrimChanges() {
        val asset = activeTrimmingAsset.value ?: return
        viewModelScope.launch {
            repository.updateAsset(
                asset.copy(
                    trimStartSec = trimStart.value,
                    trimEndSec = trimEnd.value,
                    format = trimExportFormat.value
                )
            )
            closeTrimmer()
            Toast.makeText(getApplication(), "Clip trimmed successfully (${trimEnd.value - trimStart.value}s)!", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareVideoClip(context: Context, title: String, urlOrContent: String, format: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = if (format == "GIF") "image/gif" else "video/*"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "🎬 Created with CineAI Studio: $title\nFormat: $format | #CineAI #Seedance #AIStudio")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(sendIntent, "Share Video Clip via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    // Settings
    fun setModel(modelId: String) {
        repository.updateSelectedModel(modelId)
    }

    fun setApiKey(key: String) {
        repository.updateApiKey(key)
    }

    fun setBackendUrl(url: String) {
        repository.updateBackendUrl(url)
    }
}
