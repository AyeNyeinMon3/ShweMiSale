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
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.*
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentReceiveNewOrderBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.example.shwemisale.screen.sellModule.SampleListRecyclerAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

@AndroidEntryPoint
class ReceiveNewOrderFragment : Fragment() {

    lateinit var binding: FragmentReceiveNewOrderBinding
    private val viewModel by viewModels<ReceiveNewOrderViewModel>()
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var loading: AlertDialog
    private var selectedGoldType = ""

    var oldStockTotalGoldWeightYwae = 0.0
    var totalEstimatedWastageYwae = 0.0
    var totalGoldAndWastageYwae = 0.0
    var neededGoldWeightYwae = 0.0
    var orderedGoldWeightYwae = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentReceiveNewOrderBinding.inflate(inflater).also {
            binding = it
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
//        binding.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())
//        viewModel.goldFromHomeWithValue = if (viewModel.getTotalCVoucherBuyingPrice()
//                .isEmpty()
//        ) "0" else viewModel.getTotalCVoucherBuyingPrice()
        val totalGoldWeightKpy = getKPYFromYwae(
            viewModel.getTotalGoldWeightYwae().let { if (it.isEmpty()) 0.0 else it.toDouble() })
        binding.edtGoldFromHomeWeightK.setText(totalGoldWeightKpy[0].toInt().toString())
        binding.edtGoldFromHomeWeightP.setText(totalGoldWeightKpy[1].toInt().toString())
        binding.edtGoldFromHomeWeightY.setText(totalGoldWeightKpy[2].let {
            String.format(
                "%.2f",
                it
            )
        })

        val adapter = SampleListRecyclerAdapter()
        binding.includeRvSample.rvSampleList.adapter = adapter
        // binding.spinnerGoldType.adapter = adapter
        binding.tvOrderDate.setOnClickListener {
            if (!datePicker.isAdded) {
                datePicker.show(childFragmentManager, "choose date")

            }
        }
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvOrderDate.text = date
        }
        viewModel.samplesFromRoom.observe(viewLifecycleOwner) {
            viewModel.takenSamples = it
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
                    val reasonArrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.item_drop_down_text, goldTypeList)
                    binding.actGoldType.addTextChangedListener { editable ->
                        selectedGoldType = it.data!!.find {
                            it.name == binding.actGoldType.text.toString()
                        }?.id.orEmpty()
                        val goldPrice = it.data!!.find {
                            it.name == binding.actGoldType.text.toString()
                        }?.price
                        if (goldPrice != null) {
                            viewModel.goldPrice = goldPrice.toInt()
                        }
//                        binding.edtGoldPrice.setText(goldPrice.toString())
                    }
                    binding.actGoldType.setAdapter(reasonArrayAdapter)
//                    binding.actGoldType.setText(goldTypeList[0], false)
                    binding.actGoldType.setOnClickListener {
                        binding.actGoldType.showDropdown(reasonArrayAdapter)
                    }
//                    viewModel.goldFromHomeWithKpy =
//                        (viewModel.goldPrice * viewModel.getTotalGoldWeightYwae()
//                            .let { if (it.isEmpty()) 0.0 else it.toDouble() } / 128).toString()
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        viewModel.receiveNewOrderLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog(it.data!!) {

                    }

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == binding.radioBtnWithKpy.id) {
                binding.edtGoldFromHomeValue.text?.clear()
                val totalGoldWeightKpy =
                    getKPYFromYwae(viewModel.getTotalGoldWeightYwae().toDouble())
                binding.edtGoldFromHomeWeightK.setText(totalGoldWeightKpy[0].toInt().toString())
                binding.edtGoldFromHomeWeightP.setText(totalGoldWeightKpy[1].toInt().toString())
                binding.edtGoldFromHomeWeightY.setText(totalGoldWeightKpy[2].let {
                    String.format(
                        "%.2f",
                        it
                    )
                })
                calculate()
            } else {
                binding.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())
                binding.edtGoldFromHomeWeightK.text?.clear()
                binding.edtGoldFromHomeWeightP.text?.clear()
                binding.edtGoldFromHomeWeightY.text?.clear()
                calculate()
            }
        }
        binding.btnEditGoldFromHomeValue.setOnClickListener {
            findNavController().navigate(
                ReceiveNewOrderFragmentDirections.actionGlobalGoldFromHomeFragment(
                    "Global",
                    null
                )
            )
        }
        binding.btnEditGoldFromHomeWeight.setOnClickListener {
            findNavController().navigate(
                ReceiveNewOrderFragmentDirections.actionGlobalGoldFromHomeFragment(
                    "Global",
                    null
                )
            )
        }

        binding.btnInventory.setOnClickListener {
            view.findNavController()
                .navigate(ReceiveNewOrderFragmentDirections.actionReceiveNewOrderFragmentToInventoryStockFragment())
        }
        binding.btnOutside.setOnClickListener {
            view.findNavController()
                .navigate(ReceiveNewOrderFragmentDirections.actionReceiveNewOrderFragmentToOutsideStockFragment())
        }

        binding.btnCalculate.setOnClickListener {

           calculate()
        }



        binding.btnPrint.setOnClickListener {
            val paid_amount = generateNumberFromEditText(binding.edtDeposit)
            val sampleIdMultiPartList = mutableListOf<MultipartBody.Part>()
            val sampleIdList = viewModel.takenSamples.map { it.id }
            repeat(sampleIdList.size) {
                sampleIdMultiPartList.add(
                    MultipartBody.Part.createFormData(
                        "samples[][sample_id]",
                        sampleIdList[it].toString()
                    )
                )
            }


            viewModel.submit(
                binding.edtOrderItem.text.toString(),
                selectedGoldType,
                viewModel.goldPrice.toString(),
                orderedGoldWeightYwae.toString(),
                totalEstimatedWastageYwae.toString(),
                generateNumberFromEditText(binding.edtQuantity),
                generateNumberFromEditText(binding.edtGemValue),
                generateNumberFromEditText(binding.edtFee),
                binding.tvOrderDate.text.toString(),
                binding.edtNote.text.toString(),
                viewModel.getCustomerId(),
                paid_amount,
                "0",
                oldStockSampleListId = sampleIdMultiPartList
            )
        }

    }
