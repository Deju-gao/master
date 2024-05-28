package jp.co.rakuten.ticket.checkinstation.ui.single.singleOver

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
import jp.co.rakuten.ticket.checkinstation.databinding.FragmentSingleOverBinding
import jp.co.rakuten.ticket.checkinstation.util.NetworkConnectUtil

@AndroidEntryPoint
class SingleOverFragment : Fragment() {

    companion object {
        fun newInstance() = SingleOverFragment()
    }

    private lateinit var binding: FragmentSingleOverBinding

    private lateinit var viewModel: SingleOverViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SingleOverViewModel::class.java]
        binding = FragmentSingleOverBinding.inflate(inflater, container, false)
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
        viewModel.navigateToTop.observe(viewLifecycleOwner, Observer {
            findNavController().popBackStack(R.id.singleQRStep1Fragment,false)
        })
    }

}