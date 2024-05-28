package jp.co.rakuten.ticket.checkinstation.ui.general.generalVerify

import android.os.Parcelable
import androidx.annotation.Keep
import jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget.TicketItem
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Keep
data class GeneralVerifyArguments(
    val orderNo: String,
    val tokenIdList: List<String>,
    val ticketList: List<TicketItem>,
    val performanceDate: String,
    val performanceName: String
) : Parcelable