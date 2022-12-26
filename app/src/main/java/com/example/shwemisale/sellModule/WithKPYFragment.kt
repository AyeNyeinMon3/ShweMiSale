package com.example.shwemisale.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shwemisale.databinding.FragmentWithKpyBinding

class WithKPYFragment:Fragment() {

    lateinit var binding:FragmentWithKpyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentWithKpyBinding.inflate(inflater).also {
            binding = it
        }.root
    }

}