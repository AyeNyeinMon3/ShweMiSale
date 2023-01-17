package com.example.shwemisale.buyModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentCreateNewBuyBinding

class BuyCreateNewFragment:Fragment() {
        lateinit var binding: FragmentCreateNewBuyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCreateNewBuyBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapterState = ArrayAdapter.createFromResource(requireContext(),
            R.array.state,
            R.layout.spinner_text_style
        )
        binding.spinnerState.adapter = adapterState
        val adapterTownship = ArrayAdapter.createFromResource(requireContext(),
            R.array.township,
            R.layout.spinner_text_style
        )
        binding.spinnerTownship.adapter = adapterTownship

        binding.btnSave.setOnClickListener {
            view.findNavController().navigate(BuyCreateNewFragmentDirections.actionBuyCreateNewFragmentToBuyCustomerInfoFragment())
        }

    }
}