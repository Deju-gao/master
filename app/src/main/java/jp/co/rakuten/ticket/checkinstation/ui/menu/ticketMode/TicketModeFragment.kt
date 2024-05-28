package jp.co.rakuten.ticket.checkinstation.ui.menu.ticketMode

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
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentTicketModeBinding
import jp.co.rakuten.ticket.checkinstation.ui.menu.home.MainActivity
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil
import jp.co.rakuten.ticket.checkinstation.util.SaveDataUtil
import jp.co.rakuten.ticket.checkinstation.util.TicketModeBackButton

class TicketModeFragment : Fragment() {

    companion object {
        fun newInstance() = TicketModeFragment()
    }

    private lateinit var binding: FragmentTicketModeBinding

    //adapter
    private lateinit var tickerModeRecyclerAdapter: TickerModeRecyclerAdapter

    private lateinit var viewModel: TicketModeViewModel

    private lateinit var menuActivity: MainActivity

    private lateinit var itemList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuActivity = context as MainActivity
        binding = FragmentTicketModeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[TicketModeViewModel::class.java]
        binding.viewModel = viewModel
        itemList = listOf(getString(R.string.general_mode), getString(R.string.all_mode), getString(R.string.single_mode))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        binding.currentMode.text = menuActivity.getTicketMode()
        tickerModeRecyclerAdapter.currentSelectIndex = itemList.indexOf(menuActivity.getTicketMode())
        tickerModeRecyclerAdapter.notifyDataSetChanged()
        viewModel.onViewResume()
    }

    //setup ui
    private fun setupUI() {

        //setup recycler view
        setupRecyclerView()

        //back button
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(TicketModeBackButton)
            menuActivity.setIsBackFromPrintSetting(false)
            findNavController().popBackStack(R.id.menuFragment,false)
        }

        //get mode
        viewModel.modeSelected.observe(viewLifecycleOwner, Observer {
            menuActivity.setTicketMode(itemList[it!!])
            tickerModeRecyclerAdapter.currentSelectIndex = it
            tickerModeRecyclerAdapter.notifyDataSetChanged()
            menuActivity.setIsBackFromPrintSetting(false)
            SaveDataUtil.getInstance().saveData(requireContext(), "TicketMode", itemList[it])
            findNavController().popBackStack(R.id.menuFragment,false)
        })

    }

    //setup recycler view
    private fun setupRecyclerView() {
        tickerModeRecyclerAdapter = TickerModeRecyclerAdapter {
            viewModel.modeListSelected(it)
        }
        binding.tickerModeRecyclerView.adapter = tickerModeRecyclerAdapter
        binding.tickerModeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        tickerModeRecyclerAdapter.items = itemList
        tickerModeRecyclerAdapter.notifyDataSetChanged()
    }

}