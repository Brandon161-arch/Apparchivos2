package com.example.apparchivos2.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

data class FileItem(
    val uri: Uri,
    val name: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val lastModified: Long
)

class FileRepository(private val context: Context) {

    fun listFolder(treeUri: Uri): List<FileItem> {
        val root = DocumentFile.fromTreeUri(context, treeUri) ?: return emptyList()

        return root.listFiles()
            .filter { it.name != null }
            .map { doc ->
                FileItem(
                    uri = doc.uri,
                    name = doc.name.orEmpty(),
                    isDirectory = doc.isDirectory,
                    sizeBytes = doc.length(),
                    lastModified = doc.lastModified()
                )
            }
            .sortedWith(compareByDescending<FileItem> { it.isDirectory }.thenBy { it.name.lowercase() })
    }

    fun search(treeUri: Uri, query: String): List<FileItem> {
        val root = DocumentFile.fromTreeUri(context, treeUri) ?: return emptyList()
        val results = mutableListOf<FileItem>()

        fun walk(dir: DocumentFile) {
            dir.listFiles().forEach { doc ->
                if (doc.name?.contains(query, ignoreCase = true) == true) {
                    results.add(
                        FileItem(doc.uri, doc.name.orEmpty(), doc.isDirectory, doc.length(), doc.lastModified())
                    )
                }
                if (doc.isDirectory) walk(doc)
            }
        }
        walk(root)
        return results
    }
}