package com.example.booksapp.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val user = viewModel.user.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Profile", style = MaterialTheme.typography.headlineSmall)
        Text(text = "UID: ${user?.uid.orEmpty()}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Email: ${user?.email.orEmpty()}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Name: ${user?.displayName.orEmpty()}", style = MaterialTheme.typography.bodyLarge)

        Text(
            text = "Offline cache and realtime sync are enabled. Release build disables debug logs.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )

        Button(
            onClick = {
                viewModel.signOut()
                onSignedOut()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Sign Out")
        }
    }
}
