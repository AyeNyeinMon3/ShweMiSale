package com.shwemigoldshop.shwemisale.screen.goldFromHome

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.epson.epos2.Epos2CallbackCode
import com.epson.epos2.Epos2Exception
import com.epson.epos2.printer.Printer
import com.epson.epos2.printer.PrinterStatusInfo
import com.epson.epos2.printer.ReceiveListener
import com.shwemigoldshop.shwemisale.util.*
import com.shwemigoldshop.shwemisale.data_layers.dto.printing.RebuyPrintItem
import com.shwemigoldshop.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel
import com.shwemigoldshop.shwemisale.databinding.DialogChangeFeatureBinding
import com.shwemigoldshop.shwemisale.databinding.DialogSellTypeBinding
import com.shwemigoldshop.shwemisale.databinding.DialogStockCheckBinding
import com.shwemigoldshop.shwemisale.databinding.FragmentGoldFromHomeSellBinding
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.qrscan.getBarLauncher
import com.shwemigoldshop.shwemisale.qrscan.scanQrCode
import com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket.BucketShareViewModel
import com.shwemigoldshop.shwemisale.screen.sellModule.GoldFromHomeRecyclerAdapter
import com.shwemigoldshop.shwemisale.screen.sellModule.StockCheckRecyclerAdapter
import com.shwemigoldshop.shwemisale.util.calculateLineLength
import com.shwemigoldshop.shwemisale.util.combineLists
import com.shwemigoldshop.shwemisale.util.generateQRCode
import com.shwemigoldshop.shwemisale.util.hideKeyboard

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.util.showSuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import javax.inject.Inject

@AndroidEntryPoint
class SellGoldFromHomeFragment : Fragment(), ReceiveListener {
    @Inject
    lateinit var mPrinter: Printer

    @Inject
    lateinit var localDatabase: LocalDatabase
    private val paperLength = calculateLineLength(80)
    private val magicSpace = "          "
    override fun onDestroy() {
        super.onDestroy()
    }

    lateinit var binding: FragmentGoldFromHomeSellBinding
    lateinit var alertDialogBinding: DialogStockCheckBinding
    lateinit var dialogBinding: DialogChangeFeatureBinding
    lateinit var dialogSellTypeBinding: DialogSellTypeBinding
    private val viewModel by viewModels<GoldFromHomeViewModel>()
    private val oldStockBucketSharedViewModel by activityViewModels<BucketShareViewModel>()
    private lateinit var loading: AlertDialog
    private lateinit var barlauncer: Any
    private val args by navArgs<com.shwemigoldshop.shwemisale.screen.goldFromHome.SellGoldFromHomeFragmentArgs>()
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

