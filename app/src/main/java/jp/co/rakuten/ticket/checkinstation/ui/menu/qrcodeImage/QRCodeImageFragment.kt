package jp.co.rakuten.ticket.checkinstation.ui.menu.qrcodeImage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentQRCodeImageBinding
import jp.co.rakuten.ticket.checkinstation.ui.menu.home.MainActivity
import jp.co.rakuten.ticket.checkinstation.util.*

class QRCodeImageFragment : Fragment() {

    companion object {
        fun newInstance() = QRCodeImageFragment()
    }

    private lateinit var binding: FragmentQRCodeImageBinding

    private lateinit var viewModel: QRCodeImageViewModel

    private lateinit var menuActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuActivity = context as MainActivity
        binding = FragmentQRCodeImageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[QRCodeImageViewModel::class.java]
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        if (menuActivity.getQRCodeImagePath() == "default") {
            binding.qrcodeImageView.setImageResource(R.drawable.message)
        }
        else {
            val bitmap = BitmapFactory.decodeFile(menuActivity.getQRCodeImagePath())
            binding.qrcodeImageView.setImageBitmap(bitmap)
        }
        viewModel.onViewResume()
    }

    //setup ui
    private fun setupUI() {

        //back button
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(QRCodeImageSettingBackButton)
            menuActivity.setIsBackFromPrintSetting(false)
            findNavController().popBackStack(R.id.menuFragment,false)
        }

        //open photo album
        binding.fileSelected.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(QRCodeImageSettingFileImageButton)
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder(requireContext())
                    .setMessage("この機能を利用するにはRakuten Ticketに写真へのアクセスを許可してください")
                    .setPositiveButton(
                        "設定",
                        DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri =
                                Uri.fromParts("package", requireContext().packageName, null)
                            intent.data = uri
                            try {
                                requireContext().startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            dialog.dismiss()
                        })
                    .setNegativeButton(
                        "閉じる",
                        DialogInterface.OnClickListener { dialog: DialogInterface, _: Int -> dialog.dismiss() })
                    .create().show()
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    100
//                )
            }
            else {
                openAlbum()
            }
        }

        //default image
        binding.defaultImageSelected.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(QRCodeImageSettingDefaultImageButton)
            resetDefault()
        }

    }

    //open album
    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    //reset default
    private fun resetDefault() {
        binding.qrcodeImageView.setImageResource(R.drawable.message)
        menuActivity.setQRCodeImageMethod(getString(R.string.default_image))
        menuActivity.setQRCodeImagePath("default")
        SaveDataUtil.getInstance().saveData(requireContext(), "QRCodeImagePath", "default")
        SaveDataUtil.getInstance().saveData(requireContext(), "QRCodeImageMethod", getString(R.string.default_image))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    handleImageOnKitKat(data)
                }
            }
            else {
                resetDefault()
            }
        }
    }

    private fun handleImageOnKitKat(data: Intent) {
        var imagePath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(requireContext(), uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":".toRegex()).toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                imagePath = if (docId.startsWith("raw:")) {
                    docId.replace("raw:", "")
                } else {
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                    getImagePath(contentUri, null)
                }
            } else if ("com.android.externalstorage.documents" == uri.authority) {
                val split = docId.split(":")
                val id = split[0]
                if ("primary".equals(id, ignoreCase = true)) {
                    imagePath = Environment.getExternalStorageDirectory().path + "/" + split[1]
                }
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = if ("com.google.android.apps.photos.content" == uri.authority) {
                uri.lastPathSegment
            } else {
                getImagePath(uri, null)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            imagePath = uri.path
        }
        displayImage(imagePath)
    }

    @SuppressLint("Range")
    private fun getImagePath(uri: Uri?, selection: String?): String? {
        var path: String? = null
        val cursor = requireActivity().contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            binding.qrcodeImageView.setImageBitmap(bitmap)
            menuActivity.setQRCodeImageMethod(getString(R.string.file_select_image))
            menuActivity.setQRCodeImagePath(imagePath)
            SaveDataUtil.getInstance().saveData(requireContext(), "QRCodeImagePath", imagePath)
            SaveDataUtil.getInstance().saveData(requireContext(), "QRCodeImageMethod", getString(R.string.file_select_image))
        } else {
            Toast.makeText(context, "Get image error", Toast.LENGTH_SHORT).show()
        }
    }



}