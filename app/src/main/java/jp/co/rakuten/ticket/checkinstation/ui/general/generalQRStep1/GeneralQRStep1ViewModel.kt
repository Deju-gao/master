package jp.co.rakuten.ticket.checkinstation.ui.general.generalQRStep1

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.subjects.BehaviorSubject
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectCount.GeneralSelectCountArguments
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralQRCodeScanPage
import jp.co.rakuten.ticket.checkinstation.util.execute
import javax.inject.Inject
import jp.co.rakuten.ticket.checkinstation.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@HiltViewModel
class GeneralQRStep1ViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {

    private var permissionStatus: BehaviorSubject<GeneralQRStep1PermissionStatus> =
        BehaviorSubject.createDefault(GeneralQRStep1PermissionStatus.UNKNOWN)
    val back = SingleLiveEvent<GeneralSelectCountArguments>()
    val checkToAskPermission = SingleLiveEvent<Unit>()
    val startCapture = SingleLiveEvent<Unit>()
    val stopCapture = SingleLiveEvent<Unit>()
    val askCameraPermission = SingleLiveEvent<Unit>()
    val displayNeverAskAgainDialog = SingleLiveEvent<Unit>()
    val navigateBack = SingleLiveEvent<Unit>()
    val openPermissionSettings = SingleLiveEvent<Unit>()
    val isLoadingVisible = MutableLiveData<Boolean>()
    val showDialogString = SingleLiveEvent<String>()

    init {
        requestPermission()
    }

    //resume
    fun onResumeViewModel(){
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralQRCodeScanPage)
        if (permissionStatus.value == GeneralQRStep1PermissionStatus.CHECK_NEEDED_ONLY_ON_RESUME) {
            permissionStatus.onNext(GeneralQRStep1PermissionStatus.UNKNOWN)
        }
        if (permissionStatus.value == GeneralQRStep1PermissionStatus.ACCESS_GRANTED) {
            startCapture?.postValue(Unit)
        }
    }

    //qrcode result
    fun handleQRCodeResult(result: BarcodeResult?, againMode: Boolean, context: Context){
        if (result != null && result.text.isNotEmpty()) {
            if (againMode) {
                refreshOrderQR(result.text, context)
            }
            else {
                if (result.text.length == 20) {
                    qrcodeLengthIsEqualTwenty(result.text, context)
                }
                else {
                    qrcodeLengthIsNotEqualTwenty(result.text, context)
                }
            }
        }
        else {
            showDialogString.postValue(context.getString(R.string.error_scan_qrcode))
        }
    }

    fun onPermissionResult(isAccessGranted: Boolean) {
        if (isAccessGranted) {
            permissionStatus.onNext(GeneralQRStep1PermissionStatus.ACCESS_GRANTED)
        } else {
            permissionStatus.onNext(GeneralQRStep1PermissionStatus.NOT_GRANTED)
        }
    }

    fun onOpenSettingsClick() {
        permissionStatus.onNext(GeneralQRStep1PermissionStatus.CHECK_NEEDED_ONLY_ON_RESUME)
        openPermissionSettings.postValue(Unit)
    }

    fun onNegativeButtonAlertRefuseClick() {
        navigateBack.postValue(Unit)
    }

    fun onRefusePermission() {
        permissionStatus.onNext(GeneralQRStep1PermissionStatus.REFUSE)
    }

    fun onNeverAskMeAgainResult() {
        permissionStatus.onNext(GeneralQRStep1PermissionStatus.NEVER_ASK_AGAIN)
    }

    //refresh order qr
    private fun refreshOrderQR(qrcode: String, context: Context) {
        isLoadingVisible.postValue(true)
        apiRepository.refreshOrderQR(qrcode)
            .execute(this@GeneralQRStep1ViewModel,
                onSuccess = {
                    if (qrcode.length == 20) {
                        qrcodeLengthIsEqualTwenty(qrcode, context)
                    }
                    else {
                        qrcodeLengthIsNotEqualTwenty(qrcode, context)
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(GeneralQRCodeScanPage.pageName, it)
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

    //qrcode length is not equal twenty
    private fun qrcodeLengthIsNotEqualTwenty(qrcode: String, context: Context) {
        isLoadingVisible.postValue(true)
        apiRepository.qrTicket(qrcode)
            .execute(this@GeneralQRStep1ViewModel,
                onSuccess = {
                    back.postValue(it.additional?.order?.order_no?.let { it1 -> GeneralSelectCountArguments(orderNo = it1) })
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(GeneralQRCodeScanPage.pageName, it)
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

    //qrcode length is equal twenty
    private fun qrcodeLengthIsEqualTwenty(qrcode: String, context: Context) {
        isLoadingVisible.postValue(true)
        apiRepository.qrTicketSkidata(qrcode)
            .execute(this@GeneralQRStep1ViewModel,
                onSuccess = {
                    back.postValue(it.additional?.order?.order_no?.let { it1 -> GeneralSelectCountArguments(orderNo = it1) })
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(GeneralQRCodeScanPage.pageName, it)
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

    //get current camera permission
    private fun requestPermission() {
        permissionStatus.execute(this@GeneralQRStep1ViewModel) { status ->
            when (status) {
                GeneralQRStep1PermissionStatus.UNKNOWN -> checkToAskPermission.postValue(Unit)
                GeneralQRStep1PermissionStatus.ACCESS_GRANTED -> startCapture.postValue(Unit)
                GeneralQRStep1PermissionStatus.NOT_GRANTED -> askCameraPermission.postValue(Unit)
                GeneralQRStep1PermissionStatus.NEVER_ASK_AGAIN -> displayNeverAskAgainDialog.postValue(Unit)
                GeneralQRStep1PermissionStatus.REFUSE -> navigateBack.postValue(Unit)
                else -> {
                }
            }
        }
    }

    enum class GeneralQRStep1PermissionStatus {
        UNKNOWN, ACCESS_GRANTED, NOT_GRANTED, NEVER_ASK_AGAIN, REFUSE, CHECK_NEEDED_ONLY_ON_RESUME
    }
}