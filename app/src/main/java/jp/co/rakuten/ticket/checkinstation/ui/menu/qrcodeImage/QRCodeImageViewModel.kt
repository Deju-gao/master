package jp.co.rakuten.ticket.checkinstation.ui.menu.qrcodeImage

import androidx.lifecycle.ViewModel
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.QRCodeImageSettingPage

class QRCodeImageViewModel : ViewModel() {

    //init method
    init {

    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(QRCodeImageSettingPage)
    }

}