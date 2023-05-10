package com.example.shwemisale.screen.sellModule.normalSaleScanStock.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.*
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentStockDetailBinding
import com.example.shwemisale.screen.goldFromHome.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockDetailFragment : Fragment() {
    lateinit var binding: FragmentStockDetailBinding
    private val viewModel by viewModels<ScanStockDetailViewModel>()
    private val args by navArgs<StockDetailFragmentArgs>()
    private lateinit var loading: AlertDialog
    private var selectedGeneralSaleId: String? = null
    private var selectedSize: String? = null
    private var selectedSizeId: String? = null
    private var selectedReasonId: String? = null
    private var stockGoldPrice = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentStockDetailBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()

        viewModel.getProductSizeAndReason(args.productInfo.id)
//        for goldPrice
        if(args.productInfo.order_sale_gold_price.isNullOrEmpty() || args.productInfo.order_sale_gold_price == "0"){
            viewModel.getGoldTypePrice(args.productInfo.gold_type_id)
        }else{
            stockGoldPrice = args.productInfo.order_sale_gold_price!!.toInt()
        }
        binding.edtGoldAndGemWeight.setText(args.productInfo.old_gold_and_gem_weight_gm)
        val gemWeightKpy =
            getKPYFromYwae(args.productInfo.gem_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() })
        binding.edtGemWeightK.setText(gemWeightKpy[0].toInt().toString())
        binding.edtGemWeightP.setText(gemWeightKpy[1].toInt().toString())
        binding.edtGemWeightY.setText(String.format("%.2f", gemWeightKpy[2]))
        binding.edtGemDiamondValue.setText(args.productInfo.gem_value)
        binding.edtPTclipValue.setText(args.productInfo.pt_and_clip_cost)
        binding.edtFee.setText(args.productInfo.maintenance_cost)
        viewModel.productSizeAndReasonLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    val reasonList = it.data!!.reasons.map {
                        it.reason
                    }.toMutableList()
                    reasonList.add("none")
                    val reasonArrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.item_drop_down_text, reasonList)
                    binding.actReason.setAdapter(reasonArrayAdapter)
                    binding.actReason.addTextChangedListener { editable ->
                        var selectedItem = it.data!!.reasons.find {
                            it.reason == binding.actReason.text.toString()
                        }
                        if (selectedItem != null) {
                            binding.edtNewGoldAndGemWeight.isEnabled = true
                            binding.edtOldJade.isEnabled = selectedItem.is_clip_change == "1"
                            binding.edtNewJade.isEnabled = selectedItem.is_clip_change == "1"
                            binding.edtNewGoldAndGemWeight.setText(args.productInfo.gold_and_gem_weight_gm)
                            if (selectedItem.is_clip_change=="1"){
                                binding.edtNewJade.setText(args.productInfo.new_clip_wt_gm)
                                binding.edtOldJade.setText(args.productInfo.old_clip_wt_gm)
                            }else{
                                binding.edtOldJade.text?.clear()
                                binding.edtNewJade.text?.clear()
                            }
                            selectedGeneralSaleId = selectedItem.general_sale_item_id
                            selectedReasonId = selectedItem.id
                        } else {
                            binding.edtNewGoldAndGemWeight.isEnabled = false
                            binding.edtOldJade.isEnabled = false
                            binding.edtNewJade.isEnabled = false
                            binding.edtNewGoldAndGemWeight.text?.clear()
                            binding.edtOldJade.text?.clear()
                            binding.edtNewJade.text?.clear()
                            selectedReasonId = null
                            selectedGeneralSaleId = null
                        }

                    }

