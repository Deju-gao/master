package jp.co.rakuten.ticket.checkinstation.util

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import com.caverock.androidsvg.SVG
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.ui.menu.home.MainActivity
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.ceil

//save files to local util
class BcpUtil {

    //single method
    companion object {
        private var instance: BcpUtil? = null
        fun getInstance(): BcpUtil {
            if (instance == null) instance = BcpUtil()
            return instance!!
        }
    }

    //print times
    private var printTimes = 0
    //all print count
    private var allPrintCount = 0

    /**
     * save files to local
     *
     * @param context context
     */
    fun saveFilesToLocal(context: Context) {
        val memoryPath = Environment.getDataDirectory().path + "/data/" + context.packageName
        asset2file(context, "ErrMsg0.ini", memoryPath, "ErrMsg0.ini")
        asset2file(context, "ErrMsg1.ini", memoryPath, "ErrMsg1.ini")
        asset2file(context, "PRTBV400T.ini", memoryPath, "PRTBV400T.ini")
        asset2file(context, "PrtList.ini", memoryPath, "PrtList.ini")
        asset2file(context, "resource.xml", memoryPath, "resource.xml")
        //lfm files
        asset2file(context, "SmpBV400T.lfm", memoryPath, "BV400.lfm")
    }

    /**
     * get lfm file path
     *
     * @return file path
     */
    fun getLfmFilePath(context: Context): String {
        val memoryPath = Environment.getDataDirectory().path + "/data/" + context.packageName
        return "$memoryPath/BV400.lfm"
    }

    /**
     * get bluetooth address
     *
     * @param target bluetooth address
     * @return ip address
     */
    fun getBluetoothAddress(context: Context, target: String): String {
        if (target == context.getString(R.string.select_device)) {
            return ""
        }
        var portSetting = ""
        val firstPosition = target.lastIndexOf("(")
        val endPosition = target.lastIndexOf(")")
        //add 1 to first position because it becomes mac address from the next position of
        portSetting = "Bluetooth:" + target.substring(firstPosition + 1, endPosition)
        return portSetting
    }

    /**
     * show dialog
     *
     * @param context
     * @param message
     */
    fun showAlertDialog(context: Context?, message: String?) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    /**
     * check bluetooth connect
     *
     * @param mainActivity activity
     * @return is connect
     */
    fun checkBluetoothConnect(activity: Activity, target: String): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null) {
            showAlertDialog(activity, activity.getString(R.string.error_not_support_bluetooth))
            return false
        }
        if (!adapter.isEnabled) {
            return false
        }
        if (target.isEmpty() || target == activity.getString(R.string.select_device)) {
            showAlertDialog(activity, activity.getString(R.string.error_not_exit_print))
            return false
        }
        val portName = getBluetoothAddress(activity, target)
        val prefix = "Bluetooth:"
        val macAddress = portName.substring(prefix.length, portName.length)
        var device: BluetoothDevice? = null
        try {
            device = adapter.getRemoteDevice(macAddress)
        } catch (exception: Exception) {
            showAlertDialog(activity, exception.localizedMessage)
            return false
        }
        return true
    }

    /**
     * svg string to bitmap
     *
     * @param svgString svg string
     */
    fun svgStringToBitmap(svgString: String): Bitmap {
        val svg = SVG.getFromString(svgString)
        val svgWidth = if (svg.documentWidth != -1f) svg.documentWidth else 500f
        val svgHeight = if (svg.documentHeight != -1f) svg.documentHeight else 500f
        val newBM: Bitmap = Bitmap.createBitmap(
            ceil(svgWidth.toDouble()).toInt(),
            ceil(svgHeight.toDouble()).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val bmCanvas = Canvas(newBM)
        bmCanvas.drawRGB(255, 255, 255)
        svg.renderToCanvas(bmCanvas)
        return newBM
    }

    /**
     * set print times
     *
     * @param times number value
     */
    fun setPrintTimes() {
        printTimes += 1
    }

    /**
     * get print times
     *
     * @return print times
     */
    fun getPrintTimes(): Int {
        return printTimes
    }

    /**
     * set print count
     *
     * @param count print count
     */
    fun setPrintCount(count: Int) {
        allPrintCount = count
    }

    /**
     * get print count
     *
     * @return print count
     */
    fun getPrintCount(): Int {
        return allPrintCount
    }

    /**
     * reset print values
     */
    fun resetPrintValues() {
        printTimes = 0
        allPrintCount = 0
    }

    /**
     * save asset files to local
     * @param context
     * @param inputFileName
     * @param folderName
     * @param outputFileName
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun asset2file(context: Context, inputFileName: String?, folderName: String, outputFileName: String) {
        val `as` = context.resources.assets
        val `is` = `as`.open(inputFileName!!)
        in2file(`is`, folderName, outputFileName)
    }

    /**
     * save input stream to file
     * @param in
     * @param folderName
     * @param fileName
     * @throws FileNotFoundException
     * @throws Exception
     */
    @Throws(FileNotFoundException::class, Exception::class)
    private fun in2file(`in`: InputStream?, folderName: String, fileName: String) {
        var size: Int
        val byte = ByteArray(1024)
        var fis: FileOutputStream? = null
        try {
            val fileFullPath = "$folderName/$fileName"
            fis = FileOutputStream(fileFullPath)
            while (true) {
                size = `in`!!.read(byte)
                if (size <= 0) break
                fis.write(byte, 0, size)
            }
            fis.close()
            `in`.close()
        } catch (e: FileNotFoundException) {
            try {
                `in`?.close()
                fis?.close()
            } catch (_: Exception) {
            }
            throw e
        } catch (e: Exception) {
            try {
                `in`?.close()
                fis?.close()
            } catch (_: Exception) {
            }
            throw e
        }
    }

}