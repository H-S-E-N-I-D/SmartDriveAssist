package com.smartdriveassist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.smartdriveassist.navigation.Screen
import com.smartdriveassist.ui.components.SdaBottomNav
import com.smartdriveassist.ui.theme.*

data class VehicleOption(val id: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun NavigateScreen(navController: NavHostController) {
    val currentRoute = Screen.Navigate.route
    var origin      by remember { mutableStateOf("Bambalapitiya, Colombo 4") }
    var destination by remember { mutableStateOf("Colombo Fort Police Station") }
    var selectedVehicle by remember { mutableStateOf("car") }

    val vehicles = listOf(
        VehicleOption("car",        "Car",        Icons.Default.DirectionsCar),
        VehicleOption("van",        "Van",        Icons.Default.AirportShuttle),
        VehicleOption("motorbike",  "Motorbike",  Icons.Default.TwoWheeler),
        VehicleOption("tuk",        "3-Wheeler",  Icons.Default.ElectricRickshaw),
        VehicleOption("truck",      "Truck",      Icons.Default.LocalShipping)
    )

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
            // ── Compact green top panel with route inputs ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGreen900)
                    //.padding(horizontal = 14.dp, top = 10.dp, bottom = 14.dp)
            ) {
                // Route card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                ) {
                    // Origin row
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp))
                                .background(BadgeGreen)
                                .border(2.dp, Color(0xFF5AAA5A), RoundedCornerShape(5.dp)))
                            Box(modifier = Modifier.width(1.5.dp).height(16.dp)
                                .background(Color.White.copy(alpha = 0.2f)))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Your location".uppercase(), fontSize = 10.sp,
                                color = BadgeGreen.copy(alpha = 0.6f), letterSpacing = 0.4.sp,
                                fontWeight = FontWeight.Medium)
                            Text(origin, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                color = Color.White, maxLines = 1)
                        }
                        Box(
                            modifier = Modifier.size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, null,
                                tint = BadgeGreen.copy(alpha = 0.85f), modifier = Modifier.size(14.dp))
                        }
                    }

                    // Swap divider
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).height(0.5.dp)
                            .background(Color.White.copy(alpha = 0.1f)))
                        Box(
                            modifier = Modifier.size(26.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(13.dp))
                                .clickable {
                                    val tmp = origin; origin = destination; destination = tmp
                                }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SwapVert, null,
                                tint = BadgeGreen.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                        }
                        Box(modifier = Modifier.weight(1f).height(0.5.dp)
                            .background(Color.White.copy(alpha = 0.1f)))
                    }

                    // Destination row
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.width(1.5.dp).height(16.dp)
                                .background(Color.Transparent))
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp))
                                .background(SevereRed)
                                .border(2.dp, Color(0xFFA32D2D), RoundedCornerShape(5.dp)))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Destination".uppercase(), fontSize = 10.sp,
                                color = BadgeGreen.copy(alpha = 0.6f), letterSpacing = 0.4.sp,
                                fontWeight = FontWeight.Medium)
                            Text(destination, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                color = Color.White, maxLines = 1)
                        }
                        Box(
                            modifier = Modifier.size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, null,
                                tint = BadgeGreen.copy(alpha = 0.85f), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // ── Full-screen map placeholder ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE8F0E4))
            ) {
                // Map grid background
                MapPlaceholder()

                // Map controls
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    MapControlButton(icon = Icons.Default.Add) { }
                    MapControlButton(icon = Icons.Default.Remove) { }
                    Spacer(Modifier.height(4.dp))
                    MapControlButton(icon = Icons.Default.MyLocation) { }
                }

                // ETA chip
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(0.5.dp, BorderLight, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("12 min", fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = DarkGreen800)
                        Text("4.2 km", fontSize = 9.sp, color = TextHint)
                    }
                }

                // Destination label
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SevereRed)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("Fort Police Stn", fontSize = 10.sp,
                        color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            // ── Vehicle selector bottom panel ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(0.5.dp, BorderLight, RoundedCornerShape(0.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text("Select vehicle type".uppercase(),
                    fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                    color = TextSecondary, letterSpacing = 0.3.sp)
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    vehicles.forEach { vehicle ->
                        val isSelected = selectedVehicle == vehicle.id
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) LightGreen100 else Color.White)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) DarkGreen800 else BorderLight,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedVehicle = vehicle.id }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(vehicle.icon, contentDescription = vehicle.label,
                                tint = if (isSelected) DarkGreen800 else TextHint,
                                modifier = Modifier.size(22.dp))
                            Text(vehicle.label, fontSize = 9.sp, fontWeight = FontWeight.Medium,
                                color = if (isSelected) DarkGreen800 else TextHint)
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen800)
                ) {
                    Icon(Icons.Default.Navigation, null,
                        tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Start Navigation", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun MapControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(0.5.dp, BorderLight, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = DarkGreen800, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun MapPlaceholder() {
    // Draw a stylised road-grid map in Compose Canvas
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Road horizontals
        val hRoads = listOf(0.12f, 0.26f, 0.40f, 0.55f, 0.70f, 0.84f)
        hRoads.forEach { frac ->
            drawRect(
                color = Color(0xFFF5F5F2),
                topLeft = androidx.compose.ui.geometry.Offset(0f, h * frac - 6),
                size = androidx.compose.ui.geometry.Size(w, 12f)
            )
        }

        // Road verticals
        val vRoads = listOf(0.18f, 0.38f, 0.52f, 0.72f, 0.86f)
        vRoads.forEach { frac ->
            drawRect(
                color = Color(0xFFF5F5F2),
                topLeft = androidx.compose.ui.geometry.Offset(w * frac - 6, 0f),
                size = androidx.compose.ui.geometry.Size(12f, h)
            )
        }

        // Active route - vertical line (green)
        val routeX = w * 0.52f
        drawLine(
            color = Color(0xFF2D5A2D),
            start = androidx.compose.ui.geometry.Offset(routeX, h * 0.9f),
            end = androidx.compose.ui.geometry.Offset(routeX, h * 0.26f),
            strokeWidth = 10f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        // Route turn
        drawLine(
            color = Color(0xFF2D5A2D),
            start = androidx.compose.ui.geometry.Offset(routeX, h * 0.26f),
            end = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.26f),
            strokeWidth = 10f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Current location dot
        drawCircle(
            color = Color(0x332D5A2D),
            radius = 28f,
            center = androidx.compose.ui.geometry.Offset(routeX, h * 0.88f)
        )
        drawCircle(
            color = Color(0xFF2D5A2D),
            radius = 16f,
            center = androidx.compose.ui.geometry.Offset(routeX, h * 0.88f)
        )
        drawCircle(
            color = Color.White,
            radius = 8f,
            center = androidx.compose.ui.geometry.Offset(routeX, h * 0.88f)
        )

        // Destination pin circle
        drawCircle(
            color = Color(0xFFA32D2D),
            radius = 14f,
            center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.14f)
        )
        drawCircle(
            color = Color.White,
            radius = 6f,
            center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.14f)
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NavigationScreenPreview() {

    // Create a fake NavController for preview
    val navController = androidx.navigation.compose.rememberNavController()

    NavigateScreen(
        navController = navController
    )
}