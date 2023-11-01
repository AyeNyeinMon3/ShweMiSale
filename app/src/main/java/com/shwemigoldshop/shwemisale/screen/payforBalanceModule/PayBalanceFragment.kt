package com.shwemigoldshop.shwemisale.screen.payforBalanceModule

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shwemigoldshop.shwemisale.printerHelper.AkpDownloader
import com.shwemigoldshop.shwemisale.databinding.FragmentPayBalanceBinding
import com.shwemigoldshop.shwemisale.qrscan.getBarLauncher
import com.shwemigoldshop.shwemisale.qrscan.scanQrCode
import com.shwemigoldshop.shwemisale.util.generateNumberFromEditText
import com.shwemigoldshop.shwemisale.util.hideKeyboard
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.util.showSuccessDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayBalanceFragment:Fragment() {

    lateinit var binding: FragmentPayBalanceBinding
    private val viewModel by viewModels<PayForBalanceViewModel>()
    private lateinit var loading: AlertDialog
    private lateinit var barlauncer: Any
    private val downloader by lazy { AkpDownloader(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentPayBalanceBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        barlauncer = this.getBarLauncher(requireContext()) {
            binding.edtScanVoucher.setText(it)
            viewModel.getRemainingAmount(it)
        }
        binding.textInputLayoutScanVoucher.setEndIconOnClickListener {
            this.scanQrCode(requireContext(), barlauncer)
        }
        binding.edtScanVoucher.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    // Perform action on g press
                    viewModel.getRemainingAmount(binding.edtScanVoucher.text.toString())
                    hideKeyboard(activity, binding.edtScanVoucher)
                    return true
                }
                return false
            }
        })
        binding.btnCalculate.setOnClickListener {
            val remainAmount = generateNumberFromEditText(binding.edtVoucherBalance).toInt() - generateNumberFromEditText(binding.edtDeposit).toInt()
            binding.edtBalance.setText(remainAmount.toString())
        }

        viewModel.getRemainAmountLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    binding.edtVoucherBalance.setText(it.data!!.remaining_amount)
                    binding.btnPrint.setOnClickListener {view->
                        viewModel.payBalance(
                            it.data!!.id.orEmpty(),
                            binding.edtDeposit.text.toString()
                        )
                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.payBalanceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog(it.data.orEmpty()) {
                        viewModel.logout()
                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.logoutLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
//                    Toast.makeText(requireContext(),"log out successful", Toast.LENGTH_LONG).show()
                    findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections.actionGlobalLogout())
                }
                is Resource.Error->{
                    loading.dismiss()
                    findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections.actionGlobalLogout())

                }
                else -> {}
            }
        }

    }
}