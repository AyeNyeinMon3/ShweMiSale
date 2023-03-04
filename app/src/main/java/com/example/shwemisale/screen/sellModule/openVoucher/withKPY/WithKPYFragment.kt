package com.example.shwemisale.screen.sellModule.openVoucher.withKPY

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.Resource
import com.example.shwemi.util.generateNumberFromEditText
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.showSuccessDialog
import com.example.shwemisale.databinding.FragmentWithKpyBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody

@AndroidEntryPoint
class WithKPYFragment : Fragment() {

    lateinit var binding: FragmentWithKpyBinding
    private val viewModel by viewModels<WithKPYViewModel>()
    private lateinit var loading: AlertDialog
    private val args by navArgs<WithKPYFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentWithKpyBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        //TODO fill product infos that scanned
        bindPassedData()

        viewModel.submitWithKPYLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog(it.data.orEmpty()){
                        findNavController().popBackStack()
                    }
                }
                is Resource.Error -> {

                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.getGoldPriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.goldPrice = it.data?.gold_price.toString()
                    val poloGoldKyat = getKyatsFromKPY(
                        generateNumberFromEditText(binding.edtPoloGoldK).toInt(),
                        generateNumberFromEditText(binding.edtPoloGoldP).toInt(),
                        generateNumberFromEditText(binding.edtPoloGoldY).toDouble(),
                    )

                    val poloValue = poloGoldKyat * it.data?.gold_price.orEmpty()
                        .let { if (it.isEmpty()) 0.0 else it.toDouble() }
                    binding.edtPoloValue.setText(poloValue.toInt().toString())
                    val totalPrice =
                        generateNumberFromEditText(binding.edtPoloValue).toDouble() + generateNumberFromEditText(
                            binding.edtTotalFee
                        ).toDouble() +
                                generateNumberFromEditText(binding.edtPTclipValue).toDouble() + generateNumberFromEditText(
                            binding.edtTotalGemValue
                        ).toDouble()- generateNumberFromEditText(binding.edtGoldFromHomeValue).toDouble()

                    binding.edtCharge.setText(totalPrice.toInt().toString())

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == binding.radioBtnKpy.id) {
                val goldWeightKyat = getKyatsFromKPY(
                    generateNumberFromEditText(binding.edtGoldFromHomeWeightK).toInt(),
                    generateNumberFromEditText(binding.edtGoldFromHomeWeightP).toInt(),
                    generateNumberFromEditText(binding.edtGoldFromHomeWeightY).toDouble(),
                )

                val goldFromHomeValue = goldWeightKyat * viewModel.goldPrice
                    .let { if (it.isEmpty()) 0.0 else it.toDouble() }
                val totalPrice =
                    generateNumberFromEditText(binding.edtPoloValue).toDouble() + generateNumberFromEditText(
                        binding.edtTotalFee
                    ).toDouble() +
                            generateNumberFromEditText(binding.edtPTclipValue).toDouble() + generateNumberFromEditText(
                        binding.edtTotalGemValue
                    ).toDouble()-goldFromHomeValue

                binding.edtCharge.setText(totalPrice.toInt().toString())
            } else if (checkedId == binding.radioBtnValue.id) {
                val totalPrice =
                    generateNumberFromEditText(binding.edtPoloValue).toDouble() + generateNumberFromEditText(
                        binding.edtTotalFee
                    ).toDouble() +
                            generateNumberFromEditText(binding.edtPTclipValue).toDouble() + generateNumberFromEditText(
                        binding.edtTotalGemValue
                    ).toDouble()- generateNumberFromEditText(binding.edtGoldFromHomeValue).toDouble()

                binding.edtCharge.setText(totalPrice.toInt().toString())
            }
        }

        binding.btnCalculate.setOnClickListener {
            val leftMoney =
                generateNumberFromEditText(binding.edtReducedPay).toInt() + generateNumberFromEditText(
                    binding.edtDeposit
                ).toInt() - generateNumberFromEditText(binding.edtCharge).toInt()
            binding.edtBalance.setText(leftMoney.toInt().toString())
        }

        binding.btnPrint.setOnClickListener {
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
            val paid_amount = binding.edtDeposit.text.toString()
            val reduced_cost = binding.edtReducedPay.text.toString()
            val finalInfo = viewModel.getStockFromHomeFinalInfo()

            /** old stock list manipulation */
            //List from room
            val oldStockList = viewModel.getOldStockInfoFromDataBase()

            //Fields from oldStockInfo
            val old_stocks_nameList = mutableListOf<MultipartBody.Part>()
            val oldStockImageIds = mutableListOf<MultipartBody.Part>()
            val oldStockImageFile = mutableListOf<MultipartBody.Part>()
            val oldStockCondition = mutableListOf<MultipartBody.Part>()
            val oldStockQty = mutableListOf<MultipartBody.Part>()
            val oldStockSize = mutableListOf<MultipartBody.Part>()

            val oldStockGemWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockGoldGemWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockImpurityWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockGoldWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockWastageWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockRebuyPrice = mutableListOf<MultipartBody.Part>()
            val oldStockGQinCarat = mutableListOf<MultipartBody.Part>()
            val oldStockMaintenance_cost = mutableListOf<MultipartBody.Part>()
            val oldStockGemValue = mutableListOf<MultipartBody.Part>()
            val oldStockPTAndClipCost = mutableListOf<MultipartBody.Part>()
            val oldStockCalculatedBuyingValue = mutableListOf<MultipartBody.Part>()
            val oldStockPriceForPawn = mutableListOf<MultipartBody.Part>()
            val oldStockCalculatedForPawn = mutableListOf<MultipartBody.Part>()
            val oldStockABuyingPrice = mutableListOf<MultipartBody.Part>()
            val oldStockb_voucher_buying_value = mutableListOf<MultipartBody.Part>()
            val oldStockc_voucher_buying_price = mutableListOf<MultipartBody.Part>()
            val oldStockDGoldWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockEPriceFromNewVoucher = mutableListOf<MultipartBody.Part>()
            val oldStockFVoucherShownGoldWeightY = mutableListOf<MultipartBody.Part>()

            repeat(oldStockList.size) {
                val imageFile = oldStockList[it].image
                val imageId = oldStockList[it].imageId
                old_stocks_nameList.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][stock_name]",
                        oldStockList[it].name.toString()
                    )
                )
                imageId?.let { id ->
                    oldStockImageIds.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][image][id]",
                            id
                        )
                    )
                }
                imageFile?.let { file ->
                    oldStockImageFile.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][image][file]",
                            file
                        )
                    )
                }
                oldStockCondition.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][stock_condition]",
                        oldStockList[it].oldStockCondition.toString()
                    )
                )
                oldStockQty.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][qty]",
                        oldStockList[it].qty.toString()
                    )
                )

                oldStockSize.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][size]",
                        oldStockList[it].size.toString()
                    )
                )

                oldStockGemWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gem_weight_ywae]",
                        oldStockList[it].gem_weight_ywae.toString()
                    )
                )
                oldStockGoldGemWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gold_gem_weight_ywae]",
                        getYwaeFromGram(
                            oldStockList[it].gold_and_gem_weight_gm?.toDouble() ?: 0.0
                        ).toString()
                    )
                )
                oldStockImpurityWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][impurities_weight_ywae]",
                        oldStockList[it].oldStockImpurityWeightY.toString()
                    )
                )
                oldStockGoldWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gold_weight_ywae]",
                        oldStockList[it].goldWeightYwae.toString()
                    )
                )
                oldStockWastageWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][wastage_ywae]",
                        oldStockList[it].wastage_ywae.toString()
                    )
                )
                oldStockRebuyPrice.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][rebuy_price]",
                        oldStockList[it].rebuyPrice.toString()
                    )
                )
                oldStockGQinCarat.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gq_in_carat]",
                        oldStockList[it].oldStockGQinCarat.toString()
                    )
                )
                oldStockMaintenance_cost.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][maintenance_cost]",
                        oldStockList[it].maintenance_cost.toString()
                    )
                )
                oldStockGemValue.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gem_value]",
                        oldStockList[it].gem_value.toString()
                    )
                )
                oldStockPTAndClipCost.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][pt_and_clip_cost]",
                        oldStockList[it].pt_and_clip_cost.toString()
                    )
                )
                // need to discuss
                oldStockCalculatedBuyingValue.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][calculated_buying_value]",
                        oldStockList[it].rebuyPrice.toString()
                    )
                )

                oldStockPriceForPawn.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][price_for_pawn]",
                        oldStockList[it].priceForPawn.toString()
                    )
                )

                oldStockCalculatedForPawn.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][calculated_for_pawn]",
                        oldStockList[it].calculatedPriceForPawn.toString()
                    )
                )
                oldStockABuyingPrice.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][a_buying_price]",
                        oldStockList[it].oldStockABuyingPrice.toString()
                    )
                )
                oldStockb_voucher_buying_value.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][b_voucher_buying_value]",
                        oldStockList[it].oldStockb_voucher_buying_value.toString()
                    )
                )

                oldStockc_voucher_buying_price.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][c_voucher_buying_price]",
                        oldStockList[it].oldStockc_voucher_buying_value.toString()
                    )
                )
                oldStockDGoldWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][d_gold_weight_ywae]",
                        oldStockList[it].oldStockDGoldWeightY.toString()
                    )
                )
                oldStockEPriceFromNewVoucher.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][e_price_from_new_voucher]",
                        oldStockList[it].oldStockEPriceFromNewVoucher.toString()
                    )
                )
                oldStockFVoucherShownGoldWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][f_voucher_shown_gold_weight_ywae]",
                        oldStockList[it].oldStockFVoucherShownGoldWeightY.toString()
                    )
                )
            }

            viewModel.submitWithKPY(
                productIdList,
                viewModel.getCustomerId(),
                paid_amount,
                reduced_cost,
                MultipartBody.Part.createFormData(
                    "old_voucher_paid_amount",
                    args.oldVoucherPaidAmount.toString()
                ),
                old_stocks_nameList,
                oldStockImageIds,
                oldStockImageFile,
                oldStockCondition,
                oldStockQty,
                oldStockSize,
                oldStockGemWeightY,
                oldStockGoldGemWeightY,
                oldStockImpurityWeightY,
                oldStockGoldWeightY,
                oldStockWastageWeightY,
                oldStockRebuyPrice,
                oldStockGQinCarat,
                oldStockMaintenance_cost,
                oldStockGemValue,
                oldStockPTAndClipCost,
                oldStockCalculatedBuyingValue,
                oldStockPriceForPawn,
                oldStockCalculatedForPawn,
                oldStockABuyingPrice,
                oldStockb_voucher_buying_value,
                oldStockc_voucher_buying_price,
                oldStockDGoldWeightY,
                oldStockEPriceFromNewVoucher,
                oldStockFVoucherShownGoldWeightY
            )
        }
    }

    fun bindPassedData() {
        val oldStocksList = viewModel.getOldStockInfoFromDataBase()
        val oldStockFinalInfo = viewModel.getOldStockFinalInfo()
        val scannedProducts = args.scannedProducts.toList()
        viewModel.getGoldPrice(args.scannedProducts.map { it.id })
        var totalGoldWeight = 0.0
        var totalGemWeight = 0.0
        var totalWastageWeight = 0.0
        var oldStockTotalGoldWeight = 0.0
        var totalMaintenanceFees = 0.0
        var totalGemValue = 0
        var totalPtClipFees = 0
        scannedProducts.forEach {
            totalGoldWeight += it.gold_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() }
            totalGemWeight += it.gem_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() }
            totalWastageWeight += it.wastage_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() }
            totalMaintenanceFees += it.maintenance_cost.let { if (it.isEmpty()) 0.0 else it.toDouble() }
            totalGemValue += it.gem_value.let { if (it.isEmpty()) 0 else it.toInt() }
            totalPtClipFees += it.pt_and_clip_cost.let { if (it.isEmpty()) 0 else it.toInt() }
        }
        oldStocksList.forEach {
            oldStockTotalGoldWeight += it.derived_net_gold_weight_ywae.orEmpty()
                .let { if (it.isEmpty()) 0.0 else it.toDouble() }
        }
        var neededGoldWeight = totalGoldWeight - oldStockTotalGoldWeight
        var totalWastageAndGoldWeight = totalGoldWeight + totalWastageWeight

        val totalGoldWeightKpy = getKPYFromYwae(totalGoldWeight)
        binding.edtTotalGoldWeightK.setText(totalGoldWeightKpy[0].toInt().toString())
        binding.edtTotalGoldWeightP.setText(totalGoldWeightKpy[1].toInt().toString())
        binding.edtTotalGoldWeightY.setText(totalGoldWeightKpy[2].let { String.format("%.2f", it) })

        val totalWastageWeightkpy = getKPYFromYwae(totalWastageWeight)
        binding.edtTotalDiseaseK.setText(totalWastageWeightkpy[0].toInt().toString())
        binding.edtTotalDiseaseP.setText(totalWastageWeightkpy[1].toInt().toString())
        binding.edtTotalDiseaseY.setText(totalWastageWeightkpy[2].let { String.format("%.2f", it) })

        val totalWastageAndGoldWeightkpy = getKPYFromYwae(totalWastageAndGoldWeight)
        binding.edtTotalDiseaseGWK.setText(totalWastageAndGoldWeightkpy[0].toInt().toString())
        binding.edtTotalDiseaseGWP.setText(totalWastageAndGoldWeightkpy[1].toInt().toString())
        binding.edtTotalDiseaseGWY.setText(totalWastageAndGoldWeightkpy[2].let {
            String.format(
                "%.2f",
                it
            )
        })

        val oldStockTotalGoldWeightkpy = getKPYFromYwae(oldStockTotalGoldWeight)
        binding.edtGoldFromHomeWeightK.setText(oldStockTotalGoldWeightkpy[0].toInt().toString())
        binding.edtGoldFromHomeWeightP.setText(oldStockTotalGoldWeightkpy[1].toInt().toString())
        binding.edtGoldFromHomeWeightY.setText(oldStockTotalGoldWeightkpy[2].let {
            String.format(
                "%.2f",
                it
            )
        })

        val neededGoldWeightkpy = getKPYFromYwae(neededGoldWeight)
        binding.edtPoloGoldK.setText(neededGoldWeightkpy[0].toInt().toString())
        binding.edtPoloGoldP.setText(neededGoldWeightkpy[1].toInt().toString())
        binding.edtPoloGoldY.setText(neededGoldWeightkpy[2].let { String.format("%.2f", it) })
        var neededGoldPrice = getKyatsFromKPY(
            neededGoldWeightkpy[0].toInt(),
            neededGoldWeightkpy[1].toInt(),
            neededGoldWeightkpy[2]
        )// multiply with gold price

        if (oldStockFinalInfo != null){
            binding.edtGoldFromHomeValue.setText(oldStockFinalInfo.finalVoucherPaidAmount)
            binding.edtOldVoucherPayment.setText(oldStockFinalInfo.finalVoucherPaidAmount)

        }else{

        }
        binding.edtTotalFee.setText(totalMaintenanceFees.toString())
        binding.edtTotalGemValue.setText(totalGemValue.toString())
        binding.edtPTclipValue.setText(totalPtClipFees.toString())

        // calculation need gold price


    }

}