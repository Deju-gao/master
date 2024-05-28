package jp.co.rakuten.ticket.checkinstation.ui.general.generalInputTelephone

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Keep
data class GeneralInputTelephoneArguments(
    val number: String
) : Parcelable