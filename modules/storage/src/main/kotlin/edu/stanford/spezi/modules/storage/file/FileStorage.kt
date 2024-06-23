package edu.stanford.spezi.modules.storage.file

interface FileStorage {
    suspend fun readFile(fileName: String): Result<ByteArray?>
    suspend fun deleteFile(fileName: String): Result<Unit>
    suspend fun saveFile(fileName: String, data: ByteArray): Result<Unit>
}
