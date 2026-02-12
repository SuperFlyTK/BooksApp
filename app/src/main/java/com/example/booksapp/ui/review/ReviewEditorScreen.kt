package com.example.booksapp.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ReviewEditorScreen(
    snackbarHostState: SnackbarHostState,
    onSaved: () -> Unit,
    viewModel: ReviewEditorViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    LaunchedEffect(state.infoMessage) {
        val message = state.infoMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Review Editor", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = state.text,
            onValueChange = viewModel::onTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            label = { Text("Review text") },
            isError = state.textError != null,
            supportingText = state.textError?.let { { Text(it) } },
            minLines = 4,
        )
        Text("Rating: ${state.rating}", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = state.rating.toFloat(),
            onValueChange = { viewModel.onRatingChanged(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
        )
        state.ratingError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = viewModel::submit,
            enabled = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Save review")
        }
    }
}
