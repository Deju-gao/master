package jp.co.rakuten.ticket.checkinstation.ui.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.api.response.LoginResponse
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import javax.inject.Inject
import jp.co.rakuten.ticket.checkinstation.util.*
import java.util.UUID
import jp.co.rakuten.ticket.checkinstation.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@HiltViewModel
class LoginViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {
    // TODO: Implement the ViewModel
    val userName = MutableLiveData<String>()
    val companyCode = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val login = SingleLiveEvent<Unit>()
    val showDialogEnum = SingleLiveEvent<ErrorMessageEnum>()
    val showDialogString = SingleLiveEvent<String?>()
    val isLoadingVisible = MutableLiveData<Boolean>()
    val hideKeyboard = SingleLiveEvent<Unit>()

    init {
    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(LoginPage)
    }

    //login button did tap
    fun login(context: Context){
        FirebaseLogUtil.getInstance().uploadPageLog(LoginButton)
        if (companyCode.value.isNullOrEmpty()){
            showDialogEnum.postValue(ErrorMessageEnum.COMPANY_CODE_ERROR)
            return
        }
        if (userName.value.isNullOrEmpty()){
            showDialogEnum.postValue(ErrorMessageEnum.USERNAME_ERROR)
            return
        }
        if (password.value.isNullOrEmpty()){
            showDialogEnum.postValue(ErrorMessageEnum.PASSWORD_ERROR)
            return
        }
        hideKeyboard.postValue(Unit)
        isLoadingVisible.postValue(true)
        apiRepository.login(userName.value!!, password.value!!, companyCode.value!!, UUID.randomUUID().toString())
            .execute(this@LoginViewModel,
                onSuccess = {
                    if (it.code == 200) {
                        LoginUtil.getInstance()
                            .saveUserIdAndCompanyCode(userName.value!!, companyCode.value!!)
                        login.postValue(Unit)
                    }
                    else {
                        showDialogString.postValue(it.message)
                    }
                },
                onError = {
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
                    FirebaseLogUtil.getInstance().uploadApiException(LoginPage.pageName, it)
                },
                onFinal = {
                    isLoadingVisible.postValue(false)
                }
            )
    }

    enum class ErrorMessageEnum {
        USERNAME_ERROR,
        PASSWORD_ERROR,
        COMPANY_CODE_ERROR,
    }
}