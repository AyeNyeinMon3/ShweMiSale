package com.example.shwemisale.screen.sellModule.sellStart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import com.example.shwemi.util.Resource
import com.example.shwemi.util.convertToSqlDate
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.showDropdown
import com.example.shwemisale.CustomerListRecyclerAdapter
import com.example.shwemisale.R
import com.example.shwemisale.data_layers.domain.customers.asUiModel
import com.example.shwemisale.databinding.FragmentStartSellBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class SellStartFragment : Fragment() {

    private lateinit var binding: FragmentStartSellBinding
    private val viewModel by viewModels<SellStartViewModel>()
    private lateinit var loading : AlertDialog
    private lateinit var datePicker: MaterialDatePicker<Long>

    private var selectedProvinceId = ""
    private var selectedTownshipId = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentStartSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date of Birth")
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        networkcall()
        binding.btnNew.setOnClickListener { view:View->
//            throw RuntimeException("Test Crash")
            view.findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToSellCreateNewFragment())
        }

        val adapter = CustomerListRecyclerAdapter{
            view.findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToSellCustomerInfoFragment(it))

        }
        binding.includeSearchResult.rvCustomerList.adapter = adapter
//        adapter.submitList(listOf(
//            CustomerListData("1","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
//            CustomerListData("2","Daw Than Than"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
//            CustomerListData("3","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
//            CustomerListData("4","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
//            CustomerListData("5","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
//            CustomerListData("6","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
//        ))
        binding.tvBirthDate.setOnClickListener {
            datePicker.show(childFragmentManager, "choose date")
        }
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvBirthDate.text = date
        }

        viewModel.profileLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    viewModel.saveStockFromHomeInfoFinal()
                    val navigationView =
                        requireActivity().findViewById<View>(com.example.shwemisale.R.id.navView) as NavigationView

                    val headerView = navigationView.getHeaderView(0)
                   headerView.findViewById<TextView>(com.example.shwemisale.R.id.tv_name).text = it.data
                    viewModel.getProvince()

                    viewModel.resetProfileLiveData()
                }
                is Resource.Error->{
                    loading.dismiss()
                    if (it.message == "TOKEN_EXPIRED" || it.message == "TOKEN_NOT_PROVIDED" || it.message == "TOKEN_INVALID" ){
                        findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToLoginFragment())
                    }else{
                        findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToLoginFragment())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }

                    viewModel.resetProfileLiveData()
                }
                else -> {}
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
                        viewModel.getTownShip(selectedProvinceId)

                    }
                    binding.actProvince.setAdapter(arrayAdapter)
                    binding.actProvince.setText(list[0],false)
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
                    binding.actTownship.setText(if (list.isEmpty()) "" else list[0],false)
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



        binding.imageBtnSearch.setOnClickListener {
            val name =
                if (binding.editName.text.isNullOrEmpty()) null else binding.editName.text.toString()
            val phoneNumber =
                if (binding.editPhNumber.text.isNullOrEmpty()) null else binding.editPhNumber.text.toString()
            val birthday =
                if (binding.tvBirthDate.text.isNullOrEmpty() || binding.tvBirthDate.text == "မွေးနေ့") null else binding.tvBirthDate.text.toString()
            val nrc =
                if (binding.editNRC.text.isNullOrEmpty()) null else binding.editNRC.text.toString()

            viewModel.getCustomerInfo(
                null,
                name,
                phoneNumber,
                birthday,
                null,
                selectedProvinceId,
                selectedTownshipId,
                null,
                nrc
            ).observe(viewLifecycleOwner) {
                lifecycleScope.launch {
                    adapter.submitData(it.map { it.asUiModel() })
                }
            }
        }
    }

    fun networkcall(){
        viewModel.getProfile()
    }

}









