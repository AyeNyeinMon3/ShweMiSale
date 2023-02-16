package com.example.shwemisale.screen.payforBalanceModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shwemisale.databinding.FragmentPayBalanceBinding

class PayBalanceFragment:Fragment() {

    lateinit var binding: FragmentPayBalanceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentPayBalanceBinding.inflate(inflater).also {
            binding = it
        }.root

    }
}