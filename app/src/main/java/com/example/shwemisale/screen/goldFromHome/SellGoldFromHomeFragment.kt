package com.example.shwemisale.screen.goldFromHome

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.hideKeyboard
import com.example.shwemisale.data_layers.domain.goldFromHome.asUiModel
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel
import com.example.shwemisale.databinding.DialogChangeFeatureBinding
import com.example.shwemisale.databinding.DialogSellTypeBinding
import com.example.shwemisale.databinding.DialogStockCheckBinding
import com.example.shwemisale.databinding.FragmentGoldFromHomeSellBinding
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import com.example.shwemisale.screen.sellModule.GoldFromHomeData
import com.example.shwemisale.screen.sellModule.GoldFromHomeRecyclerAdapter
import com.example.shwemisale.screen.sellModule.StockCheckRecyclerAdapter
import com.example.shwemisale.screen.sellModule.sellStart.SellStartViewModel

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellGoldFromHomeFragment : Fragment() {

    lateinit var binding: FragmentGoldFromHomeSellBinding
    lateinit var alertDialogBinding: DialogStockCheckBinding
    lateinit var dialogBinding: DialogChangeFeatureBinding
    lateinit var dialogSellTypeBinding: DialogSellTypeBinding
    private val viewModel by viewModels<GoldFromHomeViewModel>()
    private lateinit var loading: AlertDialog
    private lateinit var barlauncer: Any


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentGoldFromHomeSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
//            binding.btnSkip.isVisible = isChecked
//        }
        loading = requireContext().getAlertDialog()
        barlauncer = this.getBarLauncher(requireContext()) {
            binding.edtScanVoucher.setText(it)
            viewModel.getStockWeightByVoucher(it)
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
                    // Perform action on key press
                    viewModel.getStockWeightByVoucher(binding.edtScanVoucher.text.toString())
                    hideKeyboard(activity, binding.edtScanVoucher)
                    return true
                }
                return false
            }
        })

        binding.btnSelect.setOnClickListener {
            viewModel.getStockWeightByVoucher(binding.edtScanVoucher.text.toString())
        }
        val adapter = GoldFromHomeRecyclerAdapter({
            findNavController().navigate(
                SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToSellResellStockInfoAddedFragment(
                    it
                )
            )
        }, {
            viewModel.deleteStock(it)
        })
        binding.rvGoldFromHome.adapter = adapter
        viewModel.getStockFromHomeListFromAppdatabase()
        if (viewModel.stockFromHomeListInAppDatabase.isNotEmpty()) {
            adapter.submitList(viewModel.stockFromHomeListInAppDatabase)
            var finalPawnPrice = 0
            viewModel.stockFromHomeListInAppDatabase.map { it.calculatedPriceForPawn?:"0" }.forEach {
                finalPawnPrice += it.let { if (it.isEmpty()) 0 else it.toInt() }
            }
            var finalGoldWeightY = 0.0
            viewModel.stockFromHomeListInAppDatabase.map { it.oldStockDGoldWeightY?:"0.0" }.forEach {
                finalGoldWeightY += it.let { if (it.isEmpty()) 0.0 else it.toDouble() }
            }
            var finalVoucherPaidAmount = 0
            viewModel.stockFromHomeListInAppDatabase.map { it.oldStockc_voucher_buying_value?:"0" }.forEach {
                finalVoucherPaidAmount += it.let { if (it.isEmpty()) 0 else it.toInt() }
            }
            viewModel.saveStockFromHomeInfoFinal(
                finalPawnPrice.toString(),
                finalGoldWeightY.toString(),
                finalVoucherPaidAmount.toString()
            )

            val finalItem = viewModel.getStockFromHomeInfoFinal()
                val goldWeightKpy = getKPYFromYwae(finalItem.finalGoldWeightY.let { if (it.isEmpty()) 0.0 else it.toDouble() })
                binding.editGoldWeightK.setText(goldWeightKpy[0].toInt().toString())
                binding.editGoldWeightP.setText(goldWeightKpy[1].toInt().toString())
                binding.editGoldWeightY.setText(goldWeightKpy[2].let {
                    String.format(
                        "%.2f",
                        it
                    )
                })
                binding.edtCalculatePledgeMoney.setText(finalItem.finalPawnPrice)
                binding.edtVoucherPurchasePayment.setText(finalItem.finalVoucherPaidAmount)
        }
        viewModel.stockWeightByVoucherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    showStockCheckDialog(it.data!!)

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.stockInfoByVoucherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    adapter.submitList(it.data!!)
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.radioGpOther.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                binding.radioSale.id -> {
                    binding.btnSkip.isVisible = true
                    binding.btnContinue.isVisible = true
                    binding.btnPrint.isVisible = false
                    binding.btnContinue.setOnClickListener {
                        showSellTypeDialog()
                    }
                }
                binding.radioPawn.id -> {
                    binding.btnSkip.isVisible = false
                    binding.btnContinue.isVisible = true
                    binding.btnPrint.isVisible = false
                    binding.btnContinue.setOnClickListener {
                        view.findNavController()
                            .navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToCreatePawnFragment())
                    }
                }
                binding.radioBuy.id -> {
                    binding.btnSkip.isVisible = false
                    binding.btnContinue.isVisible = false
                    binding.btnPrint.isVisible = true
                }
            }
        }

        binding.btnSkip.setOnClickListener {
            showSellTypeDialog()
        }



        binding.btnAdd.setOnClickListener {
            view.findNavController()
                .navigate(
                    SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToSellResellStockInfoAddedFragment(
                        null
                    )
                )
        }
    }


    fun showStockCheckDialog(list: List<StockWeightByVoucherUiModel>) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        alertDialogBinding =
            DialogStockCheckBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(alertDialogBinding.root)
        val alertDialog = builder.create()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        alertDialog.setCancelable(false)
        alertDialogBinding.tvVoucherNumber.text = binding.edtScanVoucher.text.toString()
        val adapter = StockCheckRecyclerAdapter()
        alertDialogBinding.rvStockCheck.adapter = adapter
        adapter.submitList(list)
        alertDialogBinding.btnContinue.setOnClickListener {
            binding.rvGoldFromHome.visibility = View.VISIBLE
            alertDialog.dismiss()
        }
        alertDialogBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }


        alertDialogBinding.btnContinue.setOnClickListener {
            val productIdList =
                viewModel.stockWeightByVoucherLiveData.value!!.data!!.filter { it.isChecked }
                    .map { it.id }
            viewModel.getStockInfoByVoucher(binding.edtScanVoucher.text.toString(), productIdList)
            viewModel.resetstockWeightByVoucherLiveData()
            alertDialog.dismiss()
        }

        alertDialog.show()


    }

    fun showChangeFeatureDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogBinding =
            DialogChangeFeatureBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
//        dialogBinding.btnPawn.setOnClickListener {
//            view?.findNavController()?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToPawnStartFragment())
//            alertDialog.dismiss()
//        }
//        dialogBinding.btnBuy.setOnClickListener {
//            view?.findNavController()?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToBuyStartFragment())
//            alertDialog.dismiss()
//        }

        alertDialog.show()


    }

    fun showSellTypeDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogSellTypeBinding =
            DialogSellTypeBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogSellTypeBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogSellTypeBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()


        dialogSellTypeBinding.btnNormalSell.setOnClickListener {
            view?.findNavController()
                ?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToScanStockFragment())
            alertDialog.dismiss()
        }
        dialogSellTypeBinding.btnReceiveNewOrder.setOnClickListener {
            view?.findNavController()
                ?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToReceiveNewOrderFragment())
            alertDialog.dismiss()
        }
        dialogSellTypeBinding.btnAkoukSell.setOnClickListener {
            view?.findNavController()
                ?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToAkoukSellFragment())
            alertDialog.dismiss()
        }

        dialogSellTypeBinding.btnGeneralSell.setOnClickListener {
            view?.findNavController()
                ?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToGeneralSellFragment())
            alertDialog.dismiss()
        }

    }

}
