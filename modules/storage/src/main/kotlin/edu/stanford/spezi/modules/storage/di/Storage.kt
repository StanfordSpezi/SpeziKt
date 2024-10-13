package edu.stanford.spezi.modules.storage.di

import javax.inject.Qualifier

interface Storage {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Encrypted
}
