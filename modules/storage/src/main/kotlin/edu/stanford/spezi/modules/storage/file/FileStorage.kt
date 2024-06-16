package edu.stanford.spezi.modules.storage.file

interface FileStorage {
    suspend fun readFile(fileName: String): ByteArray?
    suspend fun deleteFile(fileName: String)
    suspend fun saveFile(fileName: String, data: ByteArray)
}