package jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.rakuten.ticket.checkinstation.BaseViewModel
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import jp.co.rakuten.ticket.checkinstation.api.response.OrderNoResponse
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.ui.general.generalVerify.GeneralVerifyArguments
import jp.co.rakuten.ticket.checkinstation.util.execute
import javax.inject.Inject
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralSelectTargetNextButton
import jp.co.rakuten.ticket.checkinstation.util.GeneralSelectTargetPage
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@HiltViewModel
class GeneralSelectTargetViewModel @Inject constructor(private val apiRepository: ApiRepository) : BaseViewModel() {

    val isLoadingVisible = MutableLiveData<Boolean>()
    val showDialogString = SingleLiveEvent<String>()
    val toVerifyPage = SingleLiveEvent<GeneralVerifyArguments>()
    var reloadListData = SingleLiveEvent<MutableList<TicketItem>>()
    val tokenIdList: MutableList<String> = mutableListOf()
    val selectCountString = SingleLiveEvent<String>()
    val ticketInfo = SingleLiveEvent<OrderNoResponse.Additional>()
    //ticket list
    private val ticketList: MutableList<TicketItem> = mutableListOf()
    //ticket select list
    private var ticketSelectList: MutableList<TicketItem> = mutableListOf()
    //performance info
    private var performanceDate: String = ""
    private var performanceName: String = ""

    init {

    }

    //view resume
    fun onViewResume(orderNo: String, context: Context, refreshMode: Boolean) {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectTargetPage)
        qrTicketDataCollection(orderNo, context, refreshMode)
    }

    //next button did tap
    fun onNextButtonDidTap(context: Context, orderNo: String) {
        FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectTargetNextButton)
        if (ticketSelectList.isEmpty()) {
            showDialogString.postValue(context.getString(R.string.error_no_ticket))
            return
        }
        toVerifyPage.postValue(GeneralVerifyArguments(
            orderNo = orderNo,
            tokenIdList = tokenIdList,
            ticketList = ticketSelectList,
            performanceDate = performanceDate,
            performanceName = performanceName
        ))
    }

    //on item did select
    fun onItemDidSelect(item: TicketItem) {
        if (ticketSelectList.isEmpty()) {
            ticketSelectList.add(item)
        }
        else {
            for (i in ticketSelectList.count() - 1 downTo 0) {
                if (item.id == ticketSelectList[i].id && !item.isSelected) {
                    ticketSelectList.removeAt(i)
                    break
                }
            }
            if (item.isSelected) {
                ticketSelectList.add(item)
            }
        }
        selectCountString.postValue("${ticketSelectList.size}/${ticketList.size}")
    }

    private fun qrTicketDataCollection(orderNo: String, context: Context, refreshMode: Boolean) {
        ticketList.clear()
        tokenIdList.clear()
        ticketSelectList.clear()
        isLoadingVisible.postValue(true)
        apiRepository.qrTicketDataCollection(orderNo, refreshMode)
            .execute(this@GeneralSelectTargetViewModel,
                onSuccess = {
                    if (it.collection != null) {
                        for (collection in it.collection) {
                            var status: TicketStatus = TicketStatus.ENABLE
                            var date = ""
                            //status
                            if (!collection.printedAt.isNullOrEmpty()) {
                                status = TicketStatus.OVER
                                date = collection.printedAt
                            }
                            else if (!collection.lockedAt.isNullOrEmpty()) {
                                status = TicketStatus.PRINTING
                                date = collection.lockedAt
                            }
                            //all ticket
                            ticketList.add(TicketItem(
                                collection.orderedProductItemTokenId!!,
                                collection.product?.name + "(${collection.seat?.name})",
                                date,
                                status,
                                false
                            ))
                            //token id list
                            collection.orderedProductItemTokenId.let { it1 -> tokenIdList.add(it1) }
                        }
                        reloadListData.postValue(ticketList)
                        selectCountString.postValue("${ticketSelectList.size}/${ticketList.size}")
                        //performance info
                        performanceDate = it.additional?.performance?.date.toString()
                        performanceName = it.additional?.performance?.name.toString()
                        ticketInfo.postValue(it.additional)
                    }
                },
                onError = {
                    FirebaseLogUtil.getInstance().uploadApiException(GeneralSelectTargetPage.pageName, it)
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