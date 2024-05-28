package jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TicketItem(
    val id: String,
    val content: String,
    val date: String,
    val status: TicketStatus,
    var isSelected: Boolean
): Parcelable