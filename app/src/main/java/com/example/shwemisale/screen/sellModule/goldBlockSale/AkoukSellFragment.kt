package com.example.shwemisale.screen.sellModule.goldBlockSale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.*
import com.example.shwemisale.R
import com.example.shwemisale.databinding.DialogAkoukSellAddProductBinding
import com.example.shwemisale.databinding.FragmentAkoukSellBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class AkoukSellFragment : Fragment() {

    lateinit var binding: FragmentAkoukSellBinding
    lateinit var dialogAlertBinding: DialogAkoukSellAddProductBinding
    private val viewModel by viewModels<AkoukSellViewModel>()
    lateinit var loading: AlertDialog
    lateinit var adapter: AkoukSellRecyclerAdapter
    var oldStockTotalGoldWeightYwae = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAkoukSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()

        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.goldPrice = it.data!![0].price.toString()
                    binding.edtGoldPrice.setText(viewModel.goldPrice)
                    adapter = AkoukSellRecyclerAdapter(viewModel.goldPrice)
                    binding.rvAkoukSell.adapter = adapter

                    viewModel.getPureGoldSalesItems()

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.getPureGoldItemLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    var totalCost = 0
                    var idCount = 0
                    it.data!!.forEach {
                        it.id = idCount++.toString()
                        totalCost += (it.maintenance_cost!!.toInt() + it.threading_fees!!.toInt() + viewModel.goldPrice.toInt() * (it.gold_weight_ywae!!.toDouble() / 128)).toInt()
                    }
                    adapter.submitList(it.data)

                    binding.includePayment.edtCharge.setText(totalCost.toString())
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }

            }
        }
        viewModel.createPureGoldItemLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Success") {
                        viewModel.getPureGoldSalesItems()
                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.submitGoldBlockSell.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Success") {
                        findNavController().popBackStack()
                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }


        binding.btnAdd.setOnClickListener {
            showDialogAddProduct()
        }

        binding.includePayment.btnCalculate.setOnClickListener {
            generateNumberFromEditText(binding.includePayment.edtBalance)
            var poloValue = generateNumberFromEditText(binding.includePayment.edtCharge).toInt()-
                    generateNumberFromEditText(binding.includePayment.edtGoldFromHomeValue).toInt()

            var remainedMoney = generateNumberFromEditText(binding.includePayment.edtCharge).toInt()-
                generateNumberFromEditText(binding.includePayment.edtDeposit).toInt() +
                    generateNumberFromEditText(binding.includePayment.edtReducedPay).toInt() +
                    generateNumberFromEditText(binding.includePayment.edtGoldFromHomeValue).toInt()

             binding.includePayment.edtPoloValue.setText(poloValue.toString())
            binding.includePayment.edtBalance.setText(remainedMoney.toString())

        }
        viewModel.stockFromHomeListInRoom.observe(viewLifecycleOwner) {
            viewModel.stockFromHomeList = it
            it.forEach {
                oldStockTotalGoldWeightYwae += it.goldWeightYwae.orEmpty()
                    .let { if (it.isEmpty()) 0.0 else it.toDouble() }
            }
        }
        viewModel.stockFromHomeFinalInfoInRoom.observe(viewLifecycleOwner) {
            viewModel.stockFromHomeFinalInfo = it
            binding.includePayment.edtGoldFromHomeValue.setText(it.finalVoucherPaidAmount)
        }
        binding.includePayment.btnPrint.setOnClickListener {
            val productIdList = mutableListOf<MultipartBody.Part>()
            val paid_amount = generateNumberFromEditText(binding.includePayment.edtDeposit)

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
            val oldStockGemDetailQty = mutableListOf<MultipartBody.Part>()
            val oldStockGemDetailGm = mutableListOf<MultipartBody.Part>()
            val oldStockGemDetailYwae = mutableListOf<MultipartBody.Part>()
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
                val imageFile = oldStockList[it].image?.let { File(it) }
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
                            file.name,
                            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
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
                repeat(oldStockList[it].gem_details_qty.size) { insideIndex ->
                    oldStockGemDetailQty.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$it][gem_qty]",
                            oldStockList[it].gem_details_qty[insideIndex]
                        )
                    )
                }

                repeat(oldStockList[it].gem_details_gm_per_units.size) { insideIndex ->
                    oldStockGemDetailGm.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$it][gem_weight_gm_per_unit]",
                            oldStockList[it].gem_details_gm_per_units[insideIndex]
                        )
                    )
                }
                repeat(oldStockList[it].gem_details_ywae_per_units.size) { insideIndex ->
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
            viewModel.submitPureGoldSale(
                viewModel.goldPrice,
                viewModel.getCustomerId(),
                paid_amount,
                generateNumberFromEditText(binding.includePayment.edtReducedPay),
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

    }

    fun showDialogAddProduct() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogAlertBinding = DialogAkoukSellAddProductBinding.inflate(
            inflater,
            ConstraintLayout(builder.context),
            false
        )
        builder.setView(dialogAlertBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        var type = ""
        val typeList = listOf<String>("အခဲ", "လက်ကောက်", "လက်စွပ်", "အပိုင်း")
        val typeListArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.item_drop_down_text, typeList)
        dialogAlertBinding.actType.addTextChangedListener { editable ->
            type = when (editable.toString()) {
                "အခဲ" -> {
                    "0"
                }
                "လက်ကောက်" -> {
                    "1"
                }
                "လက်စွပ်" -> {
                    "2"
                }
                "အပိုင်း" -> {
                    "3"
                }
                else -> {
                    "0"
                }
            }
        }
        dialogAlertBinding.actType.setAdapter(typeListArrayAdapter)
        dialogAlertBinding.actType.setText(typeList[0], false)
        dialogAlertBinding.actType.setOnClickListener {
            dialogAlertBinding.actType.showDropdown(typeListArrayAdapter)
        }



        dialogAlertBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }

        // alertDialog.window?.setLayout(750,900)
        dialogAlertBinding.btnContinue.setOnClickListener {
            val goldWeightYwae = getYwaeFromKPY(
                generateNumberFromEditText(dialogAlertBinding.edtGoldWeightK).toInt(),
                generateNumberFromEditText(dialogAlertBinding.edtGoldWeightP).toInt(),
                generateNumberFromEditText(dialogAlertBinding.edtGoldWeightY).toDouble(),
            )
            val wastageYwae = getYwaeFromKPY(
                generateNumberFromEditText(dialogAlertBinding.edtSellReduceK).toInt(),
                generateNumberFromEditText(dialogAlertBinding.edtSellReduceP).toInt(),
                generateNumberFromEditText(dialogAlertBinding.edtSellReduceY).toDouble(),
            )
            viewModel.createPureGoldSaleItem(
                goldWeightYwae.toString(),
                generateNumberFromEditText(dialogAlertBinding.edtFee),
                generateNumberFromEditText(dialogAlertBinding.edtNanHtoeFee),
                type,
                wastageYwae.toString()
            )
//            binding.includePayment.linearLayoutPoloValue.visibility= View.VISIBLE
            alertDialog.dismiss()

        }
        alertDialog.show()
    }

}