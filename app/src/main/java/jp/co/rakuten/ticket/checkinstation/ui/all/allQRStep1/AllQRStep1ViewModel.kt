package jp.co.rakuten.ticket.checkinstation.ui.all.allQRStep1

import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.subjects.BehaviorSubject
import jp.co.rakuten.ticket.checkinstation.AllActivity
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.api.requestBody.PrintedUpdateBody
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.ui.all.allLoading.AllLoadingArguments
import jp.co.rakuten.ticket.checkinstation.util.AllQRCodeScanPage
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.execute
import javax.inject.Inject
import jp.co.rakuten.ticket.checkinstation.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@HiltViewModel
class AllQRStep1ViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {

    private var permissionStatus: BehaviorSubject<AllQRStep1PermissionStatus> =
        BehaviorSubject.createDefault(AllQRStep1PermissionStatus.UNKNOWN)
    val back = SingleLiveEvent<AllLoadingArguments>()
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
        FirebaseLogUtil.getInstance().uploadPageLog(AllQRCodeScanPage)
        if (permissionStatus.value == AllQRStep1PermissionStatus.CHECK_NEEDED_ONLY_ON_RESUME) {
            permissionStatus.onNext(AllQRStep1PermissionStatus.UNKNOWN)
        }
        if (permissionStatus.value == AllQRStep1PermissionStatus.ACCESS_GRANTED) {
            startCapture?.postValue(Unit)
        }
    }

    //qrcode result
    fun handleQRCodeResult(result: BarcodeResult?, againMode: Boolean, allActivity: AllActivity){
        if (result != null && result.text.isNotEmpty()) {
            val target = allActivity.intent.getStringExtra("printer_target")
            if (target != null) {
                if (target.isEmpty() || target == allActivity.getString(R.string.select_device)) {
                    showDialogString.postValue(allActivity.getString(R.string.error_not_exit_print))
                }
                else {
                    if (againMode) {
                        refreshOrderQR(result.text, allActivity)
                    }
                    else {
                        if (result.text.length == 20) {
                            qrcodeLengthIsEqualTwenty(result.text, allActivity)
                        }
                        else {
                            qrcodeLengthIsNotEqualTwenty(result.text, allActivity)
                        }
                    }
                }
            }
            else {
                showDialogString.postValue(allActivity.getString(R.string.error_not_exit_print))
            }
        }
        else {
            showDialogString.postValue(allActivity.getString(R.string.error_scan_qrcode))
        }
    }

    fun onPermissionResult(isAccessGranted: Boolean) {
        if (isAccessGranted) {
            permissionStatus.onNext(AllQRStep1PermissionStatus.ACCESS_GRANTED)
        } else {
            permissionStatus.onNext(AllQRStep1PermissionStatus.NOT_GRANTED)
        }
    }

    fun onOpenSettingsClick() {
        permissionStatus.onNext(AllQRStep1PermissionStatus.CHECK_NEEDED_ONLY_ON_RESUME)
        openPermissionSettings.postValue(Unit)
    }

    fun onNegativeButtonAlertRefuseClick() {
        navigateBack.postValue(Unit)
    }

    fun onRefusePermission() {
        permissionStatus.onNext(AllQRStep1PermissionStatus.REFUSE)
    }

    fun onNeverAskMeAgainResult() {
        permissionStatus.onNext(AllQRStep1PermissionStatus.NEVER_ASK_AGAIN)
    }

    //refresh order qr
    private fun refreshOrderQR(qrcode: String, allActivity: AllActivity) {
        isLoadingVisible.postValue(true)
        apiRepository.refreshOrderQR(qrcode)
            .execute(this@AllQRStep1ViewModel,
                onSuccess = {
                    if (qrcode.length == 20) {
                        qrcodeLengthIsEqualTwenty(qrcode, allActivity)
                    }
                    else {
                        qrcodeLengthIsNotEqualTwenty(qrcode, allActivity)
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(AllQRCodeScanPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(allActivity.getString(R.string.error_network))
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
    private fun qrcodeLengthIsNotEqualTwenty(qrcode: String, allActivity: AllActivity) {
        isLoadingVisible.postValue(true)
        apiRepository.qrTicket(qrcode)
            .execute(this@AllQRStep1ViewModel,
                onSuccess = {
                    it.additional?.order?.order_no?.let { it1 -> qrTicketDataCollection(it1, allActivity) }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(AllQRCodeScanPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(allActivity.getString(R.string.error_network))
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
    private fun qrcodeLengthIsEqualTwenty(qrcode: String, allActivity: AllActivity) {
        isLoadingVisible.postValue(true)
        apiRepository.qrTicketSkidata(qrcode)
            .execute(this@AllQRStep1ViewModel,
                onSuccess = {
                    it.additional?.order?.order_no?.let { it1 -> qrTicketDataCollection(it1, allActivity) }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(AllQRCodeScanPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(allActivity.getString(R.string.error_network))
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

    //qr ticket data collection
    private fun qrTicketDataCollection(orderNo: String, allActivity: AllActivity) {
        apiRepository.qrTicketDataCollection(orderNo, allActivity.againMode)
            .execute(this@AllQRStep1ViewModel,
                onSuccess = {
                    if (it.collection != null) {
                        val tokenIdList: MutableList<String> = mutableListOf()
                        for (collection in it.collection) {
                            collection.orderedProductItemTokenId?.let { it1 -> tokenIdList.add(it1) }
                        }
                        qrSvgSourceAll(tokenIdList, orderNo, allActivity)
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(AllQRCodeScanPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(allActivity.getString(R.string.error_network))
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

    //qr svg source all
    private fun qrSvgSourceAll(tokenIdList: List<String>, orderNo: String, allActivity: AllActivity) {
        apiRepository.svgAll(tokenIdList, allActivity.againMode)
            .execute(this@AllQRStep1ViewModel,
                onSuccess = {
                    if (!it.datalist.isNullOrEmpty()) {
                        val printedList: MutableList<PrintedUpdateBody.PrintedTicketList> = mutableListOf()
                        val svgList: MutableList<List<String>> = mutableListOf()
                        for (dataList in it.datalist) {
                            if (dataList.svgList != null) {
                                for (collection in dataList.svgList) {
                                    printedList.add(PrintedUpdateBody.PrintedTicketList(dataList.orderedProductItemTokenId, collection.ticketTemplateId?.toInt()))
                                    collection.newSvg?.let { it1 -> svgList.add(it1) }
                                }
                            }
                        }
                        allActivity.printList = printedList
                        allActivity.svgList = svgList
                        back.postValue(AllLoadingArguments(orderNo = orderNo))
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(AllQRCodeScanPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(allActivity.getString(R.string.error_network))
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
        permissionStatus.execute(this@AllQRStep1ViewModel) { status ->
            when (status) {
                AllQRStep1PermissionStatus.UNKNOWN -> checkToAskPermission.postValue(Unit)
                AllQRStep1PermissionStatus.ACCESS_GRANTED -> startCapture.postValue(Unit)
                AllQRStep1PermissionStatus.NOT_GRANTED -> askCameraPermission.postValue(Unit)
                AllQRStep1PermissionStatus.NEVER_ASK_AGAIN -> displayNeverAskAgainDialog.postValue(Unit)
                AllQRStep1PermissionStatus.REFUSE -> navigateBack.postValue(Unit)
                else -> {
                }
            }
        }
    }

    enum class AllQRStep1PermissionStatus {
        UNKNOWN, ACCESS_GRANTED, NOT_GRANTED, NEVER_ASK_AGAIN, REFUSE, CHECK_NEEDED_ONLY_ON_RESUME
    }

}