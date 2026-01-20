package com.smartdriveassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.smartdriveassist.navigation.NavGraph
import com.smartdriveassist.ui.theme.BackgroundPage
import com.smartdriveassist.ui.theme.SmartDriveAssistTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartDriveAssistTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundPage
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
