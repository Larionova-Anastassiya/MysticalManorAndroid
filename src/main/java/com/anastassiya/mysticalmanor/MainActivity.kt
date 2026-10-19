package com.anastassiya.mysticalmanor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.anastassiya.mysticalmanor.ui.theme.ManorApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ManorApp()
        }
    }
}