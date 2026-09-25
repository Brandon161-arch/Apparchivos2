package com.example.apparchivos2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.apparchivos2.data.FileRepository
import com.example.apparchivos2.ui.FileListScreen
import com.example.apparchivos2.ui.FileListViewModel

class MainActivity : ComponentActivity() {

    private lateinit var repository: FileRepository
    private lateinit var viewModel: FileListViewModel

    private val openTreeLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.openFolder(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = FileRepository(applicationContext)
        viewModel = FileListViewModel(repository)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val state by viewModel.uiState.collectAsState()

                    if (state.currentFolderUri == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Selecciona una carpeta para empezar a explorar tus archivos.")
                                Spacer(Modifier.height(16.dp))
                                Button(onClick = { openTreeLauncher.launch(null) }) {
                                    Text("Elegir carpeta")
                                }
                            }
                        }
                    } else {
                        FileListScreen(
                            viewModel = viewModel,
                            onFolderClick = { item -> viewModel.openFolder(item.uri) },
                            onFileClick = { /* TODO: flujo de previsualización RF-04 */ }
                        )
                    }
                }
            }
        }
    }
}