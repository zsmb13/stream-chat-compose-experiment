package com.example.composechat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.ui.avatar.AvatarView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import io.getstream.chat.android.ui.common.extensions.getDisplayName

@Composable
fun ChannelListScreen(
    navController: NavController,
    channelListViewModel: ChannelListViewModel = viewModel(
        factory = ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.`in`("members", listOf(ChatClient.instance().getCurrentUser()?.id)),
            )
        )
    ),
) {
    val state by channelListViewModel.state.observeAsState()
    val channelState = state ?: return

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (channelState.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(channelState.channels) { channel ->
                    ChannelListItem(
                        channel = channel,
                        onClick = { navController.navigate("messagelist/${channel.cid}") },
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ChannelListItem(
    channel: Channel,
    onClick: (channel: Channel) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick(channel) }
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(channel)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = channel.getDisplayName(LocalContext.current),
                style = TextStyle(fontWeight = FontWeight.Bold),
                fontSize = 18.sp,
            )

            val lastMessageText = channel.messages.lastOrNull()?.text ?: "..."
            Text(
                text = lastMessageText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun Avatar(channel: Channel) {
    AndroidView(
        factory = { context ->
            AvatarView(context).apply {
                setChannelData(channel)
            }
        },
        modifier = Modifier.size(48.dp)
    )
}
