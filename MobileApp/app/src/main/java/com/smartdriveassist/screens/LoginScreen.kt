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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartdriveassist.ui.components.PoliceBadgeIcon
import com.smartdriveassist.ui.components.SdaTextField
import com.smartdriveassist.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe      by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundPage)) {

        // ── Green hero header ──────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen900)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Decorative circles suggestion via nested boxes
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(90.dp).clip(RoundedCornerShape(45.dp))
                    .background(DarkGreen800.copy(alpha = 0.4f)))
                PoliceBadgeIcon(modifier = Modifier.size(70.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("Law Enforcement", color = BadgeGreen.copy(alpha = 0.7f),
                fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 3.sp)
            Text("Smart Drive Assist", color = Color.White,
                fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Secure Officer Portal", color = Color.White.copy(alpha = 0.45f),
                fontSize = 12.sp)
        }

        // ── Form ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Sign in to your account", fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)

            SdaTextField(
                value = email, onValueChange = { email = it },
                label = "Officer Email", placeholder = "officer@department.gov",
                leadingIcon = Icons.Default.Email
            )

            SdaTextField(
                value = password, onValueChange = { password = it },
                label = "Password", placeholder = "Enter your password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible }
            )

            // Remember me + Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }) {
                    Switch(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        modifier = Modifier.height(22.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DarkGreen800,
                            uncheckedTrackColor = BorderField
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Remember me", fontSize = 13.sp, color = TextSecondary)
                }
                Text("Forgot password?", fontSize = 12.sp,
                    color = DarkGreen600, fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { })
            }

            // Sign In button
            Button(
                onClick = { isLoading = true; onLoginSuccess() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen800)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Sign In", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Divider(color = BorderLight, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))

            // Secure note
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Lock, contentDescription = null,
                    tint = DarkGreen700, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(5.dp))
                Text("256-bit encrypted · Authorized personnel only",
                    fontSize = 11.sp, color = TextHint, textAlign = TextAlign.Center)
            }
        }
    }
}
