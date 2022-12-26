package com.example.shwemisale.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shwemisale.databinding.FragmentWithValueBinding

class WithValueFragment:Fragment() {

    lateinit var binding:FragmentWithValueBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentWithValueBinding.inflate(inflater).also {
            binding = it
        }.root
    }

}