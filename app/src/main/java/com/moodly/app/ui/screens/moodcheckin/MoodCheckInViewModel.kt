package com.moodly.app.ui.screens.moodcheckin

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodly.app.data.repository.MoodRepository
import com.moodly.app.di.IoDispatcher
import com.moodly.app.domain.model.MoodEntry
import com.moodly.app.domain.model.MoodFactor
import com.moodly.app.domain.model.MoodType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Immutable
data class MoodCheckInUiState(
    val selectedMood: MoodType? = null,
    val moodRating: Int = 3,
    val selectedFactors: Set<MoodFactor> = emptySet(),
    val journalNote: String = "",
    val isSaving: Boolean = false,
    val momoMessage: String = "How are you feeling today?"
)

sealed interface MoodCheckInAction {
    data class SelectMood(val mood: MoodType) : MoodCheckInAction
    data class SetRating(val rating: Int) : MoodCheckInAction
    data class ToggleFactor(val factor: MoodFactor) : MoodCheckInAction
    data class SetJournalNote(val note: String) : MoodCheckInAction
    data object SaveCheckIn : MoodCheckInAction
}

@HiltViewModel
class MoodCheckInViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MoodCheckInUiState())
    val uiState: StateFlow<MoodCheckInUiState> = _uiState.asStateFlow()
    
    private var onSaveComplete: (() -> Unit)? = null
    
    fun setOnSaveComplete(callback: () -> Unit) {
        onSaveComplete = callback
    }
    
    fun onAction(action: MoodCheckInAction) {
        when (action) {
            is MoodCheckInAction.SelectMood -> selectMood(action.mood)
            is MoodCheckInAction.SetRating -> setRating(action.rating)
            is MoodCheckInAction.ToggleFactor -> toggleFactor(action.factor)
            is MoodCheckInAction.SetJournalNote -> setJournalNote(action.note)
            is MoodCheckInAction.SaveCheckIn -> saveCheckIn()
        }
    }
    
    private fun selectMood(mood: MoodType) {
        _uiState.update { state ->
            state.copy(
                selectedMood = mood,
                momoMessage = getMomoMessageForMood(mood)
            )
        }
    }
    
    private fun setRating(rating: Int) {
        _uiState.update { it.copy(moodRating = rating) }
    }
    
    private fun toggleFactor(factor: MoodFactor) {
        _uiState.update { state ->
            val newFactors = if (factor in state.selectedFactors) {
                state.selectedFactors - factor
            } else {
                state.selectedFactors + factor
            }
            state.copy(selectedFactors = newFactors)
        }
    }
    
    private fun setJournalNote(note: String) {
        _uiState.update { it.copy(journalNote = note) }
    }
    
    private fun saveCheckIn() {
        val state = _uiState.value
        val selectedMood = state.selectedMood ?: return
        
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            val entry = MoodEntry(
                moodType = selectedMood,
                rating = state.moodRating,
                factors = state.selectedFactors.toList(),
                journalNote = state.journalNote.takeIf { it.isNotBlank() }
            )
            
            withContext(ioDispatcher) { moodRepository.saveMoodEntry(entry) }
            
            _uiState.update { it.copy(isSaving = false) }
            // Back on the main thread: safe to trigger navigation
            onSaveComplete?.invoke()
        }
    }
    
    private fun getMomoMessageForMood(mood: MoodType): String = when (mood) {
        MoodType.HAPPY -> "That's lovely! Keep enjoying the little things."
        MoodType.CALM -> "It's good to feel at peace. Keep that calm energy."
        MoodType.NEUTRAL -> "It's okay to have an ordinary day, too."
        MoodType.ANXIOUS -> "Take a deep breath. I'm here for you."
        MoodType.SAD -> "It's okay to feel sad sometimes. You're not alone."
        MoodType.STRESSED -> "That sounds like a lot. Let's take it one step at a time."
    }
}
