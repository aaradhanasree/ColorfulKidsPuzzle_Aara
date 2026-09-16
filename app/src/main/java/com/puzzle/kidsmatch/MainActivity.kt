package com.puzzle.kidsmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

val KidsVibrantScheme = lightColorScheme(
    primary = Color(0xFFFF4081),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFF80AB),
    secondary = Color(0xFF00E676),
    onSecondary = Color.White,
    tertiary = Color(0xFF00E5FF),
    onTertiary = Color.Black,
    background = Color(0xFFFFF8E1),
    surface = Color.White
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = KidsVibrantScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PuzzleApp()
                }
            }
        }
    }
}
