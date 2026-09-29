package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Film & Video", // Film & Video, Sci-Fi Short, YouTube, Commercial, Animation
    val status: String = "In Production", // Pre-Production, In Production, Post-Production, Completed
    val targetDurationSeconds: Int = 180, // up to 3 minutes
    val exportFormat: String = "MP4", // MP4, WEBM, GIF
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
