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
import androidx.navigation.fragment.navArgs
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
    private val args by navArgs<SellGoldFromHomeFragmentArgs>()


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
        if (args.backpressType == "Global"){
            binding.layoutPayment.isVisible = false
            binding.radioGpOther.isVisible = false
            binding.btnContinue.isVisible = false
            binding.btnSkip.isVisible = false
            binding.btnDone.isVisible = true

            binding.btnDone.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        viewModel.getStockFromHomeList()
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

        viewModel.stockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    val adapter = GoldFromHomeRecyclerAdapter({ data ->
                        findNavController().navigate(
                            SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToSellResellStockInfoAddedFragment(
                                data,
                                it.data?.toTypedArray()
                            )
                        )
                    }, {
                        viewModel.deleteStock(it)
                    })
                    binding.rvGoldFromHome.adapter = adapter
                    adapter.submitList(it.data)
                    var totalPawnPrice = 0
                    var totalGoldWeightYwae = 0.0
                    var totalBVoucherBuyingPrice = 0

                    it.data.orEmpty().forEach {
                        totalPawnPrice += it.calculated_for_pawn!!.toInt()
                        totalGoldWeightYwae += it.gold_weight_ywae!!.toDouble()
                        totalBVoucherBuyingPrice += it.b_voucher_buying_value!!.toInt()
                    }
                    viewModel.saveTotalPawnPrice(totalPawnPrice.toString())
                    viewModel.saveTotalGoldWeightYwae(totalGoldWeightYwae.toString())
                    viewModel.saveTotalCVoucherBuyingPrice(totalBVoucherBuyingPrice.toString())

                    binding.edtCalculateTotalPawnPrice.setText(totalPawnPrice.toString())
                    binding.edtVoucherPurchasePayment.setText(totalBVoucherBuyingPrice.toString())
                    val  totalGoldWeightKpy= getKPYFromYwae(totalGoldWeightYwae)
                    binding.editGoldWeightK.setText(totalGoldWeightKpy[0].toInt().toString())
                    binding.editGoldWeightP.setText(totalGoldWeightKpy[1].toInt().toString())
                    binding.editGoldWeightY.setText(totalGoldWeightKpy[2].let { String.format("%.2f", it) })
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.stockFromHomeInfoInVoucherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    it.data!!.forEach {
                        viewModel.createStockFromHome(
                            a_buying_price = it.a_buying_price,
                            b_voucher_buying_value = it.b_voucher_buying_value,
                            c_voucher_buying_price = it.c_voucher_buying_price,
                            calculated_buying_value = it.calculated_buying_value,
                            calculated_for_pawn = it.calculated_for_pawn,
                            d_gold_weight_ywae = it.d_gold_weight_ywae,
                            e_price_from_new_voucher = it.e_price_from_new_voucher,
                            f_voucher_shown_gold_weight_ywae = it.f_voucher_shown_gold_weight_ywae,
                            gem_value = it.gem_value,
                            gem_weight_details_qty = null,
                            gem_weight_details_gm = null,
                            gem_weight_details_ywae = null,
                            gem_weight_ywae = it.gem_weight_ywae,
                            gold_weight_ywae = it.gold_weight_ywae,
                            gold_gem_weight_ywae = it.gold_gem_weight_ywae,
                            gq_in_carat = it.gq_in_carat,
                            has_general_expenses = it.has_general_expenses,
                            imageId = it.image?.id,
                            imageFile = null,
                            impurities_weight_ywae = it.impurities_weight_ywae,
                            maintenance_cost = it.maintenance_cost,
                            price_for_pawn = it.price_for_pawn,
                            pt_and_clip_cost = it.pt_and_clip_cost,
                            qty = it.qty,
                            rebuy_price = it.rebuy_price,
                            size = it.size,
                            stock_condition = it.stock_condition,
                            stock_name = it.stock_name,
                            type = it.type,
                            wastage_ywae = it.wastage_ywae,
                            rebuy_price_vertical_option = it.rebuy_price_vertical_option
                        )
                    }

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.createStockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.getStockFromHomeList()

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
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
                        null,
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
