package edu.stanford.bdh.engagehf.health.summary

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.modules.utils.MessageNotifier
import edu.stanford.spezi.modules.utils.TimeProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class HealthSummaryServiceTest {
    private val healthSummaryRepository: HealthSummaryRepository = mockk()
    private val messageNotifier: MessageNotifier = mockk()
    private val qrCodeImageBitmapGenerator: QRCodeImageBitmapGenerator = mockk()
    private val timeProvider: TimeProvider = mockk()
    private val context: Context = mockk()
    private val someUrl = "some-url"
    private val code = "123456"
    private val qrCodeSize = 200
    private val now = Instant.now()

    private val service = HealthSummaryService(
        healthSummaryRepository = healthSummaryRepository,
        messageNotifier = messageNotifier,
        ioDispatcher = UnconfinedTestDispatcher(),
        context = context,
        qrCodeImageBitmapGenerator = qrCodeImageBitmapGenerator,
        timeProvider = timeProvider
    )

    @Before
    fun setup() {
        every { timeProvider.nowInstant() } returns now
    }

    @Test
    fun `observeShareHealthSummary should emit successful ShareHealthSummary`() = runTest {
        // given
        val expectedBitmap = mockk<ImageBitmap>()
        val expectedData = ShareHealthSummary(qrCodeBitmap = expectedBitmap, oneTimeCode = code, expiresAt = now)
        coEvery { healthSummaryRepository.getShareHealthSummaryData() } returns Result.success(
            ShareHealthSummaryData(url = someUrl, code = code, expiresAt = now)
        )
        every { qrCodeImageBitmapGenerator.generate(someUrl, qrCodeSize) } returns expectedBitmap

        // when
        val result = service.observeShareHealthSummary(qrCodeSize = qrCodeSize).first()

        // then
        assertThat(result.getOrThrow()).isEqualTo(expectedData)
    }

    @Test
    fun `observeShareHealthSummary should emit failure if qr code generation fails`() = runTest {
        // given
        coEvery { healthSummaryRepository.getShareHealthSummaryData() } returns Result.success(
            ShareHealthSummaryData(url = someUrl, code = code, expiresAt = now)
        )
        every { qrCodeImageBitmapGenerator.generate(someUrl, qrCodeSize) } returns null

        // when
        val result = service.observeShareHealthSummary(qrCodeSize = qrCodeSize).first()

        // then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `observeShareHealthSummary should emit failure of ShareHealthSummary`() = runTest {
        // given
        val error = Error("Some error")
        coEvery { healthSummaryRepository.getShareHealthSummaryData() } returns Result.failure(error)

        // when
        val result = service.observeShareHealthSummary(qrCodeSize = 123).first()

        // then
        assertThat(result.exceptionOrNull()).isEqualTo(error)
    }

    @Test
    fun `observeShareHealthSummary should should reload respecting expired at of data`() = runTest {
        // given
        val expectedBitmap = mockk<ImageBitmap>()
        val validity = 5000L
        val expiresAt = now.plusMillis(validity)
        coEvery { healthSummaryRepository.getShareHealthSummaryData() } returns Result.success(
            ShareHealthSummaryData(url = someUrl, code = code, expiresAt = expiresAt)
        )
        every { qrCodeImageBitmapGenerator.generate(someUrl, qrCodeSize) } returns expectedBitmap

        // when
        val results = mutableListOf<Result<ShareHealthSummary>>()
        val job = launch {
            service.observeShareHealthSummary(qrCodeSize).toList(results)
        }

        advanceTimeBy(validity + 1)
        job.cancel()

        // then
        assertThat(results).hasSize(2)
    }
}
