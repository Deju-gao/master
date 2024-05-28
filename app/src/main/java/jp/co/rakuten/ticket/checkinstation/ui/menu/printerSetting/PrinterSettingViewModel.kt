package jp.co.rakuten.ticket.checkinstation.ui.menu.printerSetting

import android.content.Context
import androidx.lifecycle.ViewModel
import com.epson.epos2.Epos2Exception
import com.epson.epos2.discovery.Discovery
import com.epson.epos2.discovery.DiscoveryListener
import com.epson.epos2.discovery.FilterOption
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.PrinterSettingPage

class PrinterSettingViewModel : ViewModel() {

    //filter option
    private var filterOption: FilterOption? = null
    //discovery list
    private var discoveryList: ArrayList<HashMap<String, String>>? = null
    //discovery printer
    val discoveryPrinter = SingleLiveEvent<Unit>()
    //target value
    val targetValue = SingleLiveEvent<HashMap<String, String>>()
    //target index
    val targetIndex = SingleLiveEvent<Int>()

    //discovery listener
    private val discoveryListener = DiscoveryListener { deviceInfo ->
        val item = HashMap<String, String>()
        item["PrinterName"] = deviceInfo.deviceName
        item["Target"] = deviceInfo.target
        discoveryList?.add(item)
        discoveryPrinter.postValue(Unit)
    }

    //init method
    init {
        discoveryList = ArrayList<HashMap<String, String>>()
        filterOption = FilterOption()
        filterOption!!.deviceType = Discovery.TYPE_PRINTER
        filterOption!!.epsonFilter = Discovery.FILTER_NAME
        filterOption!!.usbDeviceName = Discovery.TRUE
    }

    //resume view
    fun resumeViewModel(context: Context) {
        try {
            Discovery.start(context, filterOption, discoveryListener)
        } catch (_: Exception) {
        }
        FirebaseLogUtil.getInstance().uploadPageLog(PrinterSettingPage)
    }

    //destory view
    fun onDestoryViewModel() {
        try {
            Discovery.stop()
        } catch (exception: Epos2Exception) {

        }
        filterOption = null
    }

    //get discovery list
    fun getDiscoveryList(): ArrayList<HashMap<String, String>>? {
        return discoveryList
    }

    //target did selected
    fun targetDidSelected(index: Int) {
        val targetHashMap = discoveryList?.get(index)
        var targetString = targetHashMap?.get("Target")
        if (targetString != null) {
            if (targetString.startsWith("USB:")) {
                targetString = "USB:"
                targetHashMap?.set("Target", targetString)
            }
        }
        targetValue.postValue(targetHashMap)
        targetIndex.postValue(index)
    }

}