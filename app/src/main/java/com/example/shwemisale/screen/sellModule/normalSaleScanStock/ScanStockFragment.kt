package com.example.shwemisale.screen.sellModule.normalSaleScanStock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemisale.StockCodeData
import com.example.shwemisale.StockCodeRecyclerAdapter
import com.example.shwemisale.data_layers.domain.product.asUiModel
import com.example.shwemisale.databinding.FragmentScanStockBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanStockFragment:Fragment() {

    lateinit var binding:FragmentScanStockBinding
    private val viewModel by viewModels<ScanStockViewModel>()
    private lateinit var loading:AlertDialog
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
        binding.btnContinue.setOnClickListener {
            view.findNavController().navigate(ScanStockFragmentDirections.actionScanStockFragmentToExchangeOrderFragment(viewModel.productInfoList.toTypedArray()))
        }

        val adapter = StockCodeRecyclerAdapter()
        binding.rvStockCodeItem.adapter = adapter
        viewModel.productInfoLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    viewModel.productInfoList.add(it.data!!.asUiModel())
                    adapter.submitList(viewModel.productInfoList)
                    adapter.notifyDataSetChanged()
                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}