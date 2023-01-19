package com.example.shwemisale.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.CustomerFavItemsData
import com.example.shwemisale.CustomerFavItemsRecyclerAdapter
import com.example.shwemisale.databinding.FragmentCustomerInfoSellBinding

class SellCustomerInfoFragment:Fragment() {

    lateinit var binding:FragmentCustomerInfoSellBinding

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
        val adapter = CustomerFavItemsRecyclerAdapter()
        binding.includeCustomerInfo.rvCustomerFavItems.adapter = adapter
        adapter.submitList(listOf(
            CustomerFavItemsData("1","စတားဝေးကြိုး","400,000 ကျပ်"),
            CustomerFavItemsData("2","white gold chain","500,000 ကျပ်"),
            CustomerFavItemsData("3","platinum","600,000 ကျပ်"),
            CustomerFavItemsData("4","rose gold","700,000 ကျပ်"),
            CustomerFavItemsData("5","စတားဝေးကြိုး","800,000 ကျပ်"),
            CustomerFavItemsData("6","စတားဝေးကြိုး","400,000 ကျပ်"),
            CustomerFavItemsData("7","စတားဝေးကြိုး","900,000 ကျပ်"),
            CustomerFavItemsData("8","စတားဝေးကြိုး","300,000 ကျပ်"),
            CustomerFavItemsData("9","စတားဝေးကြိုး","600,000 ကျပ်"),
            CustomerFavItemsData("10","စတားဝေးကြိုး","400,000 ကျပ်"),
            CustomerFavItemsData("11","စတားဝေးကြိုး","700,000 ကျပ်"),
            CustomerFavItemsData("11","စတားဝေးကြိုး","700,000 ကျပ်"),
        ))
        binding.btnContinue.setOnClickListener {
            view.findNavController().navigate(SellCustomerInfoFragmentDirections.actionSellCustomerInfoFragmentToSellGoldFromHomeFragment())
        }

    }
}