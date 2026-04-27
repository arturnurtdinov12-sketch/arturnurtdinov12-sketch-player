package com.tgplayer.app.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tgplayer.app.domain.model.AuthState
import com.tgplayer.app.presentation.auth.AuthViewModel

@Composable
fun SplashScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(PaddingValues(24.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("TGPlayer", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(24.dp))
        when (val s = state) {
            is AuthState.Error -> Text(
                s.message,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            else -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
