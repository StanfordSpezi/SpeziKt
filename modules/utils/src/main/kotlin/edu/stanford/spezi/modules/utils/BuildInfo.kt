package edu.stanford.spezi.modules.utils

import android.os.Build
import javax.inject.Inject

interface BuildInfo {
    fun getSdkVersion(): Int
    fun getOsVersion(): String
}

internal class BuildInfoImpl @Inject constructor() : BuildInfo {
    override fun getSdkVersion(): Int = Build.VERSION.SDK_INT
    override fun getOsVersion(): String = Build.VERSION.RELEASE
}
