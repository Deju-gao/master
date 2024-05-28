package jp.co.rakuten.ticket.checkinstation.ui.menu.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.ActivityMainBinding
import jp.co.rakuten.ticket.checkinstation.util.LoginUtil
import jp.co.rakuten.ticket.checkinstation.util.SaveDataUtil

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;

    //ticket mode
    private lateinit var ticketMode: String
    //qrcode image method
    private lateinit var qrcodeImageMethod: String
    //qrcode image path
    private lateinit var qrcodeImagePath: String
    //printer target
    private lateinit var printerTarget: HashMap<String, String>
    //blue tooth address
    private lateinit var bluetoothAddress: String
    //is back from print setting
    private var isBackFromPrintSetting: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginUtil.getInstance().clear()
        ticketMode =
            SaveDataUtil.getInstance().getData(this, "TicketMode").ifEmpty { getString(R.string.general_mode) }
        qrcodeImageMethod = SaveDataUtil.getInstance().getData(this, "QRCodeImageMethod").ifEmpty { getString(R.string.default_image) }
        qrcodeImagePath = SaveDataUtil.getInstance().getData(this, "QRCodeImagePath").ifEmpty { "default" }
        val hashMap = HashMap<String, String>()
        hashMap["PrinterName"] = getString(R.string.select_device)
        hashMap["Target"] = ""
        printerTarget = hashMap
        bluetoothAddress = getString(R.string.select_device)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    //set ticket mode
    fun setTicketMode(mode: String) {
        ticketMode = mode
    }

    //get ticket mode
    fun getTicketMode(): String {
        return ticketMode
    }

    //set qrcode image method
    fun setQRCodeImageMethod(qrcodeMethod: String) {
        qrcodeImageMethod = qrcodeMethod
    }

    //get ticket mode method
    fun getQRCodeImageMethod(): String {
        return qrcodeImageMethod
    }

    //set qrcode image path
    fun setQRCodeImagePath(imagePath: String) {
        qrcodeImagePath = imagePath
    }

    //get ticket mode path
    fun getQRCodeImagePath(): String {
        return qrcodeImagePath
    }

    //set printer target
    fun setPrinterTarget(hashMap: HashMap<String, String>) {
        printerTarget = hashMap
    }

    //get printer name
    fun getPrinterName(): String? {
        return printerTarget["PrinterName"]
    }

    //get printer target
    fun getPrinterTarget(): String? {
        return printerTarget["Target"]
    }

    //set bluetooth address
    fun setBluetoothAddress(address: String) {
        bluetoothAddress = address
    }

    //get bluetooth address
    fun getBluetoothAddress(): String {
        return bluetoothAddress
    }

    //set is back from print setting
    fun setIsBackFromPrintSetting(backFromPrintSetting: Boolean) {
        isBackFromPrintSetting = backFromPrintSetting
    }

    //get is back from print setting
    fun getIsBackFromPrintSetting(): Boolean {
        return isBackFromPrintSetting
    }

}