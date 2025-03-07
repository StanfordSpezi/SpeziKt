package edu.stanford.spezi.spezi.credentialstorage.di

import javax.inject.Qualifier

interface Storage {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Encrypted

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Unencrypted

    companion object {
        internal const val STORAGE_FILE_PREFIX = "edu.stanford.spezi.storage."
    }
}
