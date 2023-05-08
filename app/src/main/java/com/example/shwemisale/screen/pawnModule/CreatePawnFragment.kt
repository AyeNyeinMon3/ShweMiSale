package com.example.shwemisale.screen.pawnModule

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.shwemi.util.Resource
import com.example.shwemi.util.convertToSqlDate
import com.example.shwemi.util.generateNumberFromEditText
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.showSuccessDialog
import com.example.shwemisale.databinding.FragmentCreatePawnBinding
import com.example.shwemisale.screen.pawnInterestModule.PawnInterestViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.trimSubstring
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.roundToInt

@AndroidEntryPoint
class CreatePawnFragment : Fragment() {

    lateinit var binding: FragmentCreatePawnBinding
    private val viewModel by viewModels<CreatePawnViewModel>()
    private lateinit var loading: AlertDialog
    private lateinit var datePickerFrom: MaterialDatePicker<Long>
    private lateinit var datePickerTo: MaterialDatePicker<Long>
    private var is_app_functions_allowed = "0"
    private var dateInclude = false

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
        datePickerFrom = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date From")
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
        datePickerTo = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date To")
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
    }

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
            datePickerFrom.show(childFragmentManager, "Choose Date From")
        }
        datePickerFrom.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvChooseDate.text = date
        }

        binding.tvChooseDate2.setOnClickListener {
            datePickerTo.show(childFragmentManager, "Choose Date To")
        }
        datePickerTo.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvChooseDate2.text = date
        }
        viewModel.getPawnInterestRate()
        viewModel.getPawnInterestRateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    binding.edtLoanAmount.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {
                            // TODO Auto-generated method stub
                            var interestRate = "0"
                            if (s.toString().isNotBlank() && s.toString() != "0") {
                                if (s.toString().toLong() > generateNumberFromEditText(binding.edtHighLoanAmount).toLong()){
                                    Toast.makeText(requireContext(),"Must be less than total loan amount",Toast.LENGTH_LONG).show()
                                    binding.edtLoanAmount.text?.clear()
                                    binding.edtMonth.text?.clear()
                                }else{
                                    it.data?.forEach {
                                        if (s.toString().toInt() >= it.range_from!!.toInt()
                                            && s.toString().toInt() <= it.range_to!!.toInt()
                                        ) {
                                            interestRate = it.rate ?: "0"
                                        }
                                    }
                                    // calculated ထားနိုင်သည့်လ= (ဘောင်ချာဖွင့်၀ယ်ပေးငွေ- ချေးယူမည့် ပမာဏ)/ (အတိုးရာခိုင်နှုန်း /100* ချေးယူမည့်ပမာဏ). if result >=6, take 6,  if <=0, take 1
                                    val month = (viewModel.openVoucherPrice()
                                        .toDouble() - generateNumberFromEditText(binding.edtLoanAmount).toDouble()) / ((interestRate.toDouble() / 100) * generateNumberFromEditText(
                                        binding.edtLoanAmount
                                    ).toDouble())
                                    val actualMonth = if (month.isFinite()) month.toString().substringBefore(".").toInt() else 6
                                    if (actualMonth > 6) {
                                        binding.edtMonth.setText("6")
                                    } else {
                                        binding.edtMonth.setText(actualMonth.toString())
                                    }
                                }
                            }
                            binding.edtInterestPercent.setText(interestRate)
                        }


                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            // TODO Auto-generated method stub
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {

                        }
                    })

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
            viewModel.createPawnLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Loading -> {
                        loading.show()
                    }

                    is Resource.Success -> {
                        loading.dismiss()
                        requireContext().showSuccessDialog("Success") {

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