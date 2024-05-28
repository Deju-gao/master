package jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectMode

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralSelectModePage
import javax.inject.Inject

@HiltViewModel
class GeneralSelectModeViewModel @Inject constructor() : ViewModel() {

    init {

    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectModePage)
    }
}