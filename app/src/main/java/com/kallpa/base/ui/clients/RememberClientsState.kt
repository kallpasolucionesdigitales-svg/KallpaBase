package com.kallpa.base.ui.clients

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kallpa.base.data.ClientRepository

@Composable
fun rememberClientsState(repository: ClientRepository): ClientsState {
    val scope = rememberCoroutineScope()
    val state = remember(repository, scope) { ClientsState(repository, scope) }
    LaunchedEffect(state) { state.load() }
    return state
}
