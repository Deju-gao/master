package jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectCount

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Keep
data class GeneralSelectCountArguments(
    val orderNo: String
) : Parcelable