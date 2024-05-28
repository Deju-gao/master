package jp.co.rakuten.ticket.checkinstation.ui.all.allInputTelephone

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
@Keep
data class AllInputTelephoneArguments(
    val number: String
) : Parcelable