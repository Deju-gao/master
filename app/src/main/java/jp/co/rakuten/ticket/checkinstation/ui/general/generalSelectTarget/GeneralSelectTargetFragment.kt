package jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectTarget

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
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.GeneralActivity
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentGeneralSelectTargetBinding
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralSelectTargetBackButton
import jp.co.rakuten.ticket.checkinstation.util.GeneralSelectTargetToQRCodeScanButton
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class GeneralSelectTargetFragment : Fragment() {

    companion object {
        fun newInstance() = GeneralSelectTargetFragment()
    }

    private lateinit var binding: FragmentGeneralSelectTargetBinding

    private lateinit var viewModel: GeneralSelectTargetViewModel

    private lateinit var recyclerAdapter: SelectTicketAdapter

    private lateinit var generalActivity: GeneralActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[GeneralSelectTargetViewModel::class.java]
        binding = FragmentGeneralSelectTargetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        val arguments = arguments?.get("arguments") as GeneralSelectTargetArguments
        viewModel.onViewResume(arguments.orderNo, requireContext(), generalActivity.againMode)
    }

    private fun initData(){
        generalActivity = activity as GeneralActivity
        val arguments = arguments?.get("arguments") as GeneralSelectTargetArguments
        binding.nextButton.setOnClickListener {
            viewModel.onNextButtonDidTap(requireContext(), arguments.orderNo)
        }

        binding.buttonTop.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectTargetToQRCodeScanButton)
            findNavController().popBackStack(R.id.generalSelectModeFragment
                ,false)
        }
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectTargetBackButton)
            findNavController().popBackStack()
        }

        viewModel.toVerifyPage.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(
                R.id.action_generalSelectTargetFragment_to_generalVerifyFragment,
                bundleOf("arguments" to it)
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

        //reload adapter
        viewModel.reloadListData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                recyclerAdapter.items = it
                recyclerAdapter.notifyDataSetChanged()
            }
        })

        //selected count
        viewModel.selectCountString.observe(viewLifecycleOwner, Observer {
            binding.selectCountText.text = requireContext().getString(R.string.select_target_count, it)
        })

        //ticket info
        viewModel.ticketInfo.observe(viewLifecycleOwner, Observer {
            binding.dateText.text = requireContext().getString(R.string.select_target_date, it?.performance?.date)
            binding.numberText.text = requireContext().getString(R.string.select_target_order_num, it?.order?.order_no)
            binding.nameText.text = requireContext().getString(R.string.select_target_name, it?.performance?.name)
        })

    }

    private fun initAdapter(){
        recyclerAdapter = SelectTicketAdapter(requireContext()) {
            viewModel.onItemDidSelect(it)
        }
        binding.selectTicketView.adapter = recyclerAdapter
        binding.selectTicketView.layoutManager = GridLayoutManager(requireContext(),2)
    }

}