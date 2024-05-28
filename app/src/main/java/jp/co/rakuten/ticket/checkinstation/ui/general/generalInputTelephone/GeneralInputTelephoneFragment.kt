package jp.co.rakuten.ticket.checkinstation.ui.general.generalInputTelephone

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
import jp.co.rakuten.ticket.checkinstation.GeneralActivity
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentGeneralInputTelephoneBinding
import jp.co.rakuten.ticket.checkinstation.util.*

@AndroidEntryPoint
class GeneralInputTelephoneFragment : Fragment() {

    companion object {
        fun newInstance() = GeneralInputTelephoneFragment()
    }

    private lateinit var binding: FragmentGeneralInputTelephoneBinding

    private lateinit var viewModel: GeneralInputTelephoneViewModel

    private lateinit var generalActivity: GeneralActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[GeneralInputTelephoneViewModel::class.java]
        binding = FragmentGeneralInputTelephoneBinding.inflate(inflater, container, false)
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
        val arguments = arguments?.get("arguments") as GeneralInputTelephoneArguments

        generalActivity = activity as GeneralActivity

        binding.telephone.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.nextButton.callOnClick()
                true
            } else {
                false
            }
        }

        binding.nextButton.setOnClickListener {
            viewModel.onNextButtonClick(requireContext(), arguments.number, generalActivity.againMode)
        }

        binding.buttonTop.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputTelephoneToQRCodeScanButton)
            findNavController().popBackStack(R.id.generalSelectModeFragment
                ,false)
        }
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralInputTelephoneBackButton)
            findNavController().popBackStack()
        }

        viewModel.navigateToNext.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(
                R.id.action_generalInputTelephoneFragment_to_generalNeedSelectFragment,
                bundleOf("arguments" to it)
            )
        })

        viewModel.showDialogEnum.observe(viewLifecycleOwner, Observer {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setMessage(
                    when(it){
                        GeneralInputTelephoneViewModel.ErrorMessageEnum.TEL_ERROR->{
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

        //laoding
        viewModel.isLoadingVisible.observe(viewLifecycleOwner, Observer {
            val isVisible = it ?: return@Observer
            generalActivity.isUIBlocked = isVisible
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
    }

}