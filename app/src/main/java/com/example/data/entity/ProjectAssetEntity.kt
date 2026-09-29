package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_assets",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class ProjectAssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val type: String, // "VIDEO", "IMAGE", "SCRIPT"
    val title: String,
    val uriOrContent: String, // Image/Video URL or base64 or script text
    val durationSeconds: Int = 0, // up to 180s for videos
    val trimStartSec: Int = 0,
    val trimEndSec: Int = 0,
    val format: String = "MP4", // MP4, PNG, WEBM, TXT
    val promptUsed: String = "",
    val cameraMovement: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
