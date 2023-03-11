package com.example.shwemisale.screen.sellModule.openVoucher.withValue

import android.os.Bundle
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
import com.example.shwemisale.databinding.FragmentWithValueBinding
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody

@AndroidEntryPoint
class WithValueFragment : Fragment() {
    lateinit var binding: FragmentWithValueBinding
    private val viewModel by viewModels<WithValueViewModel>()
    private val args by navArgs<WithValueFragmentArgs>()
    private lateinit var loading: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentWithValueBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        bindPassedData()
        viewModel.stockFromHomeFinalInfoInRoom.observe(viewLifecycleOwner){
            viewModel.stockFromHomeFinalInfo = it
        }
        viewModel.stockFromHomeListInRoom.observe(viewLifecycleOwner){
            viewModel.stockFromHomeList = it
            var oldStockTotalGoldWeight = 0.0
            it.forEach {
                oldStockTotalGoldWeight += it.goldWeightYwae.orEmpty()
                    .let { if (it.isEmpty()) 0.0 else it.toDouble() }
            }
            val goldFromHomeValue =
                (oldStockTotalGoldWeight / 128 * viewModel.goldPrice.toInt()).toInt()
            binding.edtGoldFromHomeValue.setText(goldFromHomeValue.toString())
        }
        viewModel.getGoldPriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.goldPrice = it.data?.gold_price.toString()

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.submitWithValueLiveData.observe(viewLifecycleOwner) {
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
            val finalInfo = viewModel.stockFromHomeFinalInfo

            /** old stock list manipulation */
            //List from room
            val oldStockList = viewModel.stockFromHomeList

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
            val oldStockGemDetailQty =  mutableListOf<MultipartBody.Part>()
            val oldStockGemDetailGm =  mutableListOf<MultipartBody.Part>()
            val oldStockGemDetailYwae =  mutableListOf<MultipartBody.Part>()
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
                repeat(oldStockList[it].gem_details_qty.size){insideIndex->
                    oldStockGemDetailQty.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$it][gem_qty]",
                           oldStockList[it].gem_details_qty[insideIndex]
                        )
                    )
                }

                repeat(oldStockList[it].gem_details_gm_per_units.size){insideIndex->
                    oldStockGemDetailGm.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$it][gem_weight_gm_per_unit]",
                            oldStockList[it].gem_details_gm_per_units[insideIndex]
                        )
                    )
                }
                repeat(oldStockList[it].gem_details_ywae_per_units.size){insideIndex->
                    oldStockGemDetailYwae.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$it][gem_weight_ywae_per_unit]",
                            oldStockList[it].gem_details_ywae_per_units[insideIndex]
                        )
                    )
                }


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

            viewModel.submitWithValue(
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
                oldStockGemDetailQty,
                oldStockGemDetailGm,
                oldStockGemDetailYwae,
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
        binding.btnCalculate.setOnClickListener {
            val remainAmount = generateNumberFromEditText(binding.edtCharge).toInt() - generateNumberFromEditText(binding.edtDeposit).toInt()
            binding.edtBalance.setText(remainAmount.toString())
        }


    }

    fun bindPassedData() {
        viewModel.getGoldPrice(args.scannedProducts.map { it.id })
        var totalProductsCost = 0
        var charge = 0

        args.scannedProducts.forEach {
            totalProductsCost += it.cost.toInt()
            charge += it.pt_and_clip_cost.toInt()
            charge += it.maintenance_cost.toInt()
            charge += it.cost.toInt()
            charge -= it.promotion_discount.toInt()
        }
        var oldVoucherPaidAmount = args.oldVoucherPaidAmount
        binding.edtTotalValue.setText(totalProductsCost.toString())
        binding.edtOldVoucherPayment.setText(oldVoucherPaidAmount.toString())
        binding.edtCharge.setText(charge.toString())

    }

}