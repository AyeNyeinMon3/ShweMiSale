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
import com.example.shwemi.util.*
import com.example.shwemisale.R
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
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

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
    lateinit var adapter: GoldFromHomeRecyclerAdapter

    private var scannedCodesList = mutableListOf<String>()

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
        binding.btnDone.setOnClickListener {
            if (args.backpressType.startsWith("Pawn") && viewModel.stockFromHomeInfoLiveData.value?.data.isNullOrEmpty().not() ){
             viewModel.updateStockFromHome(viewModel.stockFromHomeInfoLiveData.value?.data.orEmpty())
            }else{
                findNavController().popBackStack()
            }
        }
        if (args.backpressType == "Global") {
            binding.layoutPayment.isVisible = false
            binding.radioGpOther.isVisible = false
            binding.btnContinue.isVisible = false
            binding.btnSkip.isVisible = false
            binding.btnDone.isVisible = true
            binding.btnAdd.isVisible = false

            viewModel.getStockFromHomeList(
                args.backpressType.startsWith("Pawn"),
                null
            )

//            if (args.pawnVoucherCode.isNullOrEmpty().not()) {
//                viewModel.getStockFromHomeForPawnList(args.pawnVoucherCode.orEmpty())
//            } else {
//                viewModel.getStockFromHomeList()
//            }
        } else if (args.backpressType.startsWith("Pawn")) {
            binding.btnAdd.isVisible = args.backpressType == "PawnNewCanEdit"
            binding.layoutPayment.isVisible = false
            binding.radioGpOther.isVisible = false
            binding.btnContinue.isVisible = false
            binding.btnSkip.isVisible = false
            binding.btnDone.isVisible = true
            viewModel.getStockFromHomeList(
                args.backpressType.startsWith("Pawn"),
                null
            )
        } else {
            binding.layoutPayment.isVisible = true
            binding.radioGpOther.isVisible = true
            binding.btnContinue.isVisible = true
            binding.btnSkip.isVisible = true
            binding.btnDone.isVisible = false
            viewModel.getStockFromHomeList(
                args.backpressType.startsWith("Pawn"),
                null
            )
        }


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

        viewModel.updateStockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Success") {
                        if (args.pawnVoucherCode.isNullOrEmpty().not()) {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                "key",
                                viewModel.stockFromHomeInfoLiveData.value?.data.orEmpty().filter { it.isChecked }.map { it.id })
                        }
                        findNavController().popBackStack()
                    }

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.deleteStockLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Delete Success") {
                        viewModel.getStockFromHomeList(
                            args.backpressType.startsWith("Pawn"),
                            args.backpressType
                        )
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.getGoldTypePrice()
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.goldPrice18KId = it.data?.find { it.name == "WG" }?.id.orEmpty()

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.stockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()

                    var idCount = 0
                    it.data!!.forEach {
                        if (it.id == 0) it.id =
                            (idCount++ + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).toInt()
                    }
                    adapter = GoldFromHomeRecyclerAdapter(args.backpressType, { data ->
                        findNavController().navigate(
                            SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToSellResellStockInfoAddedFragment(
                                data,
                                it.data?.toTypedArray(),
                                args.backpressType
                            )
                        )
                    }, {
                        viewModel.deleteStock(
                            it,
                            args.backpressType.startsWith("Pawn"),
                            args.backpressType
                        )
                    })
                    binding.rvGoldFromHome.adapter = adapter
                    adapter.submitList(it.data)
                    var totalPawnPrice = 0
                    var totalGoldWeightYwae = 0.0
                    var totalBVoucherBuyingPrice = 0

                    it.data.orEmpty().forEach {
                        totalPawnPrice += it.calculated_for_pawn!!.toInt()
                        totalGoldWeightYwae += it.f_voucher_shown_gold_weight_ywae!!.toDouble()
                        totalBVoucherBuyingPrice += it.b_voucher_buying_value!!.toInt()
                    }
                    viewModel.saveTotalPawnPrice(totalPawnPrice.toString())
                    viewModel.saveTotalGoldWeightYwae(totalGoldWeightYwae.toString())
                    viewModel.saveTotalCVoucherBuyingPrice(totalBVoucherBuyingPrice.toString())

                    binding.edtCalculateTotalPawnPrice.setText(totalPawnPrice.toString())
                    binding.edtVoucherPurchasePayment.setText(totalBVoucherBuyingPrice.toString())
                    val totalGoldWeightKpy = getKPYFromYwae(totalGoldWeightYwae)
                    binding.editGoldWeightK.setText(totalGoldWeightKpy[0].toInt().toString())
                    binding.editGoldWeightP.setText(totalGoldWeightKpy[1].toInt().toString())
                    binding.editGoldWeightY.setText(totalGoldWeightKpy[2].let {
                        String.format(
                            "%.2f",
                            it
                        )
                    })
                    if (args.pawnVoucherCode.isNullOrEmpty().not()) {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            "key",
                            it.data.orEmpty().filter { it.isChecked }.map { it.id })
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    if (it.message == "Session key not found!") {
                        adapter = GoldFromHomeRecyclerAdapter(args.backpressType, { data ->

                        }, {

                        })
                        binding.rvGoldFromHome.adapter = adapter
                        adapter.submitList(emptyList())
                        viewModel.removeTotalPawnPrice()
                        viewModel.removeTotalGoldWeightYwae()
                        viewModel.removeTotalCVoucherBuyingPrice()

                        binding.edtCalculateTotalPawnPrice.setText("0")
                        binding.edtVoucherPurchasePayment.setText("0")

                        binding.editGoldWeightK.setText("0")
                        binding.editGoldWeightP.setText("0")
                        binding.editGoldWeightY.setText("0")
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
        viewModel.pawnStockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    var idCount = 0
                    it.data!!.forEach {
                        if (it.id == 0) it.id =
                            (idCount++ + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).toInt()
                    }
                    adapter = GoldFromHomeRecyclerAdapter(args.backpressType, { data ->
                        findNavController().navigate(
                            SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToSellResellStockInfoAddedFragment(
                                data,
                                it.data?.toTypedArray(),
                                args.backpressType
                            )
                        )
                    }, {
                        viewModel.deleteStock(
                            it,
                            args.backpressType.startsWith("Pawn"),
                            args.backpressType
                        )
                    })
                    binding.rvGoldFromHome.adapter = adapter
                    adapter.submitList(it.data)
                    var totalPawnPrice = 0
                    var totalGoldWeightYwae = 0.0
                    var totalBVoucherBuyingPrice = 0

                    it.data.orEmpty().forEach {
                        totalPawnPrice += it.calculated_for_pawn!!.toInt()
                        totalGoldWeightYwae += it.f_voucher_shown_gold_weight_ywae!!.toDouble()
                        totalBVoucherBuyingPrice += it.b_voucher_buying_value!!.toInt()
                    }
                    viewModel.saveTotalPawnPrice(totalPawnPrice.toString())
                    viewModel.saveTotalGoldWeightYwae(totalGoldWeightYwae.toString())
                    viewModel.saveTotalCVoucherBuyingPrice(totalBVoucherBuyingPrice.toString())

                    binding.edtCalculateTotalPawnPrice.setText(totalPawnPrice.toString())
                    binding.edtVoucherPurchasePayment.setText(totalBVoucherBuyingPrice.toString())
                    val totalGoldWeightKpy = getKPYFromYwae(totalGoldWeightYwae)
                    binding.editGoldWeightK.setText(totalGoldWeightKpy[0].toInt().toString())
                    binding.editGoldWeightP.setText(totalGoldWeightKpy[1].toInt().toString())
                    binding.editGoldWeightY.setText(totalGoldWeightKpy[2].let {
                        String.format(
                            "%.2f",
                            it
                        )
                    })
                    if (args.pawnVoucherCode.isNullOrEmpty().not()) {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            "key",
                            it.data.orEmpty().map { it.id })
                    }

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
                    viewModel.createStockFromHome(it.data.orEmpty(), false)


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
                    viewModel.getStockFromHomeList(
                        isPawn = args.backpressType.startsWith("Pawn"),
                        args.backpressType
                    )

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
                    scannedCodesList.add(binding.edtScanVoucher.text.toString())
                    showStockCheckDialog(it.data!!)

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.buyStockLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog(it.data.orEmpty()) {
                        findNavController().popBackStack()
                    }

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
                    binding.btnPrint.setOnClickListener {
                        viewModel.buyOldStock()
                    }
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
                        null,
                        args.backpressType
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
                ?.navigate(
                    SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToScanStockFragment(
                        scannedCodesList.toTypedArray()
                    )
                )
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
