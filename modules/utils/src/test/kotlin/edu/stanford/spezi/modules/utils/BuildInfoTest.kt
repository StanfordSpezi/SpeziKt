package edu.stanford.spezi.modules.utils

import android.os.Build
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BuildInfoTest {
    private val buildInfo = BuildInfoImpl()

    @Test
    fun `it should indicate sdk version`() {
        assertThat(buildInfo.getSdkVersion()).isEqualTo(Build.VERSION.SDK_INT)
    }
}
