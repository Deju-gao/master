package jp.co.rakuten.ticket.checkinstation.ui.login

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import jp.co.rakuten.ticket.checkinstation.AllActivity
import jp.co.rakuten.ticket.checkinstation.GeneralActivity
import jp.co.rakuten.ticket.checkinstation.R
import jp.co.rakuten.ticket.checkinstation.SingleActivity
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentLoginBinding
import jp.co.rakuten.ticket.checkinstation.util.FirebaseLogUtil
import jp.co.rakuten.ticket.checkinstation.util.LoginBackButton
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil
import jp.co.rakuten.ticket.checkinstation.util.hideKeyboard

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: FragmentLoginBinding

    private lateinit var viewModel: LoginViewModel

    private lateinit var loginActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this;
        binding.viewModel = viewModel;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectUtil.isConnected(requireContext())
    }

    //setup ui
    private fun setupUI(){

        loginActivity = activity as LoginActivity

        //back button
        binding.buttonBack.setOnClickListener {
            FirebaseLogUtil.getInstance().uploadPageLog(LoginBackButton)
            loginActivity.hideKeyboard()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.password.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.loginButton.callOnClick()
                true
            } else {
                false
            }
        }

        //login button
        binding.loginButton.setOnClickListener {
            loginActivity.hideKeyboard()
            viewModel.login(requireContext())
        }

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
                        LoginViewModel.ErrorMessageEnum.USERNAME_ERROR->{
                            getString(R.string.error_no_userName)
                        }
                        LoginViewModel.ErrorMessageEnum.PASSWORD_ERROR ->{
                            getString(R.string.error_no_password)
                        }
                        LoginViewModel.ErrorMessageEnum.COMPANY_CODE_ERROR ->{
                            getString(R.string.error_no_companyCode)
                        }
                        else ->{
                            getString(R.string.error_no_userName)
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
            loginActivity.isUIBlocked = isVisible
            binding.loadingLayout.isVisible = isVisible
        })

        viewModel.hideKeyboard.observe(viewLifecycleOwner, Observer {
            val activity = activity ?: return@Observer
            val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        })

        //push to general, all or single page
        viewModel.login.observe(viewLifecycleOwner, Observer {
            var intent = Intent()
            when (requireActivity().intent.getStringExtra("ticket_mode")) {
                getString(R.string.general_mode) -> {
                    intent = Intent(activity, GeneralActivity::class.java)
                }
                getString(R.string.all_mode) -> {
                    intent = Intent(activity, AllActivity::class.java)
                }
                getString(R.string.single_mode) -> {
                    intent = Intent(activity, SingleActivity::class.java)
                }
            }
            intent.putExtra("printer_target", requireActivity().intent.getStringExtra("printer_target"))
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        })

    }

}