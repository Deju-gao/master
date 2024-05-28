package jp.co.rakuten.ticket.checkinstation.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

//save data to local util
class FirebaseLogUtil {

    //single method
    companion object {
        private var instance: FirebaseLogUtil? = null
        fun getInstance(): FirebaseLogUtil {
            if (instance == null) instance = FirebaseLogUtil()
            return instance!!
        }
    }

    /**
     * upload page log to firebase
     *
     * @param pageEnum current page name and button
     */
    fun uploadPageLog(pageEnum: VisitPage) {
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        val customerException = Exception("PAGE_NOTICE_LOG")
        firebaseCrashlytics.recordException(customerException)
        firebaseCrashlytics.setCustomKey("logType", "pageVisit")
        firebaseCrashlytics.setCustomKey("pageName", pageEnum.pageName)
        if (pageEnum.buttonName != null) {
            firebaseCrashlytics.setCustomKey("buttonClick", pageEnum.buttonName)
        }
        if (LoginUtil.getInstance().getUserId().isNotEmpty()) {
            firebaseCrashlytics.setCustomKey("userId", LoginUtil.getInstance().getUserId())
        }
    }

    /**
     * upload exception
     *
     * @param pageName current page name
     * @param throwable error
     */
    fun uploadApiException(pageName: String, throwable: Throwable) {
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        firebaseCrashlytics.recordException(throwable)
        firebaseCrashlytics.setCustomKey("logType", "apiError")
        firebaseCrashlytics.setCustomKey("pageName", pageName)
        if (LoginUtil.getInstance().getUserId().isNotEmpty()) {
            firebaseCrashlytics.setCustomKey("userId", LoginUtil.getInstance().getUserId())
        }
    }

    /**
     * upload custom log
     *
     * @param method error method
     * @param message error message
     */
    fun uploadCustomLog(method: String, message: String) {
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        val customerException = Exception("PRINT_CUSTOM_LOG")
        firebaseCrashlytics.recordException(customerException)
        firebaseCrashlytics.setCustomKey("logType", "customLog")
        firebaseCrashlytics.setCustomKey("method", method)
        firebaseCrashlytics.setCustomKey("message", message)
        if (LoginUtil.getInstance().getUserId().isNotEmpty()) {
            firebaseCrashlytics.setCustomKey("userId", LoginUtil.getInstance().getUserId())
        }
    }

}