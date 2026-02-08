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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.smartdriveassist.navigation.Screen
import com.smartdriveassist.ui.components.SdaBottomNav
import com.smartdriveassist.ui.theme.*

@Composable
fun DashboardScreen(
    navController: NavHostController,
    onReportAccident: () -> Unit
) {
    val currentRoute = Screen.Dashboard.route

    Scaffold(
        bottomBar = {
            Column {
                // FAB strip — always above bottom nav
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundPage)
                        .border(0.5.dp, Color(0xFFDDEEDD), RoundedCornerShape(0.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkGreen900)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("Report new accident", fontSize = 10.sp,
                            color = Color.White, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.width(10.dp))
                    FloatingActionButton(
                        onClick = onReportAccident,
                        modifier = Modifier.size(48.dp),
                        containerColor = DarkGreen800,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Report Accident",
                            tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
                SdaBottomNav(currentRoute = currentRoute) { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGreen900)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Dashboard", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text("Colombo Division", color = BadgeGreen.copy(alpha = 0.6f), fontSize = 11.sp)
                }
                Box {
                    Box(
                        modifier = Modifier.size(38.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Alerts",
                            tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Box(
                        modifier = Modifier.size(8.dp).clip(CircleShape)
                            .background(NotifAmber)
                            .border(1.5.dp, DarkGreen900, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            // Scrollable body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Welcome card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(0.5.dp, BorderLight, RoundedCornerShape(14.dp))
                        .padding(13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(DarkGreen800),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("JD", color = BadgeGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Good morning,", fontSize = 11.sp, color = TextSecondary)
                        Text("Officer John Doe", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(LightGreen100)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("On Duty · Badge #OFC0042", fontSize = 10.sp,
                                color = DarkGreen600, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Stat cards 2x2
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    StatCard(
                        label = "Received Alerts",
                        count = "24",
                        countColor = Color(0xFFBA7517),
                        iconBg = Color(0xFFFFF4E0),
                        icon = Icons.Default.Notifications,
                        iconColor = Color(0xFFBA7517),
                        meta = listOf("Today" to "+3", "Unread" to "7", "Priority" to "2 high"),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Inspected",
                        count = "18",
                        countColor = Color(0xFFA32D2D),
                        iconBg = Color(0xFFFCE8E8),
                        icon = Icons.Default.Warning,
                        iconColor = Color(0xFFA32D2D),
                        meta = listOf("This week" to "+5", "Minor" to "11", "Major" to "7"),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    StatCard(
                        label = "Ongoing",
                        count = "6",
                        countColor = Color(0xFF185FA5),
                        iconBg = Color(0xFFE6F1FB),
                        icon = Icons.Default.Search,
                        iconColor = Color(0xFF185FA5),
                        meta = listOf("Assigned" to "4", "Pending" to "2", "Avg time" to "1.2h"),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Completed",
                        count = "142",
                        countColor = DarkGreen600,
                        iconBg = LightGreen100,
                        icon = Icons.Default.CheckCircle,
                        iconColor = DarkGreen600,
                        meta = listOf("This month" to "+22", "Filed" to "138", "Rate" to "97%"),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Scroll hint
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, null,
                        tint = TextHint, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Scroll down to see accident trends",
                        fontSize = 10.sp, color = TextHint)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.KeyboardArrowDown, null,
                        tint = TextHint, modifier = Modifier.size(14.dp))
                }

                // Trends chart placeholder
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color.White)
                        .border(0.5.dp, BorderLight, RoundedCornerShape(13.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Accident trends", fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                .background(BackgroundPage)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Last 7 days", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LegendDot(color = DarkGreen800, label = "Reported")
                        LegendDot(color = ModerateAmber, label = "Inspected")
                    }
                    Spacer(Modifier.height(10.dp))
                    // Simple bar chart visualization
                    SimpleBarChart(
                        reportedData = listOf(5, 8, 6, 11, 7, 4, 9),
                        inspectedData = listOf(4, 6, 5, 9, 6, 3, 7),
                        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    )
                }

                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String, count: String, countColor: Color,
    iconBg: Color, icon: ImageVector, iconColor: Color,
    meta: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(Color.White)
            .border(0.5.dp, BorderLight, RoundedCornerShape(13.dp))
            .padding(11.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(7.dp)).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Text(count, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = countColor)
        Divider(color = Color(0xFFEEF4EE), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
        meta.forEach { (k, v) ->
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(k, fontSize = 10.sp, color = TextHint)
                Text(v, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(5.dp))
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun SimpleBarChart(
    reportedData: List<Int>,
    inspectedData: List<Int>,
    labels: List<String>
) {
    val maxVal = (reportedData + inspectedData).maxOrNull()?.toFloat() ?: 1f
    val chartHeight = 120.dp

    Row(
        modifier = Modifier.fillMaxWidth().height(chartHeight),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        reportedData.zip(inspectedData).zip(labels).forEach { (pair, label) ->
            val (rep, ins) = pair
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(7.dp)
                            .fillMaxHeight((rep / maxVal).coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(DarkGreen800)
                    )
                    Box(
                        modifier = Modifier
                            .width(7.dp)
                            .fillMaxHeight((ins / maxVal).coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(ModerateAmber)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(label, fontSize = 9.sp, color = TextHint)
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {

    // Create a fake NavController for preview
    val navController = androidx.navigation.compose.rememberNavController()

    DashboardScreen(
        navController = navController,
        onReportAccident = {}
    )
}