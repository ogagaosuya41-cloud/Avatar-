package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.entity.ProjectAssetEntity
import com.example.data.entity.ProjectEntity
import com.example.ui.components.VideoTrimmerDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun ProjectsScreen(
    viewModel: StudioViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val context = LocalContext.current
    val allProjects by viewModel.allProjects.collectAsState()
    val selectedProject by viewModel.selectedProject.collectAsState()
    val projectAssets by viewModel.projectAssets.collectAsState()

    val activeTrimmingAsset by viewModel.activeTrimmingAsset.collectAsState()
    val trimStartSec by viewModel.trimStart.collectAsState()
    val trimEndSec by viewModel.trimEnd.collectAsState()
    val trimFormat by viewModel.trimExportFormat.collectAsState()

    var showCreateProjectDialog by remember { mutableStateOf(false) }
    var showAddAssetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(16.dp)
    ) {
        if (selectedProject == null) {
            // Projects Overview List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Production Projects",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Organize video clips, generated artwork, and screenplay scripts",
                        style = MaterialTheme.typography.bodySmall,
                        color = StudioCyan
                    )
                }

                Button(
                    onClick = { showCreateProjectDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.CreateNewFolder, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Project", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(allProjects, key = { it.id }) { proj ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = StudioCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectProject(proj) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FolderSpecial,
                                        contentDescription = null,
                                        tint = StudioAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = proj.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Badge(containerColor = StudioPurple.copy(alpha = 0.25f)) {
                                    Text(proj.status, color = StudioPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (proj.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = proj.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 2
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("🎬 ${proj.category}", color = TextSecondary, fontSize = 11.sp)
                                    Text("⏱ Up to ${proj.targetDurationSeconds}s (3m)", color = StudioCyan, fontSize = 11.sp)
                                    Text("📦 ${proj.exportFormat}", color = StudioAmber, fontSize = 11.sp)
                                }

                                Text("Open ➔", color = StudioCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Selected Project Details View
            val proj = selectedProject!!
            var activeFilter by remember { mutableStateOf("ALL") }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.selectedProject.value = null }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Column {
                        Text(
                            text = proj.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${proj.category} • Target: ${proj.targetDurationSeconds}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioCyan
                        )
                    }
                }

                Button(
                    onClick = { showAddAssetDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Asset", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Asset Type Filters (All, Videos, Images, Scripts)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "All Assets", "VIDEO" to "Videos (${projectAssets.count { it.type == "VIDEO" }})", "IMAGE" to "Images (${projectAssets.count { it.type == "IMAGE" }})", "SCRIPT" to "Scripts (${projectAssets.count { it.type == "SCRIPT" }})").forEach { (type, label) ->
                    FilterChip(
                        selected = activeFilter == type,
                        onClick = { activeFilter = type },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val filteredAssets = remember(projectAssets, activeFilter) {
                if (activeFilter == "ALL") projectAssets else projectAssets.filter { it.type == activeFilter }
            }

            if (filteredAssets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No assets in this category", color = TextMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { showAddAssetDialog = true }) {
                            Text("Add First Asset")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAssets, key = { it.id }) { asset ->
                        ProjectAssetCard(
                            asset = asset,
                            onTrim = { viewModel.openTrimmer(asset) },
                            onShare = {
                                viewModel.shareVideoClip(
                                    context = context,
                                    title = asset.title,
                                    urlOrContent = asset.uriOrContent,
                                    format = asset.format
                                )
                            },
                            onDelete = { viewModel.deleteAsset(asset) },
                            onCopyScript = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("CineAI Script", asset.uriOrContent))
                                Toast.makeText(context, "Script copied!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Video Trimmer Dialog inside Projects
    if (activeTrimmingAsset != null) {
        VideoTrimmerDialog(
            asset = activeTrimmingAsset!!,
            startSec = trimStartSec,
            endSec = trimEndSec,
            selectedFormat = trimFormat,
            onStartChange = { viewModel.trimStart.value = it },
            onEndChange = { viewModel.trimEnd.value = it },
            onFormatChange = { viewModel.trimExportFormat.value = it },
            onSaveTrim = { viewModel.saveTrimChanges() },
            onShare = {
                viewModel.shareVideoClip(
                    context = context,
                    title = activeTrimmingAsset!!.title,
                    urlOrContent = activeTrimmingAsset!!.uriOrContent,
                    format = trimFormat
                )
            },
            onDismiss = { viewModel.closeTrimmer() }
        )
    }

    if (showCreateProjectDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateProjectDialog = false },
            onCreate = { name, desc, cat, duration ->
                viewModel.createProject(name, desc, cat, duration)
                showCreateProjectDialog = false
            }
        )
    }

    if (showAddAssetDialog && selectedProject != null) {
        AddAssetDialog(
            projectId = selectedProject!!.id,
            onDismiss = { showAddAssetDialog = false },
            onAdd = { type, title, content, duration, format ->
                viewModel.addAssetToProject(selectedProject!!.id, type, title, content, duration, format)
                showAddAssetDialog = false
            }
        )
    }
}

@Composable
fun ProjectAssetCard(
    asset: ProjectAssetEntity,
    onTrim: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onCopyScript: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = StudioCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (asset.type) {
                        "VIDEO" -> Icons.Default.Videocam
                        "IMAGE" -> Icons.Default.Image
                        else -> Icons.Default.Description
                    }
                    val badgeColor = when (asset.type) {
                        "VIDEO" -> StudioPurple
                        "IMAGE" -> StudioCyan
                        else -> StudioAmber
                    }
                    Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = asset.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (asset.type) {
                "IMAGE" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(asset.uriOrContent).crossfade(true).build(),
                            contentDescription = asset.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                "VIDEO" -> {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0D0B18),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Clip Duration: ${asset.durationSeconds}s (Trim: ${asset.trimStartSec}s - ${asset.trimEndSec}s)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = StudioCyan
                                )
                                Text(
                                    text = "Format: ${asset.format} • Ready for Social Export",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = onTrim,
                                    colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.ContentCut, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Trim", color = Color.Black, fontSize = 11.sp)
                                }
                                IconButton(onClick = onShare) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = StudioPurple)
                                }
                            }
                        }
                    }
                }
                "SCRIPT" -> {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StudioDarkBg,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = asset.uriOrContent,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                maxLines = 4,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(
                                onClick = onCopyScript,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = StudioAmber, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy Script Text", color = StudioAmber, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, category: String, duration: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Sci-Fi Short") }
    var duration by remember { mutableStateOf("120") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Production Project") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Project Premise / Description") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (Sci-Fi, Documentary, Commercial, Viral)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Target Duration Seconds (up to 180s / 3m)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(title, desc, category, duration.toIntOrNull() ?: 180) },
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyan)
            ) {
                Text("Create Project", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddAssetDialog(
    projectId: Long,
    onDismiss: () -> Unit,
    onAdd: (type: String, title: String, content: String, duration: Int, format: String) -> Unit
) {
    var selectedType by remember { mutableStateOf("SCRIPT") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Asset to Project") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("SCRIPT", "IMAGE", "VIDEO").forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type) }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Asset Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(if (selectedType == "SCRIPT") "Script / Screenplay Content" else "URL / Path") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedType == "VIDEO") {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration in Seconds (up to 180s)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fmt = if (selectedType == "VIDEO") "MP4" else if (selectedType == "IMAGE") "PNG" else "TXT"
                    onAdd(selectedType, title, content, duration.toIntOrNull() ?: 0, fmt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyan)
            ) {
                Text("Add Asset", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
