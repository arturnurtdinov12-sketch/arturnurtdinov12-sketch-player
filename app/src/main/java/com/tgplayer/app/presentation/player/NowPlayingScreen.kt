package com.tgplayer.app.presentation.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tgplayer.app.presentation.common.EmptyState

@Composable
fun NowPlayingScreen(onOpenFull: () -> Unit, viewModel: PlayerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    if (state.current == null) {
        EmptyState(title = "Nothing playing", subtitle = "Pick a track from a channel")
        return
    }
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(state.current!!.displayTitle, style = MaterialTheme.typography.headlineMedium)
            Text(state.current!!.displayArtist, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onOpenFull) { Text("Open player") }
        }
    }
}
