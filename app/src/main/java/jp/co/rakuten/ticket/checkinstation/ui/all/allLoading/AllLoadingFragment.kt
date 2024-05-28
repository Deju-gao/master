package jp.co.rakuten.ticket.checkinstation.ui.all.allLoading

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
import jp.co.rakuten.ticket.checkinstation.AllActivity
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentAllLoadingBinding
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class AllLoadingFragment : Fragment() {

    companion object {
        fun newInstance() = AllLoadingFragment()
    }

    private lateinit var binding: FragmentAllLoadingBinding

    private lateinit var viewModel: AllLoadingViewModel

    private lateinit var allActivity: AllActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[AllLoadingViewModel::class.java]
        binding = FragmentAllLoadingBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this;
        binding.viewModel = viewModel;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allActivity = activity as AllActivity
        initData()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        val arguments = arguments?.get("arguments") as AllLoadingArguments
        viewModel.onViewResume(arguments.orderNo, allActivity.printList, allActivity)
    }

    private fun initData(){
        viewModel.navigateToOver.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_allLoadingFragment_to_allOverFragment)
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