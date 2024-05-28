package jp.co.rakuten.ticket.checkinstation.api.response

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class LoginResponse(
    @Json(name = "code") val code: Int?,
    @Json(name = "message") val message: String?,
    @Json(name = "result") val result: String?
)