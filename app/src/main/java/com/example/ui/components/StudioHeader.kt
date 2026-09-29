package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class NavTabItem(
    val id: String,
    val title: String,
    val icon: ImageVector
)

val STUDIO_TABS = listOf(
    NavTabItem("Chat", "Chat", Icons.Default.Chat),
    NavTabItem("Image", "Image", Icons.Default.Image),
    NavTabItem("Video", "Video", Icons.Default.Videocam),
    NavTabItem("Image-to-Video", "Img-to-Vid", Icons.Default.SlowMotionVideo),
    NavTabItem("Analyze", "Analyze", Icons.Default.AutoFixHigh),
    NavTabItem("YouTube-to-Prompt", "YT-Prompt", Icons.Default.SmartDisplay),
    NavTabItem("Prompt Enhancer", "Enhancer", Icons.Default.AutoAwesome),
    NavTabItem("Prompt Library", "Library", Icons.Default.Bookmark),
    NavTabItem("Projects", "Projects", Icons.Default.FolderSpecial),
    NavTabItem("Settings", "Settings", Icons.Default.Tune)
)

@Composable
fun StudioHeader(
    activeTab: String,
    selectedModelName: String,
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        color = StudioSurface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MovieFilter,
                            contentDescription = "CineAI Logo",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CineAI Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(StudioGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Free Open Studio • Up to 3 min",
                                style = MaterialTheme.typography.labelSmall,
                                color = StudioGreen,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Model Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = StudioCard,
                    modifier = Modifier.clickable { onSettingsClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Active Model",
                            tint = StudioAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedModelName.length > 12) selectedModelName.take(12) + ".." else selectedModelName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Scrollable Navigation Tab Bar with all 10 modules
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                STUDIO_TABS.forEach { tab ->
                    val isSelected = tab.id == activeTab
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTabSelected(tab.id) },
                        leadingIcon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) Color.Black else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else TextPrimary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StudioCyan,
                            containerColor = StudioCard
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) StudioCyan else StudioCardBorder,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
        }
    }
}
