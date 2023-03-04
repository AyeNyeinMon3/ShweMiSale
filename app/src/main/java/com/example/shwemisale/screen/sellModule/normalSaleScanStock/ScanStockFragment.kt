package com.example.shwemisale.screen.sellModule.normalSaleScanStock

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.hideKeyboard
import com.example.shwemisale.StockCodeData
import com.example.shwemisale.StockCodeRecyclerAdapter
import com.example.shwemisale.data_layers.domain.product.asUiModel
import com.example.shwemisale.databinding.FragmentScanStockBinding
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanStockFragment:Fragment() {

    lateinit var binding:FragmentScanStockBinding
    private val viewModel by viewModels<ScanStockViewModel>()
    private lateinit var loading:AlertDialog
    private lateinit var barlauncer: Any

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentScanStockBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loading=requireContext().getAlertDialog()
        barlauncer = this.getBarLauncher(requireContext()) {
            binding.edtStockCode.setText(it)
            viewModel.getProductId(it)
        }
        binding.textInputLayoutStockCode.setEndIconOnClickListener {
            this.scanQrCode(requireContext(), barlauncer)
        }
        binding.edtStockCode.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    // Perform action on key press
                    viewModel.getProductId(binding.edtStockCode.text.toString())
                    hideKeyboard(activity, binding.edtStockCode)
                    return true
                }
                return false
            }
        })
        binding.btnContinue.setOnClickListener {
            view.findNavController().navigate(ScanStockFragmentDirections.actionScanStockFragmentToExchangeOrderFragment(viewModel.productInfoList.toTypedArray()))
        }

        val adapter = StockCodeRecyclerAdapter{
            findNavController().navigate(ScanStockFragmentDirections.actionScanStockFragmentToStockDetailFragment(it))
        }
        binding.rvStockCodeItem.adapter = adapter

        viewModel.productIdLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    viewModel.getProductInfo(it.data.orEmpty())
                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.productInfoLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    viewModel.productInfoList.add(it.data!!.asUiModel())
                    adapter.submitList(viewModel.productInfoList.toSet().toList())
                    viewModel.resetProductInfoLiveData()
                    adapter.notifyDataSetChanged()
                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

    }
}