package jp.co.rakuten.ticket.checkinstation.ui.menu.readTest

import android.content.Context
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.subjects.BehaviorSubject
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.QRCodeReadTestPage
import jp.co.rakuten.ticket.checkinstation.util.execute
import javax.inject.Inject
import jp.co.rakuten.ticket.checkinstation.R

@HiltViewModel
class ReadTestViewModel @Inject constructor() : BaseViewModel() {

    private var permissionStatus: BehaviorSubject<ReadTestPermissionStatus> =
        BehaviorSubject.createDefault(ReadTestPermissionStatus.UNKNOWN)
    val back = SingleLiveEvent<String>()
    val checkToAskPermission = SingleLiveEvent<Unit>()
    val startCapture = SingleLiveEvent<Unit>()
    val stopCapture = SingleLiveEvent<Unit>()
    val askCameraPermission = SingleLiveEvent<Unit>()
    val displayNeverAskAgainDialog = SingleLiveEvent<Unit>()
    val navigateBack = SingleLiveEvent<Unit>()
    val openPermissionSettings = SingleLiveEvent<Unit>()
    val showDialogString = SingleLiveEvent<String>()

    //init method
    init {
        requestPermission()
    }

    //resume
    fun onResumeViewModel(){
        if (permissionStatus.value == ReadTestPermissionStatus.CHECK_NEEDED_ONLY_ON_RESUME) {
            permissionStatus.onNext(ReadTestPermissionStatus.UNKNOWN)
        }
        if (permissionStatus.value == ReadTestPermissionStatus.ACCESS_GRANTED) {
            startCapture?.postValue(Unit)
        }
        FirebaseLogUtil.getInstance().uploadPageLog(QRCodeReadTestPage)
    }

    //qrcode result
    fun handleQRCodeResult(result: BarcodeResult?, context: Context){
        if (result != null && result.text.isNotEmpty()) {
            back.postValue(result.text)
        }
        else {
            showDialogString.postValue(context.getString(R.string.error_scan_qrcode))
        }
    }

    //get current camera permission
    private fun requestPermission() {
        permissionStatus.execute(this@ReadTestViewModel) { status ->
            when (status) {
                ReadTestPermissionStatus.UNKNOWN -> checkToAskPermission.postValue(Unit)
                ReadTestPermissionStatus.ACCESS_GRANTED -> startCapture.postValue(Unit)
                ReadTestPermissionStatus.NOT_GRANTED -> askCameraPermission.postValue(Unit)
                ReadTestPermissionStatus.NEVER_ASK_AGAIN -> displayNeverAskAgainDialog.postValue(Unit)
                ReadTestPermissionStatus.REFUSE -> navigateBack.postValue(Unit)
                else -> {
                }
            }
        }
    }

    fun onPermissionResult(isAccessGranted: Boolean) {
        if (isAccessGranted) {
            permissionStatus.onNext(ReadTestPermissionStatus.ACCESS_GRANTED)
        } else {
            permissionStatus.onNext(ReadTestPermissionStatus.NOT_GRANTED)
        }
    }

    fun onOpenSettingsClick() {
        permissionStatus.onNext(ReadTestPermissionStatus.CHECK_NEEDED_ONLY_ON_RESUME)
        openPermissionSettings.postValue(Unit)
    }

    fun onNegativeButtonAlertRefuseClick() {
        navigateBack.postValue(Unit)
    }

    fun onRefusePermission() {
        permissionStatus.onNext(ReadTestPermissionStatus.REFUSE)
    }

    fun onNeverAskMeAgainResult() {
        permissionStatus.onNext(ReadTestPermissionStatus.NEVER_ASK_AGAIN)
    }

    enum class ReadTestPermissionStatus {
        UNKNOWN, ACCESS_GRANTED, NOT_GRANTED, NEVER_ASK_AGAIN, REFUSE, CHECK_NEEDED_ONLY_ON_RESUME
    }

}