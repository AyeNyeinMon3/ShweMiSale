package com.example.shwemisale.screen.pawnInterestModule

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.*
import com.example.shwemisale.data_layers.dto.pawn.asDomain
import com.example.shwemisale.databinding.FragmentPawnInterestBinding
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import dagger.hilt.android.AndroidEntryPoint
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

    //radio state saved
    var lastCheckedId = -1

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
                        is_app_functions_allowed
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
                        is_app_functions_allowed
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
                        generateNumberFromEditText(binding.includePaymentBox.edtThree).toInt() + generateNumberFromEditText(
                            binding.includePaymentBox.edtTwo
                        ).toInt()
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
                    var availableMoney = (viewModel.pawnData?.remaining_debt?:"0").toInt()- (viewModel.pawnData?.prepaid_debt?:"0").toInt()- generateNumberFromEditText(binding.includePaymentBox.edtTwo).toInt()
                    var pawnPriceRemained = viewModel.getPawnPriceForRemainedPawnItem().toInt()
                    if (availableMoney>pawnPriceRemained){
                        Toast.makeText(requireContext(),"အရင်းသွင်းငွေ must less than remained pawn items price",Toast.LENGTH_LONG ).show()
                    }else{
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
                binding.edtClaimMoney.setText(
                    getRoundDownForPrice(
                        (generateNumberFromEditText(binding.edtCharge).toInt() - tierDiscount - generateNumberFromEditText(
                            binding.edtReducedPay
                        ).toInt())
                    ).toString()
                )
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
                    viewModel.getStockFromHomeForPawnList(binding.edtScanVoucher.text.toString())

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
                    viewModel.createStockFromHome(it.data.orEmpty(), true)
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
                    requireContext().showSuccessDialog("Success") {

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
                    requireContext().showSuccessDialog("Success") {

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
                    requireContext().showSuccessDialog("Success") {

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
                    requireContext().showSuccessDialog("Success") {

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
                    requireContext().showSuccessDialog("Success") {

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
                    requireContext().showSuccessDialog("Success") {

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
                    requireContext().showSuccessDialog("Success") {

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
                    requireContext().showSuccessDialog("Success") {

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
                    requireContext().showSuccessDialog("Success") {

                    }
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
                if (binding.edtScanVoucher.text.isNullOrEmpty().not()) {
                    viewModel.getStockFromHomeForPawnList(binding.edtScanVoucher.text.toString())
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
                    viewModel.getStockFromHomeForPawnList(binding.edtScanVoucher.text.toString())
                }

            }
            if (checkedId != -1) {
                binding.radioGroup.setOnCheckedChangeListener(null)
                binding.radioGroup.clearCheck()
                binding.radioGroup.setOnCheckedChangeListener(listener1)
                doFunctionForCheck(checkedId)
            }
        }

    fun doFunctionForCheck(checkedId:Int){
        binding.edtCharge.text?.clear()
        binding.edtClaimMoney.text?.clear()
        when (checkedId) {
            binding.radioPreInputOutput.id -> {
                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.edtOne.setText("0")
                binding.includePaymentBox.tvOne.text = "အရင်းကြိုသွင်းငွေ"

                binding.includePaymentBox.tilTwo.isVisible = false
                binding.includePaymentBox.tilTwo.isEnabled = true
                binding.includePaymentBox.tilThree.isVisible = false

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
                    getRoundDownForPawn(tierDiscount).toString()
            }

            binding.radioPreInterestPay.id -> {
                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.edtOne.setText("0")
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "လထည့်ရန်"
                binding.includePaymentBox.tilTwo.isEnabled = true


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

                tierDiscount = viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text =
                    getRoundDownForPawn(tierDiscount).toString()


                binding.includePaymentBox.edtOne.addTextChangedListener {
                    if (it.isNullOrEmpty().not() && isNumeric(it.toString())) {
                        val month = it.toString()
                        binding.tvTierDiscountAmount.text =
                            (getRoundDownForPawn(tierDiscount) *month.toInt()).toString()
                        val money =
                            viewModel.pawnData?.interest_per_month.let { if (it.isNullOrEmpty()) 0 else it.toInt() } * month.toInt()
                        binding.includePaymentBox.edtThree.setText(money.toString())
                    }
                }

            }

            binding.radioOnlyInterest.id -> {
                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.edtOne.setText("0")
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "တိုးယူငွေ"


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
                            null
                        )
                    )
                }
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = true
                binding.includePaymentBox.btnClick.text = "တိုးယူသက်သက်"
                checkedAction = "တိုးယူသက်သက်"
                tierDiscount = 0
                binding.tvTierDiscountAmount.text =
                    getRoundDownForPawn(tierDiscount).toString()

            }

            binding.radioInterestPay.id -> {
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
                binding.includePaymentBox.tilTwo.isEnabled = true


                binding.includePaymentBox.btnEdit.isVisible = false
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = false

                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"
                binding.includePaymentBox.btnClick.text = "အတိုးရှင်း"
                checkedAction = "အတိုးရှင်း"
                tierDiscount = viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text =
                    getRoundDownForPawn(tierDiscount).toString()

            }

            binding.radioInvestmentInterestPay.id -> {
                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
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
                tierDiscount =viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text =
                    getRoundDownForPawn(tierDiscount).toString()
            }
            binding.radioSelectInvestment.id -> {

                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())

                binding.includePaymentBox.tvTwo.isVisible = true
                binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                binding.includePaymentBox.tilTwo.isVisible = true
                binding.includePaymentBox.tilTwo.isEnabled = true



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
                tierDiscount = viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text =
                    getRoundDownForPawn(tierDiscount).toString()
            }

            binding.radioClearingInterest.id -> {

                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())

                binding.includePaymentBox.tvTwo.isVisible = true
                binding.includePaymentBox.tvTwo.text = "တိုးယူငွေ"
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
                tierDiscount =viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text =
                    getRoundDownForPawn(tierDiscount).toString()

            }

            binding.radioChoose.id -> {

                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())

                binding.includePaymentBox.tvTwo.isVisible = true
                binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                binding.includePaymentBox.tilTwo.isVisible = true
                binding.includePaymentBox.tilTwo.isEnabled = false
                binding.includePaymentBox.edtTwo.setText(
                    (viewModel.pawnData?.remaining_debt.let { if (it.isNullOrEmpty()) 0 else it.toInt() } - viewModel.pawnData?.prepaid_debt.let { if (it.isNullOrEmpty()) 0 else it.toInt() }).toString()
                )


                binding.includePaymentBox.tilThree.isVisible = true
                binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                binding.includePaymentBox.tvThree.isVisible = true
                binding.includePaymentBox.edtThree.setText(viewModel.pawnData?.interest_amount.orEmpty())


                binding.includePaymentBox.btnEdit.isVisible = false
                binding.includePaymentBox.tvLabelGoldFromHome.isVisible = false
                binding.includePaymentBox.tilTwo.isEnabled = true


                binding.tilMoneyToGive.isVisible = false
                binding.tvLabelMoneyToGive.isVisible = false
                binding.tvLabelClaimMoney.text = "တောင်းခံရန်ငွေ"

                binding.includePaymentBox.btnClick.text = "ရွေးယူ"
                checkedAction = "ရွေးယူ"
                tierDiscount = viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text =
                    getRoundDownForPawn(tierDiscount).toString()

            }

            binding.radioPawnItemSale.id -> {

                binding.includePaymentBox.tilOne.isVisible = true
                binding.includePaymentBox.tvOne.isVisible = true
                binding.includePaymentBox.tvOne.text = "အတိုးရက်"
                binding.includePaymentBox.edtOne.setText(viewModel.pawnData?.interest_days.orEmpty())

                binding.includePaymentBox.tvTwo.isVisible = true
                binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                binding.includePaymentBox.tilTwo.isVisible = true
                binding.includePaymentBox.tilTwo.isEnabled = false
                binding.includePaymentBox.edtTwo.setText(((viewModel.pawnData?.remaining_debt?:"0").toInt()-(viewModel.pawnData?.prepaid_debt?:"0").toInt()).toString())

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
                tierDiscount = viewModel.pawnData?.tier_discount.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                binding.tvTierDiscountAmount.text =
                    getRoundDownForPawn(tierDiscount).toString()
            }
        }
    }
}