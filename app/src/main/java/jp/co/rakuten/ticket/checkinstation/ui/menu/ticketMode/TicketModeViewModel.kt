package jp.co.rakuten.ticket.checkinstation.ui.menu.ticketMode

import androidx.lifecycle.ViewModel
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.util.*

class TicketModeViewModel : ViewModel() {

    val modeSelected = SingleLiveEvent<Int>()

    private var currentIndex: Int = 0

    //init method
    init {

    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(TicketModeSettingPage)
    }

    fun modeListSelected(index: Int) {
        when (index) {
            0 -> {
                FirebaseLogUtil.getInstance().uploadPageLog(TicketModeGeneralButton)
            }
            1 -> {
                FirebaseLogUtil.getInstance().uploadPageLog(TicketModeAllButton)
            }
            2 -> {
                FirebaseLogUtil.getInstance().uploadPageLog(TicketModeSingleButton)
            }
        }
        currentIndex = index
        modeSelected.postValue(index)
    }

}