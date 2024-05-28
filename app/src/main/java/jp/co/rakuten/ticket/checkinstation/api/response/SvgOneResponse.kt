package jp.co.rakuten.ticket.checkinstation.api.response

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SvgOneResponse(
    val datalist: List<Datalist>?
){
    @JsonSerializable
    data class Datalist(
        @Json(name = "printed_at") val printedAt: String?,
        @Json(name = "svg_list") val svgList: List<SvgList>?,
        @Json(name = "ordered_product_item_token_id") val orderedProductItemTokenId: Int?,
        @Json(name = "ticket_name") val ticketName: String?,
        @Json(name = "refreshed_at") val refreshedAt: String?,
        @Json(name = "order_id") val orderId: Int?,
        @Json(name = "ordered_product_item_id") val orderedProductItemId: Int?,
        @Json(name = "seat_id") val seatId: String?,
        val serial: Int?,
    ){
        @JsonSerializable
        data class SvgList(
            val svg: String?,
            val newSvg: List<String>?,
            @Json(name = "ticket_template_name")  val ticketTemplateName: String?,
            @Json(name = "ticket_template_id")  val ticketTemplateId: String?
        )
    }
}