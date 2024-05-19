package edu.stanford.spezikt.coroutines.di

import javax.inject.Qualifier

interface Dispatching {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Main

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Default

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IO

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Unconfined
}