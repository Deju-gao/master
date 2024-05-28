package jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Keep
data class GeneralSelectTargetArguments(
    val orderNo: String
) : Parcelable