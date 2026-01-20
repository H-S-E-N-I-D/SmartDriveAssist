package com.smartdriveassist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.smartdriveassist.navigation.Screen
import com.smartdriveassist.ui.components.SdaBottomNav
import com.smartdriveassist.ui.theme.*

@Composable
fun SettingsScreen(navController: NavHostController) {
    val currentRoute = Screen.Settings.route
    var notificationsEnabled by remember { mutableStateOf(true) }
    var locationEnabled      by remember { mutableStateOf(true) }
    var darkMode             by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            SdaBottomNav(currentRoute = currentRoute) { route ->
                navController.navigate(route) {
                    popUpTo(Screen.Dashboard.route) { saveState = true }
                    launchSingleTop = true
                    restoreState    = true
                }
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGreen900)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text("Settings", color = Color.White,
                    fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text("Colombo Division · Officer #OFC0042",
                    color = BadgeGreen.copy(alpha = 0.6f), fontSize = 11.sp)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Profile card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(0.5.dp, BorderLight, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape)
                            .background(DarkGreen800),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("DM", color = BadgeGreen,
                            fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dinesh Madushanka", fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("Officer ID: OFC0042", fontSize = 12.sp, color = TextSecondary)
                        Box(
                            modifier = Modifier.padding(top = 4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(LightGreen100)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Colombo Division", fontSize = 10.sp,
                                color = DarkGreen600, fontWeight = FontWeight.Medium)
                        }
                    }
                    Icon(Icons.Default.Edit, null,
                        tint = TextHint, modifier = Modifier.size(18.dp))
                }

                // Preferences section
                SettingsSection("Preferences") {
                    SettingsToggleRow(
                        icon  = Icons.Default.Notifications,
                        label = "Push notifications",
                        desc  = "Receive alert notifications",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    Divider(color = BorderLight, thickness = 0.5.dp)
                    SettingsToggleRow(
                        icon  = Icons.Default.LocationOn,
                        label = "Location services",
                        desc  = "Enable GPS for navigation",
                        checked = locationEnabled,
                        onCheckedChange = { locationEnabled = it }
                    )
                    Divider(color = BorderLight, thickness = 0.5.dp)
                    SettingsToggleRow(
                        icon  = Icons.Default.DarkMode,
                        label = "Dark mode",
                        desc  = "Switch to dark theme",
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    )
                }

                // Account section
                SettingsSection("Account") {
                    SettingsNavRow(icon = Icons.Default.Lock,    label = "Change password")
                    Divider(color = BorderLight, thickness = 0.5.dp)
                    SettingsNavRow(icon = Icons.Default.Badge,   label = "Update officer details")
                    Divider(color = BorderLight, thickness = 0.5.dp)
                    SettingsNavRow(icon = Icons.Default.History, label = "Activity log")
                }

                // Support section
                SettingsSection("Support") {
                    SettingsNavRow(icon = Icons.Default.Help,        label = "Help & FAQ")
                    Divider(color = BorderLight, thickness = 0.5.dp)
                    SettingsNavRow(icon = Icons.Default.Info,         label = "About Smart Drive Assist")
                    Divider(color = BorderLight, thickness = 0.5.dp)
                    SettingsNavRow(icon = Icons.Default.PrivacyTip,   label = "Privacy policy")
                }

                // Sign out
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SevereRedBg)
                        .border(0.5.dp, SevereRed.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .clickable {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.Logout, null,
                            tint = SevereRedText, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sign Out", fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold, color = SevereRedText)
                    }
                }

                // App version
                Text("Smart Drive Assist v1.0.0",
                    fontSize = 11.sp, color = TextHint,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(0.5.dp, BorderLight, RoundedCornerShape(14.dp))
    ) {
        Text(title.uppercase(),
            fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
            color = TextSecondary, letterSpacing = 0.4.sp,
            modifier = Modifier.padding(start = 14.dp, top = 12.dp, bottom = 6.dp))
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector, label: String, desc: String,
    checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(9.dp))
                .background(LightGreen100),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = DarkGreen700, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(desc, fontSize = 11.sp, color = TextHint)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor  = Color.White,
                checkedTrackColor  = DarkGreen800,
                uncheckedTrackColor = BorderField
            )
        )
    }
}

@Composable
private fun SettingsNavRow(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(9.dp))
                .background(LightGreen100),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = DarkGreen700, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            color = TextPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null,
            tint = TextHint, modifier = Modifier.size(18.dp))
    }
}