//                    binding.actReason.setText(reasonList[0],false)
                    var passedReason = it.data!!.reasons.find {
                        it.id == args.productInfo.edit_reason_id
                    }
                    if (passedReason!=null){
                        binding.actReason.setText(passedReason?.reason.orEmpty(), false)
                    }else{
                        binding.actReason.setText("none",false)
                    }

                    binding.actReason.setOnClickListener {
                        binding.actReason.showDropdown(reasonArrayAdapter)
                    }


                    val sizeList = it.data!!.size.map {
                        it.quantity
                    }
                    val sizeArrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.item_drop_down_text, sizeList)
                    binding.actStockSize.setAdapter(sizeArrayAdapter)
                    if (args.productInfo.size.isEmpty().not()) {
                        binding.actStockSize.setText(args.productInfo.size, false)
                    } else {
//                        binding.actStockSize.setText(
//                            sizeList.let { if (it.isEmpty()) "" else sizeList[0] },
//                            false
//                        )
                    }
                    binding.actStockSize.setOnClickListener {
                        binding.actStockSize.showDropdown(sizeArrayAdapter)
                    }
                    binding.actStockSize.addTextChangedListener { editable ->
                        selectedSizeId = it.data!!.size.find {
                            it.quantity == binding.actStockSize.text.toString()
                        }?.id
                        selectedSize = it.data!!.size.find {
                            it.quantity == binding.actStockSize.text.toString()
                        }?.quantity
                    }

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    if (it.data?.get(0)?.name.orEmpty() == "WG") {
                        stockGoldPrice =
                            (it.data?.get(0)?.price.let { if (it.isNullOrEmpty()) 0 else it.toInt() } * 16.6).toInt()
                    } else {
                        stockGoldPrice =
                            it.data?.get(0)?.price.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.updateProductInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog(it.data!!) {

                        findNavController().popBackStack()
                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        val gemWeightkpy = getKPYFromYwae(args.productInfo.gem_weight_ywae.toDouble())
        binding.edtGemWeightK.setText(gemWeightkpy[0].toInt().toString())
        binding.edtGemWeightP.setText(gemWeightkpy[1].toInt().toString())
        binding.edtGemWeightY.setText(String.format("%.2f", gemWeightkpy[2]))
        binding.edtGemWeightK.isEnabled =
            args.productInfo.gem_weight_ywae.isNotEmpty() && args.productInfo.gem_weight_ywae != "0"
        binding.edtGemWeightP.isEnabled =
            args.productInfo.gem_weight_ywae.isNotEmpty() && args.productInfo.gem_weight_ywae != "0"
        binding.edtGemWeightY.isEnabled =
            args.productInfo.gem_weight_ywae.isNotEmpty() && args.productInfo.gem_weight_ywae != "0"
        binding.edtGemDiamondValue.isEnabled =
            args.productInfo.gem_weight_ywae.isNotEmpty() && args.productInfo.gem_weight_ywae != "0"

        val wastagekpy = getKPYFromYwae(args.productInfo.wastage_weight_ywae.toDouble())
        binding.edtUnderCountK.setText(wastagekpy[0].toInt().toString())
        binding.edtUnderCountP.setText(wastagekpy[1].toInt().toString())
        binding.edtUnderCountY.setText(String.format("%.2f", wastagekpy[2]))

        binding.edtGemDiamondValue.setText(args.productInfo.gem_value)
        binding.edtPTclipValue.setText(args.productInfo.pt_and_clip_cost)
        binding.edtFee.setText(args.productInfo.maintenance_cost)
        binding.edtPromotionPay.setText(args.productInfo.promotion_discount)
        binding.edtStockValue.setText(args.productInfo.cost)

        binding.btnCalculateStockValue.setOnClickListener {
            val goldWeightYwae = if (binding.edtNewGoldAndGemWeight.isEnabled) {
                getYwaeFromGram(generateNumberFromEditText(binding.edtNewGoldAndGemWeight).toDouble()) - getYwaeFromKPY(
                    generateNumberFromEditText(binding.edtGemWeightK).toInt(),
                    generateNumberFromEditText(binding.edtGemWeightP).toInt(),
                    generateNumberFromEditText(binding.edtGemWeightY).toDouble()
                )
            } else {
                getYwaeFromGram(generateNumberFromEditText(binding.edtGoldAndGemWeight).toDouble()) - getYwaeFromKPY(
                    generateNumberFromEditText(binding.edtGemWeightK).toInt(),
                    generateNumberFromEditText(binding.edtGemWeightP).toInt(),
                    generateNumberFromEditText(binding.edtGemWeightY).toDouble()
                )
            }

            val wastageKyat = getKyatsFromKPY(
                generateNumberFromEditText(binding.edtUnderCountK).toInt(),
                generateNumberFromEditText(binding.edtUnderCountP).toInt(),
                generateNumberFromEditText(binding.edtUnderCountY).toDouble()
            )


            val goldPrice = (goldWeightYwae / 128 + wastageKyat) * stockGoldPrice
            //goldPrice =(goldWeightKyat+wastageKyat)*stockGoldPrice
            //stock value = goldPrice + maintainence_cost+ptclip+diamondGemValue-promotionPay
            val stockValue =
                goldPrice + generateNumberFromEditText(binding.edtFee).toInt() + generateNumberFromEditText(
                    binding.edtPTclipValue
                ).toInt() +
                        generateNumberFromEditText(binding.edtGemDiamondValue).toInt() - generateNumberFromEditText(
                    binding.edtPromotionPay
                ).toInt()
            val roundDownStockValue = getRoundDownForPrice(stockValue.toInt())
            binding.edtStockValue.setText(roundDownStockValue.toString())
        }

        binding.btnSelect.setOnClickListener {
//            အလေးချိန်ကွားခြား(kyat)= (လိုချင်သောတန်ဖိုး- stock တန်ဖိုး)/stock gold quality price

            val weightDifference =
                (generateNumberFromEditText(binding.edtValueWanted).toInt() - generateNumberFromEditText(
                    binding.edtStockValue
                ).toInt()) / stockGoldPrice.toDouble()
            val weightDifferencekpy = getKPYFromYwae(weightDifference * 128)
            binding.edtWeightDifferenceK.setText(weightDifferencekpy[0].toInt().toString())
            binding.edtWeightDifferenceP.setText(weightDifferencekpy[1].toInt().toString())
            binding.edtWeightDifferenceY.setText(String.format("%.2f", weightDifferencekpy[2]))

            val goldWeightGm = if (binding.edtNewGoldAndGemWeight.text.isNullOrEmpty()){
                (args.productInfo.gold_weight_ywae.toDouble()/128)*16.6
            }else{
                generateNumberFromEditText(binding.edtNewGoldAndGemWeight).toDouble()
            }

            if (args.productInfo.jewellery_type_id == "4" || args.productInfo.jewellery_type_id == "16") {
                val lengthDifference =
                   ( weightDifference * 16.6 / goldWeightGm)  * generateNumberFromEditText(
                        binding.actStockSize
                    ).toDouble()
                binding.edtLengthDifference.setText(lengthDifference.toString())
            }
//အရှည်ကွာခြား(Kyat)= အလေးချိန်ကွာခြား (K) * stock size/ gold wt of stock (K)

        }

        binding.btnCalculate.setOnClickListener {
            val gemWeightYwae = getYwaeFromKPY(
                generateNumberFromEditText(binding.edtGemWeightK).toInt(),
                generateNumberFromEditText(binding.edtGemWeightP).toInt(),
                generateNumberFromEditText(binding.edtGemWeightY).toDouble(),
            )
            val gemWeightGm = getGramFromYwae(gemWeightYwae)
            if (selectedReasonId != null && generateNumberFromEditText(binding.edtNewGoldAndGemWeight) == "0") {
                Toast.makeText(
                    requireContext(),
                    "Please Fill New Gold And Gem Weight",
                    Toast.LENGTH_LONG
                ).show()
            } else if (binding.edtNewJade.isEnabled && generateNumberFromEditText(binding.edtOldJade) == "0" && generateNumberFromEditText(
                    binding.edtNewJade
                ) == "0"
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please Fill old and new Jade Weight",
                    Toast.LENGTH_LONG
                ).show()

            } else if (gemWeightGm >= generateNumberFromEditText(binding.edtGoldAndGemWeight).toDouble()) {
                Toast.makeText(
                    requireContext(),
                    "GemWeight Must be less than Gold plus Gem Weight",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                viewModel.updateProductInfo(
                    args.productInfo.id,
                    if (!binding.edtNewGoldAndGemWeight.text.isNullOrEmpty()) {
                        generateNumberFromEditText(binding.edtNewGoldAndGemWeight)
                    } else null,
                    gem_weight_ywae = getYwaeFromKPY(
                        generateNumberFromEditText(binding.edtGemWeightK).toInt(),
                        generateNumberFromEditText(binding.edtGemWeightP).toInt(),
                        generateNumberFromEditText(binding.edtGemWeightY).toDouble(),
                    ).toString(),
                    wastage_ywae = getYwaeFromKPY(
                        generateNumberFromEditText(binding.edtUnderCountK).toInt(),
                        generateNumberFromEditText(binding.edtUnderCountP).toInt(),
                        generateNumberFromEditText(binding.edtUnderCountY).toDouble(),
                    ).toString(),
                    generateNumberFromEditText(binding.edtGemDiamondValue),
                    args.productInfo.promotion_discount,
                    selectedSizeId,
                    selectedReasonId,
                    generateNumberFromEditText(binding.edtPTclipValue),
                    generateNumberFromEditText(binding.edtFee),
                    selectedGeneralSaleId,
                    generateNumberFromEditText(binding.edtNewJade),
                    generateNumberFromEditText(binding.edtOldJade)
                )
            }

        }
    }

}