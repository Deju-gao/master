package jp.co.rakuten.ticket.checkinstation.util

import android.content.Context

//save data to local util
class LoginUtil {

    //single method
    companion object {
        private var instance: LoginUtil? = null
        fun getInstance(): LoginUtil {
            if (instance == null) instance = LoginUtil()
            return instance!!
        }
    }

    //token
    private var tokenValue: String = ""
    //user id
    private var userName: String = ""
    //company code
    private var companyCode: String = ""

    /**
     * save token
     *
     * @param token login token
     */
    fun saveToken(token: String) {
        tokenValue = token
    }

    /**
     * save user id and company code
     *
     * @param userId user id
     * @param code company code
     */
    fun saveUserIdAndCompanyCode(userId: String, code: String) {
        userName = userId
        companyCode = code
    }

    /**
     * get token
     *
     * @return token value
     */
    fun getToken(): String {
        return tokenValue
    }

    /**
     * get user id
     *
     * @return user id
     */
    fun getUserId(): String {
        return userName
    }

    /**
     * get company code
     *
     * @return company code
     */
    fun getCompanyCode(): String {
        return companyCode
    }

    /**
     * clear
     */
    fun clear() {
        tokenValue = ""
        userName = ""
        companyCode = ""
    }

}