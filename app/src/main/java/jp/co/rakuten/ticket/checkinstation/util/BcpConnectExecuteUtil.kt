package jp.co.rakuten.ticket.checkinstation.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import jp.co.toshibatec.bcp.library.BCPControl
import jp.co.toshibatec.bcp.library.LongRef
import jp.co.toshibatec.bcp.library.StringRef
import jp.co.rakuten.ticket.checkinstation.R

class BcpConnectExecuteUtil(context: Activity?, bcpControl: BCPControl?): AsyncTask<BcpConnectionExecuteData?, Void?, String>() {

    private var bcpControl: BCPControl? = null
    @SuppressLint("StaticFieldLeak")
    private var context: Activity? = null
    var progressDialog: ProgressDialog? = null

    // コンストラクタ
    init {
        this.context = context
        this.bcpControl = bcpControl
    }

    @Deprecated("Deprecated in Java")
    override fun onPreExecute() {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle(R.string.print_connect)
        progressDialog!!.setMessage(context!!.getString(R.string.print_wait))
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.setCancelable(true)
        progressDialog!!.show()
    }

    /**
     * processes that run in the background
     */
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg p0: BcpConnectionExecuteData?): String? {
        val connectData = p0[0]
        val portSettingData = connectData?.portSetting
        if (portSettingData?.isEmpty() == true || portSettingData == context!!.getString(R.string.select_device)) {
            return ""
        }
        bcpControl!!.portSetting = portSettingData
        val result = LongRef(0)
        val resultOpen = connectData?.issueMode?.let { bcpControl!!.OpenPort(it, result) }
        if (resultOpen != null) {
            connectData.isOpen.set(resultOpen)
        }
        if (false == resultOpen) {
            val message = StringRef("")
            return if (!bcpControl!!.GetMessage(result.longValue, message)) {
                context!!.getString(R.string.error_open_port)
            } else message.getStringValue()
        }
        return context!!.getString(R.string.print_connect_success)
    }

    /**
     * what to do in the main thread
     *
     * @param result
     */
    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: String) {
        progressDialog!!.dismiss()
        if (result.isNotEmpty()) {
            BcpUtil.getInstance().showAlertDialog(context, result)
        }
        context = null
        bcpControl = null
    }
}