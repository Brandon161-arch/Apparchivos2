package com.example.apparchivos2.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apparchivos2.data.FileItem
import com.example.apparchivos2.data.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FileListUiState(
    val items: List<FileItem> = emptyList(),
    val isLoading: Boolean = false,
    val currentFolderUri: Uri? = null,
    val errorMessage: String? = null
)

class FileListViewModel(private val repository: FileRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FileListUiState())
    val uiState: StateFlow<FileListUiState> = _uiState.asStateFlow()

    fun openFolder(treeUri: Uri) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = repository.listFolder(treeUri)
                _uiState.value = _uiState.value.copy(
                    items = items,
                    isLoading = false,
                    currentFolderUri = treeUri
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No se pudo leer esta carpeta"
                )
            }
        }
    }
}