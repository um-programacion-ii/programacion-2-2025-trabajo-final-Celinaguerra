package com.celi.mobile.model

import com.celi.mobile.shared.VentaResumenDto

data class PurchaseListUiState(
    val purchases: List<VentaResumenDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
