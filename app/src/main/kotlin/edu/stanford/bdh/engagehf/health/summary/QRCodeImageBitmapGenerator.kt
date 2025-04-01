package edu.stanford.bdh.engagehf.health.summary

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.CharacterSetECI
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap
import javax.inject.Inject

class QRCodeImageBitmapGenerator @Inject constructor() {

    fun generate(content: String?, size: Int): ImageBitmap? {
        if (content.isNullOrEmpty()) return null
        return runCatching {
            val encodingHints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                put(EncodeHintType.CHARACTER_SET, CharacterSetECI.UTF8)
            }

            val qrMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, encodingHints)
            val qrBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

            for (yCoordinate in 0 until size) {
                for (xCoordinate in 0 until size) {
                    qrBitmap.setPixel(
                        xCoordinate,
                        yCoordinate,
                        if (qrMatrix[xCoordinate, yCoordinate]) Color.BLACK else Color.WHITE
                    )
                }
            }

            qrBitmap.asImageBitmap()
        }.getOrNull()
    }
}
