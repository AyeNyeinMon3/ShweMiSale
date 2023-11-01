package com.shwemigoldshop.shwemisale.screen.sellModule.normalSale.customerInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.createChip
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.data_layers.domain.product.asUiModel
import com.shwemigoldshop.shwemisale.databinding.FragmentCustomerInfoSellBinding
import com.shwemigoldshop.shwemisale.screen.sellModule.CustomerFavItemsRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellCustomerInfoFragment:Fragment() {

    lateinit var binding:FragmentCustomerInfoSellBinding
    private val args by navArgs<com.shwemigoldshop.shwemisale.screen.sellModule.normalSale.customerInfo.SellCustomerInfoFragmentArgs>()
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
                    var previousCheckedChipId = ""
                    binding.includeCustomerInfo.chipGp.setOnCheckedStateChangeListener { group, checkedIds ->
                        if (checkedIds.isEmpty()) {
                            group.check(previousCheckedChipId.toInt())
                        }else{
                            previousCheckedChipId = checkedIds[0].toString()
                            val productList = it.data?.find { it.id == checkedIds[0].toString() }?.product.orEmpty()
                            adapter.submitList(productList.map { it.asUiModel() })
                        }

                    }
                }
                is Resource.Error->{
                    loading.dismiss()

                }
                else -> {}
            }
        }
        binding.btnContinue.setOnClickListener {
            view.findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.normalSale.customerInfo.SellCustomerInfoFragmentDirections.actionGlobalGoldFromHomeFragment("customer_info_fragment",null))
        }

    }
}