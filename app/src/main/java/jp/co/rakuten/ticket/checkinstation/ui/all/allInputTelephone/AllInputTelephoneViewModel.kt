package jp.co.rakuten.ticket.checkinstation.ui.all.allInputTelephone

import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.AllActivity
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.api.requestBody.PrintedUpdateBody
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.ui.all.allLoading.AllLoadingArguments
import jp.co.rakuten.ticket.checkinstation.util.AllInputTelephoneNextButton
import jp.co.rakuten.ticket.checkinstation.util.AllInputTelephonePage
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.execute
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class AllInputTelephoneViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {

    val isLoadingVisible = MutableLiveData<Boolean>()
    val showDialogString = SingleLiveEvent<String>()
    val telePhone = MutableLiveData<String>()
    val next = SingleLiveEvent<AllLoadingArguments>()
    val showDialogEnum = SingleLiveEvent<ErrorMessageEnum>()

    init {

    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(AllInputTelephonePage)
    }

    //next button did tap
    fun onNextButtonDidTap(orderNo: String, againMode: Boolean, allActivity: AllActivity) {
        FirebaseLogUtil.getInstance().uploadPageLog(AllInputTelephoneNextButton)
        val target = allActivity.intent.getStringExtra("printer_target")
        if (target != null) {
            if (target.isEmpty() || target == allActivity.getString(R.string.select_device)) {
                showDialogString.postValue(allActivity.getString(R.string.error_not_exit_print))
            }
            else {
                if (againMode) {
                    refreshOrder(orderNo, allActivity)
                }
                else {
                    verifyData(orderNo, allActivity)
                }
            }
        }
        else {
            showDialogString.postValue(allActivity.getString(R.string.error_not_exit_print))
        }
    }

    private fun refreshOrder(number: String, allActivity: AllActivity) {
        if (telePhone.value.isNullOrEmpty()){
            showDialogEnum.postValue(ErrorMessageEnum.TEL_ERROR)
            return
        }
        isLoadingVisible.postValue(true)
        apiRepository.refreshOrder2(number, telePhone.value!!)
            .execute(this@AllInputTelephoneViewModel,
                onSuccess = {
                    verifyData(number, allActivity)
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(AllInputTelephonePage.pageName, it)
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

    private fun verifyData(orderNo: String, allActivity: AllActivity) {
        if (telePhone.value.isNullOrEmpty()){
            showDialogEnum.postValue(ErrorMessageEnum.TEL_ERROR)
            return
        }
        isLoadingVisible.postValue(true)
        apiRepository.orderNoVerifiedData(orderNo, telePhone.value!!)
            .execute(this@AllInputTelephoneViewModel,
                onSuccess = {
                    qrTicketDataCollection(it.orderNo, allActivity)
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(AllInputTelephonePage.pageName, it)
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
            .execute(this@AllInputTelephoneViewModel,
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
                    FirebaseLogUtil.getInstance().uploadApiException(AllInputTelephonePage.pageName, it)
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
            .execute(this@AllInputTelephoneViewModel,
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
                        next.postValue(AllLoadingArguments(orderNo = orderNo))
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(AllInputTelephonePage.pageName, it)
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

    enum class ErrorMessageEnum {
        TEL_ERROR
    }

}