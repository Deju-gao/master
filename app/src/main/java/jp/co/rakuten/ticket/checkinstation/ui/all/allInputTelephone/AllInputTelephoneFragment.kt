package jp.co.rakuten.ticket.checkinstation.ui.all.allInputTelephone

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.AllActivity
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentAllInputTelephoneBinding
import jp.co.rakuten.ticket.checkinstation.util.AllInputTelephoneBackButton
import jp.co.rakuten.ticket.checkinstation.util.AllInputTelephoneToQRCodeScanButton
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class AllInputTelephoneFragment : Fragment() {

    companion object {
        fun newInstance() = AllInputTelephoneFragment()
    }

    private lateinit var binding: FragmentAllInputTelephoneBinding

    private lateinit var viewModel: AllInputTelephoneViewModel

    private lateinit var allActivity: AllActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[AllInputTelephoneViewModel::class.java]
        binding = FragmentAllInputTelephoneBinding.inflate(inflater, container, false)
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

    private fun initData() {

        allActivity = activity as AllActivity

        val arguments = arguments?.get("arguments") as AllInputTelephoneArguments

        binding.telephone.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.nextButton.callOnClick()
                true
            } else {
                false
            }
        }

        binding.nextButton.setOnClickListener {
            viewModel.onNextButtonDidTap(arguments.number, allActivity.againMode, allActivity)
        }

        binding.buttonTop.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(AllInputTelephoneToQRCodeScanButton)
            findNavController().popBackStack(R.id.allQRStep1Fragment, false)
        }
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(AllInputTelephoneBackButton)
            findNavController().popBackStack()
        }

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
                }
                .setCancelable(true)
                .show()
        })

        //next
        viewModel.next.observe(viewLifecycleOwner, Observer {
            allActivity.againMode = false
            findNavController().navigate(
                R.id.action_allInputTelephoneFragment_to_allLoadingFragment,
                bundleOf("arguments" to it)
            )
        })

        viewModel.showDialogEnum.observe(viewLifecycleOwner, Observer {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setMessage(
                    when(it){
                        AllInputTelephoneViewModel.ErrorMessageEnum.TEL_ERROR->{
                            getString(R.string.error_no_tel)
                        }
                        else ->{
                            getString(R.string.error_no_tel)
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