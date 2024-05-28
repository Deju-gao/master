package jp.co.rakuten.ticket.checkinstation

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.api.requestBody.PrintedUpdateBody
import jp.co.rakuten.ticket.checkinstation.databinding.ActivityGeneralBinding

@AndroidEntryPoint
class GeneralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneralBinding;

    public var againMode: Boolean = false
    public var isUIBlocked: Boolean = false

    var printList: MutableList<PrintedUpdateBody.PrintedTicketList> = mutableListOf()
    var svgList: MutableList<List<String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_general)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (isUIBlocked)
            true
        else
            super.dispatchTouchEvent(ev)
    }

}