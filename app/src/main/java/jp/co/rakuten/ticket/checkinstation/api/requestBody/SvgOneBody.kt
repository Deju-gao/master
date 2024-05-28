package jp.co.rakuten.ticket.checkinstation.api.requestBody

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SvgOneBody(
        @Json(name = "ordered_product_item_token_id") val orderedProductItemTokenId: String,
        val refreshMode: Boolean
)
