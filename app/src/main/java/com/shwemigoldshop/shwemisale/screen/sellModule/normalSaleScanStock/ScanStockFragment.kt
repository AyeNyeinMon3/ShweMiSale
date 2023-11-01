package com.shwemigoldshop.shwemisale.screen.sellModule.normalSaleScanStock

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
import androidx.navigation.fragment.navArgs
import com.shwemigoldshop.shwemisale.screen.sellModule.StockCodeRecyclerAdapter
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.util.hideKeyboard
import com.shwemigoldshop.shwemisale.data_layers.domain.product.asUiModel
import com.shwemigoldshop.shwemisale.databinding.FragmentScanStockBinding
import com.shwemigoldshop.shwemisale.qrscan.getBarLauncher
import com.shwemigoldshop.shwemisale.qrscan.scanQrCode
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanStockFragment : Fragment() {

    lateinit var binding: FragmentScanStockBinding
    private val viewModel by viewModels<ScanStockViewModel>()
    private lateinit var loading: AlertDialog
    private val args by navArgs<com.shwemigoldshop.shwemisale.screen.sellModule.normalSaleScanStock.ScanStockFragmentArgs>()
    private lateinit var barlauncer: Any
    private var stockGoldPrice = 0
    private var orderSaleGoldPrice = "0"
    private var snackBar: Snackbar? = null

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
        repeat(viewModel.productInfoList.distinctBy { it.id }.size) {
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
                    com.shwemigoldshop.shwemisale.screen.sellModule.normalSaleScanStock.ScanStockFragmentDirections.actionScanStockFragmentToExchangeOrderFragment(
                        viewModel.productInfoList.toTypedArray(),
                        if (stockGoldPrice == 0) null else stockGoldPrice.toString(),
                        args.scannedSalesCodeList,
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
                    snackBar?.dismiss()
                    val bonus = if (it.data?.bonus.isNullOrEmpty()) "0" else it.data!!.bonus
                    snackBar = Snackbar.make(
                        binding.root,
                        "${it.data?.code}'s BS : $bonus",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar?.show()
                    if (it.data?.is_order_sale == "1") {
                        if (viewModel.productInfoList.any { product -> product.is_order_sale != "1" } ||
                            viewModel.productInfoList.any{product-> product.order_sale_code != it.data?.order_sale_code}) {
                            Toast.makeText(
                                requireContext(),
                                "If order sale is scanned only order sales in same voucher can be added",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            orderSaleGoldPrice = it.data?.order_sale_gold_price.orEmpty()
                            viewModel.addProduct(it.data!!.asUiModel())
                        }
                    } else if (viewModel.productInfoList.any { product -> product.is_order_sale == "1" }) {
                        Toast.makeText(
                            requireContext(),
                            "If order sale is scanned only order sales in same voucher can be added",
                            Toast.LENGTH_LONG
                        ).show()
                    }else{
                        viewModel.addProduct(it.data!!.asUiModel())
                    }

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
                    stockGoldPrice =
                        if (viewModel.productInfoList.filter { it.is_order_sale == "1" }
                                .isNotEmpty()) {
                            orderSaleGoldPrice.toInt()
                        } else {
                            it.data!![0].price?.toInt() ?: 0
                        }
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