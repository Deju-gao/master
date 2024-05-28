package jp.co.rakuten.ticket.checkinstation.ui.single.singleLoading

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.SingleActivity
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentSingleLoadingBinding
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class SingleLoadingFragment : Fragment() {

    companion object {
        fun newInstance() = SingleLoadingFragment()
    }

    private lateinit var binding: FragmentSingleLoadingBinding

    private lateinit var viewModel: SingleLoadingViewModel

    private lateinit var singleActivity: SingleActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SingleLoadingViewModel::class.java]
        binding = FragmentSingleLoadingBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this;
        binding.viewModel = viewModel;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        singleActivity = activity as SingleActivity
        initData()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        val arguments = arguments?.get("arguments") as SingleLoadingArguments
        viewModel.onViewResume(arguments.orderNo, singleActivity.printList, singleActivity)
    }

    private fun initData(){
        viewModel.navigateToOver.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_singleLoadingFragment_to_singleOverFragment)
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