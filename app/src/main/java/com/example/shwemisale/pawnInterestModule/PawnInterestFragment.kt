package com.example.shwemisale.pawnInterestModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shwemisale.databinding.FragmentPawnInterestBinding

class PawnInterestFragment:Fragment() {

    lateinit var binding:FragmentPawnInterestBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentPawnInterestBinding.inflate(inflater).also {
            binding = it
        }.root
    }

}