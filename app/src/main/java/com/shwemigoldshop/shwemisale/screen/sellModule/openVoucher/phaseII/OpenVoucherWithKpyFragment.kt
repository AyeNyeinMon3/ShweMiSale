package com.shwemigoldshop.shwemisale.screen.sellModule.openVoucher.phaseII

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shwemigoldshop.shwemisale.printerHelper.AkpDownloader
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.generateNumberFromEditText
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.util.showSuccessDialog
import com.shwemigoldshop.shwemisale.R
import com.shwemigoldshop.shwemisale.databinding.FragmentOpenVoucherWithKpyBinding
import com.shwemigoldshop.shwemisale.printerHelper.printPdf
import com.shwemigoldshop.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.shwemigoldshop.shwemisale.screen.sellModule.openVoucher.withKPY.WithKPYViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody

@AndroidEntryPoint
class OpenVoucherWithKpyFragment : Fragment() {
    private var _binding: FragmentOpenVoucherWithKpyBinding? = null
    val binding: FragmentOpenVoucherWithKpyBinding
        get() = _binding!!

    private val args by navArgs<com.shwemigoldshop.shwemisale.screen.sellModule.openVoucher.phaseII.OpenVoucherWithKpyFragmentArgs>()
    private val viewModel by viewModels<WithKPYViewModel>()
    private lateinit var loading: AlertDialog
    private val downloader by lazy { AkpDownloader(requireContext()) }
    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storagePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                //
            }
        }
        requireActivity().onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.updateEvalue("0")
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentOpenVoucherWithKpyBinding.inflate(inflater).also {
            _binding = it
        }.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        uiDeployment()
        stateObserve()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun stateObserve() {
        viewModel.oldStockCalculationStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                "kpy" -> {
                    viewModel.setOldStockValueLiveData(
                        viewModel.getTotalCVoucherBuyingPrice()
                            .let { if (it.isEmpty()) 0 else it.toInt() })
                    binding.includeOldStockWeightLayout.tvLabelOldStockWeight.text =
                        getString(R.string.old_stock_weight)

                }

                "value" -> {
                    viewModel.setOldStockValueLiveData(
                        viewModel.getTotalCVoucherBuyingPrice()
                            .let { if (it.isEmpty()) 0 else it.toInt() })
                    binding.includeOldStockWeightLayout.tvLabelOldStockWeight.text =
                        getString(R.string.old_stock_value)

                }
            }
        }
        viewModel.calculationStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                "firstCalculation" -> {
                    viewModel.firstCalculation(generateNumberFromEditText(binding.edtReduceMoney).toInt())
                }

                "finalCalculation" -> {
                    viewModel.finalCalculation(
                        generateNumberFromEditText(binding.edtPaymentFromCustomer).toInt(),
                        generateNumberFromEditText(binding.edtReduceMoney).toInt()
                    )
                }
            }
        }

        //invoice Detail
        viewModel.totalGoldWeightOfProductsLiveData.observe(viewLifecycleOwner) {
            val kpy = getKPYFromYwae(it)
            binding.includeInvoiceDetails.tvTotalGoldWeightKpy.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )
        }
        viewModel.totalWastageWeightOfProductsLiveData.observe(viewLifecycleOwner) {
            val kpy = getKPYFromYwae(it)
            binding.includeInvoiceDetails.tvTotalWastage.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )
        }
        viewModel.totalGoldAndWastageWeightOfProductsLiveData.observe(viewLifecycleOwner) {
            val kpy = getKPYFromYwae(it)
            binding.includeInvoiceDetails.tvTotalGoldWeightWithWastage.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )
        }
        viewModel.oldStockWeightYwaeLiveData.observe(viewLifecycleOwner) {
            val kpy = getKPYFromYwae(it)
            binding.includeInvoiceDetails.tvGoldWeightFromOldStocks.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )
            binding.includeOldStockWeightLayout.tvOldStockWeight.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )
        }
        viewModel.poloGoldWeightYwaeLiveData.observe(viewLifecycleOwner) {
            val kpy = getKPYFromYwae(it)
            binding.includeInvoiceDetails.tvPoloGoldWeightKpy.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )
        }
        viewModel.poloValueLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvPoloValue.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.oldVoucherPaymentLivedata.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvOldVoucherPayment.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.oldStocksValueLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvStockFromHomeValue.text =
                getString(R.string.mmk_value, it.toString())
            binding.includeOldStockWeightLayout.tvOldStockValue.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.totalMaintainenceOfProductsLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvTotalMaintainenceCost.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.totalPtClipCostOfProductsLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvTotalPtClipCost.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.totalGemValueOfProductsLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvTotalGemValue.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.totalValuesLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvTotalValues.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.flashSaleDiscountLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvFlashSaleDiscount.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.manualReduceMoneyLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvReducedMoney.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.totalReduceMoneyLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvTotalReducedMoney.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.totalCostToPayLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvTotalCost.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.taxMoneyLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvTaxMoney.text =
                getString(R.string.mmk_value, it.toString())
            binding.includeTaxMoneyLayout.tvTaxMoney.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.totalCostToPayIncludingTaxMoney.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvTotalCostIncludingTax.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.paymentFromCustomerLiveData.observe(viewLifecycleOwner) {
            binding.edtPaymentFromCustomer.setText(it.toString())
            binding.includeInvoiceDetails.tvPaymentFromCustomer.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.remainMoneyLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvRemainMoney.text =
                getString(R.string.mmk_value, it.toString())
        }


        viewModel.submitWithKPYLiveData.observe(viewLifecycleOwner) {
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

    }

    fun uiDeployment() {
        viewModel.getGoldPrice(args.scannedProducts.toList().map { it.id })
        viewModel.setOldVoucherPaymentLiveData(args.oldVoucherPaidAmount)
//        viewModel.setOldStockWeightYwae(
//            viewModel.getTotalGoldWeightYwae()
//                .let { if (it.isEmpty()) 0.0 else it.toDouble() })
//
//        viewModel.setOldStockValueLiveData(
//            viewModel.getTotalCVoucherBuyingPrice()
//                .let { if (it.isEmpty()) 0 else it.toInt() })
        binding.radioGpOldStockCalculation.setOnCheckedChangeListener { group, checkedId ->
            binding.btnCalculate.isEnabled = true
            if (checkedId == binding.radioBtnWithKpy.id) {
                binding.includeOldStockWeightLayout.tvOldStockWeight.isVisible = true
                binding.includeOldStockWeightLayout.tvOldStockValue.isVisible = false
                viewModel.setOldStockWeightYwae(
                    viewModel.getTotalGoldWeightYwae()
                        .let { if (it.isEmpty()) 0.0 else it.toDouble() })
                viewModel.setOldStockValueLiveData(0)

                viewModel.setOldStockCalculationState("with_kpy")
                viewModel.calculateProducts(args.scannedProducts.toList())
                viewModel.calculatePoloValueWithKpy()
                viewModel.calculateTotalValue()
            } else {
                binding.includeOldStockWeightLayout.tvOldStockWeight.isVisible = false
                binding.includeOldStockWeightLayout.tvOldStockValue.isVisible = true
                viewModel.setOldStockValueLiveData(
                    viewModel.getTotalCVoucherBuyingPrice()
                        .let { if (it.isEmpty()) 0 else it.toInt() })
                viewModel.setOldStockWeightYwae(0.0)
                viewModel.setOldStockCalculationState("with_value")
                viewModel.calculateProducts(args.scannedProducts.toList())
                viewModel.calculatePoloValueWithValue()
                viewModel.calculateTotalValue()
            }
        }
        binding.radioBtnWithKpy.isChecked = true
        binding.includeOldStockWeightLayout.btnEditOldStockWeight.setOnClickListener {
            findNavController().navigate(
                com.shwemigoldshop.shwemisale.screen.sellModule.openVoucher.phaseII.OpenVoucherWithKpyFragmentDirections.actionGlobalGoldFromHomeFragment(
                    "Global",
                    null
                )
            )
        }
        editTaxMoney()
        binding.tvFillFullPayment.setOnClickListener {
            fillFullPayment()
        }
        binding.btnCalculate.setOnClickListener {
            binding.uiToHideBeforeFirstCalculation.isVisible = true
            viewModel.setCalculationState()
        }
        val productIdList = mutableListOf<MultipartBody.Part>()
        val list = args.scannedProducts.map { it.id }
        repeat(list.size) {
            productIdList.add(
                MultipartBody.Part.createFormData(
                    "product_id[]",
                    list[it]
                )
            )
        }
        binding.btnPrint.setOnClickListener {
            viewModel.submitWithKPY(
                productIdList = productIdList,
                redeem_point = null,
                old_voucher_code = args.oldVoucherCode,
                old_voucher_paid_amount = args.oldVoucherCode?.let {
                    MultipartBody.Part.createFormData(
                        "old_voucher_paid_amount",
                        it
                    )
                }
            )
        }

    }

    fun editTaxMoney() {
        binding.includeTaxMoneyLayout.btnEditTaxMoney.setOnClickListener {
            binding.includeTaxMoneyLayout.uiEditingState.isVisible = true
            binding.includeTaxMoneyLayout.uiAfterEditState.isVisible = false
        }
        binding.includeTaxMoneyLayout.btnCancelTaxMoney.setOnClickListener {
            binding.includeTaxMoneyLayout.uiEditingState.isVisible = false
            binding.includeTaxMoneyLayout.uiAfterEditState.isVisible = true
        }

        binding.includeTaxMoneyLayout.btnSaveTaxMoney.setOnClickListener {
            viewModel.setTaxMoney(generateNumberFromEditText(binding.includeTaxMoneyLayout.edtTaxMoney).toInt())
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.radioBtnWithKpy.isChecked){
            viewModel.setOldStockWeightYwae(
                viewModel.getTotalGoldWeightYwae()
                    .let { if (it.isEmpty()) 0.0 else it.toDouble() })
        }else{
            viewModel.setOldStockValueLiveData(
                viewModel.getTotalCVoucherBuyingPrice()
                    .let { if (it.isEmpty()) 0 else it.toInt() })
        }
    }

    fun fillFullPayment() {
        viewModel.setFullPayment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}