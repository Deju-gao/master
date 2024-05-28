package jp.co.rakuten.ticket.checkinstation.ui.all.allQRStep1

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.AllActivity
import jp.co.rakuten.ticket.checkinstation.BuildConfig
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentAllQrstep1Binding
import jp.co.rakuten.ticket.checkinstation.ui.menu.home.MainActivity
import jp.co.rakuten.ticket.checkinstation.util.*

@AndroidEntryPoint
class AllQRStep1Fragment : Fragment() {

    companion object {
        fun newInstance() = AllQRStep1Fragment()
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var binding: FragmentAllQrstep1Binding

    private lateinit var viewModel: AllQRStep1ViewModel

    private lateinit var allActivity: AllActivity

    var barCodeCallback: BarcodeCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[AllQRStep1ViewModel::class.java]
        binding = FragmentAllQrstep1Binding.inflate(inflater, container, false)
        binding.lifecycleOwner = this;
        binding.viewModel = viewModel;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.barcodeSurfaceView.cameraSettings.requestedCameraId = 1
        initData()
        setNoticeImage()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        checkAgainMode()
        viewModel.onResumeViewModel()
    }

    private fun initData(){
        allActivity = activity as AllActivity

        //qrcode result
        barCodeCallback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                binding.barcodeSurfaceView?.pause()
                viewModel.handleQRCodeResult(result, allActivity.againMode, allActivity)
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        }

        binding.barcodeSurfaceView.decodeContinuous(barCodeCallback)
        binding.customFinderView.setCameraPreview(binding.barcodeSurfaceView)

        binding.buttonMenu.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(AllQRCodeScanMenuButton)
            if (binding.buttonAgain.visibility == View.GONE){
                binding.buttonAgain.visibility = View.VISIBLE
            }else{
                binding.buttonAgain.visibility = View.GONE
            }
            if (binding.buttonLogout.visibility == View.GONE){
                binding.buttonLogout.visibility = View.VISIBLE
            }else{
                binding.buttonLogout.visibility = View.GONE
            }
        }

        binding.buttonLogout.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(AllQRCodeScanLogoutButton)
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.buttonToInputNumber.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(AllQRCodeScanOrderNumberButton)
            findNavController().navigate(R.id.action_allQRStep1Fragment_to_allInputNumberFragment)
        }

        viewModel.back.observe(viewLifecycleOwner, Observer {
            allActivity.againMode = false
            findNavController().navigate(
                R.id.action_allQRStep1Fragment_to_allLoadingFragment,
                bundleOf("arguments" to it)
            )
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

        //laoding
        viewModel.isLoadingVisible.observe(viewLifecycleOwner, Observer {
            val isVisible = it ?: return@Observer
            allActivity.isUIBlocked = isVisible
            binding.loadingLayout.isVisible = isVisible
        })

        //error alert
        viewModel.showDialogString.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(it)
                .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                    dialog.dismiss()
                    launchCapture()
                }
                .setCancelable(true)
                .show()
        })

    }

    //set notice image
    private fun setNoticeImage() {
        val qrCodeImagePath = SaveDataUtil.getInstance().getData(requireContext(), "QRCodeImagePath")
        if (qrCodeImagePath == "default" || qrCodeImagePath.isEmpty()) {
            binding.noticeImageView.setImageResource(R.drawable.message)
        }
        else {
            val bitmap = BitmapFactory.decodeFile(qrCodeImagePath)
            binding.noticeImageView.setImageBitmap(bitmap)
        }
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

    private fun checkAgainMode(){
        if (allActivity.againMode){
            binding.buttonAgain.text = getString(R.string.marginal_button_again_true)
            binding.buttonAgain.setOnClickListener {
                FirebaseLogUtil.getInstance().uploadPageLog(AllQRCodeScanUniversalCouponButton)
                allActivity.againMode = false
                checkAgainMode()
            }
        }else{
            binding.buttonAgain.text = getString(R.string.marginal_button_again_false)
            binding.buttonAgain.setOnClickListener {
                FirebaseLogUtil.getInstance().uploadPageLog(AllQRCodeScanReissueCouponButton)
                findNavController().navigate(R.id.action_allQRStep1Fragment_to_allAgainFragment)
            }
        }
    }

}