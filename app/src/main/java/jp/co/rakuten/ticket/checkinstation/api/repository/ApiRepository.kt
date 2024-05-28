package jp.co.rakuten.ticket.checkinstation.api.repository

import io.reactivex.Single
import jp.co.rakuten.ticket.checkinstation.api.ApiService
import jp.co.rakuten.ticket.checkinstation.api.requestBody.*
import jp.co.rakuten.ticket.checkinstation.api.response.*
import jp.co.rakuten.ticket.checkinstation.util.LoginUtil

class ApiRepository(private val apiService: ApiService) {

    /**
     * login
     *
     * @param userId user name
     * @param userPassword password
     * @param companyCode company code
     * @param deviceInfo device uuid
     *
     * @return login response
     */
    fun login(userId: String, userPassword: String, companyCode: String, deviceInfo: String): Single<LoginResponse> {
        val requestBody = LoginRequestBody(
            userId = userId,
            userPassword = userPassword,
            companyCode = companyCode,
            deviceInfo = deviceInfo
        )
        return apiService.login("http://checkin-station.stg.altr.jp/loginCheck", requestBody).map {
            it.result?.let { it1 -> LoginUtil.getInstance().saveToken(it1) }
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

    /**
     * qrcode length is not equal twenty
     *
     * @param qrcode scan qrcode result
     *
     * @return qrticket response
     */
    fun qrTicket(qrcode: String): Single<QRTicketResponse> {
        val body = QRTicketBody(qrcode,false)
        return apiService.qrTicket(body).map {
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

    /**
     * qrcode length is equal twenty
     *
     * @param qrcode scan qrcode result
     *
     * @return qrticket skidata response
     */
    fun qrTicketSkidata(qrcode: String): Single<QRTicketSkidataResponse> {
        val body = QRTicketSkidataBody(qrcode,false)
        return apiService.qrTicketSkidata(body).map {
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

    /**
     * verify order number
     *
     * @param number order number
     * @param tel telephone
     *
     * @return verify order number response
     */
    fun orderNoVerifiedData(number: String, tel: String): Single<OrderNoVerifiedDataResponse> {
        val body = OrderNoVerifiedDataBody(number, tel)
        return apiService.orderNoVerifiedData(body).map {
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

    /**
     * qr ticket data collection
     *
     * @param orderNo order number
     *
     * @return order number response
     */
    fun qrTicketDataCollection(orderNo: String, refreshMode: Boolean): Single<OrderNoResponse> {
        val body = OrderNoBody(orderNo,refreshMode)
        return apiService.orderNo(body).map {
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

    /**
     * get one ticket
     *
     * @param tokenId ordered product item token id
     *
     * @return svg one response
     */
    fun svgOne(tokenId: String, refreshMode: Boolean): Single<SvgOneResponse> {
        val body = SvgOneBody(tokenId, refreshMode)
        return apiService.svgOne(body).map {
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

    /**
     * get all tickets
     *
     * @param tokenIdList the list of ordered product item token id
     *
     * @return svg all response
     */
    fun svgAll(tokenIdList: List<String>, refreshMode: Boolean): Single<SvgAllResponse> {
        val body = SvgAllBody(tokenIdList, refreshMode)
        return apiService.svgAll(body).map {
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

    /**
     * qr update printed at
     *
     * @param orderNo order number
     * @param ticketList ticket list
     *
     * @return printed update response
     */
    fun printedUpdate(orderNo: String, ticketList: List<PrintedUpdateBody.PrintedTicketList>): Single<PrintedUpdateResponse> {
        val body = PrintedUpdateBody(
            orderNo,
            ticketList
        )
        return apiService.printedUpdate(body).map {
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

    /**
     * refresh order qr
     *
     * @param qrcode scan qrcode result
     *
     * @return refresh order qr response
     */
    fun refreshOrderQR(qrcode: String): Single<RefreshOrderQRResponse> {
        val body = RefreshOrderQRBody(qrcode)
        return apiService.refreshOrderQR(body).map {
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

    /**
     * refresh order2
     *
     * @param orderNo order number
     * @param tel telephone
     *
     * @return refresh order2 response
     */
    fun refreshOrder2(orderNo: String, tel: String): Single<RefreshOrder2Response> {
        val body = RefreshOrder2Body(orderNo, tel)
        return apiService.refreshOrder2(body).map {
            it
        }.onErrorResumeNext {
            Single.error(it)
        }
    }

}