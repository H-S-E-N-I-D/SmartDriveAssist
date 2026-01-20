package com.smartdriveassist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartdriveassist.navigation.Screen
import com.smartdriveassist.ui.theme.*

// ──────────────────────────────────────────────
// Top App Header
// ──────────────────────────────────────────────
@Composable
fun SdaHeader(
    title: String,
    subtitle: String? = null,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGreen900)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                if (showBack) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back",
                            tint = BadgeGreen.copy(alpha = 0.85f), modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                }
                Column {
                    Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    if (subtitle != null) {
                        Text(subtitle, color = BadgeGreen.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                }
            }
            Row(content = actions)
        }
    }
}

// ──────────────────────────────────────────────
// Bottom Navigation Bar
// ──────────────────────────────────────────────
data class NavItem(val label: String, val icon: ImageVector, val route: String)

val bottomNavItems = listOf(
    NavItem("Home",      Icons.Default.Home,        Screen.Dashboard.route),
    NavItem("Accidents", Icons.Default.Warning,      Screen.Accidents.route),
    NavItem("Navigate",  Icons.Default.Navigation,   Screen.Navigate.route),
    NavItem("Settings",  Icons.Default.Settings,     Screen.Settings.route)
)

@Composable
fun SdaBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.border(0.5.dp, BorderLight, RoundedCornerShape(0.dp))
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick  = { onNavigate(item.route) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) LightGreen100 else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = item.label,
                            tint = if (selected) DarkGreen800 else TextHint,
                            modifier = Modifier.size(18.dp))
                    }
                },
                label = {
                    Text(item.label,
                        fontSize = 10.sp,
                        color = if (selected) DarkGreen800 else TextHint,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

// ──────────────────────────────────────────────
// Text Field
// ──────────────────────────────────────────────
@Composable
fun SdaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    hint: String? = null,
    maxLines: Int = 1,
    minLines: Int = 1
) {
    Column(modifier = modifier) {
        Text(
            label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGreen700,
            letterSpacing = 0.4.sp
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextHint, fontSize = 13.sp) },
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null,
                    tint = DarkGreen800.copy(alpha = 0.45f), modifier = Modifier.size(18.dp))
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = DarkGreen800.copy(alpha = 0.45f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else trailingIcon,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
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
            maxLines = maxLines,
            minLines = minLines,
            singleLine = maxLines == 1
        )
        if (hint != null) {
            Text(hint, fontSize = 10.sp, color = TextHint, modifier = Modifier.padding(top = 3.dp, start = 2.dp))
        }
    }
}

// ──────────────────────────────────────────────
// Severity Badge
// ──────────────────────────────────────────────
@Composable
fun SeverityBadge(severity: String) {
    val (bg, textColor) = when (severity.lowercase()) {
        "severe"   -> SevereRedBg    to SevereRedText
        "moderate" -> ModerateAmberBg to ModerateAmberText
        else       -> MinorGreenBg   to MinorGreenText
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(severity, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

// ──────────────────────────────────────────────
// Status Badge
// ──────────────────────────────────────────────
@Composable
fun StatusBadge(status: String) {
    val (bg, textColor, dotColor) = when {
        status.contains("Progress", ignoreCase = true) ->
            Triple(ModerateAmberBg, ModerateAmberText, ModerateAmber)
        status.contains("Completed", ignoreCase = true) ->
            Triple(MinorGreenBg, MinorGreenText, MinorGreen)
        else ->
            Triple(StatusBlueBg, StatusBlueText, StatusBlue)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor))
        Spacer(Modifier.width(5.dp))
        Text(status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

// ──────────────────────────────────────────────
// Police Badge SVG-like composable
// ──────────────────────────────────────────────
@Composable
fun PoliceBadgeIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(DarkGreen800.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Shield,
            contentDescription = "Badge",
            tint = BadgeGreen,
            modifier = Modifier.size(44.dp)
        )
    }
}
