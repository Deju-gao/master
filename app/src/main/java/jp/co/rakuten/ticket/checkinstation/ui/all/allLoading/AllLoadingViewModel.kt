package jp.co.rakuten.ticket.checkinstation.ui.all.allLoading

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.AllActivity
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.api.requestBody.PrintedUpdateBody
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class AllLoadingViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {

    val navigateToOver = SingleLiveEvent<Unit>()
    val showDialogString = SingleLiveEvent<String>()

    init {
    }

    fun onViewResume(orderNo: String, ticketList: List<PrintedUpdateBody.PrintedTicketList>, allActivity: AllActivity) {
        FirebaseLogUtil.getInstance().uploadPageLog(AllLoadingPage)
        val svgList = allActivity.svgList
        BcpUtil.getInstance().setPrintCount(svgList.size)
        for (newSvgList in svgList) {
            var svgString = ""
            if (newSvgList.size == 1) {
                svgString = newSvgList[0]
            }
            else if (newSvgList.size > 1) {
                for (str in newSvgList) {
                    svgString += str
                }
            }
            val bitmap = BcpUtil.getInstance().svgStringToBitmap(svgString)
            SaveDataUtil.getInstance().saveBitmapToLocal(allActivity, bitmap)
            //print bitmap
            val target = allActivity.intent.getStringExtra("printer_target")
            if (target?.let { BcpUtil.getInstance().checkBluetoothConnect(allActivity, it) } == true) {
                val bcpPrintTicket = BcpPrintTicket.getInstance()
                bcpPrintTicket.setListener(object : BcpPrintTicket.BcpPrintCompleteListener {
                    override fun bcpPrintComplete(isSuccess: Boolean) {
                        if (isSuccess) {
                            updateTicket(orderNo, ticketList, allActivity)
                        }
                    }
                })
                bcpPrintTicket.printMethod(1, target, allActivity)
            }
        }
    }

    private fun updateTicket(orderNo: String, ticketList: List<PrintedUpdateBody.PrintedTicketList>, allActivity: AllActivity) {
        apiRepository.printedUpdate(orderNo, ticketList)
            .execute(this@AllLoadingViewModel,
                onSuccess = {
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(AllLoadingPage.pageName, it)
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
                    GlobalScope.launch(context = Dispatchers.IO) {
                        delay(2000)
                        navigateToOver.postValue(Unit)
                    }
                }
            )
    }

}