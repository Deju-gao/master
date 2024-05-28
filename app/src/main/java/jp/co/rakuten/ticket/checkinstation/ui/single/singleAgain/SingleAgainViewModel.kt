package jp.co.rakuten.ticket.checkinstation.ui.single.singleAgain

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.util.*
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SingleAgainViewModel @Inject constructor(private val apiRepository: ApiRepository) :
    BaseViewModel() {

    val number = MutableLiveData<String>()
    val showDialogEnum = SingleLiveEvent<ErrorMessageEnum>()
    val showDialogString = SingleLiveEvent<String?>()
    val isLoadingVisible = MutableLiveData<Boolean>()
    val login = SingleLiveEvent<Unit>()

    init {

    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(SingleInputPasswordPage)
    }

    //next button did tap
    fun onNextButtonDidTap(context: Context) {
        FirebaseLogUtil.getInstance().uploadPageLog(SingleInputPasswordNextButton)
        if (number.value.isNullOrEmpty()) {
            showDialogEnum.postValue(ErrorMessageEnum.PASSWORD_ERROR)
            return
        }
        isLoadingVisible.postValue(true)
        apiRepository.login(
            LoginUtil.getInstance().getUserId(),
            number.value!!,
            LoginUtil.getInstance().getCompanyCode(),
            UUID.randomUUID().toString()
        )
            .execute(this@SingleAgainViewModel,
                onSuccess = {
                    if (it.code == 200) {
                        login.postValue(Unit)
                    } else {
                        showDialogString.postValue(it.message)
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance()
                        .uploadApiException(SingleInputPasswordPage.pageName, it)
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
        PASSWORD_ERROR
    }

}