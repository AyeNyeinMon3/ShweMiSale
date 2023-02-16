package com.example.shwemisale.screen.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentOutsideStockBinding

class OutsideStockFragment:Fragment() {

    lateinit var binding: FragmentOutsideStockBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentOutsideStockBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = OutsideStockRecyclerAdapter()
        binding.rvOutsideStockItem.adapter = adapter
        adapter.submitList(listOf(
            OutsideStockData("1","3gm","အသီးကွင်း ဟန်းချိန်း","ဒီလိုပုံံစံရဲ့အဟန့်အတိုင်းလုပ်ပါမယ်"),
            OutsideStockData("2","3gm","အသီးကွင်း ဟန်းချိန်း","ဒီလိုပုံံစံရဲ့အဟန့်အတိုင်းလုပ်ပါမယ်"),
            OutsideStockData("3","3gm","အသီးကွင်း ဟန်းချိန်း","ဒီလိုပုံံစံရဲ့အဟန့်အတိုင်းလုပ်ပါမယ်"),
            OutsideStockData("4","3gm","အသီးကွင်း ဟန်းချိန်း","ဒီလိုပုံံစံရဲ့အဟန့်အတိုင်းလုပ်ပါမယ်"),
            OutsideStockData("5","3gm","အသီးကွင်း ဟန်းချိန်း","ဒီလိုပုံံစံရဲ့အဟန့်အတိုင်းလုပ်ပါမယ်"),
            OutsideStockData("6","3gm","အသီးကွင်း ဟန်းချိန်း","ဒီလိုပုံံစံရဲ့အဟန့်အတိုင်းလုပ်ပါမယ် ဒီလိုပုံံစံရဲ့အဟန့်အတိုင်းလုပ်ပါမယ်"),
        ))



    }
}