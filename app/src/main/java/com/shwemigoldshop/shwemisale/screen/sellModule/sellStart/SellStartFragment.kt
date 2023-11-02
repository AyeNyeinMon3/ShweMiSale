package com.shwemigoldshop.shwemisale.screen.sellModule.sellStart

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import com.epson.epos2.printer.Printer
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.convertToSqlDate
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.util.showDropdown
import com.shwemigoldshop.shwemisale.R
import com.shwemigoldshop.shwemisale.data_layers.domain.customers.asUiModel
import com.shwemigoldshop.shwemisale.databinding.DialogIpAddressBinding
import com.shwemigoldshop.shwemisale.databinding.FragmentStartSellBinding
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.qrscan.getBarLauncher
import com.shwemigoldshop.shwemisale.qrscan.scanQrCode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.shwemigoldshop.shwemisale.screen.sellModule.CustomerListRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SellStartFragment : Fragment() {

    private lateinit var binding: FragmentStartSellBinding
    private val viewModel by viewModels<SellStartViewModel>()
    private lateinit var loading : AlertDialog
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var dialogIpAddressBinding:DialogIpAddressBinding
    private lateinit var barlauncer: Any
    private lateinit var barlauncherForDeviceId: Any

    private var townShipList = mutableListOf<String>()
    private var provinceList = mutableListOf<String>()

    @Inject
    lateinit var printer:Printer

    @Inject
    lateinit var localDatabase:LocalDatabase

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
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()

        barlauncherForDeviceId = this.getBarLauncher(requireContext()) {
            //Scan uuid if not in shared preference
            viewModel.saveDeviceIdFromServer(it)
            viewModel.authorizeApp()
        }
        viewModel.getDeviceIdFromSharedPreference()
        viewModel.deviceIdLogInLiveData.observe(viewLifecycleOwner){
            if (it.isNullOrEmpty()){
                this.scanQrCode(requireActivity().applicationContext,barlauncherForDeviceId)
            }else{
                networkcall()
            }
        }
        viewModel.authorizeAppLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{

                }
                is Resource.Success->{
                    loading.dismiss()

                }
                is Resource.Error->{
                    loading.dismiss()
                    findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToLoginFragment())
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                }
                else->{}
            }
        }
        val adapter = CustomerListRecyclerAdapter{
            view.findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToSellCustomerInfoFragment(it))

        }
        binding.includeSearchResult.rvCustomerList.adapter = adapter
        barlauncer = this.getBarLauncher(requireContext()) {
            viewModel.getCustomerInfo(it,null,null,null, null, null , null , null, null).observe(viewLifecycleOwner) {
                lifecycleScope.launch {
                    adapter.submitData(it.map { it.asUiModel() })
                }
            }
        }
        binding.ivScan.setOnClickListener {
            this.scanQrCode(requireContext(),barlauncer)
        }
        binding.ivScanner.setOnClickListener {
            this.scanQrCode(requireContext(),barlauncer)
        }
        binding.btnNew.setOnClickListener { view:View->
//            throw RuntimeException("Test Crash")
            view.findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.sellStart.SellStartFragmentDirections.actionSellStartFragmentToSellCreateNewFragment())

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

        viewModel.profileLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    viewModel.saveStockFromHomeInfoFinal()
                    val navigationView =
                        requireActivity().findViewById<View>(com.shwemigoldshop.shwemisale.R.id.navView) as NavigationView

                    val headerView = navigationView.getHeaderView(0)
                   headerView.findViewById<TextView>(com.shwemigoldshop.shwemisale.R.id.tv_name).text = it.data
                    viewModel.getProvince()

                    viewModel.resetProfileLiveData()
                    Toast.makeText(requireContext(),"connection status is ${printer.status.connection.toString()}",Toast.LENGTH_LONG).show()

                    //auto search when app start
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
                is Resource.Error->{
                    loading.dismiss()
                    if (it.message == "TOKEN_EXPIRED" || it.message == "TOKEN_NOT_PROVIDED" || it.message == "TOKEN_INVALID" ){
                        findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.sellStart.SellStartFragmentDirections.actionSellStartFragmentToLoginFragment())
                    }else{
                        findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.sellStart.SellStartFragmentDirections.actionSellStartFragmentToLoginFragment())
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
                    provinceList = it.data!!.map { it.name }.filterNotNull().toMutableList()
                    provinceList.add("None")
                    val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,provinceList)
                    binding.actProvince.addTextChangedListener {editable->
                        if (editable.toString() == "None"){
                            selectedProvinceId = ""
                            viewModel.getTownShip(selectedProvinceId)

                        }else{
                            selectedProvinceId = it.data!!.find {
                                it.name==editable.toString()
                            }?.id.toString()
                            viewModel.getTownShip(selectedProvinceId)
                        }
                    }
                    binding.actProvince.setAdapter(arrayAdapter)
                    binding.actProvince.setText(provinceList.last(),false)
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
                    if (selectedProvinceId == ""){
                        townShipList = mutableListOf("None")
                        townShipList.toSet()
                    }else{
                        townShipList = it.data!!.map { it.name }.filterNotNull().toMutableList()
                        townShipList.add("None")
                    }
                    val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,townShipList)
                    binding.actTownship.addTextChangedListener {editable->
                        if (editable.toString() == "None"){
                            selectedTownshipId = ""
                        }else{
                            selectedTownshipId = it.data!!.find {
                                it.name==binding.actTownship.text.toString()
                            }?.id.toString()
                        }

                    }
                    binding.actTownship.setAdapter(arrayAdapter)
                    binding.actTownship.setText(if (townShipList.isEmpty()) "" else townShipList.last(),false)
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








