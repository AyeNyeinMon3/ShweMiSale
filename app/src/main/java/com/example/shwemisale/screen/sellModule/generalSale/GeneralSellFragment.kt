package com.example.shwemisale.screen.sellModule.generalSale

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
import com.example.shwemisale.data_layers.domain.generalSale.GeneralSaleListDomain
import com.example.shwemisale.databinding.DialogGeneralSellAddProductBinding
import com.example.shwemisale.databinding.FragmentGeneralSellBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.example.shwemisale.screen.sellModule.goldBlockSale.AkoukSellRecyclerAdapter
import com.example.shwemisale.screen.sellModule.openVoucher.withKPY.WithKPYFragmentDirections
import com.example.shwemisale.screen.sellModule.sellStart.SellStartFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralSellFragment : Fragment() {

    lateinit var binding: FragmentGeneralSellBinding
    lateinit var dialogAlertBinding: DialogGeneralSellAddProductBinding
    private val viewModel by viewModels<GeneralSaleViewModel>()
    lateinit var loading: AlertDialog
    lateinit var adapter: GeneralSellRecyclerAdapter
    var generalSaleItemId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentGeneralSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        binding.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
        binding.btnCalculate.setOnClickListener {
            //           ကျန်ငွေ= ပိုလိုတန်ဖိုး- လျော့ပေးငွေ- ပေးသွင်းငွေ
            generateNumberFromEditText(binding.edtBalance)
            var poloValue = generateNumberFromEditText(binding.edtCharge).toInt() -
                    generateNumberFromEditText(binding.edtGoldFromHomeValue).toInt()

            var remainedMoney = poloValue -
                    generateNumberFromEditText(binding.edtDeposit).toInt() -
                    generateNumberFromEditText(binding.edtReducedPay).toInt()

            binding.edtPoloValue.setText(poloValue.toString())
            binding.edtBalance.setText(remainedMoney.toString())
        }
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(
                WithKPYFragmentDirections.actionGlobalGoldFromHomeFragment(
                    "Global",
                    null
                )
            )
        }
        viewModel.getGeneralSaleReasons()
        viewModel.generalSaleReasonLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.generalSaleItemListForMap = it.data

                }

                is Resource.Error -> {
                    loading.dismiss()
                    if (it.message == "Session key not found!") {
                        adapter.submitList(emptyList())
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
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
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.goldPrice = it.data?.find { it.name == "15P GQ" }?.price.orEmpty()
                    viewModel.getGeneralSaleItems()

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        viewModel.createGeneralItemLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Success") {
                        viewModel.getGeneralSaleItems()
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.updateGeneralSaleItemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Update Success") {
                        viewModel.getGeneralSaleItems()
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.deleteGeneralSaleItemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Delete Success") {
                        viewModel.getGeneralSaleItems()
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.submitGeneralSaleLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Success") {
                        viewModel.logout()
//                        findNavController().popBackStack()
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.generalSalesItemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    adapter = GeneralSellRecyclerAdapter(
                        viewModel.goldPrice,
                        viewModel.generalSaleItemListForMap.orEmpty(),
                        {
                            showDialogAddProduct(it)
                        },
                        {
                            viewModel.deleteGeneralSaleItem(it)
                        })
                    binding.rvGeneralSell.adapter = adapter
                    var totalCost = 0
                    var idCount = 0
                    it.data!!.forEach {
                        it.id = idCount++
                        totalCost += (it.maintenance_cost.toInt() + (viewModel.goldPrice.toInt() * (((it.gold_weight_gm.toDouble()/16.6) + (it.wastage_ywae.toDouble()) / 128)))).toInt()
                    }
                    adapter.submitList(it.data)

                    binding.edtCharge.setText(getRoundDownForPrice(totalCost).toString())
                }

                is Resource.Error -> {
                    loading.dismiss()
                    if (it.message == "Session key not found!") {
                        adapter = GeneralSellRecyclerAdapter(
                            viewModel.goldPrice,
                            viewModel.generalSaleItemListForMap.orEmpty(),
                            {
                                showDialogAddProduct(it)
                            },
                            {
                                viewModel.deleteGeneralSaleItem(it)
                            })
                        binding.rvGeneralSell.adapter = adapter
                        adapter.submitList(emptyList())
                        binding.edtCharge.text?.clear()
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                }

            }
        }
        binding.btnAdd.setOnClickListener {
            showDialogAddProduct(null)
        }
        binding.btnPrint.setOnClickListener {
            viewModel.submitGeneralSale(
                generateNumberFromEditText(binding.edtDeposit),
                generateNumberFromEditText(binding.edtReducedPay),
            )
        }
    }

    fun showDialogAddProduct(item: GeneralSaleListDomain?) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogAlertBinding = DialogGeneralSellAddProductBinding.inflate(
            inflater,
            ConstraintLayout(builder.context), false
        )
        builder.setView(dialogAlertBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        viewModel.getGeneralSaleReasons()
        viewModel.generalSaleReasonLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.generalSaleItemListForMap = it.data
                    val reasonList = it.data.orEmpty().map { it.name.orEmpty() }
                    val reasonListAdapter =
                        ArrayAdapter(requireContext(), R.layout.item_drop_down_text, reasonList)
                    dialogAlertBinding.actReason.setAdapter(reasonListAdapter)
                    dialogAlertBinding.actReason.addTextChangedListener { editable ->
                        generalSaleItemId = it.data!!.find {
                            it.name == dialogAlertBinding.actReason.text.toString()
                        }?.id.toString()
                    }
                    dialogAlertBinding.actReason.setText(reasonList[0], false)
                    dialogAlertBinding.actReason.setOnClickListener {
                        dialogAlertBinding.actReason.showDropdown(reasonListAdapter)
                    }

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        if (item != null) {
            dialogAlertBinding.actReason.setText(viewModel.generalSaleItemListForMap!!.find {
                it.id == item.general_sale_item_id
            }?.name.toString())

            dialogAlertBinding.edtGoldWeightGm.setText(item.gold_weight_gm)
            dialogAlertBinding.edtFee.setText(item.maintenance_cost)
            dialogAlertBinding.edtQuantity.setText(item.qty)
            val wastageKpy = getKPYFromYwae(item.wastage_ywae.toDouble())
            dialogAlertBinding.edtUnderCountK.setText(wastageKpy[0].toInt().toString())
            dialogAlertBinding.edtUnderCountP.setText(wastageKpy[1].toInt().toString())
            dialogAlertBinding.edtUnderCountY.setText(wastageKpy[2].let {
                String.format(
                    "%.2f",
                    it
                )
            })
            dialogAlertBinding.btnContinue.setOnClickListener {
                viewModel.updateGeneralSalesItems(
                    GeneralSaleListDomain(
                        item.id,
                        generalSaleItemId,
                        gold_weight_gm = generateNumberFromEditText(dialogAlertBinding.edtGoldWeightGm),
                        maintenance_cost = generateNumberFromEditText(dialogAlertBinding.edtFee),
                        qty = generateNumberFromEditText(dialogAlertBinding.edtQuantity),
                        getYwaeFromKPY(
                            generateNumberFromEditText(dialogAlertBinding.edtUnderCountK).toInt(),
                            generateNumberFromEditText(dialogAlertBinding.edtUnderCountP).toInt(),
                            generateNumberFromEditText(dialogAlertBinding.edtUnderCountY).toDouble()
                        ).toString()
                    )

                )
                alertDialog.dismiss()
            }
        } else {
            dialogAlertBinding.btnContinue.setOnClickListener {
                viewModel.createGeneralSaleItem(
                    generalSaleItemId,
                    gold_weight_gm = generateNumberFromEditText(dialogAlertBinding.edtGoldWeightGm),
                    maintenance_cost = generateNumberFromEditText(dialogAlertBinding.edtFee),
                    qty = generateNumberFromEditText(dialogAlertBinding.edtQuantity),
                    getYwaeFromKPY(
                        generateNumberFromEditText(dialogAlertBinding.edtUnderCountK).toInt(),
                        generateNumberFromEditText(dialogAlertBinding.edtUnderCountP).toInt(),
                        generateNumberFromEditText(dialogAlertBinding.edtUnderCountY).toDouble()
                    ).toString()
                )
                alertDialog.dismiss()
            }
        }

        dialogAlertBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        //alertDialog.window?.setLayout(750,900)


    }

}