package com.example.shwemisale.screen.sellModule.recieveNewOrder

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
import androidx.navigation.findNavController
import com.example.shwemi.util.Resource
import com.example.shwemi.util.convertToSqlDate
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.showDropdown
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentReceiveNewOrderBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.sellModule.SampleListRecyclerAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReceiveNewOrderFragment:Fragment() {

    lateinit var binding:FragmentReceiveNewOrderBinding
    private val viewModel by viewModels<ReceiveNewOrderViewModel>()
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var loading: AlertDialog
    private var selectedGoldType = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentReceiveNewOrderBinding.inflate(inflater).also {
            binding=it
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date")
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
        val adapter = SampleListRecyclerAdapter()
        binding.includeRvSample.rvSampleList.adapter = adapter
       // binding.spinnerGoldType.adapter = adapter
        binding.tvOrderDate.setOnClickListener {
            datePicker.show(childFragmentManager, "choose date")
        }
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvOrderDate.text = date
        }
        viewModel.samplesFromRoom.observe(viewLifecycleOwner){
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    val goldTypeList = it.data!!.map {
                        it.name.orEmpty()
                    }
                    val reasonArrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,goldTypeList)
                    binding.actGoldType.addTextChangedListener {editable->
                        selectedGoldType = it.data!!.find {
                            it.name==binding.actGoldType.text.toString()
                        }?.id.orEmpty()
                        val goldPrice = it.data!!.find {
                            it.name==binding.actGoldType.text.toString()
                        }?.price
                        binding.edtGoldPrice.setText(goldPrice.toString())
                    }
                    binding.actGoldType.setAdapter(reasonArrayAdapter)
                    binding.actGoldType.setText(goldTypeList[0],false)
                    binding.actGoldType.setOnClickListener {
                        binding.actGoldType.showDropdown(reasonArrayAdapter)
                    }

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        binding.btnInventory.setOnClickListener {
            view.findNavController().navigate(ReceiveNewOrderFragmentDirections.actionReceiveNewOrderFragmentToInventoryStockFragment())
        }
        binding.btnOutside.setOnClickListener {
            view.findNavController().navigate(ReceiveNewOrderFragmentDirections.actionReceiveNewOrderFragmentToOutsideStockFragment())
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
            oldStockTotalGoldWeight += it.goldWeightYwae.orEmpty()
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