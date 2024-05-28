package jp.co.rakuten.ticket.checkinstation.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import jp.co.toshibatec.bcp.library.BCPControl
import jp.co.toshibatec.bcp.library.LongRef
import jp.co.toshibatec.bcp.library.StringRef
import jp.co.rakuten.ticket.checkinstation.R

class BcpExecuteUtil(context: Activity?, bcpControl: BCPControl?): AsyncTask<BcpPrintData?, Void?, String>() {

    //bcp print complete listener
    interface BcpPrintCompleteListener {
        fun bcpPrintComplete(isSuccess: Boolean)
    }

    private var bcpControl: BCPControl? = null
    @SuppressLint("StaticFieldLeak")
    private var context: Activity? = null
    private var mGetStatus = ""
    private val completion = 2
    private var progressDialog: ProgressDialog? = null
    //complete listener
    private var completeListener: BcpPrintCompleteListener? = null

    init {
        this.context = context
        this.bcpControl = bcpControl
    }

    //set listener
    fun setListener(completeListener: BcpPrintCompleteListener) {
        this.completeListener = completeListener
    }

    @Deprecated("Deprecated in Java")
    override fun onPreExecute() {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle(R.string.print_run)
        progressDialog!!.setMessage(context!!.getString(R.string.print_wait))
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg p0: BcpPrintData?): String? {
        val printData = p0[0]
        var ret: Long = 0
        printData?.result = 0
        printData?.statusMessage = ""
        val lfmFileFullPath = printData?.lfmFileFullPath
        mGetStatus = ""
        ret = lfmFileFullPath?.let { loadLfmFile(it) }!!
        if (ret != 0L) {
            return String.format("loadLfmFile Error = %08x %s", ret, lfmFileFullPath)
        }
        if (printData.currentIssueMode == completion) {
            ret = changePosition()
            if (ret != 0L) {
                return String.format("changePosition Error = %08x ", ret)
            }
        }

        // change issue mode
        ret = changeIssueMode()
        if (ret != 0L) {
            return String.format("changeIssueMode Error = %08x ", ret)
        }
        // set object
//        ret = setObjectDataEx(printData)
//        if (ret != 0L) {
//            return String.format("setObjectDataEx Error = %08x ", ret)
//        }
        //set bitmap
        ret = setBitmapToPrint(SaveDataUtil.getInstance().getPrintBitmapPath(context!!))
        if (ret != 0L) {
            return String.format("setBitmap Error = %08x ", ret)
        }
        val printCount = printData.printCount
        val printerStatus = StringRef("")
        // print
        ret = executePrint(printCount, printerStatus)
        if (ret != 0L) {
            printData.result = ret
            val message = StringRef("")
            if (ret == 0x800A044EL) {
                val errCode = printerStatus.getStringValue().substring(0, 2)
                bcpControl!!.GetMessage(errCode, message)
            } else {
                if (!bcpControl!!.GetMessage(ret, message)) {
                    message.setStringValue(String.format("executePrint Error = %08x ", ret))
                }
            }
            printData.statusMessage = message.getStringValue()

            return if (bcpControl!!.IsIssueRetryError()) {
                "RetryError"
            } else {
                "Error"
            }
        } else {
            printData.result = 0
        }
        if (printData.currentIssueMode == completion) {
            mGetStatus = this.printerStatus
        }
        printData.statusMessage = "Success"
        return "Success"
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: String) {
        BcpUtil.getInstance().setPrintTimes()
        if (BcpUtil.getInstance().getPrintTimes() == BcpUtil.getInstance().getPrintCount()) {
            progressDialog!!.dismiss()
            if (result == "Success") {
                BcpUtil.getInstance().showAlertDialog(context, context!!.getString(R.string.print_complete))
                completeListener?.bcpPrintComplete(true)
            }
            else {
                BcpUtil.getInstance().showAlertDialog(context, result)
                completeListener?.bcpPrintComplete(false)
            }
            BcpUtil.getInstance().resetPrintValues()
        }
        bcpControl = null
        context = null
    }

    private fun changePosition(): Long {
        val result = LongRef(0)
        val mode = 0
        val adjust = -15
        return if (!bcpControl!!.ChangePosition(mode, adjust, result)) {
            result.longValue
        } else 0
    }

    /**
     *
     * @return
     */
    private val printerStatus: String
        get() {
            val printerStatus = StringRef(" ")
            val result = LongRef(0)
            return if (!bcpControl!!.GetStatus(printerStatus, result)) {
                "GetStatus call Error"
            } else {
                "GetStatus = $printerStatus : Result = $result"
            }
        }

    /**
     *
     * @param printCount print count
     * @return
     */
    private fun executePrint(printCount: Int, printerStatus: StringRef): Long {
        var ret: Long = 0
        val result = LongRef(0)
        val cutInterval = 10
        if (!bcpControl!!.Issue(printCount, cutInterval, printerStatus, result)) {
            ret = result.longValue
        }
        return ret
    }

    /**
     *
     * @return
     */
    private fun setObjectDataEx(printData: BcpPrintData): Long {
        var ret: Long = 0
        val result = LongRef(0)
        val keySet: Set<String?>? = printData.objectDataList?.keys
        val keyIte = keySet?.iterator()
        while (keyIte?.hasNext() == true) {
            val key = keyIte.next() as String
            if (!bcpControl!!.SetObjectDataEx(key, printData.objectDataList?.get(key), result)) {
                ret = result.longValue
                break
            }
        }
        return ret
    }

    /**
     * set bitmap to print
     */
    private fun setBitmapToPrint(bitmapPath: String): Long {
        var ret: Long = 0
        val result = LongRef(0)
        if (!bcpControl!!.SetObjectData(1, bitmapPath, result)) {
            ret = result.longValue
        }
        return ret
    }

    /**
     * change mode
     *
     * @return mode
     */
    private fun changeIssueMode(): Long {
        val flag = 0
        val type = 0
        val result = LongRef(0)
        bcpControl!!.ChangeIssueMode(flag, type, result)
        return result.longValue
    }

    /**
     * set port
     *
     * @return port
     */
    private fun openPort(): Long {
        val result = LongRef(0)
        val issueMode = 1
        return if (!bcpControl!!.OpenPort(issueMode, result)) {
            result.longValue
        } else 0
    }

    /**
     *
     * @param lfmFileFullPath
     * @return
     */
    private fun loadLfmFile(lfmFileFullPath: String): Long {
        val result = LongRef(0)
        return if (!bcpControl!!.LoadLfmFile(lfmFileFullPath, result)) {
            result.longValue
        } else 0
    }
}