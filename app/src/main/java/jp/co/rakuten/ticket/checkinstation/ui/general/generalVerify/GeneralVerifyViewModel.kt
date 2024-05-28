package jp.co.rakuten.ticket.checkinstation.ui.general.generalVerify

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.GeneralActivity
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.api.requestBody.PrintedUpdateBody
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralConfirmNextButton
import jp.co.rakuten.ticket.checkinstation.util.GeneralConfirmPage
import jp.co.rakuten.ticket.checkinstation.util.execute
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class GeneralVerifyViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {

    val isLoadingVisible = MutableLiveData<Boolean>()
    val showDialogString = SingleLiveEvent<String>()
    val toLoadingPage = SingleLiveEvent<Unit>()

    init {

    }

    //view resume
    fun onViewResume(tokenIdList: List<String>, generalActivity: GeneralActivity, refreshMode: Boolean) {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralConfirmPage)
        qrSvgSourceAll(tokenIdList, generalActivity, refreshMode)
    }

    //next button did tap
    fun onNextButtonDidTap(generalActivity: GeneralActivity) {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralConfirmNextButton)
        val target = generalActivity.intent.getStringExtra("printer_target")
        if (target != null) {
            if (target.isEmpty() || target == generalActivity.getString(R.string.select_device)) {
                showDialogString.postValue(generalActivity.getString(R.string.error_not_exit_print))
            }
            else {
                toLoadingPage.postValue(Unit)
            }
        }
        else {
            showDialogString.postValue(generalActivity.getString(R.string.error_not_exit_print))
        }
    }

    //qr svg source all
    private fun qrSvgSourceAll(tokenIdList: List<String>, generalActivity: GeneralActivity, refreshMode: Boolean) {
        isLoadingVisible.postValue(true)
        apiRepository.svgAll(tokenIdList, refreshMode)
            .execute(this@GeneralVerifyViewModel,
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
                        generalActivity.printList = printedList
                        generalActivity.svgList = svgList
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(GeneralConfirmPage.pageName, it)
                    when(it) {
                        is IOException, is SocketTimeoutException, is UnknownHostException -> {
                            showDialogString.postValue(generalActivity.getString(R.string.error_network))
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

}