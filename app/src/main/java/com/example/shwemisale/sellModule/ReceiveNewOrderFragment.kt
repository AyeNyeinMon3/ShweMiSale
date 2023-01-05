package com.example.shwemisale.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentReceiveNewOrderBinding

class ReceiveNewOrderFragment:Fragment() {

    lateinit var binding:FragmentReceiveNewOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentReceiveNewOrderBinding.inflate(inflater).also {
            binding=it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.gold_type,R.layout.spinner_text_style_2)
        binding.spinnerGoldType.adapter = adapter

        binding.btnInventory.setOnClickListener {
            view.findNavController().navigate(ReceiveNewOrderFragmentDirections.actionReceiveNewOrderFragmentToInventoryStockFragment())
        }
        binding.btnOutside.setOnClickListener {
            view.findNavController().navigate(ReceiveNewOrderFragmentDirections.actionReceiveNewOrderFragmentToOutsideStockFragment())
        }
    }


}