fun calculate(){
    val singleWastageYwae = getYwaeFromKPY(
        generateNumberFromEditText(binding.edtSingleEstimatedWastageK).toInt(),
        generateNumberFromEditText(binding.edtSingleEstimatedWastageP).toInt(),
        generateNumberFromEditText(binding.edtSingleEstimatedWastageY).toDouble(),
    )

    orderedGoldWeightYwae = getYwaeFromKPY(
        generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
        generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
        generateNumberFromEditText(binding.edtGoldWeightY).toDouble(),
    )*generateNumberFromEditText(binding.edtQuantity).toInt()

    totalEstimatedWastageYwae =
        generateNumberFromEditText(binding.edtQuantity).toInt() * singleWastageYwae
    val totalEstimatedWastageKpy = getKPYFromYwae(totalEstimatedWastageYwae)
    binding.edtEstimatedUnderCountK.setText(totalEstimatedWastageKpy[0].toInt().toString())
    binding.edtEstimatedUnderCountP.setText(totalEstimatedWastageKpy[1].toInt().toString())
    binding.edtEstimatedUnderCountY.setText(totalEstimatedWastageKpy[2].let {
        String.format(
            "%.2f",
            it
        )
    })

    totalGoldAndWastageYwae = orderedGoldWeightYwae + totalEstimatedWastageYwae
    val totalGoldAndWastageKpy = getKPYFromYwae(totalGoldAndWastageYwae)
    binding.edtDiseaseGoldWeightK.setText(totalGoldAndWastageKpy[0].toInt().toString())
    binding.edtDiseaseGoldWeightP.setText(totalGoldAndWastageKpy[1].toInt().toString())
    binding.edtDiseaseGoldWeightY.setText(totalGoldAndWastageKpy[2].let {
        String.format(
            "%.2f",
            it
        )
    })

    if (binding.radioBtnWithKpy.isChecked) {
        oldStockTotalGoldWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtGoldFromHomeWeightK).toInt(),
            generateNumberFromEditText(binding.edtGoldFromHomeWeightP).toInt(),
            generateNumberFromEditText(binding.edtGoldFromHomeWeightY).toDouble(),
        )
    }else{
        oldStockTotalGoldWeightYwae = 0.0
    }

    neededGoldWeightYwae = totalGoldAndWastageYwae - oldStockTotalGoldWeightYwae
    val neededGoldWeightKpy = getKPYFromYwae(neededGoldWeightYwae)
    binding.edtPoloGoldWeightK.setText(neededGoldWeightKpy[0].toInt().toString())
    binding.edtPoloGoldWeightP.setText(neededGoldWeightKpy[1].toInt().toString())
    binding.edtPoloGoldWeightY.setText(neededGoldWeightKpy[2].let {
        String.format(
            "%.2f",
            it
        )
    })


    val poloValue =
        (neededGoldWeightYwae / 128) * viewModel.goldPrice
    binding.edtPoloValue.setText(getRoundDownForPrice(poloValue.toInt()).toString())
    val goldFromHomeValue = if (binding.radioBtnWithKpy.isChecked){
        0.0
    }else{
        generateNumberFromEditText(binding.edtGoldFromHomeValue).toDouble()
    }
    val estimatedCharge =
        poloValue - goldFromHomeValue + generateNumberFromEditText(
            binding.edtFee
        ).toDouble() + generateNumberFromEditText(binding.edtGemValue).toDouble()
    binding.edtEstimatedCharge.setText(
        getRoundDownForPrice(estimatedCharge.toInt()).toString()
    )
    val remainedMoney =
        estimatedCharge - generateNumberFromEditText(binding.edtDeposit).toInt()
    binding.edtBalance.setText(remainedMoney.toInt().toString())
}

}