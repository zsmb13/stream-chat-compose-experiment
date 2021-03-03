package com.example.composechat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composechat.ui.theme.ComposeChatTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeChatTheme {
                Surface(color = MaterialTheme.colors.background) {
                    ChatApp()
                }
            }
        }
    }
}

@Composable
fun ChatApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "channellist") {
        composable("channellist") {
            ChannelListScreen(navController = navController)
        }
        composable("messagelist/{cid}") { backStackEntry ->
            MessageListScreen(
                navController = navController,
                cid = backStackEntry.arguments?.getString("cid")!!
            )
        }
    }
}
