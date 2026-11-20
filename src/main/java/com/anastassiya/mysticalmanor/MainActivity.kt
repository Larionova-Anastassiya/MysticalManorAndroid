package com.anastassiya.mysticalmanor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.anastassiya.mysticalmanor.ui.theme.ManorApp


import com.anastassiya.mysticalmanor.data.local.ManorDatabase
import com.anastassiya.mysticalmanor.data.repository.JournalRepository

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ManorApp(
                journalRepository = JournalRepository(
                    ManorDatabase.getDatabase(applicationContext)
                )
            )
        }
    }
}