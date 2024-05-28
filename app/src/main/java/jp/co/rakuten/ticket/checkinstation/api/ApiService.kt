package jp.co.rakuten.ticket.checkinstation.api

import io.reactivex.Single
import jp.co.rakuten.ticket.checkinstation.api.requestBody.*
import jp.co.rakuten.ticket.checkinstation.api.response.*
import retrofit2.http.*

interface ApiService {

    @POST()
    fun login(@Url url: String, @Body body: LoginRequestBody): Single<LoginResponse>

    @POST("checkinstation/v2/qr/ticketdata")
    fun qrTicket(@Body body: QRTicketBody): Single<QRTicketResponse>

    @POST("checkinstation/v2/qr/ticketdata/skidata")
    fun qrTicketSkidata(@Body body: QRTicketSkidataBody): Single<QRTicketSkidataResponse>

    @POST("checkinstation/v2/orderno/verified_data")
    fun orderNoVerifiedData(@Body body: OrderNoVerifiedDataBody): Single<OrderNoVerifiedDataResponse>

    @POST("checkinstation/v2/qr/ticketdata/collection")
    fun orderNo(@Body body: OrderNoBody): Single<OrderNoResponse>

    @POST("/dotNetApi/api/qr/SvgSourceOne")
    fun svgOne(@Body body: SvgOneBody): Single<SvgOneResponse>

    @POST("/dotNetApi/api/qr/SvgSourceAll")
    fun svgAll(@Body body: SvgAllBody): Single<SvgAllResponse>

    @POST("checkinstation/v2/qr/printed/update")
    fun printedUpdate(@Body body: PrintedUpdateBody): Single<PrintedUpdateResponse>

    @POST("checkinstation/v2/refresh/order/qr")
    fun refreshOrderQR(@Body body: RefreshOrderQRBody): Single<RefreshOrderQRResponse>

    @POST("checkinstation/v2/refresh/order2")
    fun refreshOrder2(@Body body: RefreshOrder2Body): Single<RefreshOrder2Response>

}