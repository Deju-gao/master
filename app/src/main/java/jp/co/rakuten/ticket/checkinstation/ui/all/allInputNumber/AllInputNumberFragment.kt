package jp.co.rakuten.ticket.checkinstation.ui.all.allInputNumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentAllInputNumberBinding
import jp.co.rakuten.ticket.checkinstation.util.AllInputOrderNumberToQRCodeScanButton
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class AllInputNumberFragment : Fragment() {

    companion object {
        fun newInstance() = AllInputNumberFragment()
    }

    private lateinit var binding: FragmentAllInputNumberBinding

    private lateinit var viewModel: AllInputNumberViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[AllInputNumberViewModel::class.java]
        binding = FragmentAllInputNumberBinding.inflate(inflater, container, false)
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

        binding.buttonToQR.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(AllInputOrderNumberToQRCodeScanButton)
            findNavController().navigate(R.id.action_allInputNumberFragment_to_allQRStep1Fragment)
        }

        viewModel.navigateToNext.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(
                R.id.action_allInputNumberFragment_to_allInputTelephoneFragment, bundleOf("arguments" to it)
            )
        })

        viewModel.showDialogEnum.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(
                    when(it){
                        AllInputNumberViewModel.ErrorMessageEnum.ORDER_NO_ERROR->{
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
    }

}