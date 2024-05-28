package jp.co.rakuten.ticket.checkinstation.api.requestBody

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class PrintedUpdateBody(
        @Json(name = "order_no") val orderNo: String,
        @Json(name = "printed_ticket_list") val printedTicketList: List<PrintedTicketList>
){
        @JsonSerializable
        data class PrintedTicketList(
                @Json(name = "token_id") val tokenId: Int?,
                @Json(name = "template_id") val templateId: Int?
        )
}
