package com.example.shwemisale.screen.sellModule.openVoucher.withValue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.shwemisale.databinding.FragmentWithValueBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithValueFragment:Fragment() {
    lateinit var binding:FragmentWithValueBinding
    private val viewModel by viewModels<WithValueViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentWithValueBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}