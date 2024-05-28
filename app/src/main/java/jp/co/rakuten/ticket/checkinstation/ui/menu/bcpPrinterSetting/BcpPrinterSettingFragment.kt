package jp.co.rakuten.ticket.checkinstation.ui.menu.bcpPrinterSetting

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentBcpPrinterSettingBinding
import jp.co.rakuten.ticket.checkinstation.ui.menu.home.MainActivity
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil
import jp.co.rakuten.ticket.checkinstation.util.PrinterSettingBackButton
import jp.co.rakuten.ticket.checkinstation.util.SaveDataUtil

class BcpPrinterSettingFragment : Fragment() {

    companion object {
        fun newInstance() = BcpPrinterSettingFragment()
    }

    private lateinit var binding: FragmentBcpPrinterSettingBinding

    private lateinit var viewModel: BcpPrinterSettingViewModel

    private lateinit var printerSettingRecyclerAdapter: BcpPrinterSettingRecyclerAdapter

    private lateinit var menuActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuActivity = context as MainActivity
        binding = FragmentBcpPrinterSettingBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[BcpPrinterSettingViewModel::class.java]
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
        viewModel.resumeViewModel()
        if (SaveDataUtil.getInstance().getData(requireContext(), "TargetIndex").isNotEmpty() &&
            menuActivity.getBluetoothAddress() != getString(R.string.select_device)) {
            printerSettingRecyclerAdapter.currentSelectIndex = SaveDataUtil.getInstance().getData(requireContext(), "TargetIndex").toInt()
        }
    }

    //setup ui
    private fun setupUI() {

        setupRecyclerView()

        //back button
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(PrinterSettingBackButton)
            menuActivity.setIsBackFromPrintSetting(true)
            findNavController().popBackStack(R.id.menuFragment,false)
        }

        //discovery print
        viewModel.discoveryPrinter.observe(viewLifecycleOwner, Observer {
            printerSettingRecyclerAdapter.items = viewModel.getDiscoveryList()!!
            printerSettingRecyclerAdapter.notifyDataSetChanged()
        })

        //target value
        viewModel.targetValue.observe(viewLifecycleOwner, Observer {
            menuActivity.setBluetoothAddress(it!!)
            menuActivity.setIsBackFromPrintSetting(true)
            findNavController().popBackStack(R.id.menuFragment,false)
        })

        //target index
        viewModel.targetIndex.observe(viewLifecycleOwner, Observer {
            printerSettingRecyclerAdapter.currentSelectIndex = it!!
            printerSettingRecyclerAdapter.notifyDataSetChanged()
            SaveDataUtil.getInstance().saveData(requireContext(), "TargetIndex", it.toString())
        })

    }

    //setup recycler view
    private fun setupRecyclerView() {
        printerSettingRecyclerAdapter = BcpPrinterSettingRecyclerAdapter {
            viewModel.targetDidSelected(it)
        }
        binding.printerRecyclerView.adapter = printerSettingRecyclerAdapter
        binding.printerRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        printerSettingRecyclerAdapter.items = viewModel.getDiscoveryList()!!
        printerSettingRecyclerAdapter.notifyDataSetChanged()
    }

}