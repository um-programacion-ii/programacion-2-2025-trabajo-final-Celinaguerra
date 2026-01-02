package com.celi.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.celi.mobile.model.PurchaseListUiState
import com.celi.mobile.shared.MobileApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PurchaseListViewModel(
    private val api: MobileApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseListUiState(isLoading = true))
    val uiState: StateFlow<PurchaseListUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                _uiState.emit(_uiState.value.copy(isLoading = true, error = null))
                val purchases = api.getPurchases()
                _uiState.emit(
                    _uiState.value.copy(
                        purchases = purchases,
                        isLoading = false,
                        error = null
                    )
                )
            } catch (e: Exception) {
                _uiState.emit(
                    _uiState.value.copy(
                        isLoading = false,
                        error = when {
                            e.message?.contains("401") == true -> "Sesión expirada. Por favor inicie sesión nuevamente."
                            e.message?.contains("Network") == true -> "Error de conexión. Verifique su internet."
                            else -> e.message ?: "Error al cargar compras"
                        }
                    )
                )
            }
        }
    }
}

class PurchaseListViewModelFactory(
    private val api: MobileApi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PurchaseListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PurchaseListViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
