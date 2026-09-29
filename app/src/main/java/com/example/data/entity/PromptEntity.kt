package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val promptText: String,
    val enhancedText: String = "",
    val category: String = "Cinematic", // Cinematic, Characters, Sci-Fi, Landscapes, Action, Anime, Commercial, YouTube
    val tags: String = "cinematic,photorealistic", // comma-separated
    val model: String = "Seedance 2.5",
    val cameraAngle: String = "Eye Level",
    val lighting: String = "Cinematic Golden Hour",
    val style: String = "Photorealistic",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
