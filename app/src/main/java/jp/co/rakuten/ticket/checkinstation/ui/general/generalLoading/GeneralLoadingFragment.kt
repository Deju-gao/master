package jp.co.rakuten.ticket.checkinstation.ui.general.generalLoading

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
import jp.co.rakuten.ticket.checkinstation.GeneralActivity
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentGeneralLoadingBinding
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class GeneralLoadingFragment : Fragment() {

    companion object {
        fun newInstance() = GeneralLoadingFragment()
    }

    private lateinit var binding: FragmentGeneralLoadingBinding

    private lateinit var viewModel: GeneralLoadingViewModel

    private lateinit var generalActivity: GeneralActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[GeneralLoadingViewModel::class.java]
        binding = FragmentGeneralLoadingBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this;
        binding.viewModel = viewModel;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generalActivity = activity as GeneralActivity
        initData()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
        val arguments = arguments?.get("arguments") as GeneralLoadingArguments
        viewModel.onViewResume(arguments.orderNo, generalActivity.printList, generalActivity)
    }

    private fun initData(){
        viewModel.navigateToOver.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_generalLoadingFragment_to_generalOverFragment)
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