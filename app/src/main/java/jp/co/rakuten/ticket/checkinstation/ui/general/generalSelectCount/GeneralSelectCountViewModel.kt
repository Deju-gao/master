package jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectCount

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget.TicketItem
import jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget.TicketStatus
import jp.co.rakuten.ticket.checkinstation.ui.general.generalVerify.GeneralVerifyArguments
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralSelectCountModePage
import jp.co.rakuten.ticket.checkinstation.util.execute
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class GeneralSelectCountViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {

    val isLoadingVisible = MutableLiveData<Boolean>()
    val showDialogString = SingleLiveEvent<String>()
    val toVerifyPage = SingleLiveEvent<GeneralVerifyArguments>()
    //ticket list
    private val ticketList: MutableList<TicketItem> = mutableListOf()

    init {

    }

    //view resume
    fun onViewResume() {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectCountModePage)
    }

    fun onSelectOneCardDidTap(orderNo: String, context: Context, refreshMode: Boolean) {
        ticketList.clear()
        isLoadingVisible.postValue(true)
        apiRepository.qrTicketDataCollection(orderNo, refreshMode)
            .execute(this@GeneralSelectCountViewModel,
                onSuccess = {
                    if (it.collection != null) {
                        val tokenIdList: MutableList<String> = mutableListOf()
                        for (collection in it.collection) {
                            var status: TicketStatus = TicketStatus.ENABLE
                            //status
                            if (collection.printedAt.isNullOrEmpty() && collection.lockedAt.isNullOrEmpty()) {
                                //all ticket
                                ticketList.add(
                                    TicketItem(
                                        collection.orderedProductItemTokenId!!,
                                        collection.product?.name + "(${collection.seat?.name})",
                                        if (collection.printedAt.isNullOrEmpty()) "" else collection.printedAt,
                                        status,
                                        false
                                    )
                                )
                                tokenIdList.add(collection.orderedProductItemTokenId)
                            }
                        }
                        if (ticketList.isNotEmpty()) {
                            toVerifyPage.postValue(GeneralVerifyArguments(
                                orderNo = orderNo,
                                tokenIdList = tokenIdList,
                                ticketList = ticketList,
                                performanceDate = it.additional?.performance?.date!!,
                                performanceName = it.additional.performance.name!!
                            ))
                        }
                        else {
                            showDialogString.postValue(context.getString(R.string.error_ticket_empty))
                        }
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(GeneralSelectCountModePage.pageName, it)
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

}