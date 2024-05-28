package jp.co.rakuten.ticket.checkinstation.api.response

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class RefreshOrder2Response(
    @Json(name = "refreshed_at") val refreshedAt: String?,
    @Json(name = "order_no") val orderNo: String?
)