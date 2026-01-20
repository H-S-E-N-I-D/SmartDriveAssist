package com.smartdriveassist.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartdriveassist.ui.components.SdaTextField
import com.smartdriveassist.ui.theme.*

@Composable
fun ReportAccidentScreen(onBack: () -> Unit) {
    var location        by remember { mutableStateOf("Galle Rd, Bambalapitiya, Colombo 4") }
    var description     by remember { mutableStateOf("") }
    var severity        by remember { mutableStateOf("moderate") }
    var severityDrop    by remember { mutableStateOf("moderate") }
    var dropExpanded    by remember { mutableStateOf(false) }
    var photoCount      by remember { mutableStateOf(0) }
    var submitted       by remember { mutableStateOf(false) }
    var submitting      by remember { mutableStateOf(false) }

    val severities = listOf(
        "minor"    to "Minor — No injuries, minor damage",
        "moderate" to "Moderate — Some injuries, significant damage",
        "severe"   to "Severe — Critical injuries or fatalities"
    )

    Scaffold(
        bottomBar = {
            Column {
                // Submit button strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundPage)
                        .border(0.5.dp, Color(0xFFDDEEDD), RoundedCornerShape(0.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Button(
                        onClick = {
                            submitting = true
                            submitted  = false
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (submitted) DarkGreen900 else DarkGreen800
                        ),
                        enabled = !submitting
                    ) {
                        if (submitting && !submitted) {
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(1800)
                                submitting = false
                                submitted  = true
                            }
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Submitting...", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        } else if (submitted) {
                            Icon(Icons.Default.CheckCircle, null,
                                tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Report Submitted", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        } else {
                            Icon(Icons.Default.CheckCircle, null,
                                tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Submit Report", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Spacer for bottom nav look
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(bottom = 14.dp, top = 10.dp)
                )
            }
        },
        containerColor = BackgroundPage
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGreen900)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBackIosNew, null,
                        tint = BadgeGreen.copy(alpha = 0.85f), modifier = Modifier.size(15.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Report Accident", color = Color.White,
                        fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("Colombo Division · #OFC0042",
                        color = BadgeGreen.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }

            // Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // ── Location ──────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    FieldLabel("Accident Location", required = true)
                    SdaTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = "",
                        placeholder = "Search or tap to pin on map...",
                        leadingIcon = Icons.Default.LocationOn
                    )
                    // Confirmed location preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightGreen100)
                            .border(0.5.dp, BorderField, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null,
                            tint = DarkGreen600, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(location.ifEmpty { "Tap to select location" },
                            fontSize = 11.sp, color = MinorGreenText,
                            fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.Check, null,
                            tint = DarkGreen600, modifier = Modifier.size(13.dp))
                    }
                }

                // ── Description ───────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    FieldLabel("Description", required = true)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= 300) description = it },
                        placeholder = {
                            Text("Describe what happened, number of vehicles involved, injuries...",
                                color = TextHint, fontSize = 13.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Description, null,
                                tint = DarkGreen800.copy(alpha = 0.45f),
                                modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor    = DarkGreen800,
                            unfocusedBorderColor  = BorderField,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = BackgroundField,
                            focusedTextColor      = TextPrimary,
                            unfocusedTextColor    = TextPrimary
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                        minLines = 3,
                        maxLines = 6
                    )
                    Text("${description.length} / 300",
                        fontSize = 10.sp, color = TextHint,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 2.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }

                // ── Severity ──────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FieldLabel("Accident Severity", required = true)

                    // Toggle cards
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("minor", "moderate", "severe").forEach { sev ->
                            val isSelected = severity == sev
                            val (barColor, bg, textColor, label) = when (sev) {
                                "minor"    -> listOf(MinorGreen, MinorGreenBg, MinorGreenText, "Minor")
                                "moderate" -> listOf(ModerateAmber, ModerateAmberBg, ModerateAmberText, "Moderate")
                                else       -> listOf(SevereRed, SevereRedBg, SevereRedText, "Severe")
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) bg as Color else Color.White)
                                    .border(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) barColor as Color else BorderLight,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { severity = sev; severityDrop = sev }
                                    .padding(vertical = 9.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(barColor as Color))
                                Text(label as String, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    color = if (isSelected) textColor as Color else TextSecondary)
                            }
                        }
                    }

                    // Dropdown
                    ExposedDropdownMenuBox(
                        expanded = dropExpanded,
                        onExpandedChange = { dropExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = severities.firstOrNull { it.first == severityDrop }?.second ?: "",
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                Icon(Icons.Default.Warning, null,
                                    tint = DarkGreen800.copy(alpha = 0.45f), modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropExpanded)
                            },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor    = DarkGreen800,
                                unfocusedBorderColor  = BorderField,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = BackgroundField,
                                focusedTextColor      = TextPrimary,
                                unfocusedTextColor    = TextPrimary
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                        )
                        ExposedDropdownMenu(
                            expanded = dropExpanded,
                            onDismissRequest = { dropExpanded = false }
                        ) {
                            severities.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, fontSize = 13.sp) },
                                    onClick = {
                                        severityDrop = key
                                        severity     = key
                                        dropExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // ── Attach Images ─────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FieldLabel("Attach Images", required = false)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.5.dp, Color(0xFFB0D0B0), RoundedCornerShape(12.dp),
                                // dashed look with alpha-blended border
                            )
                            .clickable { if (photoCount < 3) photoCount++ }
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, null,
                                tint = DarkGreen600, modifier = Modifier.size(30.dp))
                            Text("Tap to attach photos",
                                fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text("JPG, PNG · Max 5MB per image",
                                fontSize = 10.sp, color = TextHint)
                        }
                    }

                    if (photoCount > 0) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val thumbColors = listOf(
                                LightGreen100 to DarkGreen600,
                                StatusBlueBg  to StatusBlue,
                                ModerateAmberBg to ModerateAmber
                            )
                            repeat(photoCount) { i ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(thumbColors[i].first)
                                        .border(0.5.dp, BorderLight, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(Icons.Default.Image, null,
                                            tint = thumbColors[i].second, modifier = Modifier.size(20.dp))
                                        Text("Photo ${i+1}", fontSize = 9.sp,
                                            color = thumbColors[i].second, fontWeight = FontWeight.Medium)
                                    }
                                    // Remove button
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SevereRed)
                                            .align(Alignment.TopEnd)
                                            .clickable { photoCount-- },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Close, null,
                                            tint = Color.White, modifier = Modifier.size(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String, required: Boolean) {
    Row {
        Text(text.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
            color = DarkGreen700, letterSpacing = 0.4.sp)
        if (required) {
            Text(" *", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SevereRed)
        }
    }
}
