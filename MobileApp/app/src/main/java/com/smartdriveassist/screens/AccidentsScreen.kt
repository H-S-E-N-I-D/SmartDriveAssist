package com.smartdriveassist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.smartdriveassist.navigation.Screen
import com.smartdriveassist.ui.components.*
import com.smartdriveassist.ui.theme.*

data class AccidentItem(
    val id: String,
    val datetime: String,
    val severity: String,
    val location: String,
    val status: String
)

val sampleAccidents = listOf(
    AccidentItem("RAC-COL-20260325-54616", "2026/03/25 · 08:42 AM", "Severe",
        "Galle Rd, Bambalapitiya, Colombo 4", "Inspection in Progress"),
    AccidentItem("RAC-COL-20260323-48821", "2026/03/23 · 03:15 PM", "Moderate",
        "Marine Dr, Colombo Fort, Colombo 1", "Reported"),
    AccidentItem("RAC-COL-20260320-39204", "2026/03/20 · 11:05 AM", "Severe",
        "Duplication Rd, Kollupitiya, Colombo 3", "Reported"),
    AccidentItem("RAC-COL-20260315-27762", "2026/03/15 · 05:50 PM", "Moderate",
        "Baseline Rd, Maradana, Colombo 10", "Inspection in Progress"),
    AccidentItem("RAC-COL-20260308-14290", "2026/03/08 · 09:10 AM", "Minor",
        "Negombo Rd, Wattala, Colombo 15", "Inspection Completed")
)

@Composable
fun AccidentsScreen(navController: NavHostController) {
    val filters = listOf("All (5)", "Reported (2)", "In Progress (2)", "Completed (1)", "Severe", "Moderate")
    var activeFilter by remember { mutableStateOf("All (5)") }
    val currentRoute = Screen.Accidents.route

    Scaffold(
        bottomBar = {
            SdaBottomNav(currentRoute = currentRoute) { route ->
                navController.navigate(route) {
                    popUpTo(Screen.Dashboard.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
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
                    Text("My Reportings", color = Color.White,
                        fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text("Colombo Division · Dinesh Madushanka",
                        color = BadgeGreen.copy(alpha = 0.6f), fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FilterList, null,
                        tint = BadgeGreen.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                }
            }

            // Filter pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGreen900)
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                filters.forEach { filter ->
                    val isActive = activeFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isActive) BadgeGreen else Color.White.copy(alpha = 0.08f)
                            )
                            .border(
                                1.dp,
                                if (isActive) BadgeGreen else Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { activeFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(filter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isActive) TextPrimary else Color.White.copy(alpha = 0.6f))
                    }
                }
            }

            // Summary chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Total" to "5", "Severe" to "2", "Moderate" to "2", "Minor" to "1")
                    .forEachIndexed { i, (label, count) ->
                        val countColor = listOf(TextPrimary, SevereRed, ModerateAmber, MinorGreen)[i]
                        Column(
                            modifier = Modifier.weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(0.5.dp, BorderLight, RoundedCornerShape(10.dp))
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(label, fontSize = 10.sp, color = TextHint)
                            Spacer(Modifier.height(2.dp))
                            Text(count, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = countColor)
                        }
                    }
            }

            // List
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sampleAccidents.forEach { accident ->
                    AccidentCard(accident)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AccidentCard(item: AccidentItem) {
    val severityBarColor = when (item.severity.lowercase()) {
        "severe"   -> SevereRed
        "moderate" -> ModerateAmber
        else       -> MinorGreen
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(0.5.dp, BorderLight, RoundedCornerShape(16.dp))
            .clickable { }
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Severity bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .defaultMinSize(minHeight = 54.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(severityBarColor)
                    .fillMaxHeight()
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                // Accident ID
                Text(
                    item.id,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.3.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(item.datetime, fontSize = 11.sp, color = TextSecondary)
                    SeverityBadge(item.severity)
                }
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null,
                        tint = TextHint, modifier = Modifier.size(11.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(item.location, fontSize = 11.sp, color = TextHint,
                        maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }
        }

        Divider(color = Color(0xFFEEF4EE), thickness = 0.5.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusBadge(item.status)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { }
            ) {
                Text("View", fontSize = 11.sp, color = DarkGreen800, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Default.ChevronRight, null,
                    tint = DarkGreen800, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AccidentsScreenPreview() {

    // Create a fake NavController for preview
    val navController = androidx.navigation.compose.rememberNavController()

    AccidentsScreen(
        navController = navController,
    )
}
