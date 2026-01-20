package com.smartdriveassist.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartdriveassist.ui.components.PoliceBadgeIcon
import com.smartdriveassist.ui.components.SdaTextField
import com.smartdriveassist.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var firstName       by remember { mutableStateOf("") }
    var lastName        by remember { mutableStateOf("") }
    var officerId       by remember { mutableStateOf("") }
    var division        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var mobile          by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var pwVisible       by remember { mutableStateOf(false) }
    var cpVisible       by remember { mutableStateOf(false) }
    var divExpanded     by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }

    val divisions = listOf("Colombo","Kandy","Galle","Jaffna","Kurunegala",
        "Ratnapura","Badulla","Trincomalee","Batticaloa","Anuradhapura","Matara","Negombo")

    val passwordStrength = when {
        password.length < 4 -> 0
        password.length < 8 -> 1
        password.matches(Regex(".*[A-Z].*")) && password.matches(Regex(".*[0-9].*")) &&
                password.matches(Regex(".*[^A-Za-z0-9].*")) -> 4
        password.matches(Regex(".*[A-Z].*")) || password.matches(Regex(".*[0-9].*")) -> 3
        else -> 2
    }
    val strengthColor = listOf(Color.Transparent, SevereRed, ModerateAmber, MinorGreen, DarkGreen800)
    val strengthLabel = listOf("", "Weak", "Fair", "Good", "Strong")

    Column(modifier = Modifier.fillMaxSize().background(BackgroundPage)) {

        // Compact header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen900)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(21.dp))
                    .background(DarkGreen800.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Shield, tint = BadgeGreen,
                    contentDescription = null, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Law Enforcement", color = BadgeGreen.copy(alpha = 0.7f),
                    fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.5.sp)
                Text("Smart Drive Assist", color = Color.White,
                    fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text("Officer Registration", color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
            }
        }

        // Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Text("Create your officer account", fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)

            // First + Last name
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SdaTextField(
                    value = firstName, onValueChange = { firstName = it },
                    label = "First Name", placeholder = "John",
                    leadingIcon = Icons.Default.Person,
                    modifier = Modifier.weight(1f)
                )
                SdaTextField(
                    value = lastName, onValueChange = { lastName = it },
                    label = "Last Name", placeholder = "Doe",
                    leadingIcon = Icons.Default.Person,
                    modifier = Modifier.weight(1f)
                )
            }

            // Officer ID + Division
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SdaTextField(
                    value = officerId.uppercase(),
                    onValueChange = { if (it.length <= 10) officerId = it.uppercase() },
                    label = "Officer ID", placeholder = "OFC0012345",
                    leadingIcon = Icons.Default.Badge,
                    hint = "${officerId.length}/10",
                    modifier = Modifier.weight(1f)
                )
                // Division dropdown
                Column(modifier = Modifier.weight(1f)) {
                    Text("Division".uppercase(), fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold, color = DarkGreen700, letterSpacing = 0.4.sp)
                    Spacer(Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = divExpanded,
                        onExpandedChange = { divExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = division.ifEmpty { "Select..." },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = divExpanded)
                            },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkGreen800,
                                unfocusedBorderColor = BorderField,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = BackgroundField,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = if (division.isEmpty()) TextHint else TextPrimary
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                        )
                        ExposedDropdownMenu(
                            expanded = divExpanded,
                            onDismissRequest = { divExpanded = false }
                        ) {
                            divisions.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d, fontSize = 13.sp) },
                                    onClick = { division = d; divExpanded = false }
                                )
                            }
                        }
                    }
                }
            }

            SdaTextField(
                value = email, onValueChange = { email = it },
                label = "Email Address", placeholder = "officer@department.gov",
                leadingIcon = Icons.Default.Email
            )

            // Mobile with country code prefix
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BackgroundField)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🇱🇰  +94", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
                SdaTextField(
                    value = mobile, onValueChange = { mobile = it },
                    label = "Mobile Number", placeholder = "07X XXX XXXX",
                    leadingIcon = Icons.Default.Phone,
                    modifier = Modifier.weight(1f)
                )
            }

            // Password
            SdaTextField(
                value = password, onValueChange = { password = it },
                label = "Password", placeholder = "Create a password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true, passwordVisible = pwVisible,
                onTogglePassword = { pwVisible = !pwVisible }
            )
            // Strength bar
            if (password.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(4) { i ->
                        Box(
                            modifier = Modifier
                                .weight(1f).height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (i < passwordStrength) strengthColor[passwordStrength] else BorderLight)
                        )
                    }
                }
                Text(strengthLabel[passwordStrength], fontSize = 10.sp,
                    color = strengthColor[passwordStrength],
                    modifier = Modifier.padding(start = 2.dp, top = 2.dp))
            }

            // Confirm password
            SdaTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = "Confirm Password", placeholder = "Re-enter your password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true, passwordVisible = cpVisible,
                onTogglePassword = { cpVisible = !cpVisible }
            )
            if (confirmPassword.isNotEmpty()) {
                val match = password == confirmPassword
                Text(
                    if (match) "Passwords match" else "Passwords do not match",
                    fontSize = 10.sp,
                    color = if (match) MinorGreen else SevereRed,
                    modifier = Modifier.padding(start = 2.dp, top = 2.dp)
                )
            }

            Button(
                onClick = { isLoading = true; onRegisterSuccess() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen800)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Create Account", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Already have an account? ", fontSize = 13.sp, color = TextHint)
                Text("Sign in", fontSize = 13.sp, color = DarkGreen600,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateToLogin() })
            }

            Divider(color = BorderLight, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 2.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, null, tint = DarkGreen700, modifier = Modifier.size(11.dp))
                Spacer(Modifier.width(5.dp))
                Text("256-bit encrypted · Authorized personnel only",
                    fontSize = 10.sp, color = TextHint, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
