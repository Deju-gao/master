package jp.co.rakuten.ticket.checkinstation.ui.general.generalAgain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.GeneralActivity
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentGeneralAgainBinding
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralInputPasswordBackButton
import jp.co.rakuten.ticket.checkinstation.util.GeneralInputPasswordToQRCodeScanButton
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class GeneralAgainFragment : Fragment() {

    companion object {
        fun newInstance() = GeneralAgainFragment()
    }

    private lateinit var binding: FragmentGeneralAgainBinding

    private lateinit var viewModel: GeneralAgainViewModel

    private lateinit var generalActivity: GeneralActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[GeneralAgainViewModel::class.java]
        binding = FragmentGeneralAgainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this;
        binding.viewModel = viewModel;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        viewModel.onViewResume()
    }

    private fun initData(){

        generalActivity = activity as GeneralActivity

        binding.password.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.nextButton.callOnClick()
                true
            } else {
                false
            }
        }

        binding.nextButton.setOnClickListener {
            viewModel.onNextButtonDidTap(requireContext())
        }

        binding.buttonTop.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputPasswordToQRCodeScanButton)
            findNavController().popBackStack(R.id.generalSelectModeFragment
                ,false)
        }
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputPasswordBackButton)
            findNavController().popBackStack()
        }

        viewModel.login.observe(viewLifecycleOwner, Observer {
            generalActivity.againMode = true
            findNavController().popBackStack()
        })

        viewModel.showDialogString.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(it)
                .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        })

        viewModel.showDialogEnum.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(
                    when(it){
                        GeneralAgainViewModel.ErrorMessageEnum.PASSWORD_ERROR ->{
                            getString(R.string.error_no_password)
                        }
                        else -> {
                            ""
                        }
                    }
                )
                .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        })

        viewModel.isLoadingVisible.observe(viewLifecycleOwner, Observer {
            val isVisible = it ?: return@Observer
            generalActivity.isUIBlocked = isVisible
            binding.loadingLayout.isVisible = isVisible
        })

    }

}