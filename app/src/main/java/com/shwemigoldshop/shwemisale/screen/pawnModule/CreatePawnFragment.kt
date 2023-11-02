package com.shwemigoldshop.shwemisale.screen.pawnModule

import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shwemigoldshop.shwemisale.printerHelper.AkpDownloader
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.convertMillisecondsToDate
import com.shwemigoldshop.shwemisale.util.convertToSqlDate
import com.shwemigoldshop.shwemisale.util.generateNumberFromEditText
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.util.showSuccessDialog
import com.shwemigoldshop.shwemisale.databinding.FragmentCreatePawnBinding
import com.shwemigoldshop.shwemisale.printerHelper.printPdf
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


@AndroidEntryPoint
class CreatePawnFragment : Fragment() {

    lateinit var binding: FragmentCreatePawnBinding
    private val viewModel by viewModels<CreatePawnViewModel>()
    private lateinit var loading: AlertDialog
    private lateinit var datePickerFrom: MaterialDatePicker<Long>
    private lateinit var datePickerTo: MaterialDatePicker<Long>
    private var is_app_functions_allowed = "0"
    private var dateInclude = false
    private val downloader by lazy { AkpDownloader(requireContext()) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCreatePawnBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val constraintsBuilder = CalendarConstraints.Builder()

        val currentTimeMillis = System.currentTimeMillis()

// Set the minimum date to disable past days

// Set the minimum date to disable past days
        constraintsBuilder.setValidator(object : DateValidator {
            override fun isValid(date: Long): Boolean {
                val today = LocalDate.now()
                val setDate = convertMillisecondsToDate(date)
                return setDate.isAfter(today) || setDate.isEqual(today)
            }

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(parcel: Parcel, i: Int) {}
        })
        val constraints = constraintsBuilder.build()


        datePickerFrom = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date From")
            .setCalendarConstraints(constraints)
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
        datePickerTo = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date To")
            .setCalendarConstraints(constraints)
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()

        binding.cbDate.setOnCheckedChangeListener { compoundButton, ischecked ->
            dateInclude = ischecked
        }
        binding.edtHighLoanAmount.setText(viewModel.getPawnPrice())
        binding.switchApp.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) is_app_functions_allowed = "1" else is_app_functions_allowed = "0"
        }
        binding.tvChooseDate.setOnClickListener {
            if (!datePickerFrom.isAdded) {
                datePickerFrom.show(childFragmentManager, "Choose Date From")
            }
        }
        datePickerFrom.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvChooseDate.text = date
        }

        binding.tvChooseDate2.setOnClickListener {
            if (!datePickerTo.isAdded){
                datePickerTo.show(childFragmentManager, "Choose Date To")
            }
        }
        datePickerTo.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvChooseDate2.text = date
        }
        binding.btnGetInterestRate.setOnClickListener {
            viewModel.getPawnInterestRate(generateNumberFromEditText(binding.edtLoanAmount))
        }
        viewModel.getPawnInterestRateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    val month = (viewModel.openVoucherPrice()
                        .toDouble() - generateNumberFromEditText(binding.edtLoanAmount).toDouble()) / (((it.data?.rate?:"0.0").toDouble() / 100) * generateNumberFromEditText(
                        binding.edtLoanAmount
                    ).toDouble())
                    val actualMonth =
                        if (month.isFinite()) month.toString().substringBefore(".")
                            .toInt() else 6
                    if (actualMonth > 6) {
                        binding.edtMonth.setText("6")
                    } else {
                        binding.edtMonth.setText(actualMonth.toString())
                    }
                    binding.edtInterestPercent.setText(it.data?.rate.orEmpty())

//                    binding.edtLoanAmount.addTextChangedListener(object : TextWatcher {
//                        override fun afterTextChanged(s: Editable) {
//                            // TODO Auto-generated method stub
//                            var interestRate = "0"
//                            if (s.toString().isNotBlank() && s.toString() != "0") {
//                                if (s.toString()
//                                        .toLong() > generateNumberFromEditText(binding.edtHighLoanAmount).toLong()
//                                ) {
//                                    Toast.makeText(
//                                        requireContext(),
//                                        "Must be less than total loan amount",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                    binding.edtLoanAmount.text?.clear()
//                                    binding.edtMonth.text?.clear()
//                                } else {
//                                    it.data?.forEach {
//                                        if (s.toString().toInt() >= it.range_from!!.toInt()
//                                            && s.toString().toInt() <= it.range_to!!.toInt()
//                                        ) {
//                                            interestRate = it.rate ?: "0"
//                                        }
//                                    }
//                                    // calculated ထားနိုင်သည့်လ= (ဘောင်ချာဖွင့်၀ယ်ပေးငွေ- ချေးယူမည့် ပမာဏ)/ (အတိုးရာခိုင်နှုန်း /100* ချေးယူမည့်ပမာဏ). if result >=6, take 6,  if <=0, take 1
//                                    val month = (viewModel.openVoucherPrice()
//                                        .toDouble() - generateNumberFromEditText(binding.edtLoanAmount).toDouble()) / ((interestRate.toDouble() / 100) * generateNumberFromEditText(
//                                        binding.edtLoanAmount
//                                    ).toDouble())
//                                    val actualMonth =
//                                        if (month.isFinite()) month.toString().substringBefore(".")
//                                            .toInt() else 6
//                                    if (actualMonth > 6) {
//                                        binding.edtMonth.setText("6")
//                                    } else {
//                                        binding.edtMonth.setText(actualMonth.toString())
//                                    }
//                                }
//                            }
//                            binding.edtInterestPercent.setText(interestRate)
//                        }
//
//
//                        override fun beforeTextChanged(
//                            s: CharSequence,
//                            start: Int,
//                            count: Int,
//                            after: Int
//                        ) {
//                            // TODO Auto-generated method stub
//                        }
//
//                        override fun onTextChanged(
//                            s: CharSequence,
//                            start: Int,
//                            before: Int,
//                            count: Int
//                        ) {
//
//                        }
//                    })

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
            viewModel.logoutLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Loading -> {
                        loading.show()
                    }

                    is Resource.Success -> {
                        loading.dismiss()
//                    Toast.makeText(requireContext(),"log out successful", Toast.LENGTH_LONG).show()
                        findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections.actionGlobalLogout())
                    }

                    is Resource.Error -> {
                        loading.dismiss()
                        findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections.actionGlobalLogout())

                    }

                    else -> {}
                }
            }

            viewModel.createPawnLiveData.observe(viewLifecycleOwner) {
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
                        printPdf(
                            downloader.downloadFile(it.data.orEmpty()).orEmpty(),
                            requireContext()
                        )
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

            binding.btnPrint.setOnClickListener {
                if (dateInclude) {
                    viewModel.storePawn(
                        binding.edtLoanAmount.text.toString(),
                        binding.edtInterestPercent.text.toString(),
                        binding.edtMonth.text.toString(),
                        binding.tvChooseDate.text.toString(),
                        binding.tvChooseDate2.text.toString(),
                        is_app_functions_allowed
                    )
                } else {
                    viewModel.storePawn(
                        binding.edtLoanAmount.text.toString(),
                        binding.edtInterestPercent.text.toString(),
                        binding.edtMonth.text.toString(),
                        null,
                        null,
                        is_app_functions_allowed
                    )
                }
            }
        }
        // [voucher Buying priceB- debt] / [interest rate/100*(debt))] = result
        // if result >6, take 6 for ထားနိုင်သည့်လ, if result<=0, take 1 for ထားနိုင်သည့်လ
    }

}