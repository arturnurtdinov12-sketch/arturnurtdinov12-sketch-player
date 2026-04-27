package com.tgplayer.app.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tgplayer.app.domain.model.RepeatMode
import com.tgplayer.app.presentation.common.formatDuration
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    onMinimize: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var scrubbing by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(Unit) {
        while (true) { viewModel.tick(); delay(500) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onMinimize) { Icon(Icons.Filled.ExpandMore, null) }
            Spacer(Modifier.weight(1f))
            Text("Now Playing", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(48.dp))
        }
        Spacer(Modifier.height(24.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    state.current?.displayTitle?.take(2)?.uppercase() ?: "♪",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            state.current?.displayTitle ?: "Nothing playing",
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1
        )
        Text(
            state.current?.displayArtist ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )

        Spacer(Modifier.height(16.dp))
        val total = (state.current?.durationMs ?: state.durationMs).coerceAtLeast(1L)
        val pos = scrubbing ?: state.positionMs.toFloat()
        Slider(
            value = pos.coerceIn(0f, total.toFloat()),
            onValueChange = { scrubbing = it },
            onValueChangeFinished = {
                scrubbing?.let { viewModel.seekTo(it.toLong()) }
                scrubbing = null
            },
            valueRange = 0f..total.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatDuration(pos.toLong()), style = MaterialTheme.typography.bodySmall)
            Text(formatDuration(total), style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.toggleShuffle() }) {
                Icon(
                    Icons.Filled.Shuffle, null,
                    tint = if (state.shuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { viewModel.previous() }) { Icon(Icons.Filled.SkipPrevious, null) }
            IconButton(onClick = { viewModel.playPause() }, modifier = Modifier.size(72.dp)) {
                Icon(
                    if (state.isPlaying) Icons.Filled.PauseCircleFilled else Icons.Filled.PlayCircleFilled,
                    null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { viewModel.next() }) { Icon(Icons.Filled.SkipNext, null) }
            IconButton(onClick = { viewModel.cycleRepeat() }) {
                val icon = if (state.repeatMode == RepeatMode.ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat
                val tint = if (state.repeatMode != RepeatMode.OFF) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                Icon(icon, null, tint = tint)
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = { state.current?.let { viewModel.toggleLike(it) } }) {
                val liked = state.current?.isLiked == true
                Icon(
                    if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    null,
                    tint = if (liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
