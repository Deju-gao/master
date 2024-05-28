package jp.co.rakuten.ticket.checkinstation.ui.general.generalNeedSelect

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.GeneralActivity
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentGeneralNeedSelectTargetBinding
import jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectCount.GeneralSelectCountFragment
import jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget.GeneralSelectTargetArguments
import jp.co.rakuten.ticket.checkinstation.util.*

@AndroidEntryPoint
class GeneralNeedSelectFragment : Fragment() {

    companion object {
        fun newInstance() = GeneralSelectCountFragment()
    }

    private lateinit var binding: FragmentGeneralNeedSelectTargetBinding

    private lateinit var viewModel: GeneralNeedSelectViewModel

    private lateinit var generalActivity: GeneralActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[GeneralNeedSelectViewModel::class.java]
        binding = FragmentGeneralNeedSelectTargetBinding.inflate(inflater, container, false)
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

        val arguments = arguments?.get("arguments") as GeneralNeedSelectArguments

        binding.unneededSelectTargetCard.setOnClickListener {
            viewModel.onUnneededSelectTargetCardDidTap(arguments.orderNo, requireContext(), generalActivity.againMode)
        }

        binding.needSelectTargetCard.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralNeedSelectTicketButton)
            findNavController().navigate(
                R.id.action_generalNeedSelectFragment_to_generalSelectTargetFragment,
                bundleOf("arguments" to GeneralSelectTargetArguments(orderNo = arguments.orderNo))
            )
        }

        binding.buttonTop.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralNeedSelectToQRCodeScanButton)
            findNavController().popBackStack(R.id.generalSelectModeFragment,false)
        }
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralNeedSelectBackButton)
            findNavController().popBackStack()
        }

        viewModel.toVerifyPage.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(
                R.id.action_generalNeedSelectFragment_to_generalVerifyFragment, bundleOf("arguments" to it)
            )
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