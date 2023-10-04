package com.example.shwemisale.screen.goldFromHome.bucket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shwemisale.databinding.FragmentOldStockBucketListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldStockBucketListFragment : Fragment() {
    private lateinit var binding:FragmentOldStockBucketListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentOldStockBucketListBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeOldStockNoItemState.button.setOnClickListener {
            findNavController().navigate(OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToOldStockDetailFragment())
        }
    }
}