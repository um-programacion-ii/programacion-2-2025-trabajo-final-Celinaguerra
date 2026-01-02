package com.celi.mobile.model

import com.celi.mobile.shared.EventDetail

data class EventDetailUiState(
    val event: EventDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class EventDetailEffect {
    data class ResumeSelection(
        val eventId: Long,
        val seats: List<Pair<String, Int>>,
        val expiresAt: String?
    ) : EventDetailEffect()
}

