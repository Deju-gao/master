package jp.co.rakuten.ticket.checkinstation.ui.general.generalInputNumber

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.ui.general.generalInputTelephone.GeneralInputTelephoneArguments
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralInputOrderNumberNextButton
import jp.co.rakuten.ticket.checkinstation.util.GeneralInputOrderNumberPage
import javax.inject.Inject

@HiltViewModel
class GeneralInputNumberViewModel @Inject constructor() : ViewModel() {

    val number = MutableLiveData<String>()
    val navigateToNext = SingleLiveEvent<GeneralInputTelephoneArguments>()
    val showDialogEnum = SingleLiveEvent<ErrorMessageEnum>()

    init {

    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputOrderNumberPage)
    }

    fun onNextButtonClick() {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputOrderNumberNextButton)
        if (number.value.isNullOrEmpty()){
            showDialogEnum.postValue(ErrorMessageEnum.ORDER_NO_ERROR)
            return
        }
        navigateToNext.postValue(GeneralInputTelephoneArguments(number = number.value!!))
    }

    enum class ErrorMessageEnum {
        ORDER_NO_ERROR
    }
}