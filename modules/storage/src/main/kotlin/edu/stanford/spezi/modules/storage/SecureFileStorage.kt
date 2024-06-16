package edu.stanford.spezi.modules.storage

interface SecureFileStorage {
    suspend fun readFile(fileName: String): ByteArray?
    suspend fun deleteFile(fileName: String)
    suspend fun saveFile(fileName: String, data: ByteArray)
}
