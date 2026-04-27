package com.tgplayer.app.presentation.channels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tgplayer.app.R

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ChannelSelectScreen(
    onDone: () -> Unit,
    viewModel: ChannelsViewModel = hiltViewModel()
) {
    val all by viewModel.allChannels.collectAsState()
    val refreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) { viewModel.refresh() }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.channel_select_title)) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        if (refreshing) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(all, key = { it.id }) { ch ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setAdded(ch.id, !ch.isAdded) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(ch.title.take(1).uppercase(), color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(ch.title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                        Checkbox(checked = ch.isAdded, onCheckedChange = { viewModel.setAdded(ch.id, it) })
                    }
                }
            }
        }

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) { Text(stringResource(R.string.channel_select_save)) }
    }
}
