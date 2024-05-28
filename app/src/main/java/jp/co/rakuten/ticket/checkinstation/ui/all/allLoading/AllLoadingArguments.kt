package jp.co.rakuten.ticket.checkinstation.ui.all.allLoading

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllLoadingArguments(
    val orderNo: String
) : Parcelable