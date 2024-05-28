package jp.co.rakuten.ticket.checkinstation.util

import java.util.concurrent.atomic.AtomicBoolean

class BcpConnectionExecuteData {
    /**
     * issue mode
     */
    var issueMode = 0
    /**
     * port setting
     */
    var portSetting: String? = null
    /**
     * is open
     */
    var isOpen = AtomicBoolean(false)
}