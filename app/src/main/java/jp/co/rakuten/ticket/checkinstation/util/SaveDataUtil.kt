package jp.co.rakuten.ticket.checkinstation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.ThumbnailUtils
import android.os.Environment
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


//save data to local util
class SaveDataUtil {

    //single method
    companion object {
        private var instance: SaveDataUtil? = null
        fun getInstance(): SaveDataUtil {
            if (instance == null) instance = SaveDataUtil()
            return instance!!
        }
    }

    /**
     * save data to local
     *
     * @param context page that call the method
     * @param key save key
     * @param value save value
     */
    fun saveData(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * get data from local
     *
     * @param context page that call the method
     * @param key save key
     */
    fun getData(context: Context, key: String): String {
        val result = context.getSharedPreferences(key, Context.MODE_PRIVATE).getString(key, "")
        if (result != null) {
            return result.ifEmpty {
                ""
            }
        }
        return ""
    }

    /**
     * get data from local
     *
     * @param context page that call the method
     * @param key save key
     */
    fun clearData(context: Context, key: String) {
        context.getSharedPreferences(key, Context.MODE_PRIVATE).edit().clear().apply()
    }

    /**
     * save bitmap to local
     *
     * @param context page context
     * @param bitmap image bitmap
     */
    fun saveBitmapToLocal(context: Context, bitmap: Bitmap) {
        val memoryPath = Environment.getDataDirectory().path + "/data/" + context.packageName
        val bmpPath = "$memoryPath/printImage.bmp"
        val bmpData = BitmapUtil.getInstance().changeToMonochromeBitmap(bitmap)
        try {
            val fileOutputStream = FileOutputStream(bmpPath)
            fileOutputStream.write(bmpData)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * get bitmap
     *
     * @param context page context
     */
    fun getPrintBitmapPath(context: Context): String {
        val memoryPath = Environment.getDataDirectory().path + "/data/" + context.packageName
        return "$memoryPath/printImage.bmp"
    }

}
