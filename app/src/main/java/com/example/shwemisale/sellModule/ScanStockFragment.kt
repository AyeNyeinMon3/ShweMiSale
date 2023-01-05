package com.example.shwemisale.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.StockCodeData
import com.example.shwemisale.StockCodeRecyclerAdapter
import com.example.shwemisale.databinding.FragmentScanStockBinding

class ScanStockFragment:Fragment() {

    lateinit var binding:FragmentScanStockBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentScanStockBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnContinue.setOnClickListener {
            view.findNavController().navigate(ScanStockFragmentDirections.actionScanStockFragmentToExchangeOrderFragment())
        }

        val adapter = StockCodeRecyclerAdapter()
        binding.rvStockCodeItem.adapter = adapter
        adapter.submitList(listOf(
            StockCodeData("1","SM-PRD-000000001","12”","400,000"),
            StockCodeData("2","SM-PRD-000000002","12”","500,000"),
            StockCodeData("3","SM-PRD-000000003","12”","600,000"),
            StockCodeData("4","SM-PRD-000000004","12”","700,000"),
            StockCodeData("5","SM-PRD-000000005","12”","800,000"),
            StockCodeData("5","SM-PRD-000000005","12”","800,000"),
            StockCodeData("5","SM-PRD-000000005","12”","800,000"),
            StockCodeData("5","SM-PRD-000000005","12”","800,000"),
            StockCodeData("5","SM-PRD-000000005","12”","800,000"),
        ))



    }
}