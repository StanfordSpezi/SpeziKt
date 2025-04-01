package edu.stanford.bdh.engagehf.health.summary

import android.graphics.Bitmap
import com.google.common.truth.Truth.assertThat
import com.google.zxing.common.BitMatrix
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.Test

class QRCodeImageBitmapGeneratorTest {
    private val generator = QRCodeImageBitmapGenerator()

    @Test
    fun `it should generate image bitmap qr code correctly`() {
        // given
        val size = 100
        val bitMatrix = mockk<BitMatrix>()
        val bitmap = mockk<Bitmap>()
        mockkStatic(Bitmap::class)
        every { Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565) } returns bitmap
        every { bitmap.setPixel(any(), any(), any()) } just Runs
        every { bitmap.height } returns size
        every { bitmap.width } returns size
        every { bitMatrix.get(any(), any()) } returns true

        // when
        val qrCodeImageBitmap = generator.generate(content = "edu.stanford.bdh.engagehf", size = size)

        // then
        assertThat(qrCodeImageBitmap).isNotNull()
        assertThat(qrCodeImageBitmap?.width).isEqualTo(size)
        assertThat(qrCodeImageBitmap?.height).isEqualTo(size)
        unmockkStatic(Bitmap::class)
    }

    @Test
    fun `it should not generate any image bitmap in case content is null`() {
        // when
        val qrCodeImageBitmap = generator.generate(content = null, size = 100)

        // then
        assertThat(qrCodeImageBitmap).isNull()
    }
}
