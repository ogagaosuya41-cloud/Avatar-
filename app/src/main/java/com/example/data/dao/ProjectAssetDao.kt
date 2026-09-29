package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ProjectAssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectAssetDao {
    @Query("SELECT * FROM project_assets WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getAssetsForProject(projectId: Long): Flow<List<ProjectAssetEntity>>

    @Query("SELECT * FROM project_assets WHERE projectId = :projectId AND type = :type ORDER BY createdAt DESC")
    fun getAssetsByType(projectId: Long, type: String): Flow<List<ProjectAssetEntity>>

    @Query("SELECT COUNT(*) FROM project_assets WHERE projectId = :projectId")
    suspend fun countForProject(projectId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: ProjectAssetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assets: List<ProjectAssetEntity>)

    @Update
    suspend fun updateAsset(asset: ProjectAssetEntity)

    @Delete
    suspend fun deleteAsset(asset: ProjectAssetEntity)

    @Query("DELETE FROM project_assets WHERE id = :id")
    suspend fun deleteAssetById(id: Long)
}
