package com.shwemigoldshop.shwemisale.screen.goldFromHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.shwemigoldshop.shwemisale.databinding.DialogStockCheckBinding
import com.shwemigoldshop.shwemisale.screen.sellModule.StockCheckRecyclerAdapter
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StocksInVoucherDialogFragment(private val scannedVoucher:String):DialogFragment() {
    private var _binding : DialogStockCheckBinding?=null
    val binding:DialogStockCheckBinding
        get() = _binding!!

    private val viewModel by viewModels<GoldFromHomeViewModel>()
    private lateinit var loading: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DialogStockCheckBinding.inflate(inflater).also {
            _binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        this.isCancelable = false
        viewModel.getStockWeightByVoucher(scannedVoucher)
        binding.tvVoucherNumber.text = scannedVoucher
        val adapter = StockCheckRecyclerAdapter{data ->
            viewModel.getStockInfoByVoucher(scannedVoucher, listOf(data.id))
            viewModel.getStockWeightByVoucher(scannedVoucher)
        }
        binding.rvStockCheck.adapter = adapter

        viewModel.stockWeightByVoucherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    var itemList = it.data.orEmpty().toMutableList()
                    var productIdList = it.data?.map { it.id }.orEmpty().toMutableList()
                    var commonIdList = productIdList.intersect(viewModel.checkedProductIdFromVoucher.toSet()).toList()
                    productIdList.removeAll(commonIdList.toSet())
                    adapter.submitList(itemList.filter { productIdList.contains(it.id) })

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.ivClose.setOnClickListener {
            this.dismiss()
        }


        binding.btnContinue.setOnClickListener {
//            val productIdList =
//                viewModel.stockWeightByVoucherLiveData.value!!.data!!.filter { it.isChecked }
//                    .map { it.id }
//            viewModel.getStockInfoByVoucher(scannedVoucher, productIdList)
            this.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}