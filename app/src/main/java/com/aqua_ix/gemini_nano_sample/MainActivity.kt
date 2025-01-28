package com.aqua_ix.gemini_nano_sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aqua_ix.gemini_nano_sample.presentation.chat.ChatScreen
import com.aqua_ix.gemini_nano_sample.presentation.settings.SettingsScreen
import com.aqua_ix.gemini_nano_sample.ui.theme.GemininanosampleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GemininanosampleTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        TopBar(
                            navController = navController,
                            onNavigateUp = { navController.navigateUp() }
                        )
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "chat",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("chat") {
                            ChatScreen()
                        }
                        composable("settings") {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController,
    onNavigateUp: () -> Unit
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    TopAppBar(
        title = {
            Text(
                text = when (currentRoute) {
                    "chat" -> stringResource(R.string.screen_title_chat)
                    "settings" -> stringResource(R.string.screen_title_settings)
                    else -> ""
                }
            )
        },
        navigationIcon = {
            if (currentRoute != "chat") {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.navigation_back)
                    )
                }
            }
        },
        actions = {
            if (currentRoute == "chat") {
                IconButton(
                    onClick = { navController.navigate("settings") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.navigation_settings)
                    )
                }
            }
        }
    )
}
