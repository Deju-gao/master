package jp.co.rakuten.ticket.checkinstation.ui.general.generalInputNumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentGeneralInputNumberBinding
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralInputOrderNumberBackButton
import jp.co.rakuten.ticket.checkinstation.util.GeneralInputOrderNumberToQRCodeScanButton
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class GeneralInputNumberFragment : Fragment() {

    companion object {
        fun newInstance() = GeneralInputNumberFragment()
    }

    private lateinit var binding: FragmentGeneralInputNumberBinding

    private lateinit var viewModel: GeneralInputNumberViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[GeneralInputNumberViewModel::class.java]
        binding = FragmentGeneralInputNumberBinding.inflate(inflater, container, false)
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

        binding.username.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.nextButton.callOnClick()
                true
            } else {
                false
            }
        }

        binding.nextButton.setOnClickListener {
            viewModel.onNextButtonClick()
        }

        binding.buttonTop.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputOrderNumberToQRCodeScanButton)
            findNavController().popBackStack(R.id.generalSelectModeFragment
                ,false)
        }
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputOrderNumberBackButton)
            findNavController().popBackStack()
        }

        viewModel.showDialogEnum.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(
                    when(it){
                        GeneralInputNumberViewModel.ErrorMessageEnum.ORDER_NO_ERROR->{
                            getString(R.string.error_no_orderNo)
                        }
                        else ->{
                            getString(R.string.error_no_orderNo)
                        }
                    }
                )
                .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        })

        viewModel.navigateToNext.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(
                R.id.action_generalInputNumberFragment_to_generalInputTelephoneFragment, bundleOf("arguments" to it)
            )
        })
    }

}