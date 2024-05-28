package jp.co.rakuten.ticket.checkinstation.util

import android.graphics.Bitmap
import kotlin.experimental.or

class BitmapUtil {

    //single method
    companion object {
        private var instance: BitmapUtil? = null
        fun getInstance(): BitmapUtil {
            if (instance == null) instance = BitmapUtil()
            return instance!!
        }
    }

    /**
     * saved in monochrome bmp format
     *
     * @param bmp bitmap
     * @return byte array
     */
    fun changeToMonochromeBitmap(bmp: Bitmap): ByteArray {
        val binary = gray2Binary(bmp)
        val data = compressMonoBitmap(bmp, binary)
        val header = addBMPImageHeader(data!!.size + 62)
        val info = addBMPImageInfoHeader(bmp.width, bmp.height)
        val buffer = ByteArray(62 + data.size)
        System.arraycopy(header, 0, buffer, 0, header.size)
        System.arraycopy(info, 0, buffer, 14, info.size)
        System.arraycopy(data, 0, buffer, 62, data.size)
        return buffer
    }

    /**
     * convert the color image to a grayscale image and binarize it
     *
     * @param bmp bitmap
     * @return int array
     */
    private fun gray2Binary(bmp: Bitmap): IntArray {
        val width = bmp.width
        val height = bmp.height
        val pixels = IntArray(width * height)
        bmp.getPixels(
            pixels,
            0,
            width,
            0,
            0,
            width,
            height
        )
        for (i in 0 until height) {
            for (j in 0 until width) {
                var grey = pixels[width * i + j]
                val alpha = grey and -0x1000000 shr 24
                val red = grey and 0x00FF0000 shr 16
                val green = grey and 0x0000FF00 shr 8
                val blue = grey and 0x000000FF
                if (alpha == 0) {
                    pixels[width * i + j] = 0
                    continue
                }
                grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
                grey = if (grey < 200) 1 else 0
                pixels[width * i + j] = grey
            }
        }
        return pixels
    }

    /**
     * compressed to a full monochrome bmp array
     *
     * @param bmp bitmap
     * @param binary data
     * @return byte array
     */
    private fun compressMonoBitmap(bmp: Bitmap, binary: IntArray): ByteArray? {
        val width = bmp.width
        val height = bmp.height
        val widthBytes = (width + 31) / 32 * 4
        val news = ByteArray(widthBytes * height)
        for (i in height downTo 1) {
            for (j in 0 until width) {
                if (binary[width * (i - 1) + j] > 0) news[(height - i) * widthBytes + j / 8] =
                    news[(height - i) * widthBytes + j / 8] or (1 shl 7 - j % 8).toByte()
            }
        }
        return news
    }

    /**
     * bmp header
     *
     * @param size file size
     * @return byte array
     */
    private fun addBMPImageHeader(size: Int): ByteArray {
        val buffer = ByteArray(14)
        buffer[0] = 0x42
        buffer[1] = 0x4D
        buffer[2] = size.toByte()
        buffer[3] = (size shr 8).toByte()
        buffer[4] = (size shr 16).toByte()
        buffer[5] = (size shr 24).toByte()
        buffer[6] = 0x00
        buffer[7] = 0x00
        buffer[8] = 0x00
        buffer[9] = 0x00
        buffer[10] = 0x3E
        buffer[11] = 0x00
        buffer[12] = 0x00
        buffer[13] = 0x00
        return buffer
    }

    /**
     * bmp info header
     *
     * @param w width
     * @param h height
     * @return byte array
     */
    private fun addBMPImageInfoHeader(w: Int, h: Int): ByteArray {
        val buffer = ByteArray(48)
        buffer[0] = 0x28
        buffer[1] = 0x00
        buffer[2] = 0x00
        buffer[3] = 0x00
        buffer[4] = w.toByte()
        buffer[5] = (w shr 8).toByte()
        buffer[6] = (w shr 16).toByte()
        buffer[7] = (w shr 24).toByte()
        buffer[8] = h.toByte()
        buffer[9] = (h shr 8).toByte()
        buffer[10] = (h shr 16).toByte()
        buffer[11] = (h shr 24).toByte()
        buffer[12] = 0x01
        buffer[13] = 0x00
        buffer[14] = 0x01
        buffer[15] = 0x00
        buffer[16] = 0x00
        buffer[17] = 0x00
        buffer[18] = 0x00
        buffer[19] = 0x00
        buffer[20] = 0x00
        buffer[21] = 0x00
        buffer[22] = 0x00
        buffer[23] = 0x00
        buffer[24] = 0x00
        buffer[25] = 0x00
        buffer[26] = 0x00
        buffer[27] = 0x00
        buffer[28] = 0x00
        buffer[29] = 0x00
        buffer[30] = 0x00
        buffer[31] = 0x00
        buffer[32] = 0x00
        buffer[33] = 0x00
        buffer[34] = 0x00
        buffer[35] = 0x00
        buffer[36] = 0x00
        buffer[37] = 0x00
        buffer[38] = 0x00
        buffer[39] = 0x00
        buffer[40] = 0xFF.toByte()
        buffer[41] = 0xFF.toByte()
        buffer[42] = 0xFF.toByte()
        buffer[43] = 0x00.toByte()
        buffer[44] = 0x00.toByte()
        buffer[45] = 0x00.toByte()
        buffer[46] = 0x00.toByte()
        buffer[47] = 0xFF.toByte()
        return buffer
    }

}