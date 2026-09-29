package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiModels
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun SettingsScreen(
    viewModel: StudioViewModel
) {
    val context = LocalContext.current
    val currentModel by viewModel.selectedModel.collectAsState()
    val savedApiKey by viewModel.apiKey.collectAsState()
    val savedBackendUrl by viewModel.backendUrl.collectAsState()

    var tempKey by remember(savedApiKey) { mutableStateOf(savedApiKey) }
    var tempUrl by remember(savedBackendUrl) { mutableStateOf(savedBackendUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Modular Studio & Models",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Free-first modular AI architecture. Swap models freely with zero lock-in.",
                style = MaterialTheme.typography.bodySmall,
                color = StudioCyan
            )
        }

        // Free-First Guarantee Banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F1E19),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = StudioGreen,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "100% Free-First AI Studio Guarantee",
                        fontWeight = FontWeight.Bold,
                        color = StudioGreen,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "No subscription traps, no mandatory paid keys. Seedance 2.5 and open-source models work out-of-the-box for all modules.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Modular Model Selector
        Text(
            text = "Active AI Generation Engine:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        AiModels.AVAILABLE_MODELS.forEach { modelOption ->
            val isSelected = currentModel == modelOption.id
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) StudioCard.copy(alpha = 0.9f) else StudioDarkBg,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) StudioCyan else StudioCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setModel(modelOption.id)
                        Toast.makeText(context, "Switched model to ${modelOption.name}", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { viewModel.setModel(modelOption.id) },
                        colors = RadioButtonDefaults.colors(selectedColor = StudioCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = modelOption.name,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) StudioCyan else TextPrimary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (modelOption.isFree) {
                                Badge(containerColor = StudioGreen.copy(alpha = 0.2f)) {
                                    Text("FREE", color = StudioGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Badge(containerColor = StudioAmber.copy(alpha = 0.2f)) {
                                    Text("OPTIONAL API", color = StudioAmber, fontSize = 9.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = modelOption.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Divider(color = StudioCardBorder)

        // Optional API Connection (Not required)
        Text(
            text = "Optional API & Custom Endpoints:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "If you wish to connect your own Gemini API key or private Google Cloud server, configure them below:",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        OutlinedTextField(
            value = tempKey,
            onValueChange = { tempKey = it },
            label = { Text("Optional Gemini API Key") },
            placeholder = { Text("Leave blank to keep 100% free mode") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_api_key_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioCyan,
                unfocusedBorderColor = StudioCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = tempUrl,
            onValueChange = { tempUrl = it },
            label = { Text("Custom Cloud Run / Ollama Backend URL") },
            placeholder = { Text("https://your-custom-backend.run.app") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioCyan,
                unfocusedBorderColor = StudioCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Button(
            onClick = {
                viewModel.setApiKey(tempKey)
                viewModel.setBackendUrl(tempUrl)
                Toast.makeText(context, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Studio Configuration", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
