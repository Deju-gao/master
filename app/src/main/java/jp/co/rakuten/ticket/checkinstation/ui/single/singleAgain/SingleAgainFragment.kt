package jp.co.rakuten.ticket.checkinstation.ui.single.singleAgain

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
import jp.co.rakuten.ticket.checkinstation.SingleActivity
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentSingleAgainBinding
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil
import jp.co.rakuten.ticket.checkinstation.util.SingleInputPasswordBackButton
import jp.co.rakuten.ticket.checkinstation.util.SingleInputPasswordMenuButton

@AndroidEntryPoint
class SingleAgainFragment : Fragment() {

    companion object {
        fun newInstance() = SingleAgainFragment()
    }

    private lateinit var binding: FragmentSingleAgainBinding

    private lateinit var viewModel: SingleAgainViewModel

    private lateinit var singleActivity: SingleActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SingleAgainViewModel::class.java]
        binding = FragmentSingleAgainBinding.inflate(inflater, container, false)
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

        singleActivity = activity as SingleActivity

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
            FirebaseLogUtil.getInstance().uploadPageLog(SingleInputPasswordMenuButton)
            findNavController().popBackStack(R.id.singleQRStep1Fragment
                ,false)
        }
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(SingleInputPasswordBackButton)
            findNavController().popBackStack()
        }

        viewModel.login.observe(viewLifecycleOwner, Observer {
            val singleActivity: SingleActivity = activity as SingleActivity
            singleActivity.againMode = true
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
                        SingleAgainViewModel.ErrorMessageEnum.PASSWORD_ERROR ->{
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
            singleActivity.isUIBlocked = isVisible
            binding.loadingLayout.isVisible = isVisible
        })

    }

}