package jp.co.rakuten.ticket.checkinstation.ui.single.singleLoading

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SingleLoadingArguments(
    val orderNo: String
) : Parcelable