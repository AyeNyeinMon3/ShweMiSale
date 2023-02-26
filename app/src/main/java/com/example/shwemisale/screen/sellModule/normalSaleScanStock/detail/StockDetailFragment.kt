package com.example.shwemisale.screen.sellModule.normalSaleScanStock.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentStockDetailBinding
import com.example.shwemisale.screen.sellModule.normalSaleScanStock.ScanStockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockDetailFragment:Fragment() {
    lateinit var binding: FragmentStockDetailBinding
    private val viewModel by viewModels<ScanStockViewModel>()

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

    }
}