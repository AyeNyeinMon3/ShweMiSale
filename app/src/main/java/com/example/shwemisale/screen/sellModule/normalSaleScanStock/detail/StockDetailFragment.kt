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
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.sellModule.normalSaleScanStock.ScanStockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockDetailFragment:Fragment() {
    lateinit var binding: FragmentStockDetailBinding
    private val viewModel by viewModels<ScanStockViewModel>()
    private val args by navArgs<StockDetailFragmentArgs>()
    private lateinit var loading: AlertDialog
    private var selectedGeneralSaleId:String? = null
    private var selectedSize:String? = null
    private var selectedReasonId:String? = null
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
        loading=requireContext().getAlertDialog()

            viewModel.getProductSizeAndReason(args.productInfo.id)
//        for goldPrice
            viewModel.getGoldTypePrice(args.productInfo.gold_type_id)
            binding.edtGoldAndGemWeight.setText(args.productInfo.gold_and_gem_weight_gm)
        viewModel.productSizeAndReasonLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    val reasonList = it.data!!.reasons.map {
                        it.reason
                    }
                    val reasonArrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,reasonList)
                    binding.actReason.addTextChangedListener {editable->
                        var slectedItem = it.data!!.reasons.find {
                            it.reason==binding.actReason.text.toString()
                        }
                        binding.edtNewGoldAndGemWeight.isEnabled = true
                        binding.edtOldJade.isEnabled = slectedItem?.is_clip_change =="1"
                        binding.edtNewJade.isEnabled = slectedItem?.is_clip_change =="1"
                        selectedGeneralSaleId = slectedItem?.general_sale_item_id

                        selectedReasonId = it.data!!.reasons.find {
                            it.reason==binding.actReason.text.toString()
                        }?.id
                    }
                    binding.actReason.setAdapter(reasonArrayAdapter)
//                    binding.actReason.setText(reasonList[0],false)
                    binding.actReason.setOnClickListener {
                        binding.actReason.showDropdown(reasonArrayAdapter)
                    }

                    val sizeList = it.data!!.size.map {
                        it.quantity
                    }
                    val sizeArrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,sizeList)
                    binding.actStockSize.addTextChangedListener {editable->
                        selectedSize = it.data!!.size.find {
                            it.quantity==binding.actStockSize.text.toString()
                        }?.id
                    }
                    binding.actStockSize.setAdapter(sizeArrayAdapter)
                    binding.actStockSize.setText(sizeList[0],false)
                    binding.actStockSize.setOnClickListener {
                        binding.actStockSize.showDropdown(sizeArrayAdapter)
                    }
                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    stockGoldPrice = it.data!![0].price?.toInt()?:0
                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.updateProductInfoLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    requireContext().showSuccessDialog(it.data!!){
                        viewModel.getProductInfo(args.productInfo.id)
                        findNavController().popBackStack()
                    }
                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        val gemWeightkpy = getKPYFromYwae(args.productInfo.gem_weight_ywae.toDouble())
        binding.edtGemWeightK.setText(gemWeightkpy[0].toInt().toString())
        binding.edtGemWeightP.setText(gemWeightkpy[1].toInt().toString())
        binding.edtGemWeightY.setText(String.format("%.2f", gemWeightkpy[2]))

        val wastagekpy = getKPYFromYwae(args.productInfo.wastage_weight_ywae.toDouble())
        binding.edtUnderCountK.setText(wastagekpy[0].toInt().toString())
        binding.edtUnderCountP.setText(wastagekpy[1].toInt().toString())
        binding.edtUnderCountY.setText(String.format("%.2f", wastagekpy[2]))

        binding.edtGemDiamondValue.setText(args.productInfo.gem_value)
        binding.edtPTclipValue.setText(args.productInfo.pt_and_clip_cost)
        binding.edtFee.setText(args.productInfo.maintenance_cost)
        binding.edtPromotionPay.setText(args.productInfo.promotion_discount)
        binding.edtStockValue.setText(args.productInfo.cost)

        binding.btnSelect.setOnClickListener {
//            အလေးချိန်ကွားခြား(kyat)= (လိုချင်သောတန်ဖိုး- stock တန်ဖိုး)/stock gold quality price
            val weightDifference = generateNumberFromEditText(binding.edtValueWanted).toDouble()/ stockGoldPrice.toDouble()
            val weightDifferencekpy = getKPYFromYwae(weightDifference * 128)
            binding.edtWeightDifferenceK.setText(weightDifferencekpy[0].toInt().toString())
            binding.edtWeightDifferenceP.setText(weightDifferencekpy[1].toInt().toString())
            binding.edtWeightDifferenceY.setText(String.format("%.2f", weightDifferencekpy[2]))

//အရှည်ကွာခြား(Kyat)= အလေးချိန်ကွာခြား (K) * stock size/ gold wt of stock (K)

        }
        binding.btnCalculate.setOnClickListener {
            viewModel.updateProductInfo(
                args.productInfo.id,
                args.productInfo.gold_and_gem_weight_gm,
                args.productInfo.gem_weight_ywae,
                args.productInfo.gem_value,
                args.productInfo.promotion_discount,
                selectedSize,
                selectedReasonId,
                generateNumberFromEditText(binding.edtPTclipValue),
                selectedGeneralSaleId,
                generateNumberFromEditText(binding.edtNewJade),
                generateNumberFromEditText(binding.edtOldJade)
            )
        }
    }
}