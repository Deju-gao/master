package jp.co.rakuten.ticket.checkinstation.util

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import com.epson.epos2.Epos2Exception
import com.epson.epos2.printer.Printer
import com.epson.epos2.printer.PrinterStatusInfo
import com.epson.epos2.printer.ReceiveListener
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent

//print ticket util
class PrintTicket: ReceiveListener {

    companion object {
        private var instance: PrintTicket? = null
        fun getInstance(): PrintTicket {
            if (instance == null) instance = PrintTicket()
            return instance!!
        }
    }

    //printer
    private var printer: Printer? = null
    //show dialog
    private val showDialog = SingleLiveEvent<String>()
    //complete listener
    private var completeListener: PrintCompleteListener? = null

    /**
     * initialize the printer
     *
     * @param context page that call the method
     * @return can the printer be initialized
     */
    fun initializeThePrinter(context: Context): Boolean {
        try {
            printer = Printer(Printer.TM_M30II, Printer.MODEL_ANK, context)
        } catch (exception: Exception) {
            Toast.makeText(context, "init printer error", Toast.LENGTH_SHORT).show()
            return false
        }
        printer!!.setReceiveEventListener(this)
        return true
    }

    /**
     * reset printer
     */
    fun resetPrinter() {
        if (printer == null) {
            return
        }
        printer!!.setReceiveEventListener(null)
        printer = null
    }

    /**
     * run print receipt sequence
     *
     * @param ipAddress printer ip address
     * @return can run print
     */
    fun runPrintReceiptSequence(ipAddress: String, isTestData: Boolean, context: Context): Boolean {
        if (isTestData) {
            if (!createReceiptTestData(context)) {
                return false
            }
        }
        else {
            if (!createReceiptData()) {
                return false
            }
        }
        if (!printData(ipAddress)) {
            return false
        }
        return true
    }

    /**
     * set complete listener
     *
     * @param listener print complete listener
     */
    fun setCompleteListener(listener: PrintCompleteListener) {
        completeListener = listener
    }

    /**
     * create receipt data
     *
     * @return can create receipt data
     */
    private fun createReceiptData(): Boolean {
        if (printer == null) {
            return false
        }
        printer!!.addPageBegin()
        printer!!.addPageArea(0, 0, 200, 100)
        printer!!.addTextAlign(Printer.ALIGN_LEFT)
        printer!!.addText("Test Left Top")
        printer!!.addPageArea(400, 0, 200, 100)
        printer!!.addTextAlign(Printer.ALIGN_RIGHT)
        printer!!.addText("Test Right Top")
        printer!!.addPageArea(0, 150, 200, 100)
        printer!!.addTextAlign(Printer.ALIGN_LEFT)
        printer!!.addText("Test Left Bottom")
        printer!!.addPageArea(400, 150, 200, 100)
        printer!!.addTextAlign(Printer.ALIGN_RIGHT)
        printer!!.addText("Test Right Bottom")
        printer!!.addPageEnd()
        printer!!.addCut(Printer.CUT_FEED)
        return true
    }

    /**
     * create receipt test data
     *
     * @return can create receipt test data
     */
    private fun createReceiptTestData(context: Context): Boolean {
        if (printer == null) {
            return false
        }
        var method = ""
        var textData: StringBuilder? = StringBuilder()
        try {

            val ticketData = BitmapFactory.decodeResource(context.resources, R.drawable.ticket)
            method = "addImage"
            printer!!.addImage(
                ticketData,
                0,
                0,
                ticketData.width,
                ticketData.height,
                Printer.COLOR_1,
                Printer.MODE_MONO,
                Printer.HALFTONE_DITHER,
                Printer.PARAM_DEFAULT.toDouble(),
                Printer.COMPRESS_AUTO)

            method = "addCut"
            printer!!.addCut(Printer.CUT_FEED)
        } catch (exception: Exception) {
            printer!!.clearCommandBuffer()
            showDialog.postValue(exception.message)
            return false
        }
        textData = null
        return true
    }

    /**
     * print data
     *
     * @param ipAddress printer ip address
     * @return can print data
     */
    private fun printData(ipAddress: String): Boolean {
        if (printer == null) {
            return false
        }
        if (!connectPrinter(ipAddress)) {
            printer!!.clearCommandBuffer()
        }
        try {
            printer!!.sendData(Printer.PARAM_DEFAULT)
        } catch (exception: Exception) {
            printer!!.clearCommandBuffer()
            showDialog.postValue(exception.message)
            try {
                printer!!.disconnect()
            } catch (exception: Exception) {
                showDialog.postValue(exception.message)
            }
            return false
        }
        return true
    }

    /**
     * connect printer
     *
     * @param ipAddress printer ip address
     * @return is the printer connected
     */
    private fun connectPrinter(ipAddress: String): Boolean {
        if (printer == null) {
            return false
        }
        try {
            printer!!.connect(ipAddress, Printer.PARAM_DEFAULT)
        } catch (exception: Exception) {
            showDialog.postValue(exception.message)
            return false
        }
        return true
    }

    /**
     * disconnect printer
     */
    private fun disconnectPrinter() {
        if (printer == null) {
            return
        }
        while (true) {
            try {
                printer!!.disconnect()
                break
            } catch (exception: Exception) {
                if (exception is Epos2Exception) {
                    //Note: If printer is processing such as printing and so on, the disconnect API returns ERR_PROCESSING.
                    if ((exception as Epos2Exception).errorStatus == Epos2Exception.ERR_PROCESSING) {
                        try {
                            Thread.sleep(500)
                        } catch (ex: java.lang.Exception) {
                        }
                    } else {
                        break
                    }
                } else {
                    break
                }
                break
            }
        }
        printer!!.clearCommandBuffer()
    }

    /**
     * printer listener
     */
    override fun onPtrReceive(p0: Printer?, p1: Int, p2: PrinterStatusInfo?, p3: String?) {
        disconnectPrinter()
        completeListener?.printComplete()
    }

}

//print complete listener
interface PrintCompleteListener {
    fun printComplete()
}