package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ChatDao
import com.example.data.dao.ProjectAssetDao
import com.example.data.dao.ProjectDao
import com.example.data.dao.PromptDao
import com.example.data.entity.ChatMessageEntity
import com.example.data.entity.ProjectAssetEntity
import com.example.data.entity.ProjectEntity
import com.example.data.entity.PromptEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PromptEntity::class,
        ProjectEntity::class,
        ProjectAssetEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun promptDao(): PromptDao
    abstract fun projectDao(): ProjectDao
    abstract fun projectAssetDao(): ProjectAssetDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cineai_studio_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateData(database)
                    }
                }
            }
        }

        suspend fun prepopulateData(database: AppDatabase) {
            val promptDao = database.promptDao()
            val projectDao = database.projectDao()
            val assetDao = database.projectAssetDao()
            val chatDao = database.chatDao()

            if (promptDao.count() == 0) {
                promptDao.insertAll(
                    listOf(
                        PromptEntity(
                            title = "Majestic Siamese Cat at Golden Sunset",
                            promptText = "A cat on a roof",
                            enhancedText = "A majestic Siamese cat perches on a weathered, red-tiled roof at sunset. The warm, golden light casts long shadows, creating a serene and slightly mysterious atmosphere. Shot with a wide-angle lens from a low angle, emphasizing the cat's silhouette against the vibrant, gradient sky. Style: Cinematic, photorealistic, 8k, bokeh highlights.",
                            category = "Cinematic",
                            tags = "cinematic,animals,golden hour,low angle",
                            model = "Seedance 2.5",
                            cameraAngle = "Low Angle, Wide-angle 24mm",
                            lighting = "Golden Hour Rim Light",
                            style = "Photorealistic Cinematic",
                            isFavorite = true
                        ),
                        PromptEntity(
                            title = "Quantum Supercomputer Neural Core",
                            promptText = "Super computer",
                            enhancedText = "An immense quantum supercomputer mainframe glowing in an ultra-clean server cathedral. Intricate optic glass tubes circulate luminous cryo-fluid, pulsing with cyan and deep amber data streams. Camera slowly dollies forward along the reflective black obsidian floor, mist swirling from sub-zero cooling vents. Volumetric ray tracing, anamorphic flare, Blade Runner aesthetic.",
                            category = "Sci-Fi",
                            tags = "supercomputer,quantum,cyberpunk,volumetric light",
                            model = "Seedance 2.5",
                            cameraAngle = "Slow Dolly Forward, 35mm Anamorphic",
                            lighting = "Neon Cyan & Deep Amber Volumetric",
                            style = "Cyberpunk Sci-Fi Epic",
                            isFavorite = true
                        ),
                        PromptEntity(
                            title = "Morning Dew Macro Documentary",
                            promptText = "Dew on leaf documentary",
                            enhancedText = "A slow-motion close-up of crystal dew drops on a vibrant emerald leaf, with an ultra-shallow depth of field. Soft, diffused morning light illuminates intricate microscopic leaf veins. The camera gently pans across the foliage with gentle breeze swaying the stalk. Style: Documentary, macro photography, serene, BBC Earth tone.",
                            category = "Landscapes",
                            tags = "documentary,macro,nature,slow motion",
                            model = "Seedance 2.5",
                            cameraAngle = "Extreme Macro Close-up, Gentle Pan",
                            lighting = "Soft Morning Diffused Light",
                            style = "Documentary Realism",
                            isFavorite = false
                        ),
                        PromptEntity(
                            title = "Hypercar Night Drift in Rain",
                            promptText = "Sports car drifting in Tokyo",
                            enhancedText = "A custom midnight purple hypercar executing a precision power slide around a rain-slicked Tokyo intersection. Wet asphalt mirrors dazzling neon signs and holographic advertisements. Ground-level tracking shot matched to wheel rotation, water spray caught in headlight beams. Style: Cinematic action, high frame rate, motion blur.",
                            category = "Action",
                            tags = "action,cars,tokyo,rain,tracking shot",
                            model = "Seedance 2.5",
                            cameraAngle = "Ground-level Chase Cam, 50mm",
                            lighting = "Reflective Neon & Headlight Rim",
                            style = "High Octane Action",
                            isFavorite = false
                        )
                    )
                )
            }

            if (projectDao.count() == 0) {
                val projId1 = projectDao.insertProject(
                    ProjectEntity(
                        title = "Quantum Supercomputer Vision",
                        description = "Exploration teaser showcasing next-generation quantum computing cores and neural interfaces.",
                        category = "Sci-Fi Short",
                        status = "In Production",
                        targetDurationSeconds = 120,
                        exportFormat = "MP4"
                    )
                )

                assetDao.insertAll(
                    listOf(
                        ProjectAssetEntity(
                            projectId = projId1,
                            type = "SCRIPT",
                            title = "Scene 1 & 2 Script - The Core Awakens",
                            uriOrContent = "SCENE 1: CRYOGENIC LAB - NIGHT\n\nA sea of black monoliths hums with 10 petahertz of processing power. Deep breath of condensing nitrogen.\n\nNARRATOR (V.O.)\nBefore the signal, there was only cold mathematics.\n\nSCENE 2: QUANTUM NEXUS\n\nThe central chamber activates. Glass cylinders ignite with cerulean luminescent data spirals.",
                            durationSeconds = 60,
                            format = "TXT"
                        ),
                        ProjectAssetEntity(
                            projectId = projId1,
                            type = "IMAGE",
                            title = "Concept Art: Supercomputer Neural Hub",
                            uriOrContent = "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=1200&q=80",
                            durationSeconds = 0,
                            format = "PNG",
                            promptUsed = "Massive quantum supercomputer server room with glowing blue wires and glass coolant tubes"
                        ),
                        ProjectAssetEntity(
                            projectId = projId1,
                            type = "VIDEO",
                            title = "Clip 01: Slow Dolly Into Core",
                            uriOrContent = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                            durationSeconds = 45,
                            trimStartSec = 5,
                            trimEndSec = 35,
                            format = "MP4",
                            cameraMovement = "Slow Dolly Forward",
                            promptUsed = "Slow cinematic tracking shot into quantum supercomputer core"
                        )
                    )
                )

                val projId2 = projectDao.insertProject(
                    ProjectEntity(
                        title = "Wild Serenity: Macro World",
                        description = "Nature documentary segment focusing on micro-ecosystems and morning wildlife.",
                        category = "Documentary",
                        status = "Pre-Production",
                        targetDurationSeconds = 180,
                        exportFormat = "MP4"
                    )
                )

                assetDao.insertAsset(
                    ProjectAssetEntity(
                        projectId = projId2,
                        type = "SCRIPT",
                        title = "Episode Outline: Dawn Drops",
                        uriOrContent = "OUTLINE:\n- 00:00 to 00:45: Golden hour awakening, dew condensation on moss.\n- 00:45 to 01:30: Insect awakening in macro scale.\n- 01:30 to 03:00: Sunlight breaking through canopy, evaporation timelapse.",
                        durationSeconds = 180,
                        format = "TXT"
                    )
                )
            }

            if (chatDao.count() == 0) {
                chatDao.insertMessage(
                    ChatMessageEntity(
                        role = "assistant",
                        content = "Welcome to CineAI Studio! I'm your open creative assistant. You can brainstorm screenplays, describe a scene to generate video clips up to 3 minutes, turn basic ideas into cinematic prompts, or analyze any YouTube video's camera choreography. Everything is free-first with zero subscription locks. What are we creating today?",
                        timestamp = System.currentTimeMillis() - 60000,
                        modelUsed = "Seedance 2.5 / CineAI Free Engine"
                    )
                )
            }
        }
    }
}
