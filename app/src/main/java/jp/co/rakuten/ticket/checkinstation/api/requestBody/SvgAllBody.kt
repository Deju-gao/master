package jp.co.rakuten.ticket.checkinstation.api.requestBody

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SvgAllBody(
        @Json(name = "token_id_list") val tokenIdList: List<String>,
        val refreshMode: Boolean
)
