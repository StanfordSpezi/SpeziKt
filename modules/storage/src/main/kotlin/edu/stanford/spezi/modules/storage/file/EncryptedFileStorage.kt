package edu.stanford.spezi.modules.storage.file

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class EncryptedFileStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) :
    FileStorage {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private fun getEncryptedFile(fileName: String): EncryptedFile {
        val file = File(context.filesDir, fileName)

        return EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }

    override suspend fun saveFile(fileName: String, data: ByteArray) = withContext(ioDispatcher) {
        deleteFile(fileName)
        val encryptedFile = getEncryptedFile(fileName)
        encryptedFile.openFileOutput().use { outputStream ->
            outputStream.write(data)
        }
    }

    override suspend fun readFile(fileName: String): ByteArray? = withContext(ioDispatcher) {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return@withContext null

        val encryptedFile = getEncryptedFile(fileName)
        return@withContext encryptedFile.openFileInput().use { inputStream ->
            inputStream.readBytes()
        }
    }

    override suspend fun deleteFile(fileName: String) = withContext(ioDispatcher) {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            file.delete()
        }
    }
}