    override fun onPtrReceive(p0: Printer?, p1: Int, p2: PrinterStatusInfo?, p3: String?) {
        lifecycleScope.launchWhenStarted {
            if (p1 == Epos2CallbackCode.CODE_SUCCESS) {
//Displays successful print messages

                mPrinter.clearCommandBuffer()
                mPrinter.setReceiveEventListener(null)
                Toast.makeText(requireContext(), "Print Receive Success", Toast.LENGTH_LONG).show()
            } else {
//Displays error messages
                Toast.makeText(requireContext(), "Print Receive Fail", Toast.LENGTH_LONG).show()

            }
        }
        Thread {
            //Abort process
//            disconnectPrinter()
        }.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
//            binding.btnSkip.isVisible = isChecked
//        }
        loading = requireContext().getAlertDialog()

        // delete filled data items in bucket
        oldStockBucketSharedViewModel.removeDataFilledItems()

        mPrinter.setReceiveEventListener(this)
        localDatabase.removeGemWeightDetailSessionKey()
        if (args.backpressType == "Global") {
            binding.layoutPayment.isVisible = false
            binding.radioGpOther.isVisible = false
            binding.btnContinue.isVisible = false
            binding.btnSkip.isVisible = false
            binding.btnDone.isVisible = true
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
            binding.layoutPayment.isVisible = true
            binding.radioGpOther.isVisible = false
            binding.btnContinue.isVisible = false
            binding.btnSkip.isVisible = false
            binding.btnDone.isVisible = true
            if (args.backpressType == "PawnNewCanEdit" || args.backpressType == "PawnSelect") {
                viewModel.getStockFromHomeList(
                    args.backpressType.startsWith("Pawn"),
                    null
                )
            } else {
                viewModel.getStockFromHomeForPawnList(args.pawnVoucherCode.orEmpty(),false)
            }

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

                    if (args.backpressType == "PawnSelectNoEdit"){
                        viewModel.getStockFromHomeForPawnList(args.pawnVoucherCode.orEmpty(),false)
                    }else{
                        viewModel.getStockFromHomeList(
                            args.backpressType.startsWith("Pawn"),
                            null
                        )
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

        viewModel.pawnStockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    adapter = GoldFromHomeRecyclerAdapter(args.backpressType, { data ->
                        findNavController().navigate(
                            com.shwemigoldshop.shwemisale.screen.goldFromHome.SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToOldStockDetailFragment(
                                data,
                                args.backpressType,
                                null
                            )
                        )
                    }, {
                        viewModel.deleteStock(
                            it.id.toString()
                        )
                    }, { id, isChecked ->
                        viewModel.updateStockFromHome(isChecked, id)
                    })
                    binding.rvGoldFromHome.adapter = adapter
                    adapter.submitList(it.data)
                    binding.btnDone.setOnClickListener { view ->

                        if (args.backpressType.startsWith("Pawn") && it.data.isNullOrEmpty()
                                .not()
                        ) {
                            if (it.data.orEmpty().size == it.data?.filter { it.isChecked }
                                    .orEmpty().size &&
                                args.backpressType == "PawnSelectNoEdit"
                            ) {
                                Toast.makeText(
                                    requireContext(),
                                    "You can't check all items",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                var totalVoucherBuyingPriceForPawn = 0
                                var totalPawnPriceForRemainedPawnItems = 0
                                it.data.orEmpty().forEach {
                                    if (it.isChecked) {
                                        totalVoucherBuyingPriceForPawn += it.b_voucher_buying_value!!.toInt()
                                    } else {
                                        totalPawnPriceForRemainedPawnItems += it.calculated_for_pawn!!.toInt()
                                    }
                                }
                                viewModel.saveVoucherBuyingPriceForPawn(
                                    totalVoucherBuyingPriceForPawn.toString()
                                )
                                viewModel.savePawnPriceForRemainedPawnItems(
                                    totalPawnPriceForRemainedPawnItems.toString()
                                )
                            }
                        }
                        if (args.pawnVoucherCode.isNullOrEmpty().not()) {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                "key",
                                it.data.orEmpty().filter { it.isChecked }.map { it.id })
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

        viewModel.logoutLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Done") {
                        findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections.actionGlobalLogout())
                    }

//                    Toast.makeText(requireContext(),"log out successful", Toast.LENGTH_LONG).show()
                }

                is Resource.Error -> {
                    loading.dismiss()
                    findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections.actionGlobalLogout())

                }

                else -> {}
            }
        }

        viewModel.stockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    var productIdList = mutableListOf<String>()
                    it.data?.map { it.productId }?.forEach {
                        productIdList.addAll(it.orEmpty())
                    }
                    viewModel.checkedProductIdFromVoucher = productIdList
                    var idCount = 0
                    it.data!!.forEach {
                        if (it.id == null) it.localId =
                            (idCount++ + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).toInt()
                    }
                    adapter = GoldFromHomeRecyclerAdapter(args.backpressType, { data ->
                        findNavController().navigate(
                            com.shwemigoldshop.shwemisale.screen.goldFromHome.SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToOldStockDetailFragment(
                                data,
                                args.backpressType,
                                null
                            )
                        )
                    }, {
                        viewModel.deleteStock(
                            it.id.toString()
                        )
                    }, { id, isChecked ->
                        viewModel.updateStockFromHome(isChecked, id)
                    })
                    binding.rvGoldFromHome.adapter = adapter
                    adapter.submitList(it.data)
                    if (it.data.isNullOrEmpty()) {
                        localDatabase.removeStockFromHomeSessionKey()
                    }
                    //pawn
                    binding.btnDone.setOnClickListener { view ->


                        if (args.backpressType.startsWith("Pawn") && it.data.isNullOrEmpty()
                                .not()
                        ) {
                            if (it.data.orEmpty().size == it.data?.filter { it.isChecked }
                                    .orEmpty().size &&
                                args.backpressType == "PawnSelectNoEdit"
                            ) {
                                Toast.makeText(
                                    requireContext(),
                                    "You can't check all items",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                var totalVoucherBuyingPriceForPawn = 0
                                var totalPawnPriceForRemainedPawnItems = 0
                                it.data.orEmpty().forEach {
                                    if (it.isChecked) {
                                        totalVoucherBuyingPriceForPawn += it.b_voucher_buying_value!!.toInt()
                                    } else {
                                        totalPawnPriceForRemainedPawnItems += it.calculated_for_pawn!!.toInt()
                                    }
                                }
                                viewModel.saveVoucherBuyingPriceForPawn(
                                    totalVoucherBuyingPriceForPawn.toString()
                                )
                                viewModel.savePawnPriceForRemainedPawnItems(
                                    totalPawnPriceForRemainedPawnItems.toString()
                                )
                            }

                        }
                        findNavController().popBackStack()

                    }

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
                    viewModel.saveTotalBVoucherBuyingPrice(totalBVoucherBuyingPrice.toString())

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

                        }, { id, isChecked ->

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

        viewModel.stockFromHomeInfoInVoucherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    it.data?.forEach { oldStock ->
                        viewModel.createStockFromHome(
                            oldStock.image.id,
                            oldStock.a_buying_price.orEmpty(),
                            oldStock.b_voucher_buying_value.orEmpty(),
                            oldStock.c_voucher_buying_price.orEmpty(),
                            oldStock.calculated_buying_value.orEmpty(),
                            oldStock.calculated_for_pawn.orEmpty(),
                            oldStock.d_gold_weight_ywae.orEmpty(),
                            oldStock.e_price_from_new_voucher.orEmpty(),
                            oldStock.f_voucher_shown_gold_weight_ywae.orEmpty(),
                            oldStock.gem_value.orEmpty(),
                            oldStock.gem_weight_ywae.orEmpty(),
                            oldStock.gold_gem_weight_ywae.orEmpty(),
                            oldStock.gold_weight_ywae.orEmpty(),
                            oldStock.gq_in_carat.orEmpty(),
                            oldStock.has_general_expenses.orEmpty(),

                            oldStock.impurities_weight_ywae.orEmpty(),
                            oldStock.maintenance_cost.orEmpty(),
                            oldStock.price_for_pawn.orEmpty(),
                            oldStock.pt_and_clip_cost.orEmpty(),
                            oldStock.qty.orEmpty(),
                            oldStock.rebuy_price.orEmpty(),
                            oldStock.size.orEmpty(),
                            oldStock.stock_condition.orEmpty(),
                            oldStock.stock_name.orEmpty(),
                            oldStock.type.orEmpty(),
                            oldStock.wastage_ywae.orEmpty(),
                            oldStock.rebuy_price_vertical_option.orEmpty(),
                            productIdList = oldStock.productId,
                            isEditable = oldStock.isEditable,
                            isChecked = oldStock.isChecked,
                            isPawn = false
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
                    var itemList = it.data.orEmpty().toMutableList()
                    var productIdList = it.data?.map { it.id }.orEmpty().toMutableList()
                    var commonIdList = productIdList.intersect(viewModel.checkedProductIdFromVoucher.toSet()).toList()
                    productIdList.removeAll(commonIdList.toSet())

                    showStockCheckDialog(itemList.filter { productIdList.contains(it.id) })

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
                    viewModel.printRebuy(it.data.orEmpty())
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.printRebuyLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    if (mPrinter.status.connection == Printer.FALSE) {
                        try {
                            mPrinter.connect("TCP:" + localDatabase.getPrinterIp(), Printer.PARAM_DEFAULT)
                        } catch (e: Epos2Exception) {
                            //Cannot Connect to Printer IP : ${localDatabase.getPrinterIp()}
                            showErrorDialog(e.message ?: "Cannot Connect to Printer IP : ${localDatabase.getPrinterIp()}")
                        }
                    }else if (mPrinter.status.connection == Printer.TRUE){
                        Toast.makeText(requireContext(),"Printer Connect Success",Toast.LENGTH_LONG).show()
                    }
                    printSample(
                        it.data?.sold_at.orEmpty(),
                        it.data?.code.orEmpty(),
                        it.data?.user?.address.orEmpty(),
                        it.data?.salesperson.orEmpty(),
                        it.data?.user?.name.orEmpty(),
                        (it.data?.total_cost ?: 0).toString(),
                        it.data?.items.orEmpty()
                    )
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
                            .navigate(com.shwemigoldshop.shwemisale.screen.goldFromHome.SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToCreatePawnFragment())
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
//            view.findNavController()
//                .navigate(
//                    SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToSellResellStockInfoAddedFragment(
//                        null,
//                        args.backpressType
//                    )
//                )
            findNavController().navigate(com.shwemigoldshop.shwemisale.screen.goldFromHome.SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToOldStockBucketListFragment())
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
                    com.shwemigoldshop.shwemisale.screen.goldFromHome.SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToScanStockFragment(
                        scannedCodesList.toTypedArray()
                    )
                )
            alertDialog.dismiss()
        }
        dialogSellTypeBinding.btnReceiveNewOrder.setOnClickListener {
            view?.findNavController()
                ?.navigate(com.shwemigoldshop.shwemisale.screen.goldFromHome.SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToReceiveNewOrderFragment())
            alertDialog.dismiss()
        }
        dialogSellTypeBinding.btnAkoukSell.setOnClickListener {
            view?.findNavController()
                ?.navigate(com.shwemigoldshop.shwemisale.screen.goldFromHome.SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToAkoukSellFragment())
            alertDialog.dismiss()
        }

        dialogSellTypeBinding.btnGeneralSell.setOnClickListener {
            view?.findNavController()
                ?.navigate(com.shwemigoldshop.shwemisale.screen.goldFromHome.SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToGeneralSellFragment())
            alertDialog.dismiss()
        }

    }

    private fun printSample(
        date: String,
        voucherNumber: String,
        address: String,
        salesPerson: String,
        customerName: String,
        totalRebuyPrice: String,
        itemList: List<RebuyPrintItem>
    ) {
        try {
            // Start the print job
            mPrinter.beginTransaction()
            val lineLength = paperLength
            var numSpaces = 0
            var spaces = ""

            mPrinter?.addText("-------------------------------------------------\n")
            mPrinter?.addTextAlign(Printer.ALIGN_CENTER)
            mPrinter?.addText("Rebuy Voucher\n")
            mPrinter?.addText("-------------------------------------------------\n")


            // Print an image
            mPrinter?.addTextAlign(Printer.ALIGN_LEFT)
            mPrinter?.addText("Date")

            numSpaces = lineLength - date.length - "Date".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces$date\n")

            mPrinter?.addText("Voucher Number")

            numSpaces =
                lineLength - voucherNumber.length - "Voucher Number".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces$voucherNumber\n")


            val qrCodeContent = voucherNumber // Replace with your desired content

            val qrCodeWidth = 150 // Adjust the size based on your requirements

            val qrCodeHeight = 150
            val qrCodeBitmap = generateQRCode(qrCodeContent, qrCodeWidth, qrCodeHeight)
            mPrinter?.addTextAlign(Printer.ALIGN_RIGHT)
            mPrinter?.addImage(
                qrCodeBitmap,
                0,
                0,
                qrCodeWidth,
                qrCodeHeight,
                Printer.PARAM_DEFAULT,
                Printer.PARAM_DEFAULT,
                Printer.PARAM_DEFAULT,
                Printer.PARAM_DEFAULT.toDouble(),
                Printer.PARAM_DEFAULT
            );

            mPrinter?.addText("\n")
            mPrinter?.addTextAlign(Printer.ALIGN_LEFT)
            mPrinter?.addText("Customer Name")

            numSpaces =
                lineLength - customerName.length - "Customer Name".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces$customerName\n")

            mPrinter?.addText("Address")
            numSpaces = lineLength - address.length - "Address".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces$address\n")
            mPrinter?.addText("------------------------------------------\n")

            val data1 = listOf("necklace,earrings", "1K 2P 3Y", "9000000", "1033500")
            val data2 = listOf("ring", "1K 2P 3Y", "900000", "1033500")
            val dataList = listOf(data1, data2)
            val columnWidths = listOf(10, 10, 10, 10)
            printTableRowWithFixPosition(itemList, columnWidths, mPrinter)


            // Replace mPrinter with your Printer object

//            printTableRow("Item Name","Gold Weight","Price","Rebuy Price")
//
//            printTableRow("necklace","1K 2P 3Y","900000","1033500")
//            printTableRow("ring","1K 2P 3Y","900000","1033500")
            mPrinter?.addText("\n")
            mPrinter?.addText("Total Rebuy Price")
            numSpaces =
                lineLength - totalRebuyPrice.length - "Total Rebuy Price Kyats".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces$totalRebuyPrice Kyats\n")
            mPrinter?.addText("-------------------------------------------------\n")


            mPrinter?.addTextAlign(Printer.ALIGN_RIGHT)
            mPrinter?.addText("SalePerson")
            numSpaces = lineLength - salesPerson.length - "SalePerson".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces$salesPerson\n")
            mPrinter?.addFeedLine(9)
            mPrinter?.addCut(Printer.CUT_FEED)
            // End the print
            mPrinter?.sendData(Printer.PARAM_DEFAULT)

        } catch (e: Epos2Exception) {
            e.printStackTrace()
        } finally {
            try {
                // End the transaction
                mPrinter.endTransaction()
                viewModel.logout()
            } catch (e: Epos2Exception) {
                e.printStackTrace()
            }
        }
    }

    fun printTableRowWithFixPosition(
        columns: List<RebuyPrintItem>,
        columnWidths: List<Int>,
        printer: Printer
    ) {
        val headerList = listOf("Item Name", "Gold Weight", "Price", "Rebuy")

        val combineList = combineLists(headerList, columns)
        for (item in combineList) {
            mPrinter?.addTextAlign(Printer.ALIGN_LEFT)
            mPrinter?.addText(item.first + magicSpace)


            val numSpaces = paperLength - item.second.length - item.first.length
            val spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces${item.second}")
            mPrinter?.addText("\n")
            if (item.first == "Rebuy") {
                mPrinter?.addText("-------------------------------------------------\n")
            }
        }

    }
    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("OK") { dialog, which ->
            // do nothing
        }

        val dialog = builder.create()
        dialog.show()
    }
}
