package jp.co.rakuten.ticket.checkinstation.ui.menu.home

import androidx.lifecycle.ViewModel
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.util.*

class MenuViewModel : ViewModel() {

    val toLogin = SingleLiveEvent<Unit>()
    val showDialogString = SingleLiveEvent<String>()

    //init method
    init {

    }

    //view create
    fun onViewCreate(activity: MainActivity) {
        if (activity.getBluetoothAddress() != activity.getString(R.string.select_device) && activity.getIsBackFromPrintSetting()) {
            BcpPrintTicket.getInstance().initializeTheBcpPrinter(activity)
        }
    }

    //view resume
    fun onViewResume(activity: MainActivity) {
        FirebaseLogUtil.getInstance().uploadPageLog(HomePage)
    }

    //to login button did tap
    fun toLoginButtonDidTap(activity: MainActivity) {
        val target = activity.getPrinterTarget()
        if (target != null) {
            if (target.isEmpty() || target == activity.getString(R.string.select_device)) {
                showDialogString.postValue(activity.getString(R.string.error_not_exit_print))
            }
        }
        else {
            toLogin.postValue(Unit)
        }
    }

    //print test
    fun printTest(activity: MainActivity, target: String) {
        //TM-30
//        PrintTicket.getInstance().initializeThePrinter(context)
//        if (!PrintTicket.getInstance().runPrintReceiptSequence(target, true, context)) {
//
//        }
        if (BcpUtil.getInstance().checkBluetoothConnect(activity, target)) {
            val bcpPrintTicket = BcpPrintTicket.getInstance()
            bcpPrintTicket.setListener(object : BcpPrintTicket.BcpPrintCompleteListener {
                override fun bcpPrintComplete(isSuccess: Boolean) {

                }
            })
            bcpPrintTicket.printMethod(1, target, activity)
        }
    }

}