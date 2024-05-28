package jp.co.rakuten.ticket.checkinstation.api.response

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class OrderNoResponse(
    val status: String?,
    @Json(name = "printed_at") val printedAt: String?,
    val additional: Additional?,
    @Json(name = "canceled_at") val canceledAt: String?,
    val collection: List<Collection>?
){
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
    data class Collection(
        val product: Product?,
        @Json(name = "locked_at") val lockedAt: String?,
        @Json(name = "ordered_product_item_token_id") val orderedProductItemTokenId: String?,
        val seat: Seat?,
        @Json(name = "printed_at") val printedAt: String?,
        @Json(name = "refreshed_at") val refreshedAt: String?
    ){
        @JsonSerializable
        data class Product(
            val name: String?
        )
        @JsonSerializable
        data class Seat(
            val id: String?,
            val name: String?,
        )
    }
}