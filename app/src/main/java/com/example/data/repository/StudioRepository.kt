package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.AppDatabase
import com.example.data.ai.AiEngine
import com.example.data.ai.AiModels
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.ProjectAssetEntity
import com.example.data.entity.ProjectEntity
import com.example.data.entity.PromptEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StudioRepository(
    context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context)
) {
    val aiEngine = AiEngine()
    private val prefs: SharedPreferences = context.getSharedPreferences("cineai_settings", Context.MODE_PRIVATE)

    private val promptDao = database.promptDao()
    private val projectDao = database.projectDao()
    private val assetDao = database.projectAssetDao()
    private val chatDao = database.chatDao()

    // Preferences StateFlows for responsive UI
    private val _selectedModel = MutableStateFlow(prefs.getString("selected_model", AiModels.ID_SEEDANCE_2_5) ?: AiModels.ID_SEEDANCE_2_5)
    val selectedModel = _selectedModel.asStateFlow()

    private val _apiKey = MutableStateFlow(prefs.getString("api_key", "") ?: "")
    val apiKey = _apiKey.asStateFlow()

    private val _backendUrl = MutableStateFlow(prefs.getString("backend_url", "https://api.cineai-studio.internal") ?: "https://api.cineai-studio.internal")
    val backendUrl = _backendUrl.asStateFlow()

    fun updateSelectedModel(modelId: String) {
        _selectedModel.value = modelId
        prefs.edit().putString("selected_model", modelId).apply()
    }

    fun updateApiKey(key: String) {
        _apiKey.value = key
        prefs.edit().putString("api_key", key).apply()
    }

    fun updateBackendUrl(url: String) {
        _backendUrl.value = url
        prefs.edit().putString("backend_url", url).apply()
    }

    // Prompts
    fun getAllPrompts(): Flow<List<PromptEntity>> = promptDao.getAllPrompts()
    fun getPromptsByCategory(category: String): Flow<List<PromptEntity>> =
        if (category == "All") promptDao.getAllPrompts() else promptDao.getPromptsByCategory(category)
    fun searchPrompts(query: String): Flow<List<PromptEntity>> = promptDao.searchPrompts(query)
    fun getFavoritePrompts(): Flow<List<PromptEntity>> = promptDao.getFavoritePrompts()
    suspend fun insertPrompt(prompt: PromptEntity): Long = promptDao.insertPrompt(prompt)
    suspend fun updatePrompt(prompt: PromptEntity) = promptDao.updatePrompt(prompt)
    suspend fun deletePrompt(prompt: PromptEntity) = promptDao.deletePrompt(prompt)
    suspend fun deletePromptById(id: Long) = promptDao.deletePromptById(id)

    // Projects
    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    fun getProjectById(id: Long): Flow<ProjectEntity?> = projectDao.getProjectById(id)
    suspend fun insertProject(project: ProjectEntity): Long = projectDao.insertProject(project)
    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)
    suspend fun deleteProject(project: ProjectEntity) = projectDao.deleteProject(project)
    suspend fun deleteProjectById(id: Long) = projectDao.deleteProjectById(id)

    // Project Assets
    fun getAssetsForProject(projectId: Long): Flow<List<ProjectAssetEntity>> = assetDao.getAssetsForProject(projectId)
    fun getAssetsByType(projectId: Long, type: String): Flow<List<ProjectAssetEntity>> = assetDao.getAssetsByType(projectId, type)
    suspend fun insertAsset(asset: ProjectAssetEntity): Long = assetDao.insertAsset(asset)
    suspend fun updateAsset(asset: ProjectAssetEntity) = assetDao.updateAsset(asset)
    suspend fun deleteAsset(asset: ProjectAssetEntity) = assetDao.deleteAsset(asset)
    suspend fun deleteAssetById(id: Long) = assetDao.deleteAssetById(id)

    // Chat Messages
    fun getChatMessages(): Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    suspend fun insertChatMessage(message: ChatMessageEntity): Long = chatDao.insertMessage(message)
    suspend fun clearChat() = chatDao.clearChat()
}
