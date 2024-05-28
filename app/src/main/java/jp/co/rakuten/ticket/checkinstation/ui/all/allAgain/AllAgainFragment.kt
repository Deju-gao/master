package jp.co.rakuten.ticket.checkinstation.ui.all.allAgain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.AllActivity
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentAllAgainBinding
import jp.co.rakuten.ticket.checkinstation.util.AllInputPasswordBackButton
import jp.co.rakuten.ticket.checkinstation.util.AllInputPasswordMenuButton
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class AllAgainFragment : Fragment() {

    companion object {
        fun newInstance() = AllAgainFragment()
    }

    private lateinit var binding: FragmentAllAgainBinding

    private lateinit var viewModel: AllAgainViewModel

    private lateinit var allActivity: AllActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[AllAgainViewModel::class.java]
        binding = FragmentAllAgainBinding.inflate(inflater, container, false)
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

        allActivity = activity as AllActivity

        binding.password.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.nextButton.callOnClick()
                true
            } else {
                false
            }
        }

        binding.nextButton.setOnClickListener {
            viewModel.onNextButtonDidTap(requireContext())
        }

        binding.buttonTop.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(AllInputPasswordMenuButton)
            findNavController().popBackStack(R.id.allQRStep1Fragment
                ,false)
        }
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(AllInputPasswordBackButton)
            findNavController().popBackStack()
        }

        viewModel.login.observe(viewLifecycleOwner, Observer {
            allActivity.againMode = true
            findNavController().popBackStack()
        })

        viewModel.showDialogString.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(it)
                .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        })

        viewModel.showDialogEnum.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setMessage(
                    when(it){
                        AllAgainViewModel.ErrorMessageEnum.PASSWORD_ERROR ->{
                            getString(R.string.error_no_password)
                        }
                        else -> {
                            ""
                        }
                    }
                )
                .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        })

        viewModel.isLoadingVisible.observe(viewLifecycleOwner, Observer {
            val isVisible = it ?: return@Observer
            allActivity.isUIBlocked = isVisible
            binding.loadingLayout.isVisible = isVisible
        })

    }

}