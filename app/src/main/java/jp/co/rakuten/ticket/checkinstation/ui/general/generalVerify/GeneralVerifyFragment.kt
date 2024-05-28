package jp.co.rakuten.ticket.checkinstation.ui.general.generalVerify

import android.annotation.SuppressLint
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
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentGeneralVerifyBinding
import jp.co.rakuten.ticket.checkinstation.ui.general.generalLoading.GeneralLoadingArguments
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.GeneralConfirmBackButton
import jp.co.rakuten.ticket.checkinstation.util.GeneralConfirmToQRCodeScanButton
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class GeneralVerifyFragment : Fragment() {

    companion object {
        fun newInstance() = GeneralVerifyFragment()
    }

    private lateinit var binding: FragmentGeneralVerifyBinding

    private lateinit var viewModel: GeneralVerifyViewModel

    private lateinit var recyclerAdapter: TicketListAdapter

    private lateinit var generalActivity: GeneralActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[GeneralVerifyViewModel::class.java]
        binding = FragmentGeneralVerifyBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this;
        binding.viewModel = viewModel;
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
        val arguments = arguments?.get("arguments") as GeneralVerifyArguments
        setPerformanceInfo(arguments)
        viewModel.onViewResume(arguments.tokenIdList, generalActivity, generalActivity.againMode)
    }

    private fun initData(){
        generalActivity = activity as GeneralActivity
        val arguments = arguments?.get("arguments") as GeneralVerifyArguments
        binding.nextButton.setOnClickListener {
            viewModel.onNextButtonDidTap(generalActivity)
        }

        binding.buttonTop.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralConfirmToQRCodeScanButton)
            findNavController().popBackStack(R.id.generalSelectModeFragment
                ,false)
        }
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralConfirmBackButton)
            findNavController().popBackStack()
        }

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

        //to loading
        viewModel.toLoadingPage.observe(viewLifecycleOwner, Observer {
            generalActivity.againMode = false
            findNavController().navigate(
                R.id.action_generalVerifyFragment_to_generalLoadingFragment,
                bundleOf("arguments" to GeneralLoadingArguments(arguments.orderNo))
            )
        })

    }

    //set performance info
    @SuppressLint("StringFormatMatches")
    private fun setPerformanceInfo(arguments: GeneralVerifyArguments) {
        binding.dateText.text = requireContext().getString(R.string.select_target_date, arguments.performanceDate)
        binding.nameText.text = requireContext().getString(R.string.select_target_name, arguments.performanceName)
        binding.ticketCountText.text = requireContext().getString(R.string.verify_select_target_count, arguments.ticketList.size)
    }

    private fun initAdapter(){
        val arguments = arguments?.get("arguments") as GeneralVerifyArguments
        recyclerAdapter = TicketListAdapter(requireContext())
        try {
            val list = arguments.ticketList
            recyclerAdapter.items = list
        } catch (e: Exception) {
            recyclerAdapter.items = emptyList()
        }
        binding.ticketListView.adapter = recyclerAdapter
        binding.ticketListView.layoutManager = GridLayoutManager(requireContext(),2)
    }

}