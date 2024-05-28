package jp.co.rakuten.ticket.checkinstation.ui.general.generalSelectMode

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.GeneralActivity
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentGeneralSelectModeBinding
import jp.co.rakuten.ticket.checkinstation.ui.menu.home.MainActivity
import jp.co.rakuten.ticket.checkinstation.util.*

@AndroidEntryPoint
class GeneralSelectModeFragment : Fragment() {

    companion object {
        fun newInstance() = GeneralSelectModeFragment()
    }

    private lateinit var binding: FragmentGeneralSelectModeBinding

    private lateinit var viewModel: GeneralSelectModeViewModel

    private lateinit var generalActivity: GeneralActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[GeneralSelectModeViewModel::class.java]
        binding = FragmentGeneralSelectModeBinding.inflate(inflater, container, false)
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
        checkAgainMode()
    }

    private fun initData(){
        generalActivity = activity as GeneralActivity
        binding.allSelectQR.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectModeQRCodeScanButton)
            findNavController().navigate(R.id.action_generalSelectModeFragment_to_generalQRStep1Fragment)
        }
        binding.allSelectNumber.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectOrderNumberButton)
            findNavController().navigate(R.id.action_generalSelectModeFragment_to_generalInputNumberFragment)
        }

        binding.buttonMenu.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectModeMenuButton)
            if (binding.buttonAgain.visibility == View.GONE){
                binding.buttonAgain.visibility = View.VISIBLE
            }else{
                binding.buttonAgain.visibility = View.GONE
            }
            if (binding.buttonLogout.visibility == View.GONE){
                binding.buttonLogout.visibility = View.VISIBLE
            }else{
                binding.buttonLogout.visibility = View.GONE
            }
        }
        binding.buttonLogout.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectModeLogoutButton)
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun checkAgainMode(){
        if (generalActivity.againMode){
            binding.buttonAgain.text = getString(R.string.marginal_button_again_true)
            binding.buttonAgain.setOnClickListener {
                FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectModeUniversalCouponButton)
                generalActivity.againMode = false
                checkAgainMode()
            }
        }else{
            binding.buttonAgain.text = getString(R.string.marginal_button_again_false)
            binding.buttonAgain.setOnClickListener {
                FirebaseLogUtil.getInstance().uploadPageLog(GeneralSelectModeReissueCouponButton)
                findNavController().navigate(R.id.action_generalSelectModeFragment_to_generalAgainFragment)
            }
        }
    }

}
