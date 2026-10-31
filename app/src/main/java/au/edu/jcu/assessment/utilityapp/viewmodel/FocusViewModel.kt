package au.edu.jcu.assessment.utilityapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.data.QuoteRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// All values needed by the two screens live in one UI state.
data class FocusUiState(
    val selectedMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val running: Boolean = false,
    val completedSessions: Int = 0,
    val showQuotes: Boolean = true,
    val quoteText: String = "Refresh to get a study quote.",
    val quoteAuthor: String = "",
    val loadingQuote: Boolean = false,
    val quoteError: String? = null,
    val sessionFinished: Boolean = false
)

class FocusViewModel(private val repository: QuoteRepository) : ViewModel() {
    private val _state = MutableStateFlow(FocusUiState())
    val state: StateFlow<FocusUiState> = _state.asStateFlow()
    private var timerJob: Job? = null

    init { refreshQuote() }

    fun startOrPause() {
        if (_state.value.running) {
            timerJob?.cancel()
            _state.update { it.copy(running = false) }
            return
        }
        if (_state.value.remainingSeconds == 0) return

        _state.update { it.copy(running = true, sessionFinished = false) }
        timerJob = viewModelScope.launch {
            while (isActive && _state.value.remainingSeconds > 0) {
                delay(1_000)
                _state.update { current ->
                    if (!current.running) current else {
                        val remaining = (current.remainingSeconds - 1).coerceAtLeast(0)
                        current.copy(
                            remainingSeconds = remaining,
                            running = remaining > 0,
                            completedSessions = current.completedSessions + if (remaining == 0) 1 else 0,
                            sessionFinished = remaining == 0
                        )
                    }
                }
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                remainingSeconds = it.selectedMinutes * 60,
                running = false,
                sessionFinished = false
            )
        }
    }

    fun setSessionMinutes(minutes: Int) {
        if (minutes !in listOf(15, 25, 45)) return
        timerJob?.cancel()
        _state.update {
            it.copy(
                selectedMinutes = minutes,
                remainingSeconds = minutes * 60,
                running = false,
                sessionFinished = false
            )
        }
    }

    fun setShowQuotes(enabled: Boolean) {
        _state.update { it.copy(showQuotes = enabled) }
    }

    fun refreshQuote() {
        viewModelScope.launch {
            _state.update { it.copy(loadingQuote = true, quoteError = null) }
            try {
                val result = repository.getRandomQuote()
                _state.update {
                    it.copy(
                        quoteText = result.quote,
                        quoteAuthor = result.author,
                        loadingQuote = false
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _state.update {
                    it.copy(
                        loadingQuote = false,
                        quoteError = "Could not load a quote. Check your internet connection."
                    )
                }
            }
        }
    }

    class Factory(private val repository: QuoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FocusViewModel::class.java)) {
                return FocusViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel type")
        }
    }
}
