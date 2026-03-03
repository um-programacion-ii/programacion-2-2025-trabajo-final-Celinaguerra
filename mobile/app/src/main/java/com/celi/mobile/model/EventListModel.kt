package com.celi.mobile.model

import com.celi.mobile.shared.EventSummary

data class EventListUiState(
    val events: List<EventSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

