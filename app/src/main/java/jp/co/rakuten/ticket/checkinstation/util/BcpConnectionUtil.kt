package jp.co.rakuten.ticket.checkinstation.util

import android.app.Activity
import android.os.Environment
import jp.co.rakuten.ticket.checkinstation.ui.menu.home.MainActivity
import jp.co.toshibatec.bcp.library.BCPControl
import jp.co.toshibatec.bcp.library.LongRef
import jp.co.toshibatec.bcp.library.StringRef

class BcpConnectionUtil {

    /**
     * open port
     *
     * @param activity
     * @param bcpControl
     * @param connectData
     * @param issueMode
     */
    fun openPort(activity: MainActivity, bcpControl: BCPControl, connectData: BcpConnectionExecuteData, issueMode: Int) {
        if (!connectData.isOpen.get()) {
            val systemPath = Environment.getDataDirectory().path + "/data/" + activity.packageName
            bcpControl.systemPath = systemPath
            val portSetting = getPortSetting(activity)
            connectData.issueMode = issueMode
            connectData.portSetting = portSetting.toString()
            bcpControl.usePrinter = 40
            val connectTask = BcpConnectExecuteUtil(activity, bcpControl)
            connectTask.execute(connectData)
        }
    }

    /**
     * close port
     *
     * @param activity
     * @param bcpControl
     * @param connectData
     */
    fun closePort(activity: Activity?, bcpControl: BCPControl, connectData: BcpConnectionExecuteData) {
        if (connectData.isOpen.get()) {
            val result = LongRef(0)
            if (!bcpControl.ClosePort(result)) {
                val message = StringRef("")
                if (!bcpControl.GetMessage(result.longValue, message)) {
                    BcpUtil.getInstance().showAlertDialog(activity, "Port close error")
                } else {
                    BcpUtil.getInstance().showAlertDialog(activity, message.stringValue)
                }
                return
            } else {
                connectData.isOpen.set(false)
            }
        }
    }

    /**
     * get port setting
     *
     * @param mainActivity
     */
    private fun getPortSetting(mainActivity: MainActivity): StringBuilder {
        val portSetting = StringBuilder()
        if (mainActivity.getBluetoothAddress().isNotEmpty()) {
            portSetting.insert(0, mainActivity.getBluetoothAddress())
            portSetting.insert(0, BcpUtil.getInstance().getBluetoothAddress(mainActivity, portSetting.toString()))
        }
        return portSetting
    }

}