package jp.co.rakuten.ticket.checkinstation.ui.general.generalLoading

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class GeneralLoadingArguments(
    val orderNo: String
) : Parcelable