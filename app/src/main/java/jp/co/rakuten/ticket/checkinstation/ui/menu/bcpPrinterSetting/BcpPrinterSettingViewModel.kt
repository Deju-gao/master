package jp.co.rakuten.ticket.checkinstation.ui.menu.bcpPrinterSetting

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.lifecycle.ViewModel
import jp.co.rakuten.ticket.checkinstation.event.SingleLiveEvent

class BcpPrinterSettingViewModel : ViewModel() {

    //discovery list
    private var discoveryList: ArrayList<HashMap<String, String>>? = null
    //discovery printer
    val discoveryPrinter = SingleLiveEvent<Unit>()
    //target value
    val targetValue = SingleLiveEvent<String>()
    //target index
    val targetIndex = SingleLiveEvent<Int>()

    //init method
    init {
        discoveryList = ArrayList<HashMap<String, String>>()
    }

    //resume view
    fun resumeViewModel() {
        val bluetoothDevice = BluetoothAdapter.getDefaultAdapter().bondedDevices
        for (device in bluetoothDevice) {
            val hashMap = HashMap<String, String>()
            hashMap["PrinterName"] = device.name
            hashMap["Target"] = device.address
            discoveryList?.add(hashMap)
        }
    }

    //get discovery list
    fun getDiscoveryList(): ArrayList<HashMap<String, String>>? {
        return discoveryList
    }

    //target did selected
    fun targetDidSelected(index: Int) {
        val targetHashMap = discoveryList?.get(index)
        targetValue.postValue(targetHashMap?.get("PrinterName") + " (" + targetHashMap?.get("Target") + ")")
        targetIndex.postValue(index)
    }

}