package jp.co.rakuten.ticket.checkinstation.api.response

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class QRTicketSkidataResponse(
    @Json(name = "printed_at") val printedAt: String?,
    val status: String?,
    @Json(name = "refreshed_at") val refreshedAt: String?,
    val additional: Additional?,
    val canceled_at: String?,
    @Json(name = "ordered_product_item_token_id") val orderedProductItemTokenId: String?,
    val product: Product?,
    val seat: Seat?,
){
    @JsonSerializable
    data class Product(
        val name: String?
    )
    @JsonSerializable
    data class Additional(
        val event: Event?,
        val performance: Performance?,
        val user: String?,
        val order: Order?,
    ){
        @JsonSerializable
        data class Event(
            val id: String?
        )
        @JsonSerializable
        data class Performance(
            val date: String?,
            val id: String?,
            val name: String?,
        )
        @JsonSerializable
        data class Order(
            val note: String?,
            val id: String?,
            val order_no: String?
        )
    }
    @JsonSerializable
    data class Seat(
        val id: String?,
        val name: String?,
    )
}