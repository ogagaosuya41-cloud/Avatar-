package com.example.data.ai

data class ModelOption(
    val id: String,
    val name: String,
    val description: String,
    val isFree: Boolean = true,
    val type: ModelType
)

enum class ModelType {
    ALL_IN_ONE,
    TEXT_AND_CHAT,
    IMAGE_GEN,
    VIDEO_GEN
}

object AiModels {
    const val ID_SEEDANCE_2_5 = "seedance_2_5"
    const val ID_KINGS_3_0 = "kings_3_0"
    const val ID_FREE_OPEN = "free_opensource"
    const val ID_GEMINI_3_5_FLASH = "gemini_3_5_flash"
    const val ID_VEO_VIDEO = "veo_3_1_fast"
    const val ID_LOCAL_OLLAMA = "local_ollama"
    const val ID_CUSTOM_BACKEND = "custom_backend"

    val AVAILABLE_MODELS = listOf(
        ModelOption(
            id = ID_SEEDANCE_2_5,
            name = "Seedance 2.5",
            description = "Open cinematic video model. High dynamic motion, multi-shot pacing up to 3 mins.",
            isFree = true,
            type = ModelType.VIDEO_GEN
        ),
        ModelOption(
            id = ID_KINGS_3_0,
            name = "Kings 3.0",
            description = "Next-gen ultra-consistent video generation with complex motion physics and 1080p rendering.",
            isFree = true,
            type = ModelType.VIDEO_GEN
        ),
        ModelOption(
            id = ID_FREE_OPEN,
            name = "Free Open-Source (Llama / FLUX)",
            description = "Completely free open-source generation. Zero API keys required, no subscription.",
            isFree = true,
            type = ModelType.ALL_IN_ONE
        ),
        ModelOption(
            id = ID_GEMINI_3_5_FLASH,
            name = "Gemini 3.5 Flash",
            description = "Google Cloud high-speed multimodal reasoning & prompt analysis.",
            isFree = false,
            type = ModelType.TEXT_AND_CHAT
        ),
        ModelOption(
            id = ID_VEO_VIDEO,
            name = "Veo 3.1 Fast",
            description = "Cinematic video generation with precise camera choreography.",
            isFree = false,
            type = ModelType.VIDEO_GEN
        ),
        ModelOption(
            id = ID_LOCAL_OLLAMA,
            name = "Local AI / Ollama",
            description = "Self-hosted local neural network endpoint (e.g. localhost / 10.0.2.2:11434).",
            isFree = true,
            type = ModelType.ALL_IN_ONE
        ),
        ModelOption(
            id = ID_CUSTOM_BACKEND,
            name = "Google Cloud Custom Backend",
            description = "Connect your self-hosted Cloud Run or GPU server instance.",
            isFree = true,
            type = ModelType.ALL_IN_ONE
        )
    )
}
