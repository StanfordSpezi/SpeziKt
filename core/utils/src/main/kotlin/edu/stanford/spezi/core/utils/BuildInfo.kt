package edu.stanford.spezi.core.utils

import android.os.Build
import javax.inject.Inject

interface BuildInfo {
    fun getSdkVersion(): Int
}

internal class BuildInfoImpl @Inject constructor() : BuildInfo {
    override fun getSdkVersion(): Int = Build.VERSION.SDK_INT
}
