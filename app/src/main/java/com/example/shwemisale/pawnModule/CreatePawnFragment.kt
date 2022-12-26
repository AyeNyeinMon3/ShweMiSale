package com.example.shwemisale.pawnModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shwemisale.databinding.FragmentCreatePawnBinding

class CreatePawnFragment:Fragment() {

    lateinit var binding:FragmentCreatePawnBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCreatePawnBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}