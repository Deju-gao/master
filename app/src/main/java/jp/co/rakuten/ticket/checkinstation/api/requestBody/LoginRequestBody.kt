package jp.co.rakuten.ticket.checkinstation.api.requestBody

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class LoginRequestBody(
        @Json(name = "userId") val userId: String,
        @Json(name = "userPassword") val userPassword: String,
        @Json(name = "companyCode") val companyCode: String,
        @Json(name = "deviceInfo") val deviceInfo: String,
)
