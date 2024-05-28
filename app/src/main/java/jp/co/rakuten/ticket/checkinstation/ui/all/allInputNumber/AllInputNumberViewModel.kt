package jp.co.rakuten.ticket.checkinstation.ui.all.allInputNumber

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.ui.all.allInputTelephone.AllInputTelephoneArguments
import jp.co.rakuten.ticket.checkinstation.util.AllInputOrderNumberNextButton
import jp.co.rakuten.ticket.checkinstation.util.AllInputOrderNumberPage
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import javax.inject.Inject

@HiltViewModel
class AllInputNumberViewModel @Inject constructor() : ViewModel() {

    val number = MutableLiveData<String>()
    val navigateToNext = SingleLiveEvent<AllInputTelephoneArguments>()
    val showDialogEnum = SingleLiveEvent<ErrorMessageEnum>()

    init {

    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(AllInputOrderNumberPage)
    }

    fun onNextButtonClick() {
        FirebaseLogUtil.getInstance().uploadPageLog(AllInputOrderNumberNextButton)
        if (number.value.isNullOrEmpty()){
            showDialogEnum.postValue(ErrorMessageEnum.ORDER_NO_ERROR)
            return
        }
        navigateToNext.postValue(AllInputTelephoneArguments(number = number.value!!))
    }

    enum class ErrorMessageEnum {
        ORDER_NO_ERROR
    }

}