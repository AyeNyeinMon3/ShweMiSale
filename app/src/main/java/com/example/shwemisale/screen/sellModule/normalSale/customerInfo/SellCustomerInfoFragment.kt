package com.example.shwemisale.screen.sellModule.normalSale.customerInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.Resource
import com.example.shwemi.util.createChip
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.showDropdown
import com.example.shwemisale.CustomerFavItemsData
import com.example.shwemisale.CustomerFavItemsRecyclerAdapter
import com.example.shwemisale.R
import com.example.shwemisale.data_layers.domain.product.asUiModel
import com.example.shwemisale.databinding.FragmentCustomerInfoSellBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellCustomerInfoFragment:Fragment() {

    lateinit var binding:FragmentCustomerInfoSellBinding
    private val args by navArgs<SellCustomerInfoFragmentArgs>()
    private val viewModel by viewModels<SellCustomerInfoViewModel>()
    private lateinit var loading : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCustomerInfoSellBinding.inflate(inflater).also {
            binding= it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
        val adapter = CustomerFavItemsRecyclerAdapter()
        binding.includeCustomerInfo.rvCustomerFavItems.adapter = adapter
        viewModel.saveCustomerId(args.customerData.id)
        binding.tvAddress.text = args.customerData.address
        binding.tvBirthDate.text = args.customerData.date_of_birth
        binding.tvName.text = args.customerData.name
        binding.tvPhNumber.text = args.customerData.phone
        binding.tvNRC.text = args.customerData.nrc
        viewModel.getCustomerWhistList(args.customerData.id)

        viewModel.customerWhistListLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    binding.includeCustomerInfo.chipGp.removeAllViews()
                    for (item in it.data!!) {
                        val chip = requireContext().createChip(item.name)
                        chip.id = item.id.toInt()
                        binding.includeCustomerInfo.chipGp.addView(chip)
                    }
                    binding.includeCustomerInfo.chipGp.setOnCheckedStateChangeListener { group, checkedIds ->
                        val productList = it.data!!.find { it.id == checkedIds[0].toString() }!!.product
                        adapter.submitList(productList!!.map { it.asUiModel() })
                    }
                }
                is Resource.Error->{
                    loading.dismiss()

                }
                else -> {}
            }
        }
        binding.btnContinue.setOnClickListener {
            view.findNavController().navigate(SellCustomerInfoFragmentDirections.actionGlobalGoldFromHomeFragment("customer_info_fragment",null))
        }

    }
}