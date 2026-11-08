package com.guali.tutor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.guali.tutor.ui.PantallaPrincipal
import com.guali.tutor.ui.theme.GualiTutorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GualiTutorTheme {
                PantallaPrincipal()
            }
        }
    }
}
