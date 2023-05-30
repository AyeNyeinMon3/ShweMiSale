package com.example.shwemisale.screen.sellModule.goldBlockSale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import com.example.shwemisale.data_layers.domain.pureGoldSale.PureGoldListDomain
import com.example.shwemisale.databinding.DialogAkoukSellAddProductBinding
import com.example.shwemisale.databinding.FragmentAkoukSellBinding
import com.example.shwemisale.screen.goldFromHome.getGramFromYwae
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.example.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections
import com.example.shwemisale.screen.sellModule.openVoucher.withKPY.WithKPYFragmentDirections
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

    override fun onResume() {
        super.onResume()
        binding.includePayment.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
//        binding.includePayment.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())
        binding.edtGoldPrice.addTextChangedListener {
            viewModel.goldPrice = it.toString()
            adapter = AkoukSellRecyclerAdapter(viewModel.goldPrice, {
                showDialogAddProduct(it)
            }, {
                viewModel.deletePureGoldSalesItems(it)
            })

            binding.rvAkoukSell.adapter = adapter
        }
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    binding.edtGoldPrice.setText(it.data!![0].price.toString())
                    adapter = AkoukSellRecyclerAdapter(viewModel.goldPrice, {
                        showDialogAddProduct(it)
                    }, {
                        viewModel.deletePureGoldSalesItems(it)
                    })

                    binding.rvAkoukSell.adapter = adapter
                    viewModel.getPureGoldSalesItems()
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.logoutLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
//                    Toast.makeText(requireContext(),"log out successful", Toast.LENGTH_LONG).show()
                    findNavController().navigate(GeneralSellFragmentDirections.actionGlobalLogout())
                }
                is Resource.Error->{
                    loading.dismiss()
                    findNavController().navigate(GeneralSellFragmentDirections.actionGlobalLogout())

                }
                else -> {}
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
                        totalCost += (it.maintenance_cost!!.toInt() +                                                                 it.threading_fees!!.toInt() + (viewModel.goldPrice.toInt() * ((it.gold_weight_ywae!!.toDouble() / 128) + (it.wastage_ywae!!.toDouble() / 128)))).toInt()
                    }
                    adapter.submitList(it.data)
                    binding.edtGoldPrice.isEnabled = adapter.currentList.size < 1

                    binding.includePayment.edtCharge.setText(getRoundDownForPrice(totalCost).toString())
                }

                is Resource.Error -> {
                    loading.dismiss()
                    if (it.message == "Session key not found!") {
                        adapter.submitList(emptyList())
                        binding.includePayment.edtCharge.text?.clear()
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }

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
        viewModel.deletePureGoldSaleItemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Delete Success") {
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

        viewModel.updatePureGoldSaleItemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Update Success") {
                        viewModel.getPureGoldSalesItems()
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.includePayment.btnEdit.setOnClickListener {
            findNavController().navigate(
                WithKPYFragmentDirections.actionGlobalGoldFromHomeFragment(
                    "Global",
                    null
                )
            )
        }
        binding.btnAdd.setOnClickListener {
            showDialogAddProduct(null)
        }

        binding.includePayment.btnCalculate.setOnClickListener {
            //           ကျန်ငွေ= ပိုလိုတန်ဖိုး- လျော့ပေးငွေ- ပေးသွင်းငွေ
            generateNumberFromEditText(binding.includePayment.edtBalance)
            var poloValue = generateNumberFromEditText(binding.includePayment.edtCharge).toInt() -
                    generateNumberFromEditText(binding.includePayment.edtGoldFromHomeValue).toInt()

            var remainedMoney = poloValue -
                    generateNumberFromEditText(binding.includePayment.edtDeposit).toInt() -
                    generateNumberFromEditText(binding.includePayment.edtReducedPay).toInt()

            binding.includePayment.edtPoloValue.setText(poloValue.toString())
            binding.includePayment.edtBalance.setText(remainedMoney.toString())

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

    fun showDialogAddProduct(item: PureGoldListDomain?) {
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
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialogAlertBinding.btnSelectGm.setOnClickListener {
            val ywae =
                getYwaeFromGram(generateNumberFromEditText(dialogAlertBinding.edtGoldWeightGm).toDouble())
            val kpy = getKPYFromYwae(ywae)
            dialogAlertBinding.edtGoldWeightK.setText(kpy[0].toInt().toString())
            dialogAlertBinding.edtGoldWeightP.setText(kpy[1].toInt().toString())
            dialogAlertBinding.edtGoldWeightY.setText(kpy[2].let { String.format("%.2f", it) })
        }
        dialogAlertBinding.btnSelectKpy.setOnClickListener {
            val ywae = getYwaeFromKPY(
                generateNumberFromEditText(dialogAlertBinding.edtGoldWeightK).toInt(),
                generateNumberFromEditText(dialogAlertBinding.edtGoldWeightP).toInt(),
                generateNumberFromEditText(dialogAlertBinding.edtGoldWeightY).toDouble(),
            )
            val gm = getGramFromYwae(ywae)
            dialogAlertBinding.edtGoldWeightGm.setText(gm.toString())
        }

        var type = ""
        val typeList =
            listOf<String>("အခေါက် အခဲ", "အခေါက် လက်ကောက်", "အခေါက် လက်စွပ်", "အခေါက် အပိုင်း")
        val typeListArrayAdapter =
            ArrayAdapter(requireContext(), R.layout.item_drop_down_text, typeList)

        dialogAlertBinding.actType.setAdapter(typeListArrayAdapter)
        dialogAlertBinding.actType.setText(typeList[0], false)
        type = "0"
        dialogAlertBinding.actType.setOnClickListener {
            dialogAlertBinding.actType.showDropdown(typeListArrayAdapter)
        }
        dialogAlertBinding.actType.addTextChangedListener { editable ->
            type = when (editable.toString()) {
                "အခေါက် အခဲ" -> {
                    "0"
                }

                "အခေါက် လက်ကောက်" -> {
                    "1"
                }

                "အခေါက် လက်စွပ်" -> {
                    "2"
                }

                "အခေါက် အပိုင်း" -> {
                    "3"
                }

                else -> {
                    "0"
                }
            }
        }

        if (item != null) {
            dialogAlertBinding.actType.setText(typeList[item.type!!.toInt()])
            dialogAlertBinding.edtGoldWeightGm.setText(getGramFromYwae(item.gold_weight_ywae!!.toDouble()).toString())
            val goldWeightKpy = getKPYFromYwae(item.gold_weight_ywae.toDouble())
            dialogAlertBinding.edtGoldWeightK.setText(goldWeightKpy[0].toInt().toString())
            dialogAlertBinding.edtGoldWeightP.setText(goldWeightKpy[1].toInt().toString())
            dialogAlertBinding.edtGoldWeightY.setText(goldWeightKpy[2].let {
                String.format(
                    "%.2f",
                    it
                )
            })

            val wastageKpy = getKPYFromYwae(item.wastage_ywae!!.toDouble())
            dialogAlertBinding.edtSellReduceK.setText(wastageKpy[0].toInt().toString())
            dialogAlertBinding.edtSellReduceP.setText(wastageKpy[1].toInt().toString())
            dialogAlertBinding.edtSellReduceY.setText(wastageKpy[2].let {
                String.format(
                    "%.2f",
                    it
                )
            })

            dialogAlertBinding.edtFee.setText(item.maintenance_cost)
            dialogAlertBinding.edtNanHtoeFee.setText(item.threading_fees)
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
                viewModel.updatePureGoldSalesItems(
                    PureGoldListDomain(
                        item.id,
                        goldWeightYwae.toString(),
                        generateNumberFromEditText(dialogAlertBinding.edtFee),
                        generateNumberFromEditText(dialogAlertBinding.edtNanHtoeFee),
                        type,
                        wastageYwae.toString()
                    )
                )
                alertDialog.dismiss()
            }
        } else {
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
                alertDialog.dismiss()
            }
        }
        dialogAlertBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        // alertDialog.window?.setLayout(750,900)
        alertDialog.show()
    }

}