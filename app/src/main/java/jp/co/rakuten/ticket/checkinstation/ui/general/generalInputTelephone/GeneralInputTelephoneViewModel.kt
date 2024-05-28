package jp.co.rakuten.ticket.checkinstation.ui.general.generalInputTelephone

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.ui.general.generalNeedSelect.GeneralNeedSelectArguments
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralInputTelephoneNextButton
import jp.co.rakuten.ticket.checkinstation.util.GeneralInputTelephonePage
import jp.co.rakuten.ticket.checkinstation.util.execute
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class GeneralInputTelephoneViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {

    val telePhone = MutableLiveData<String>()
    val navigateToNext = SingleLiveEvent<GeneralNeedSelectArguments>()
    val isLoadingVisible = MutableLiveData<Boolean>()
    val showDialogString = SingleLiveEvent<String>()
    val showDialogEnum = SingleLiveEvent<ErrorMessageEnum>()

    init {

    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputTelephonePage)
    }

    //next button did tap
    fun onNextButtonClick(context: Context, number: String, againMode: Boolean) {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputTelephoneNextButton)
        if (againMode) {
            refreshOrder(context, number)
        }
        else {
            verifyData(context, number)
        }
    }

    private fun refreshOrder(context: Context, number: String) {
        if (telePhone.value.isNullOrEmpty()){
            showDialogEnum.postValue(ErrorMessageEnum.TEL_ERROR)
            return
        }
        isLoadingVisible.postValue(true)
        apiRepository.refreshOrder2(number, telePhone.value!!)
            .execute(this@GeneralInputTelephoneViewModel,
                onSuccess = {
                    verifyData(context, number)
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(GeneralInputTelephonePage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(context.getString(R.string.error_network))
                        }
                        is HttpException -> {
                            showDialogString.postValue(it.message())
                        }
                        else -> {
                            showDialogString.postValue(it.message)
                        }
                    }
                },
                onFinal = {
                    isLoadingVisible.postValue(false)
                }
            )
    }

    private fun verifyData(context: Context, number: String) {
        if (telePhone.value.isNullOrEmpty()){
            showDialogEnum.postValue(ErrorMessageEnum.TEL_ERROR)
            return
        }
        isLoadingVisible.postValue(true)
        apiRepository.orderNoVerifiedData(number, telePhone.value!!)
            .execute(this@GeneralInputTelephoneViewModel,
                onSuccess = {
                    navigateToNext.postValue(GeneralNeedSelectArguments(orderNo = it.orderNo))
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(GeneralInputTelephonePage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(context.getString(R.string.error_network))
                        }
                        is HttpException -> {
                            showDialogString.postValue(it.message())
                        }
                        else -> {
                            showDialogString.postValue(it.message)
                        }
                    }
                },
                onFinal = {
                    isLoadingVisible.postValue(false)
                }
            )
    }

    enum class ErrorMessageEnum {
        TEL_ERROR
    }

}