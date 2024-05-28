package jp.co.rakuten.ticket.checkinstation.util

import android.app.Activity
import android.os.Environment
import jp.co.rakuten.ticket.checkinstation.ui.menu.home.MainActivity
import jp.co.toshibatec.bcp.library.BCPControl
import jp.co.toshibatec.bcp.library.BCPControl.LIBBcpControlCallBack
import jp.co.toshibatec.bcp.library.StringRef
import jp.co.rakuten.ticket.checkinstation.R

class BcpPrintTicket: LIBBcpControlCallBack {

    //single method
    companion object {
        private var instance: BcpPrintTicket? = null
        fun getInstance(): BcpPrintTicket {
            if (instance == null) instance = BcpPrintTicket()
            return instance!!
        }
    }

    //bcp print complete listener
    interface BcpPrintCompleteListener {
        fun bcpPrintComplete(isSuccess: Boolean)
    }

    //bcp control
    private var bcpControl: BCPControl? = null
    //connect data
    private val bcpConnectionData = BcpConnectionExecuteData()
    //current issue mode
    private var currentIssueMode = 1
    //print data
    private var printData = BcpPrintData()
    //connection delegate
    private var connectionDelegate: BcpConnectionUtil? = null
    //completion return
    private val completionReturn = 2
    //complete listener
    private var completeListener: BcpPrintCompleteListener? = null

    /**
     * initialize the bcp printer
     *
     * @param activity main activity
     */
    fun initializeTheBcpPrinter(activity: MainActivity) {
        if (bcpControl == null) {
            bcpControl = BCPControl(this)
            val systemPath = Environment.getDataDirectory().path + "/data/" + activity.packageName
            bcpControl!!.systemPath = systemPath
            bcpControl!!.language = 0
            connectionDelegate = BcpConnectionUtil()
            openBluetoothPort(activity)
        }
        else {
            connectionDelegate = BcpConnectionUtil()
            openBluetoothPort(activity)
        }
    }

    /**
     * print method
     *
     * @param printCount print count
     * @param target bluetooth address
     * @param activity activity
     */
    fun printMethod(printCount: Int, target: String, activity: Activity) {
        if (target == activity.getString(R.string.select_device)) {
            BcpUtil.getInstance().showAlertDialog(activity, activity.getString(R.string.error_not_exit_print))
            return
        }
        printData.currentIssueMode = currentIssueMode
        printData.printCount = printCount

        //print demo
//        val labelItemList = HashMap<String, String>()
//        labelItemList["品名 ﾃﾞｰﾀ"] = "BV400-T"
//        labelItemList["製品ｺｰﾄﾞ ﾃﾞｰﾀ"] = "21052376"
//        labelItemList["ﾊﾞｰｺｰﾄﾞ"] = "21052376"
//        printData.objectDataList = labelItemList

        val filePath = BcpUtil.getInstance().getLfmFilePath(activity)
        printData.lfmFileFullPath = filePath

        val bcpExecuteUtil = BcpExecuteUtil(activity, bcpControl)
        bcpExecuteUtil.setListener(object : BcpExecuteUtil.BcpPrintCompleteListener {
            override fun bcpPrintComplete(isSuccess: Boolean) {
                completeListener?.bcpPrintComplete(isSuccess)
            }
        })
        bcpExecuteUtil.execute(printData)
    }

    //set listener
    fun setListener(completeListener: BcpPrintCompleteListener) {
        this.completeListener = completeListener
    }

    /**
     * reset param
     */
    fun reset(activity: Activity) {
        closeBluetoothPort(activity)
        bcpControl = null
        connectionDelegate = null
    }

    /**
     * open bluetooth port
     *
     * @param activity main activity
     */
    private fun openBluetoothPort(activity: MainActivity) {
        if (!bcpConnectionData.isOpen.get()) {
            bcpControl?.let {
                connectionDelegate?.openPort(activity,
                    it, bcpConnectionData, completionReturn)
            }
            currentIssueMode = completionReturn
        }
    }

    /**
     * close bluetooth port
     *
     * @param activity main activity
     */
    private fun closeBluetoothPort(activity: Activity) {
        bcpControl?.let { connectionDelegate?.closePort(activity, it, bcpConnectionData) }
    }

    /**
     * status receive method from printer
     * @param PrinterStatus reception status string
     * @param Result status information
     */
    //LIBBcpControlCallBack
    override fun BcpControl_OnStatus(PrinterStatus: String?, Result: Long) {
        val message = StringRef("")
        if (false == bcpControl?.GetMessage(Result, message)) {
        }
    }

}