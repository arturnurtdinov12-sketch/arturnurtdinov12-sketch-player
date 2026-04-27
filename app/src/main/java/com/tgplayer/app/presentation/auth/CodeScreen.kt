package com.tgplayer.app.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tgplayer.app.R
import com.tgplayer.app.domain.model.AuthState

@Composable
fun CodeScreen(onSubmit: (String) -> Unit, viewModel: AuthViewModel) {
    var code by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.auth_code_title), style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = code,
            onValueChange = { code = it.filter { c -> c.isDigit() } },
            label = { Text(stringResource(R.string.auth_code_hint)) },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (state is AuthState.Error) {
            Text((state as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = { onSubmit(code) },
            enabled = code.length >= 4,
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.auth_continue)) }
    }
}
