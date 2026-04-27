package com.tgplayer.app.presentation.channels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tgplayer.app.domain.model.Track
import com.tgplayer.app.domain.model.TrackSortOrder
import com.tgplayer.app.presentation.common.formatDuration

@OptIn(ExperimentalFoundationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TrackListScreen(
    channelId: Long,
    onBack: () -> Unit,
    onOpenPlayer: () -> Unit,
    viewModel: TrackListViewModel = hiltViewModel()
) {
    val tracks by viewModel.trackList.collectAsState()
    val sort by viewModel.sort.collectAsState()
    val state = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val last = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= tracks.size - 5 && tracks.isNotEmpty()
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) viewModel.loadOlder() }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Channel #$channelId") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } },
            actions = {
                var menu by remember { mutableStateOf(false) }
                IconButton(onClick = { menu = true }) { Icon(Icons.Filled.Sort, null) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    TrackSortOrder.values().forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order.label) },
                            onClick = { viewModel.setSort(order); menu = false }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )

        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(tracks, key = { _, t -> t.id }) { index, track ->
                TrackRow(
                    index = index + 1,
                    track = track,
                    onClick = { viewModel.playFrom(index); onOpenPlayer() },
                    onLikeClick = { viewModel.toggleLiked(track) }
                )
            }
        }
    }
    @Suppress("UNUSED_VARIABLE") val _s = sort // touch state to avoid unused warning
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TrackRow(
    index: Int,
    track: Track,
    onClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "%02d".format(index),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(track.displayTitle, style = MaterialTheme.typography.titleSmall, maxLines = 1)
            Text(
                track.displayArtist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        Text(
            formatDuration(track.durationMs),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        IconButton(onClick = onLikeClick) {
            Icon(
                if (track.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = if (track.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
