package com.example.shwemisale.sellModule.sellStart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemisale.CustomerListData
import com.example.shwemisale.CustomerListRecyclerAdapter
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentStartSellBinding
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellStartFragment : Fragment() {

    private lateinit var binding: FragmentStartSellBinding
    private val viewModel by viewModels<SellStartViewModel>()
    private lateinit var loading : AlertDialog
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        networkcall()
        val adapterState = ArrayAdapter.createFromResource(requireContext(),
            R.array.state,
            R.layout.spinner_text_style
        )
//        binding.spinnerState.adapter = adapterState
        var adapterTown = ArrayAdapter.createFromResource(requireContext(),
            R.array.township,
            R.layout.spinner_text_style
        )
        //binding.spinnerTownship.adapter = adapterTown



        binding.btnNew.setOnClickListener { view:View->
            view.findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToSellCreateNewFragment())
        }

        val adapter = CustomerListRecyclerAdapter()
//        binding.includeSearchResult.rv.adapter = adapter
        adapter.submitList(listOf(
            CustomerListData("1","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
            CustomerListData("2","Daw Than Than"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
            CustomerListData("3","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
            CustomerListData("4","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
            CustomerListData("5","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
            CustomerListData("6","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
        ))


        viewModel.profileLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    viewModel.resetProfileLiveData()
                }
                is Resource.Error->{
                    loading.dismiss()
                    if (it.message == "TOKEN_EXPIRED" || it.message == "TOKEN_NOT_PROVIDED" || it.message == "TOKEN_INVALID" ){
                        findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToLoginFragment())
                    }
                    if (it.message == "Server Error"){
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
                    val list = it.data!!.map { it.name }
                    val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,list)
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
                    val list = it.data!!.map { it.name }
                    val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,list)
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
            viewModel.searchCustomerData(null,
            binding.editName.text.toString(),
            binding.editPhNumber.text.toString(),
            binding.tvBirthDate.text.toString(),
            null,
                selectedProvinceId,
                selectedTownshipId,
                null,
                binding.editNRC.text.toString()
            )
        }


    }

    fun networkcall(){
        viewModel.getProfile()
        viewModel.getProvince()
        viewModel.getTownShip()
    }

}









