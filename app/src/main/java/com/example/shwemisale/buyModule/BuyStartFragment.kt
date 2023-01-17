package com.example.shwemisale.buyModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.CustomerListData
import com.example.shwemisale.CustomerListRecyclerAdapter
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentStartBuyBinding

class BuyStartFragment : Fragment() {

    private lateinit var binding: FragmentStartBuyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentStartBuyBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapterState = ArrayAdapter.createFromResource(requireContext(),
            R.array.state,
            R.layout.spinner_text_style
        )
        binding.spinnerState.adapter = adapterState
        var adapterTown = ArrayAdapter.createFromResource(requireContext(),
            R.array.township,
            R.layout.spinner_text_style
        )
        binding.spinnerTownship.adapter = adapterTown



        binding.btnNew.setOnClickListener { view:View->
            view.findNavController().navigate(BuyStartFragmentDirections.actionBuyStartFragmentToBuyCreateNewFragment())
        }

        val adapter = CustomerListRecyclerAdapter()
        binding.includeSearchResult.rvCustomerList.adapter = adapter
        adapter.submitList(listOf(
            CustomerListData("1","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
            CustomerListData("2","Daw Than Than"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
            CustomerListData("3","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
            CustomerListData("4","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
            CustomerListData("5","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
            CustomerListData("6","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
        ))

    }

}









