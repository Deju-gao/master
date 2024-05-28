package jp.co.rakuten.ticket.checkinstation.util

class BcpPrintData {
    /**
     * the objectDataList to set
     */
    var objectDataList: HashMap<String, String>? = null
    /**
     * the lfmFileFullPath to set
     */
    var lfmFileFullPath = ""
    /**
     * the printCount to set
     */
    var printCount = 1
        set(printCount) {
            var count = printCount
            if (count < 1) {
                count = 1
            }
            field = count
        }
    /**
     * the currentIssueMode to set
     */
    var currentIssueMode = 1
    /**
     * the statusMessage to set
     */
    var statusMessage = ""
    /**
     * the result to set
     */
    var result: Long = 0
    /**
     * issue mode
     */
    var issueMode: Int = 0

    //init
    init {
        objectDataList = HashMap()
    }

    protected fun finalize() {
        objectDataList = null
    }
}