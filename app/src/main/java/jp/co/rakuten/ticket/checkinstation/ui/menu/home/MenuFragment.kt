package jp.co.rakuten.ticket.checkinstation.ui.menu.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentMenuBinding
import jp.co.rakuten.ticket.checkinstation.ui.login.LoginActivity
import jp.co.rakuten.ticket.checkinstation.util.*

class MenuFragment : Fragment() {

    companion object {
        fun newInstance() = MenuFragment()
    }

    private val requestPermission = 100

    private lateinit var viewModel: MenuViewModel

    private lateinit var binding: FragmentMenuBinding

    private lateinit var menuActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuActivity = context as MainActivity
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BcpUtil.getInstance().saveFilesToLocal(requireContext())

        setupUI()
        requestRuntimePermission()
        viewModel.onViewCreate(menuActivity)
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        binding.ticketMode.text = menuActivity.getTicketMode()
        binding.qrcodeImage.text = menuActivity.getQRCodeImageMethod()
        binding.printerSetting.text = menuActivity.getBluetoothAddress()
        viewModel.onViewResume(menuActivity)
    }

    //setup ui
    private fun setupUI() {

        //to printer setting fragment
        binding.printerSettingBackView.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(HomePagePrinterSettingButton)
            findNavController().navigate(R.id.action_menuFragment_to_bcpPrinterSettingFragment)
        }

        //to read test fragment
        binding.testReadBackView.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(HomePageQRCodeReadTestButton)
            findNavController().navigate(R.id.action_menuFragment_to_readTestFragment)
        }

        //to ticket mode fragment
        binding.ticketModeBackView.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(HomePageTicketModeButton)
            findNavController().navigate(R.id.action_menuFragment_to_ticketModeFragment)
        }

        //to qrcode image fragment
        binding.qrCodeImageBackView.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(HomePageQRCodeImageSettingButton)
            findNavController().navigate(R.id.action_menuFragment_to_qrcodeImageFragment)
        }

        //to login fragment
        binding.loginBackView.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(HomePageLoginButton)
            viewModel.toLoginButtonDidTap(menuActivity)
        }

        //print test
        binding.printTestBackView.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(HomePagePrintTestButton)
//            menuActivity.getPrinterTarget()
//                ?.let { it1 -> viewModel.printTest(menuActivity, it1) }
            val bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.qr_read)
            SaveDataUtil.getInstance().saveBitmapToLocal(requireContext(), bitmap)
            viewModel.printTest(menuActivity, menuActivity.getBluetoothAddress())
        }

        //to login
        viewModel.toLogin.observe(viewLifecycleOwner, Observer {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("printer_target", menuActivity.getBluetoothAddress())
            intent.putExtra("ticket_mode", menuActivity.getTicketMode())
            startActivity(intent)
        })

        //error alert
        viewModel.showDialogString.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(it)
                .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        })

    }

    //request permission
    private fun requestRuntimePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val requestPermissions: MutableList<String> = ArrayList()
        val permissionStorage =
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionStorage == PackageManager.PERMISSION_DENIED) {
            requestPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (Build.VERSION_CODES.S <= Build.VERSION.SDK_INT) {
            // If your app targets Android 12 (API level 31) and higher, it's recommended that you declare BLUETOOTH permission.
            val permissionBluetoothScan =
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.BLUETOOTH_SCAN)
            val permissionBluetoothConnect =
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.BLUETOOTH_CONNECT)
            if (permissionBluetoothScan == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            if (permissionBluetoothConnect == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
        if (requestPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                requestPermissions.toTypedArray(),
                requestPermission
            )
        }
    }

    //permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != requestPermission || grantResults.isEmpty()) {
            return
        }
        val requestPermissions: MutableList<String> = java.util.ArrayList()
        for (i in permissions.indices) {
            if (permissions[i] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(permissions[i])
            }
            if (Build.VERSION_CODES.S <= Build.VERSION.SDK_INT) {
                // If your app targets Android 12 (API level 31) and higher, it's recommended that you declare BLUETOOTH permission.
                if (permissions[i] == Manifest.permission.BLUETOOTH_SCAN && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    requestPermissions.add(permissions[i])
                }
                if (permissions[i] == Manifest.permission.BLUETOOTH_CONNECT && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    requestPermissions.add(permissions[i])
                }
            }
        }
        if (requestPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                requestPermissions.toTypedArray(),
                requestPermission
            )
        }
    }

}