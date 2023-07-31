package com.example.shwemisale.screen.pawnInterestModule

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epson.epos2.Epos2Exception
import com.epson.epos2.printer.Printer
import com.example.satoprintertest.AkpDownloader
import com.example.shwemi.util.*
import com.example.shwemisale.data_layers.dto.pawn.asDomain
import com.example.shwemisale.data_layers.dto.printing.PawnedStock
import com.example.shwemisale.data_layers.dto.printing.RebuyPrintItem
import com.example.shwemisale.data_layers.dto.printing.asPrintData
import com.example.shwemisale.databinding.FragmentPawnInterestBinding
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.printerHelper.printPdf
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import com.example.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PawnInterestFragment : Fragment() {

    lateinit var binding: FragmentPawnInterestBinding
    private val viewModel by viewModels<PawnInterestViewModel>()
    private lateinit var barlauncer: Any
    private lateinit var loading: AlertDialog
    private var is_app_functions_allowed = "0"
    private var oldStockIdList: List<String> = emptyList()
    var checkedAction = ""
    var tierDiscount = 0
    private val downloader by lazy { AkpDownloader(requireContext()) }
    @Inject
    lateinit var localDatabase: LocalDatabase

    @Inject
    lateinit var mPrinter: Printer
    private val paperLength = calculateLineLength(80)
    private val magicSpace = "          "

    //radio state saved
    var lastCheckedId = -1


    override fun onResume() {
        super.onResume()
        if (binding.tvLabelClaimMoney.text == "ဘောင်ချာဖွင့်ဝယ်ပေးငွေ") {
            binding.edtClaimMoney.setText(viewModel.getTotalVoucherBuyingPrice())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentPawnInterestBinding.inflate(inflater).also {
            binding = it
        }.root
    }

//    override fun onPause() {
//        super.onPause()
//        binding.radioGroup.setOnCheckedChangeListener(null);
//        binding.radioGroup2.setOnCheckedChangeListener(null);
//    }
//
//    override fun onResume() {
//        super.onResume()
//        binding.radioGroup.setOnCheckedChangeListener(listener1);
//        binding.radioGroup2.setOnCheckedChangeListener(listener2);
//    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()


        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<List<String>>("key")
            ?.observe(viewLifecycleOwner) { result ->
                // the result.
                oldStockIdList = result
            }
        binding.switchApp.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) is_app_functions_allowed = "1" else is_app_functions_allowed = "0"
        }
        barlauncer = this.getBarLauncher(requireContext()) {
            binding.edtScanVoucher.setText(it)
            viewModel.pawnVoucherScan(it)
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
                    viewModel.pawnVoucherScan(binding.edtScanVoucher.text.toString())
                    hideKeyboard(activity, binding.edtScanVoucher)
                    return true
                }
                return false
            }
        })
        binding.radioGroup.setOnCheckedChangeListener(listener1);
        binding.radioGroup2.setOnCheckedChangeListener(listener2);
        /** default select interest pay */
        binding.radioInterestPay.isChecked = true
        binding.includePaymentBox.tilOne.isVisible = true
        binding.includePaymentBox.tvOne.isVisible = true
        binding.includePaymentBox.tvOne.text = "အတိုးရက်"
        binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())

        binding.includePaymentBox.tvThree.isVisible = true
        binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
        binding.includePaymentBox.tilThree.isVisible = true
        binding.includePaymentBox.edtThree.setText(viewModel.pawnData?.interest_amount.orEmpty())


        binding.includePaymentBox.tilTwo.isVisible = false
        binding.includePaymentBox.tvTwo.isVisible = false

        binding.includePaymentBox.btnEdit.isVisible = false
        binding.includePaymentBox.tvLabelGoldFromHome.isVisible = false
        binding.includePaymentBox.btnClick.text = "အတိုးရှင်း"
        checkedAction = "အတိုးရှင်း"

        binding.btnPrint.setOnClickListener {
            when (checkedAction) {
                "ကြိုသွင်း/ထုတ်" -> {
                    viewModel.createPrepaidDebt(
                        binding.edtScanVoucher.text.toString(),
                        binding.includePaymentBox.edtOne.text.toString(),
                        binding.edtReducedPay.text.toString(),
                        is_app_functions_allowed
                    )
                }

                "ကြိုတိုးရှင်း" -> {
                    viewModel.createPrepaidInterest(
                        binding.edtScanVoucher.text.toString(),
                        binding.includePaymentBox.edtOne.text.toString(),
                        binding.edtReducedPay.text.toString(),
                        is_app_functions_allowed
                    )
                }

                "တိုးယူသက်သက်" -> {
                    if (viewModel.pawnData?.prepaid_debt.let { if (it.isNullOrEmpty()) 0 else it.toInt() } > 0) {
                        Toast.makeText(
                            requireContext(),
                            "Prepaid Debt must be larger than zero kyats",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        viewModel.increaseDebt(
                            binding.edtScanVoucher.text.toString(),
                            binding.includePaymentBox.edtOne.text.toString(),
                            binding.edtReducedPay.text.toString(),
                            is_app_functions_allowed
                        )
                    }

                }

                "အတိုးရှင်း" -> {
                    viewModel.payInterest(
                        binding.edtScanVoucher.text.toString(),
                        binding.edtReducedPay.text.toString(),
                        is_app_functions_allowed
                    )
                }

                "အရင်းသွင်းအတိုးရှင်း" -> {
                    viewModel.payInterestAndSettleDebt(
                        binding.edtScanVoucher.text.toString(),
                        binding.edtReducedPay.text.toString(),
                        binding.includePaymentBox.edtTwo.text.toString(),
                        is_app_functions_allowed
                    )
                }

                "အရင်းသွင်း ခွဲရွေး" -> {
                    viewModel.payInterestAndReturnStock(
                        binding.edtScanVoucher.text.toString(),
                        binding.edtReducedPay.text.toString(),
                        binding.includePaymentBox.edtTwo.text.toString(),
                        oldStockIdList,// old stock id unknown
                        is_app_functions_allowed
                    )
                }

                "တိုးယူ အတိုးရှင်း" -> {
                    viewModel.payInterestAndIncreaseDebt(
                        binding.edtScanVoucher.text.toString(),
                        binding.includePaymentBox.edtTwo.text.toString(),
                        binding.edtReducedPay.text.toString(),
                        is_app_functions_allowed,

                    )
                }

                "ရွေးယူ" -> {
                    viewModel.settle(
                        binding.edtScanVoucher.text.toString(),
                        binding.includePaymentBox.edtOne.text.toString(),
                        is_app_functions_allowed
                    )
                }

                "ရောင်းချ" -> {
                    viewModel.sellOldStock(
                        binding.edtScanVoucher.text.toString(),
                        binding.edtReducedPay.text.toString(),
                        is_app_functions_allowed,
                        oldStockIdList
                    )
                }
            }
        }

        binding.includePaymentBox.btnClick.setOnClickListener {
            when (checkedAction) {
                "ကြိုသွင်း/ထုတ်" -> {
                    binding.edtCharge.setText(binding.includePaymentBox.edtOne.text.toString())
                }

                "ကြိုတိုးရှင်း" -> {
                    binding.edtCharge.setText(binding.includePaymentBox.edtThree.text.toString())

                }

                "တိုးယူသက်သက်" -> {

                    val totalPawnPrice = viewModel.getTotalPawnPrice().toInt()
                    var availableMoney =
                        totalPawnPrice - viewModel.pawnData?.remaining_debt.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                    if (generateNumberFromEditText(binding.includePaymentBox.edtOne).toInt() > availableMoney) {
                        Toast.makeText(
                            requireContext(),
                            "တိုးယူငွေ must less than available money",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        binding.edtCharge.setText(binding.includePaymentBox.edtOne.text.toString())
                    }

                }

                "အတိုးရှင်း" -> {
                    val cost =
                        generateNumberFromEditText(binding.includePaymentBox.edtThree).toInt()
                    binding.edtCharge.setText(cost.toString())

                }

                "အရင်းသွင်းအတိုးရှင်း" -> {
                    val cost =
                        generateNumberFromEditText(binding.includePaymentBox.edtThree).toInt() + generateNumberFromEditText(
                            binding.includePaymentBox.edtTwo
                        ).toInt()
                    binding.edtCharge.setText(cost.toString())
                }

                "အရင်းသွင်း ခွဲရွေး" -> {
                    var availableMoney = (viewModel.pawnData?.remaining_debt
                        ?: "0").toInt() - (viewModel.pawnData?.prepaid_debt
                        ?: "0").toInt() - generateNumberFromEditText(binding.includePaymentBox.edtTwo).toInt()
                    var pawnPriceRemained = viewModel.getPawnPriceForRemainedPawnItem().toInt()
                    if (availableMoney > pawnPriceRemained) {
                        Toast.makeText(
                            requireContext(),
                            "အရင်းသွင်းငွေ must less than remained pawn items price",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val cost =
                            generateNumberFromEditText(binding.includePaymentBox.edtThree).toInt() + generateNumberFromEditText(
                                binding.includePaymentBox.edtTwo
                            ).toInt()
                        binding.edtCharge.setText(cost.toString())
                    }

                }

                "တိုးယူ အတိုးရှင်း" -> {
                    val cost =
                        generateNumberFromEditText(
                            binding.includePaymentBox.edtTwo
                        ).toInt() - generateNumberFromEditText(binding.includePaymentBox.edtThree).toInt()
                    val totalPawnPrice = viewModel.getTotalPawnPrice().toInt()
                    var availableMoney =
                        totalPawnPrice - viewModel.pawnData?.remaining_debt.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                    if (generateNumberFromEditText(binding.includePaymentBox.edtTwo).toInt() > availableMoney) {
                        Toast.makeText(
                            requireContext(),
                            "တိုးယူငွေ must less than available money",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        binding.edtCharge.setText(cost.toString())
                    }
                }

                "ရွေးယူ" -> {
                    val cost =
                        generateNumberFromEditText(binding.includePaymentBox.edtThree).toInt() + generateNumberFromEditText(
                            binding.includePaymentBox.edtTwo
                        ).toInt()
                    binding.edtCharge.setText(cost.toString())
                }

                "ရောင်းချ" -> {
                    val cost =
                        generateNumberFromEditText(binding.includePaymentBox.edtThree).toInt() + generateNumberFromEditText(
                            binding.includePaymentBox.edtTwo
                        ).toInt()
                    binding.edtClaimMoney.setText(viewModel.getTotalVoucherBuyingPrice())
                    val moneyToGive =
                        viewModel.getTotalVoucherBuyingPrice().toInt() - generateNumberFromEditText(
                            binding.edtCharge
                        ).toInt() - tierDiscount - generateNumberFromEditText(
                            binding.edtReducedPay
                        ).toInt()
                    binding.edtMoneyToGive.setText(getRoundDownForPrice(moneyToGive).toString())
                    binding.edtCharge.setText(cost.toString())
                }
            }
        }
        binding.btnSelect.setOnClickListener {

            if (checkedAction == "ရောင်းချ") {
                val cost =
                    generateNumberFromEditText(binding.includePaymentBox.edtThree).toInt() + generateNumberFromEditText(
                        binding.includePaymentBox.edtTwo
                    ).toInt()
                binding.edtClaimMoney.setText(viewModel.getTotalVoucherBuyingPrice())
                val moneyToGive =
                    viewModel.getTotalVoucherBuyingPrice().toInt() - generateNumberFromEditText(
                        binding.edtCharge
                    ).toInt() - tierDiscount - generateNumberFromEditText(
                        binding.edtReducedPay
                    ).toInt()
                binding.edtMoneyToGive.setText(getRoundDownForPrice(moneyToGive).toString())
                binding.edtCharge.setText(cost.toString())
            } else {
                Log.i("akpcustom", tierDiscount.toString())
                Log.i(
                    "akpcustomCharge",
                    generateNumberFromEditText(binding.edtCharge).toInt().toString()
                )
                binding.edtClaimMoney.setText(
                    getRoundDownForPrice(
                        (generateNumberFromEditText(binding.edtCharge).toInt() - tierDiscount - generateNumberFromEditText(
                            binding.edtReducedPay
                        ).toInt())
                    ).toString()
                )
            }
        }

        viewModel.logoutLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
//                    Toast.makeText(requireContext(),"log out successful", Toast.LENGTH_LONG).show()
                    findNavController().navigate(GeneralSellFragmentDirections.actionGlobalLogout())
                }

                is Resource.Error -> {
                    loading.dismiss()
                    findNavController().navigate(GeneralSellFragmentDirections.actionGlobalLogout())

                }

                else -> {}
            }
        }
        viewModel.pdfDownloadLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    printPdf(downloader.downloadFile(it.data.orEmpty()).orEmpty(), requireContext())
                    requireContext().showSuccessDialog("Press Ok When Printing is finished!") {
                        viewModel.logout()
                    }
                }

                is Resource.Error -> {

                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.getPawnVoucherScanLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.pawnData = it.data?.asDomain()
                    binding.edtName.setText(it.data?.username)
                    binding.edtPrePay.setText(it.data?.prepaid_debt)
                    binding.edtPrePayMonth.setText(it.data?.prepaid_months)
                    binding.edtRemainingDebt.setText(it.data?.remaining_debt)
                    binding.tvTier.text = it.data?.tier_name
                    binding.tvTierDiscountAmount.text = it.data?.tier_discount
                    if (it.data?.remark.isNullOrEmpty().not()) {
                        Toast.makeText(
                            requireContext(),
                            it.data?.remark.orEmpty(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    doFunctionForCheck(if (lastCheckedId == -1) binding.radioInterestPay.id else lastCheckedId)
                    if (binding.radioPawnItemSale.isChecked){
                        viewModel.getStockFromHomeForPawnList(binding.edtScanVoucher.text.toString(),true)

                    }else if (binding.radioOnlyInterest.isChecked || binding.radioSelectInvestment.isChecked || binding.radioClearingInterest.isChecked || binding.radioPawnItemSale.isChecked) {
                        viewModel.getStockFromHomeForPawnList(binding.edtScanVoucher.text.toString(),false)
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
//                    if (!binding.radioSelectInvestment.isChecked || !binding.radioPawnItemSale.isChecked){
//                        it.data?.forEach { oldStock->
//                            viewModel.createStockFromHome(
//                                oldStock.a_buying_price.orEmpty(),
//                                oldStock.b_voucher_buying_value.orEmpty(),
//                                oldStock.c_voucher_buying_price.orEmpty(),
//                                oldStock.calculated_buying_value.orEmpty(),
//                                oldStock.calculated_for_pawn.orEmpty(),
//                                oldStock.d_gold_weight_ywae.orEmpty(),
//                                oldStock.e_price_from_new_voucher.orEmpty(),
//                                oldStock.f_voucher_shown_gold_weight_ywae.orEmpty(),
//                                oldStock.gem_value.orEmpty(),
//                                oldStock.gem_weight_ywae.orEmpty(),
//                                oldStock.gold_gem_weight_ywae.orEmpty(),
//                                oldStock.gold_weight_ywae.orEmpty(),
//                                oldStock.gq_in_carat.orEmpty(),
//                                oldStock.has_general_expenses.orEmpty(),
//
//                                oldStock.impurities_weight_ywae.orEmpty(),
//                                oldStock.maintenance_cost.orEmpty(),
//                                oldStock.price_for_pawn.orEmpty(),
//                                oldStock.pt_and_clip_cost.orEmpty(),
//                                oldStock.qty.orEmpty(),
//                                oldStock.rebuy_price.orEmpty(),
//                                oldStock.size.orEmpty(),
//                                oldStock.stock_condition.orEmpty(),
//                                oldStock.stock_name.orEmpty(),
//                                oldStock.type.orEmpty(),
//                                oldStock.wastage_ywae.orEmpty(),
//                                oldStock.rebuy_price_vertical_option.orEmpty(),
//                                productIdList = oldStock.productId,
//                                isEditable = oldStock.isEditable,
//                                isChecked = oldStock.isChecked,
//                                isPawn = false
//                            )
//                        }
//                    }
                    var totalVoucherBuyingPriceForPawn = 0

//                    it.data.orEmpty().forEach {
//                        totalVoucherBuyingPriceForPawn += it.b_voucher_buying_value!!.toInt()
//                    }
//                    viewModel.saveTotalVoucherBuyingPrice(totalVoucherBuyingPriceForPawn.toString())

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.createPrepaidDebtLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.createPrepaidInterestLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.increaseDebtLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.payInterestAndIncreaseDebtLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.payInterestLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.payInterestAndSettleDebtLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.payInterestAndReturnStockLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.settleLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.sellOldStockLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.printPawnSale(it.data.orEmpty())
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.printPawnSaleLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

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
                    var totalRebuyPrice = 0
                    var totalInterestAndDebt = (it.data?.remaining_debt?:"0").toInt()+(it.data?.interest_amount?:"0").toInt()
                    it.data?.pawned_stocks?.forEach { pawnedStock ->
                        totalRebuyPrice += (pawnedStock.b_voucher_buying_value?:"0").toInt()
                    }

                    var buyPriceFromShop = totalRebuyPrice-totalInterestAndDebt+(it.data?.reduced_amount?:"0").toInt()
                    printSample(
                        date = it.data?.invoiced_date.orEmpty(),
                        voucherNumber = it.data?.code.orEmpty(),
                        address = it.data?.user?.address.orEmpty(),
                        salesPerson = it.data?.salesperson.orEmpty(),
                        customerName = it.data?.user?.name.orEmpty(),
                        totalRebuyPrice = totalRebuyPrice.toString(),
                        totalInterestAndDebt = totalInterestAndDebt.toString(),
                        reducedMoney = it.data?.reduced_amount.orEmpty(),
                        buyPriceFromShop = buyPriceFromShop.toString(),
                        itemList = it.data!!.pawned_stocks,
                    )
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }


    }


    private val listener1: RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { group, checkedId ->

            if (lastCheckedId != checkedId && checkedId != binding.radioInterestPay.id) {
                lastCheckedId = checkedId
                if (binding.radioPawnItemSale.isChecked){
                    viewModel.getStockFromHomeForPawnList(binding.edtScanVoucher.text.toString(),true)

                }else if (binding.radioOnlyInterest.isChecked || binding.radioSelectInvestment.isChecked || binding.radioClearingInterest.isChecked || binding.radioPawnItemSale.isChecked) {
                    viewModel.getStockFromHomeForPawnList(binding.edtScanVoucher.text.toString(),false)
                }

            }

            if (checkedId != -1) {
                binding.radioGroup2.setOnCheckedChangeListener(null) // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                binding.radioGroup2.clearCheck() // clear the second RadioGroup!
                binding.radioGroup2.setOnCheckedChangeListener(listener2) //reset the listener
                doFunctionForCheck(checkedId)

            }
        }

    private val listener2: RadioGroup.OnCheckedChangeListener =
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            if (lastCheckedId != checkedId && checkedId != binding.radioInterestPay.id) {
                lastCheckedId = checkedId
                if (binding.edtScanVoucher.text.isNullOrEmpty().not()) {
                    if (binding.radioPawnItemSale.isChecked){
                        viewModel.getStockFromHomeForPawnList(binding.edtScanVoucher.text.toString(),true)

                    }else if (binding.radioOnlyInterest.isChecked || binding.radioSelectInvestment.isChecked || binding.radioClearingInterest.isChecked || binding.radioPawnItemSale.isChecked) {
                        viewModel.getStockFromHomeForPawnList(binding.edtScanVoucher.text.toString(),false)
                    }
                }
            }
            if (checkedId != -1) {
                binding.radioGroup.setOnCheckedChangeListener(null)
                binding.radioGroup.clearCheck()
                binding.radioGroup.setOnCheckedChangeListener(listener1)
                doFunctionForCheck(checkedId)
            }
        }

    fun doFunctionForCheck(checkedId: Int) {
        binding.edtCharge.text?.clear()
        binding.edtClaimMoney.text?.clear()
        when (checkedId) {
            binding.radioPreInputOutput.id -> {
                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.edtOne.setText("0")
                binding.includePaymentBox.tvOne.text = "အရင်းကြိုသွင်းငွေ"
                binding.includePaymentBox.tilOne.isEnabled = true
                binding.includePaymentBox.tvMaximumMoney.isVisible = false



                binding.includePaymentBox.tilTwo.isVisible = false
                binding.includePaymentBox.tilTwo.isEnabled = true
                binding.includePaymentBox.tilThree.isVisible = false
                binding.includePaymentBox.edtThree.isEnabled = true


                binding.includePaymentBox.tvTwo.isVisible = false
                binding.includePaymentBox.tvThree.isVisible = false

                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"

                binding.includePaymentBox.btnEdit.isVisible = false
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = false

                binding.includePaymentBox.btnClick.text = "ကြိုသွင်း/ထုတ်"
                checkedAction = "ကြိုသွင်း/ထုတ်"
                tierDiscount = 0
                binding.tvTierDiscountAmount.text =
                    tierDiscount.toString()
            }

            binding.radioPreInterestPay.id -> {
                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.edtOne.setText("0")
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "လထည့်ရန်"
                binding.includePaymentBox.tilTwo.isEnabled = true
                binding.includePaymentBox.tilOne.isEnabled = true
                binding.includePaymentBox.tilThree.isEnabled = false
                binding.includePaymentBox.tvMaximumMoney.isVisible = false



                binding.includePaymentBox.tvThree.isVisible = true
                binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                binding.includePaymentBox.tilThree.isVisible = true
                binding.includePaymentBox.edtThree.setText(viewModel.pawnData?.interest_per_month.orEmpty())

                binding.includePaymentBox.tilTwo.isVisible = false
                binding.includePaymentBox.tvTwo.isVisible = false

                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"

                binding.includePaymentBox.btnEdit.isVisible = false
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = false
                binding.includePaymentBox.btnClick.text = "ကြိုတိုးရှင်း"
                checkedAction = "ကြိုတိုးရှင်း"

                tierDiscount =
                    viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text =
                    tierDiscount.toString()


                binding.includePaymentBox.edtOne.addTextChangedListener {
                    if (it.isNullOrEmpty().not() && isNumeric(it.toString())) {
                        val month = it.toString()
                        val tierPercentage =
                            (viewModel.pawnData?.tier_discount_percentage ?: "0").toDouble() / 100
                        tierDiscount = getRoundDownForPawn(
                            (tierPercentage * month.toDouble() *
                                    (viewModel.pawnData?.interest_per_month
                                        ?: "0").toDouble()).toInt()
                        )
                        binding.tvTierDiscountAmount.text = tierDiscount.toString()
                        binding.tvTierDiscountAmountUnderReducedPay.text = tierDiscount.toString()
                        val money =
                            viewModel.pawnData?.interest_per_month.let { if (it.isNullOrEmpty()) 0 else it.toInt() } * month.toInt()
                        binding.includePaymentBox.edtThree.setText(getRoundDownForPawn(money).toString())
                    }
                }
                binding.includePaymentBox.edtOne.setText("1")

            }

            binding.radioOnlyInterest.id -> {
                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.edtOne.setText("0")
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "တိုးယူငွေ"
                val totalPawnPrice = viewModel.getTotalPawnPrice().toInt()
                binding.includePaymentBox.tvMaximumMoney.isVisible = true
                var availableMoney =
                    totalPawnPrice - viewModel.pawnData?.remaining_debt.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                if (availableMoney<0) availableMoney = 0
                binding.includePaymentBox.tvMaximumMoney.text = "Maximum Money : $availableMoney"

                binding.includePaymentBox.tilOne.isEnabled = true
                binding.includePaymentBox.edtThree.isEnabled = true


                binding.includePaymentBox.tvThree.isVisible = false
                binding.includePaymentBox.tilThree.isVisible = false

                binding.includePaymentBox.tilTwo.isVisible = false
                binding.includePaymentBox.tvTwo.isVisible = false
                binding.includePaymentBox.tilTwo.isEnabled = true


                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"

                binding.includePaymentBox.btnEdit.isVisible = true
                binding.includePaymentBox.btnEdit.setOnClickListener {
                    findNavController().navigate(
                        PawnInterestFragmentDirections.actionGlobalGoldFromHomeFragment(
                            "PawnNewCanEdit",
                            binding.edtScanVoucher.text.toString()
                        )
                    )
                }
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = true
                binding.includePaymentBox.btnClick.text = "တိုးယူသက်သက်"
                checkedAction = "တိုးယူသက်သက်"
                tierDiscount = 0
                binding.tvTierDiscountAmount.text =
                    tierDiscount.toString()

            }

            binding.radioInterestPay.id -> {
                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.tilOne.isEnabled = false

                binding.includePaymentBox.tilThree.isEnabled = false

                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())

                binding.includePaymentBox.tvThree.isVisible = true
                binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                binding.includePaymentBox.tilThree.isVisible = true
                binding.includePaymentBox.edtThree.setText(viewModel.pawnData?.interest_amount.orEmpty())
                binding.includePaymentBox.tvMaximumMoney.isVisible = false


                binding.includePaymentBox.tilTwo.isVisible = false
                binding.includePaymentBox.tvTwo.isVisible = false
                binding.includePaymentBox.tilTwo.isEnabled = true


                binding.includePaymentBox.btnEdit.isVisible = false
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = false

                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"
                binding.includePaymentBox.btnClick.text = "အတိုးရှင်း"
                checkedAction = "အတိုးရှင်း"
                tierDiscount =
                    viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text = tierDiscount.toString()
                binding.tvTierDiscountAmountUnderReducedPay.text = tierDiscount.toString()


            }

            binding.radioInvestmentInterestPay.id -> {
                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.tilOne.isEnabled = false
                binding.includePaymentBox.tvMaximumMoney.isVisible = false


                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())


                binding.includePaymentBox.tvTwo.isVisible = true
                binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                binding.includePaymentBox.tilTwo.isVisible = true
                binding.includePaymentBox.tilTwo.isEnabled = true


                binding.includePaymentBox.tilThree.isVisible = true
                binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                binding.includePaymentBox.tvThree.isVisible = true
                binding.includePaymentBox.edtThree.setText(viewModel.pawnData?.interest_amount.orEmpty())

                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"


                binding.includePaymentBox.btnEdit.isVisible = false
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = false
                binding.includePaymentBox.btnClick.text = "အရင်းသွင်းအတိုးရှင်း"
                checkedAction = "အရင်းသွင်းအတိုးရှင်း"
                tierDiscount =
                    viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text = tierDiscount.toString()
                binding.tvTierDiscountAmountUnderReducedPay.text = tierDiscount.toString()

            }

            binding.radioSelectInvestment.id -> {

                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.tilOne.isEnabled = false


                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())

                binding.includePaymentBox.tvTwo.isVisible = true
                binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                binding.includePaymentBox.tilTwo.isVisible = true
                binding.includePaymentBox.tilTwo.isEnabled = true
                var availableMoney = (viewModel.pawnData?.remaining_debt
                    ?: "0").toInt() - (viewModel.pawnData?.prepaid_debt
                    ?: "0").toInt() - generateNumberFromEditText(binding.includePaymentBox.edtTwo).toInt()
                var pawnPriceRemained = viewModel.getPawnPriceForRemainedPawnItem().toInt()
                var moneyToShow = availableMoney - pawnPriceRemained
                if (moneyToShow<0) moneyToShow = 0

                binding.includePaymentBox.tvMaximumMoney.isVisible = true
                binding.includePaymentBox.tvMaximumMoney.text = "Available Money : $moneyToShow"



                binding.includePaymentBox.tilThree.isVisible = true
                binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                binding.includePaymentBox.tvThree.isVisible = true
                binding.includePaymentBox.edtThree.setText(viewModel.pawnData?.interest_amount.orEmpty())


                binding.includePaymentBox.btnEdit.isVisible = true
                binding.includePaymentBox.btnEdit.setOnClickListener {
                    findNavController().navigate(
                        PawnInterestFragmentDirections.actionGlobalGoldFromHomeFragment(
                            "PawnSelectNoEdit",
                            binding.edtScanVoucher.text.toString()
                        )
                    )
                }
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = true

                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"

                binding.includePaymentBox.btnClick.text = "အရင်းသွင်း ခွဲရွေး"
                checkedAction = "အရင်းသွင်း ခွဲရွေး"
                tierDiscount =
                    viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text = tierDiscount.toString()
                binding.tvTierDiscountAmountUnderReducedPay.text = tierDiscount.toString()

            }

            binding.radioClearingInterest.id -> {

                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.tilOne.isEnabled = false


                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())

                binding.includePaymentBox.tvTwo.isVisible = true
                binding.includePaymentBox.tvTwo.text = "တိုးယူငွေ"
                val totalPawnPrice = viewModel.getTotalPawnPrice().toInt()
                var availableMoney =
                    totalPawnPrice - viewModel.pawnData?.remaining_debt.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.includePaymentBox.tvMaximumMoney.isVisible = true
                if (availableMoney<0) availableMoney = 0
                binding.includePaymentBox.tvMaximumMoney.text = "Available Money : $availableMoney"

                binding.includePaymentBox.tilTwo.isVisible = true
                binding.includePaymentBox.tilTwo.isEnabled = true
                binding.includePaymentBox.edtTwo.text?.clear()

                binding.includePaymentBox.tilThree.isVisible = true
                binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                binding.includePaymentBox.tvThree.isVisible = true
                binding.includePaymentBox.edtThree.setText(viewModel.pawnData?.interest_amount.orEmpty())

                binding.includePaymentBox.btnEdit.isVisible = true
                binding.includePaymentBox.btnEdit.setOnClickListener {
                    findNavController().navigate(
                        PawnInterestFragmentDirections.actionGlobalGoldFromHomeFragment(
                            "PawnNewCanEdit",
                            binding.edtScanVoucher.text.toString()
                        )
                    )
                }
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = true

                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"

                binding.includePaymentBox.btnClick.text = "တိုးယူ အတိုးရှင်း"
                checkedAction = "တိုးယူ အတိုးရှင်း"
                tierDiscount =
                    viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text = tierDiscount.toString()
                binding.tvTierDiscountAmountUnderReducedPay.text = tierDiscount.toString()


            }

            binding.radioChoose.id -> {

                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.tilOne.isEnabled = false
                binding.includePaymentBox.tvMaximumMoney.isVisible = false


                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())

                binding.includePaymentBox.tvTwo.isVisible = true
                binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                binding.includePaymentBox.tilTwo.isVisible = true
                binding.includePaymentBox.tilTwo.isEnabled = true
                binding.includePaymentBox.edtTwo.setText(
                    (viewModel.pawnData?.remaining_debt.let { if (it.isNullOrEmpty()) 0 else it.toInt() } - viewModel.pawnData?.prepaid_debt.let { if (it.isNullOrEmpty()) 0 else it.toInt() }).toString()
                )


                binding.includePaymentBox.tilThree.isVisible = true
                binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                binding.includePaymentBox.tvThree.isVisible = true
                binding.includePaymentBox.edtThree.setText(viewModel.pawnData?.interest_amount.orEmpty())


                binding.includePaymentBox.btnEdit.isVisible = false
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = false
                binding.includePaymentBox.tilTwo.isEnabled = false


                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"
                binding.includePaymentBox.tilThree.isEnabled = false

                binding.includePaymentBox.btnClick.text = "ရွေးယူ"
                checkedAction = "ရွေးယူ"
                tierDiscount =
                    viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text = tierDiscount.toString()
                binding.tvTierDiscountAmountUnderReducedPay.text = tierDiscount.toString()


            }

            binding.radioPawnItemSale.id -> {

                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.tilOne.isEnabled = false
                binding.includePaymentBox.tilThree.isEnabled = false
                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())
                binding.includePaymentBox.tvMaximumMoney.isVisible = false

                binding.includePaymentBox.tvTwo.isVisible = true
                binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                binding.includePaymentBox.tilTwo.isVisible = true
                binding.includePaymentBox.tilTwo.isEnabled = false
                binding.includePaymentBox.edtTwo.setText(
                    ((viewModel.pawnData?.remaining_debt
                        ?: "0").toInt() - (viewModel.pawnData?.prepaid_debt
                        ?: "0").toInt()).toString()
                )

                binding.includePaymentBox.tilThree.isVisible = true
                binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                binding.includePaymentBox.tvThree.isVisible = true
                binding.includePaymentBox.edtThree.setText(viewModel.pawnData?.interest_amount.orEmpty())

                binding.tilMoneyToGive.isVisible = true
                binding.tvLabelMoneyToGive.isVisible = true
                binding.tvLabelClaimMoney.text = "ဘောင်ချာဖွင့်ဝယ်ပေးငွေ"
                binding.edtClaimMoney.setText(viewModel.getTotalVoucherBuyingPrice())

                binding.includePaymentBox.btnEdit.isVisible = true
                binding.includePaymentBox.btnEdit.setOnClickListener {
                    findNavController().navigate(
                        PawnInterestFragmentDirections.actionGlobalGoldFromHomeFragment(
                            "PawnSelect",
                            binding.edtScanVoucher.text.toString()
                        )
                    )
                }
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = true

                binding.includePaymentBox.btnClick.text = "ရောင်းချ"
                checkedAction = "ရောင်းချ"
                tierDiscount =
                    viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text = tierDiscount.toString()
                binding.tvTierDiscountAmountUnderReducedPay.text = tierDiscount.toString()

            }
        }
    }

    private fun printSample(
        date: String,
        voucherNumber: String,
        address: String,
        salesPerson: String,
        customerName: String,
        totalRebuyPrice: String,
        totalInterestAndDebt: String,
        reducedMoney: String,
        buyPriceFromShop: String,
        itemList: List<PawnedStock>
    ) {
        try {
            // Start the print job
            mPrinter.beginTransaction()
            val lineLength = paperLength
            var numSpaces = 0
            var spaces = ""

            mPrinter?.addText("-------------------------------------------------\n")
            mPrinter?.addTextAlign(Printer.ALIGN_CENTER)
            mPrinter?.addText("Pawn Items Sale Voucher\n")
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

            val qrCodeWidth = 200 // Adjust the size based on your requirements

            val qrCodeHeight = 200
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


            mPrinter?.addText("Total Interest And Debt")
            numSpaces =
                lineLength - totalInterestAndDebt.length - "Total Interest And Debt".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces$totalInterestAndDebt\n")

            mPrinter?.addText("Reduced Money")
            numSpaces =
                lineLength - reducedMoney.length - "Reduced Money".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces$reducedMoney\n")


            mPrinter?.addText("Buy Price From Shop")
            numSpaces =
                lineLength - buyPriceFromShop.length - "Buy Price From Shop".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter?.addText("$spaces$buyPriceFromShop\n")

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
        columns: List<PawnedStock>,
        columnWidths: List<Int>,
        printer: Printer
    ) {
        val headerList = listOf("Item Name", "Gold Weight", "Price", "Rebuy")

        val combineList = combineLists(headerList, columns.map { it.asPrintData() })
        for (item in combineList) {
            printer?.addTextAlign(Printer.ALIGN_LEFT)
            printer?.addText(item.first + magicSpace)


            val numSpaces = paperLength - item.second.length - item.first.length
            val spaces = " ".repeat(numSpaces)
            printer?.addText("$spaces${item.second}")
            printer?.addText("\n")
            if (item.first == "Rebuy") {
                printer?.addText("-------------------------------------------------\n")
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