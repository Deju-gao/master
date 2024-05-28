package jp.co.rakuten.ticket.checkinstation.ui.menu.readTest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.BuildConfig
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentReadTestBinding
import jp.co.rakuten.ticket.checkinstation.ui.menu.home.MainActivity
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil
import jp.co.rakuten.ticket.checkinstation.util.QRCodeReadTestBackButton

@AndroidEntryPoint
class ReadTestFragment : Fragment() {

    companion object {
        fun newInstance() = ReadTestFragment()
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var binding: FragmentReadTestBinding

    private lateinit var viewModel: ReadTestViewModel

    private lateinit var menuActivity: MainActivity

    var barCodeCallback: BarcodeCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReadTestBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ReadTestViewModel::class.java]
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.barcodeSurfaceView.cameraSettings.requestedCameraId = 1

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        viewModel.onResumeViewModel()
    }

    //setup ui
    private fun setupUI() {
        menuActivity = activity as MainActivity
        //back button
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(QRCodeReadTestBackButton)
            menuActivity.setIsBackFromPrintSetting(false)
            findNavController().popBackStack(R.id.menuFragment,false)
        }

        //qrcode result
        barCodeCallback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                binding.barcodeSurfaceView?.pause()
                viewModel.handleQRCodeResult(result, requireContext())
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        }

        binding.barcodeSurfaceView.decodeContinuous(barCodeCallback)

        binding.customFinderView.setCameraPreview(binding.barcodeSurfaceView)

        viewModel.back.observe(viewLifecycleOwner, Observer {
            binding.qrcodeLabel.text = it
        })

        viewModel.checkToAskPermission.observe(viewLifecycleOwner, Observer {

            viewModel.onPermissionResult(isPermissionGranted())

        })

        viewModel.startCapture.observe(viewLifecycleOwner, Observer {
            launchCapture()
        })

        viewModel.stopCapture.observe(viewLifecycleOwner, Observer {
            binding.barcodeSurfaceView.pause()
        })

        viewModel.navigateBack.observe(viewLifecycleOwner, Observer {
            menuActivity.setIsBackFromPrintSetting(false)
            findNavController().navigateUp()
        })

        viewModel.askCameraPermission.observe(viewLifecycleOwner, Observer {
            this.requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                1
            )
        })

        viewModel.displayNeverAskAgainDialog.observe(viewLifecycleOwner, Observer {
            createNeverAskAgainDialog().show()
        })

        viewModel.openPermissionSettings.observe(viewLifecycleOwner, Observer {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            )
            startActivity(intent)
        })

        //error alert
        viewModel.showDialogString.observe(viewLifecycleOwner, Observer {
            android.app.AlertDialog.Builder(requireContext())
                .setMessage(it)
                .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        })

    }

    //get permission
    private fun isPermissionGranted(): Boolean {
        val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    //create ask dialog
    private fun createNeverAskAgainDialog(): AlertDialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.scan_qrcode_dialog_camera_permission_error_title))
            .setMessage(R.string.scan_qrcode_dialog_camera_permission_error_message)
            .setPositiveButton(getString(R.string.scan_qrcode_dialog_camera_permission_error_button)) { _, _ ->
                viewModel.onOpenSettingsClick()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> viewModel.onNegativeButtonAlertRefuseClick() }
            .setOnCancelListener { viewModel.onNegativeButtonAlertRefuseClick() }
            .create()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                val permission = permissions[0]
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCapture()
                }
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        // User said no, need to go back
                        viewModel.onRefusePermission()
                    } else {
                        // User check "never ask me again"
                        viewModel.onNeverAskMeAgainResult()
                    }
                }
            }
        }
    }

    //launch barcode view
    private fun launchCapture() {
        binding.barcodeSurfaceView.resume()
    }

}