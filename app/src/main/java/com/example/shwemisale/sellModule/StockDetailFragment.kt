package com.example.shwemisale.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentStockDetailBinding

class StockDetailFragment:Fragment() {
    lateinit var binding:FragmentStockDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentStockDetailBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.content,R.layout.spinner_text_style_3)
       // binding.spinnerContent.adapter = adapter
    }
}