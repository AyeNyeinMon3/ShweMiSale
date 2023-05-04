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
import com.example.shwemisale.StockCodeRecyclerAdapter
import com.example.shwemisale.data_layers.domain.product.asUiModel
import com.example.shwemisale.databinding.FragmentScanStockBinding
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanStockFragment : Fragment() {

    lateinit var binding: FragmentScanStockBinding
    private val viewModel by viewModels<ScanStockViewModel>()
    private lateinit var loading: AlertDialog
    private lateinit var barlauncer: Any
    private var stockGoldPrice = 0

    private var isContinuable = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentScanStockBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        repeat(viewModel.productInfoList.distinctBy { it.id }.size){
            viewModel.getProductInfo(viewModel.productInfoList[it].id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
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
            if (isContinuable) {
                view.findNavController().navigate(
                    ScanStockFragmentDirections.actionScanStockFragmentToExchangeOrderFragment(
                        viewModel.productInfoList.toTypedArray(),
                        if (stockGoldPrice == 0) null else stockGoldPrice.toString()
                    )
                )
            } else {
                Toast.makeText(requireContext(), "Scan At least one Product", Toast.LENGTH_LONG)
                    .show()
            }
        }

        val adapter = StockCodeRecyclerAdapter({
            findNavController().navigate(
                ScanStockFragmentDirections.actionScanStockFragmentToStockDetailFragment(
                    it
                )
            )
        }, {
            viewModel.deleteItem(it)
            viewModel.removeTemp(it)
        })

        binding.rvStockCodeItem.adapter = adapter

        viewModel.productIdLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    viewModel.getProductInfo(it.data.orEmpty())
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.productInfoScanLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.addProduct(it.data!!.asUiModel())

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    stockGoldPrice = it.data!![0].price?.toInt() ?: 0
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.productInfoLiveData.observe(viewLifecycleOwner) {
            isContinuable = viewModel.productInfoList.isNotEmpty()
            if (viewModel.productInfoList.map { it.gold_type_id }.toSet().toList().size == 1) {
                viewModel.getGoldTypePrice(viewModel.productInfoList[0].gold_type_id)
            } else {
                stockGoldPrice = 0
            }
            adapter.submitList(viewModel.productInfoList.distinctBy { it.id })
            adapter.notifyDataSetChanged()
        }

    }
}