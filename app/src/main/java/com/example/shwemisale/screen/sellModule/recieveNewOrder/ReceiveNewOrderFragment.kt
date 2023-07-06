package com.example.shwemisale.screen.sellModule.recieveNewOrder

import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.satoprintertest.AkpDownloader
import com.example.shwemi.util.*
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentReceiveNewOrderBinding
import com.example.shwemisale.printerHelper.printPdf
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.example.shwemisale.screen.sellModule.SampleListRecyclerAdapter
import com.example.shwemisale.screen.sellModule.exchangeOrderAndOldItem.ExchangeOrderFragmentDirections
import com.example.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections
import com.google.android.material.datepicker.CalendarConstraints
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
    private val downloader by lazy { AkpDownloader(requireContext()) }

    var oldStockTotalGoldWeightYwae = 0.0
    var totalEstimatedWastageYwae = 0.0
    var totalGoldAndWastageYwae = 0.0
    var neededGoldWeightYwae = 0.0
    var orderedGoldWeightYwae = 0.0

    var estimatedCharge = 0.0
    var poloValue = 0.0

    override fun onResume() {
        super.onResume()
        if (binding.radioBtnWithKpy.isChecked) {
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
        }else{
            binding.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())
        }

    }

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
        val constraintsBuilder = CalendarConstraints.Builder()

        val currentTimeMillis = System.currentTimeMillis()

// Set the minimum date to disable past days

// Set the minimum date to disable past days
        constraintsBuilder.setValidator(object : CalendarConstraints.DateValidator {
            override fun isValid(date: Long): Boolean {
                return date >= currentTimeMillis
            }

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(parcel: Parcel, i: Int) {}
        })
        val constraints = constraintsBuilder.build()
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date")
            .setCalendarConstraints(constraints)
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.M)
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

        val adapter = SampleListRecyclerAdapter{
            viewModel.removeSampleFromRoom(it)
        }
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
                    }.filter { it != "Rebuy Price [100%]" && it !="WG" }
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
                            viewModel.getStockFromHomeList()
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
        viewModel.stockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.updateStockFromHome(
                        viewModel.goldPrice.toString(),
                        it.data.orEmpty()
                    )
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.updateStockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), "Old Stock's Data Updated", Toast.LENGTH_LONG)
                        .show()
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

        viewModel.receiveNewOrderLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.pdfDownloadLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    printPdf(downloader.downloadFile(it.data.orEmpty()).orEmpty(), requireContext())
                    requireContext().showSuccessDialog("Press Ok When Printing is finished!") {
                        viewModel.logout()
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
            if (generateNumberFromEditText(binding.edtQuantity).toInt() > 0){
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
//            if(generateNumberFromEditText(binding.edtDeposit).toDouble()>poloValue){
//                Toast.makeText(requireContext(),"ပေးသွင်းငွေသည် ပိုလိုတန်ဖိုးထက် မကြီးရန်",Toast.LENGTH_LONG).show()
//            }else{
                viewModel.submit(
                    binding.edtOrderItem.text.toString(),
                    selectedGoldType,
                    viewModel.goldPrice.toString(),
                    orderedGoldWeightYwae.toString(),
                    getYwaeFromKPY(
                        generateNumberFromEditText(binding.edtSingleEstimatedWastageK).toInt(),
                        generateNumberFromEditText(binding.edtSingleEstimatedWastageP).toInt(),
                        generateNumberFromEditText(binding.edtSingleEstimatedWastageY).toDouble(),
                    ).toString(),
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
            }else{
                Toast.makeText(requireContext(),"Qty must be larger than zero",Toast.LENGTH_LONG).show()
            }

        }

    }

    fun calculate() {
        val singleWastageYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtSingleEstimatedWastageK).toInt(),
            generateNumberFromEditText(binding.edtSingleEstimatedWastageP).toInt(),
            generateNumberFromEditText(binding.edtSingleEstimatedWastageY).toDouble(),
        )

        orderedGoldWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightY).toDouble(),
        )

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
        } else {
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


        poloValue =
            (neededGoldWeightYwae / 128) * viewModel.goldPrice
        binding.edtPoloValue.setText(getRoundDownForPrice(poloValue.toInt()).toString())

        val goldFromHomeValue = if (binding.radioBtnWithKpy.isChecked) {
            0.0
        } else {
            generateNumberFromEditText(binding.edtGoldFromHomeValue).toDouble()
        }
        estimatedCharge =
            poloValue + generateNumberFromEditText(
                binding.edtFee
            ).toDouble() + generateNumberFromEditText(binding.edtGemValue).toDouble() - goldFromHomeValue

        binding.edtEstimatedCharge.setText(
            getRoundDownForPrice(estimatedCharge.toInt()).toString()
        )
        val remainedMoney =
            estimatedCharge - generateNumberFromEditText(binding.edtDeposit).toLong()
        binding.edtBalance.setText(getRoundDownForPrice(remainedMoney.toInt()).toString())
    }

}