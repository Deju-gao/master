package jp.co.rakuten.ticket.checkinstation.ui.single.singleQRStep1

import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.subjects.BehaviorSubject
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.SingleActivity
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.api.requestBody.PrintedUpdateBody
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.ui.single.singleLoading.SingleLoadingArguments
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.SingleQRCodeScanPage
import jp.co.rakuten.ticket.checkinstation.util.execute
import javax.inject.Inject
import jp.co.rakuten.ticket.checkinstation.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@HiltViewModel
class SingleQRStep1ViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {

    private var permissionStatus: BehaviorSubject<SingleQRStep1PermissionStatus> =
        BehaviorSubject.createDefault(SingleQRStep1PermissionStatus.UNKNOWN)
    val back = SingleLiveEvent<SingleLoadingArguments>()
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
        FirebaseLogUtil.getInstance().uploadPageLog(SingleQRCodeScanPage)
        if (permissionStatus.value == SingleQRStep1PermissionStatus.CHECK_NEEDED_ONLY_ON_RESUME) {
            permissionStatus.onNext(SingleQRStep1PermissionStatus.UNKNOWN)
        }
        if (permissionStatus.value == SingleQRStep1PermissionStatus.ACCESS_GRANTED) {
            startCapture.postValue(Unit)
        }
    }

    //qrcode result
    fun handleQRCodeResult(result: BarcodeResult?, againMode: Boolean, singleActivity: SingleActivity){
        if (result != null && result.text.isNotEmpty()) {
            val target = singleActivity.intent.getStringExtra("printer_target")
            if (target != null) {
                if (target.isEmpty() || target == singleActivity.getString(R.string.select_device)) {
                    showDialogString.postValue(singleActivity.getString(R.string.error_not_exit_print))
                }
                else {
                    if (againMode) {
                        refreshOrderQR(result.text, singleActivity)
                    }
                    else {
                        if (result.text.length == 20) {
                            qrcodeLengthIsEqualTwenty(result.text, singleActivity)
                        }
                        else {
                            qrcodeLengthIsNotEqualTwenty(result.text, singleActivity)
                        }
                    }
                }
            }
            else {
                showDialogString.postValue(singleActivity.getString(R.string.error_not_exit_print))
            }
        }
        else {
            showDialogString.postValue(singleActivity.getString(R.string.error_scan_qrcode))
        }
    }

    fun onPermissionResult(isAccessGranted: Boolean) {
        if (isAccessGranted) {
            permissionStatus.onNext(SingleQRStep1PermissionStatus.ACCESS_GRANTED)
        } else {
            permissionStatus.onNext(SingleQRStep1PermissionStatus.NOT_GRANTED)
        }
    }

    fun onOpenSettingsClick() {
        permissionStatus.onNext(SingleQRStep1PermissionStatus.CHECK_NEEDED_ONLY_ON_RESUME)
        openPermissionSettings.postValue(Unit)
    }

    fun onNegativeButtonAlertRefuseClick() {
        navigateBack.postValue(Unit)
    }

    fun onRefusePermission() {
        permissionStatus.onNext(SingleQRStep1PermissionStatus.REFUSE)
    }

    fun onNeverAskMeAgainResult() {
        permissionStatus.onNext(SingleQRStep1PermissionStatus.NEVER_ASK_AGAIN)
    }

    //refresh order qr
    private fun refreshOrderQR(qrcode: String, singleActivity: SingleActivity) {
        isLoadingVisible.postValue(true)
        apiRepository.refreshOrderQR(qrcode)
            .execute(this@SingleQRStep1ViewModel,
                onSuccess = {
                    if (qrcode.length == 20) {
                        qrcodeLengthIsEqualTwenty(qrcode, singleActivity)
                    }
                    else {
                        qrcodeLengthIsNotEqualTwenty(qrcode, singleActivity)
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(SingleQRCodeScanPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(singleActivity.getString(R.string.error_network))
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
    private fun qrcodeLengthIsNotEqualTwenty(qrcode: String, singleActivity: SingleActivity) {
        isLoadingVisible.postValue(true)
        apiRepository.qrTicket(qrcode)
            .execute(this@SingleQRStep1ViewModel,
                onSuccess = {
                    it.orderedProductItemTokenId?.let { it1 -> qrSvgSourceOne(it1, singleActivity) }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(SingleQRCodeScanPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(singleActivity.getString(R.string.error_network))
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
    private fun qrcodeLengthIsEqualTwenty(qrcode: String, singleActivity: SingleActivity) {
        isLoadingVisible.postValue(true)
        apiRepository.qrTicketSkidata(qrcode)
            .execute(this@SingleQRStep1ViewModel,
                onSuccess = {
                    it.orderedProductItemTokenId?.let { it1 -> qrSvgSourceOne(it1, singleActivity) }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(SingleQRCodeScanPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(singleActivity.getString(R.string.error_network))
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

    //qr svg source one
    private fun qrSvgSourceOne(tokenId: String, singleActivity: SingleActivity) {
        apiRepository.svgOne(tokenId, singleActivity.againMode)
            .execute(this@SingleQRStep1ViewModel,
                onSuccess = {
                    val orderedProductItemTokenId = it.datalist?.get(0)?.orderedProductItemTokenId
                    val printedList: MutableList<PrintedUpdateBody.PrintedTicketList> = mutableListOf()
                    val svgList: MutableList<List<String>> = mutableListOf()
                    if (it.datalist?.get(0)?.svgList != null) {
                        for (collection in it.datalist[0].svgList!!) {
                            collection.ticketTemplateId?.let { it1 -> printedList.add(
                                PrintedUpdateBody.PrintedTicketList(orderedProductItemTokenId, it1.toInt())) }
                            collection.newSvg?.let { it1 -> svgList.add(it1) }
                        }
                    }
                    singleActivity.printList = printedList
                    singleActivity.svgList = svgList
                    back.postValue(SingleLoadingArguments(orderNo = it.datalist?.get(0)?.orderId.toString()))
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(SingleQRCodeScanPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(singleActivity.getString(R.string.error_network))
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
        permissionStatus.execute(this@SingleQRStep1ViewModel) { status ->
            when (status) {
                SingleQRStep1PermissionStatus.UNKNOWN -> checkToAskPermission.postValue(Unit)
                SingleQRStep1PermissionStatus.ACCESS_GRANTED -> startCapture.postValue(Unit)
                SingleQRStep1PermissionStatus.NOT_GRANTED -> askCameraPermission.postValue(Unit)
                SingleQRStep1PermissionStatus.NEVER_ASK_AGAIN -> displayNeverAskAgainDialog.postValue(Unit)
                SingleQRStep1PermissionStatus.REFUSE -> navigateBack.postValue(Unit)
                else -> {
                }
            }
        }
    }

    enum class SingleQRStep1PermissionStatus {
        UNKNOWN, ACCESS_GRANTED, NOT_GRANTED, NEVER_ASK_AGAIN, REFUSE, CHECK_NEEDED_ONLY_ON_RESUME
    }
}