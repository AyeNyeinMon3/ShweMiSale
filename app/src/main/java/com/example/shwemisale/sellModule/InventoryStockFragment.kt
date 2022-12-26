package com.example.shwemisale.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.MainActivity
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentInventoryStockBinding

class InventoryStockFragment:Fragment() {

    lateinit var binding: FragmentInventoryStockBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentInventoryStockBinding.inflate(inflater).also {
            binding = it
        }.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = InventoryStockRecyclerAdapter()
        binding.rvStockCodeItem.adapter = adapter
        adapter.submitList(listOf(
            InventoryStockData("1","SM-PRD-000000002"),
            InventoryStockData("2","SM-PRD-000000003"),
            InventoryStockData("3","SM-PRD-000000004"),
            InventoryStockData("4","SM-PRD-000000005"),
            InventoryStockData("5","SM-PRD-000000006"),
        ))

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()


        binding.inventoryStockFragmentMaterialToolbar.setNavigationIcon(R.drawable.icon_back)
        binding.inventoryStockFragmentMaterialToolbar.setNavigationOnClickListener {
            view.findNavController().navigate(InventoryStockFragmentDirections.actionInventoryStockFragmentToReceiveNewOrderFragment())
        }


    }

}