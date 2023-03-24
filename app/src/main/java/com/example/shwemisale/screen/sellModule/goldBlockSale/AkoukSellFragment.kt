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
        binding.includePayment.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())

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


        binding.includePayment.btnPrint.setOnClickListener {
            val paid_amount = generateNumberFromEditText(binding.includePayment.edtDeposit)
            viewModel.submitPureGoldSale(
                viewModel.goldPrice,
                viewModel.getCustomerId(),
                paid_amount,
                generateNumberFromEditText(binding.includePayment.edtReducedPay),
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