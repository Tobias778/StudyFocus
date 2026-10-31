package au.edu.jcu.assessment.utilityapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.assessment.utilityapp.viewmodel.FocusUiState
import au.edu.jcu.assessment.utilityapp.viewmodel.FocusViewModel
import java.util.Locale

@Composable
fun FocusApp(viewModel: FocusViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf("Focus") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == "Focus",
                    onClick = { selectedTab = "Focus" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Focus") },
                    label = { Text("Focus") }
                )
                NavigationBarItem(
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { contentPadding ->
        when (selectedTab) {
            "Focus" -> FocusScreen(
                state = state,
                onStartOrPause = viewModel::startOrPause,
                onReset = viewModel::resetTimer,
                onRefreshQuote = viewModel::refreshQuote,
                modifier = Modifier.padding(contentPadding)
            )
            else -> SettingsScreen(
                state = state,
                onMinutesSelected = viewModel::setSessionMinutes,
                onShowQuotesChanged = viewModel::setShowQuotes,
                modifier = Modifier.padding(contentPadding)
            )
        }
    }
}

@Composable
private fun FocusScreen(
    state: FocusUiState,
    onStartOrPause: () -> Unit,
    onReset: () -> Unit,
    onRefreshQuote: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Study Focus", style = MaterialTheme.typography.headlineMedium)
        Text("One session at a time", style = MaterialTheme.typography.bodyMedium)

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Focus timer", style = MaterialTheme.typography.titleMedium)
                Text(
                    formatTime(state.remainingSeconds),
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center
                )
                LinearProgressIndicator(
                    progress = {
                        state.remainingSeconds.toFloat() / (state.selectedMinutes * 60)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onStartOrPause,
                        enabled = state.remainingSeconds > 0
                    ) {
                        Text(if (state.running) "Pause" else "Start")
                    }
                    OutlinedButton(onClick = onReset) { Text("Reset") }
                }
                if (state.sessionFinished) {
                    Text("Session complete!", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Completed sessions", style = MaterialTheme.typography.titleMedium)
                Text("${state.completedSessions}", style = MaterialTheme.typography.headlineLarge)
            }
        }

        if (state.showQuotes) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Study inspiration", style = MaterialTheme.typography.titleMedium)
                    Text(state.quoteText)
                    if (state.quoteAuthor.isNotBlank()) Text("— ${state.quoteAuthor}")
                    state.quoteError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    FilledTonalButton(
                        onClick = onRefreshQuote,
                        enabled = !state.loadingQuote
                    ) { Text(if (state.loadingQuote) "Loading…" else "New quote") }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    state: FocusUiState,
    onMinutesSelected: (Int) -> Unit,
    onShowQuotesChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Session length", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(15, 25, 45).forEach { minutes ->
                FilterChip(
                    selected = state.selectedMinutes == minutes,
                    onClick = { onMinutesSelected(minutes) },
                    label = { Text("$minutes min") }
                )
            }
        }
        Text("Changing the length resets the current timer.")
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show study quotes")
            Switch(
                checked = state.showQuotes,
                onCheckedChange = onShowQuotesChanged
            )
        }
        Text(
            "Settings apply immediately. They are not saved after the app closes.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// Formatting is a pure function, so it is simple to unit test.
fun formatTime(seconds: Int): String = String.format(
    Locale.US, "%02d:%02d", seconds / 60, seconds % 60
)
