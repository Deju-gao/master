package jp.co.rakuten.ticket.checkinstation.ui.single.singleOver

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.SingleCompletePage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleOverViewModel @Inject constructor() : ViewModel() {

    val navigateToTop = SingleLiveEvent<Unit>()

    init {
        GlobalScope.launch(context = Dispatchers.IO) {
            delay(2000)
            navigateToTop.postValue(Unit)
        }
    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(SingleCompletePage)
    }
}