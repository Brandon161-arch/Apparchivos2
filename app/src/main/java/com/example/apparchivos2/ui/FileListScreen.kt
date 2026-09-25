package com.example.apparchivos2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.net.Uri
import com.example.apparchivos2.data.FileItem
import com.example.apparchivos2.ui.theme.AppArchivos2Theme

@Composable
fun FileListScreen(
    viewModel: FileListViewModel,
    onFolderClick: (FileItem) -> Unit,
    onFileClick: (FileItem) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    FileListContent(
        state = state,
        onFolderClick = onFolderClick,
        onFileClick = onFileClick
    )
}

@Composable
fun FileListContent(
    state: FileListUiState,
    onFolderClick: (FileItem) -> Unit,
    onFileClick: (FileItem) -> Unit
) {
    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        state.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(state.errorMessage ?: "")
        }

        state.items.isEmpty() && state.currentFolderUri != null -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Esta carpeta está vacía")
        }

        state.items.isNotEmpty() -> LazyColumn(Modifier.fillMaxSize()) {
            items(state.items) { item ->
                FileRow(
                    item = item,
                    onClick = { if (item.isDirectory) onFolderClick(item) else onFileClick(item) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FileListPreview() {
    AppArchivos2Theme {
        FileListContent(
            state = FileListUiState(
                items = listOf(
                    FileItem(Uri.EMPTY, "Documentos", true, 0, 0),
                    FileItem(Uri.EMPTY, "foto.jpg", false, 1024 * 500, 0),
                    FileItem(Uri.EMPTY, "notas.txt", false, 1024 * 2, 0)
                ),
                currentFolderUri = Uri.EMPTY
            ),
            onFolderClick = {},
            onFileClick = {}
        )
    }
}

@Composable
private fun FileRow(item: FileItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (item.isDirectory) Icons.Filled.Folder else Icons.Filled.InsertDriveFile,
            contentDescription = null
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(item.name, style = MaterialTheme.typography.bodyLarge)
            if (!item.isDirectory) {
                Text(
                    "${item.sizeBytes / 1024} KB",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}