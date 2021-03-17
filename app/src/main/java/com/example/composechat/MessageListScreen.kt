package com.example.composechat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.getstream.sdk.chat.adapter.MessageListItem
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory

@Composable
fun MessageListScreen(
    navController: NavHostController,
    cid: String,
) {
    Column(Modifier.fillMaxSize()) {
        val factory = MessageListViewModelFactory(cid)

        MessageList(
            navController = navController,
            factory = factory,
            modifier = Modifier.weight(1f),
        )
        MessageInput(factory = factory)
    }
}

@Composable
fun MessageInput(
    factory: MessageListViewModelFactory,
    messageInputViewModel: MessageInputViewModel = viewModel(factory = factory),
) {
    var inputValue by remember { mutableStateOf("") }

    fun sendMessage() {
        messageInputViewModel.sendMessage(inputValue)
        inputValue = ""
    }

    Row {
        TextField(
            modifier = Modifier.weight(1f),
            value = inputValue,
            onValueChange = { inputValue = it },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions { sendMessage() },
        )
        Button(
            modifier = Modifier.height(56.dp),
            onClick = { sendMessage() },
            enabled = inputValue.isNotBlank(),
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = stringResource(R.string.cd_button_send)
            )
        }
    }
}

@Composable
fun MessageList(
    navController: NavHostController,
    factory: MessageListViewModelFactory,
    modifier: Modifier = Modifier,
    messageListViewModel: MessageListViewModel = viewModel(factory = factory),
) {
    val state by messageListViewModel.state.observeAsState()
    val messageState = state ?: return

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (messageState) {
            is MessageListViewModel.State.Loading -> {
                CircularProgressIndicator()
            }
            is MessageListViewModel.State.NavigateUp -> {
                navController.popBackStack()
            }
            is MessageListViewModel.State.Result -> {
                val messageItems = messageState.messageListItem.items
                    .filterIsInstance<MessageListItem.MessageItem>()
                    .filter { it.message.text.isNotBlank() }
                    .asReversed()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true,
                ) {
                    items(messageItems) { message ->
                        MessageCard(message)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageCard(messageItem: MessageListItem.MessageItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = when {
            messageItem.isMine -> Alignment.End
            else -> Alignment.Start
        },
    ) {
        Card(
            modifier = Modifier.widthIn(max = 340.dp),
            shape = cardShapeFor(messageItem),
            backgroundColor = when {
                messageItem.isMine -> MaterialTheme.colors.primary
                else -> MaterialTheme.colors.secondary
            },
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = messageItem.message.text,
                color = when {
                    messageItem.isMine -> MaterialTheme.colors.onPrimary
                    else -> MaterialTheme.colors.onSecondary
                },
            )
        }
        Text(
            text = messageItem.message.user.name,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun cardShapeFor(message: MessageListItem.MessageItem): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when {
        message.isMine -> roundedCorners.copy(bottomStart = CornerSize(0))
        else -> roundedCorners.copy(bottomEnd = CornerSize(0))
    }
}
