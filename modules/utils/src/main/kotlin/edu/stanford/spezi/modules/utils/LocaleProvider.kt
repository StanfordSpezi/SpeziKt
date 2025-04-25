package edu.stanford.spezi.modules.utils

import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

interface LocaleProvider {
    fun getDefaultLocale(): Locale
}

@Singleton
internal class LocaleProviderImpl @Inject constructor() : LocaleProvider {
    override fun getDefaultLocale(): Locale = Locale.getDefault()
}
