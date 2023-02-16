package com.example.shwemisale.screen.sellModule.addUser

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
import com.example.shwemi.util.*
import com.example.shwemisale.R
import com.example.shwemisale.data_layers.domain.customers.asUiModel
import com.example.shwemisale.databinding.FragmentCreateNewSellBinding
import com.example.shwemisale.screen.sellModule.sellStart.SellStartViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SellCreateNewFragment:Fragment() {
     private lateinit var binding: FragmentCreateNewSellBinding
    private val viewModel by viewModels<SellCreateNewViewModel>()
    private lateinit var loading : AlertDialog
    private var selectedProvinceId = ""
    private var selectedTownshipId = ""
    private lateinit var datePicker: MaterialDatePicker<Long>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date of Birth")
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCreateNewSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
        val adapterState = ArrayAdapter.createFromResource(requireContext(),
            R.array.state,
            R.layout.spinner_text_style
        )
       // binding.spinnerState.adapter = adapterState
        val adapterTownship = ArrayAdapter.createFromResource(requireContext(),
            R.array.township,
            R.layout.spinner_text_style
        )
        var selectedGender = "female"
        binding.radioGpGender.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == binding.radioBtnMale.id){
                selectedGender = "male"
            }else{
                selectedGender = "female"
            }
        }


        viewModel.provinceLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    val list = it.data!!.map { it.name }.filterNotNull()
                    val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,list)
                    binding.actProvince.addTextChangedListener {editable->
                        selectedProvinceId = it.data!!.find {
                            it.name==binding.actProvince.text.toString()
                        }?.id.toString()
                    }
                    binding.actProvince.setAdapter(arrayAdapter)
                    binding.actProvince.setText(list[0],false)
                    selectedProvinceId = it.data!!.find {
                        it.name==list[0]
                    }?.id.toString()

                    binding.actProvince.setOnClickListener {
                        binding.actProvince.showDropdown(arrayAdapter)
                    }
                    viewModel.resetProvinceLiveData()

                }
                is Resource.Error->{
                    loading.dismiss()

                    viewModel.resetProvinceLiveData()
                }
                else -> {}
            }
        }


        viewModel.townShipLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    val list = it.data!!.map { it.name }.filterNotNull()
                    val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,list)
                    binding.actTownship.addTextChangedListener {editable->
                        selectedTownshipId = it.data!!.find {
                            it.name==binding.actTownship.text.toString()
                        }?.id.toString()
                    }
                    binding.actTownship.setAdapter(arrayAdapter)
                    binding.actTownship.setText(list[0],false)
                    selectedTownshipId = it.data!!.find {
                        it.name==list[0]
                    }?.id.toString()
                    binding.actTownship.setOnClickListener {
                        binding.actTownship.showDropdown(arrayAdapter)
                    }
                    viewModel.resetTownshipLiveData()

                }
                is Resource.Error->{
                    loading.dismiss()

                    viewModel.resetTownshipLiveData()
                }
                else -> {}
            }
        }

        viewModel.addUserLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    loading.show()

                }
                is Resource.Success->{
                    loading.dismiss()
                    requireContext().showSuccessDialog("Customer was successfully created"){
                        viewModel.resetAddUserLiveData()
                        view.findNavController().navigate(SellCreateNewFragmentDirections.actionSellCreateNewFragmentToSellCustomerInfoFragment(it.data!!.asUiModel()))
                    }
                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    viewModel.resetAddUserLiveData()
                }
                else -> {}

            }
        }
        binding.tvBirthDate.setOnClickListener {
            datePicker.show(childFragmentManager, "choose date")
        }
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvBirthDate.text = date
        }

        binding.btnSave.setOnClickListener {
            viewModel.addUser(
                binding.editName.text.toString(),
                binding.editPhNumber.text.toString(),
                binding.tvBirthDate.text.toString(),
                selectedGender,
                selectedProvinceId,
                selectedTownshipId,
                binding.editAddress.text.toString(),
                binding.editNRC.text.toString()
            )
        }


    }
}