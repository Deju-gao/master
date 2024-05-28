package jp.co.rakuten.ticket.checkinstation.api.requestBody

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class QRTicketBody(
        val qrsigned: String,
        val refreshMode: Boolean
)
