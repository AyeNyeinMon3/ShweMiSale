package com.example.shwemisale.screen.goldFromHome.bucket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.shwemisale.databinding.FragmentOldStockBucketListBinding
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldStockBucketListFragment : Fragment() {
    private lateinit var binding:FragmentOldStockBucketListBinding
    private val shareViewModel by activityViewModels<BucketShareViewModel>()
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
            findNavController().navigate(OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToOldStockDetailFragment(null,null,"0","0.0"))
        }
        binding.includeOldStockNoItemState.btnAddOnTop.setOnClickListener {
            findNavController().navigate(OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToAddOldStockToBucketFragment())
        }
        binding.includeOldStockWithItems.btnAddOnTop.setOnClickListener {
            findNavController().navigate(OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToAddOldStockToBucketFragment())

        }
        binding.includeOldStockNoItemState.button.setOnClickListener {
            findNavController().navigate(OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToAddOldStockToBucketFragment())

        }

        binding.includeOldStockWithItems.root.isVisible = shareViewModel.oldStockInBucketList.value.isNullOrEmpty().not()
        binding.includeOldStockNoItemState.root.isVisible = shareViewModel.oldStockInBucketList.value.isNullOrEmpty()
        val adapter = OldStockBucketRecyclerAdapter({oldStock ->
             val oldStockDetailToPass = shareViewModel.dataFilledOldStock.find { it.id.toString()== oldStock.oldStockId }
            val ywae = getYwaeFromKPY((oldStock.weightK?:"0").toInt(),(oldStock.weightP?:"0").toInt(),(oldStock.weightY?:"0.0").toDouble())
            findNavController().navigate(OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToOldStockDetailFragment(
                oldStockDetailToPass,null,oldStock.weightGm.orEmpty(),ywae.toString()))

        },{item ->
            shareViewModel.removeOldStockBucket(item)
        })
        binding.includeOldStockWithItems.rvOldStockBucket.adapter = adapter
        shareViewModel.oldStockInBucketList.observe(viewLifecycleOwner){
            binding.includeOldStockWithItems.root.isVisible = it.isNullOrEmpty().not()
            binding.includeOldStockNoItemState.root.isVisible = it.isNullOrEmpty()

            binding.includeOldStockWithItems.tvValueDataFilledItems.text =
                it?.count { !it.oldStockId.isNullOrEmpty() }
                    .toString()
            binding.includeOldStockWithItems.tvValueDataEmptyItems.text =
                it?.count { it.oldStockId.isNullOrEmpty() }
                    .toString()
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
    }
}