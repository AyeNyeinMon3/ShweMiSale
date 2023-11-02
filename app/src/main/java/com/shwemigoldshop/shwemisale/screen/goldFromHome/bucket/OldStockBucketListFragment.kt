package com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.R
import com.shwemigoldshop.shwemisale.databinding.FragmentOldStockBucketListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldStockBucketListFragment : Fragment() {
    private lateinit var binding:FragmentOldStockBucketListBinding
    private val shareViewModel by activityViewModels<BucketShareViewModel>()
    private val viewModel by viewModels<OldStockBucketListViewModel>()
    private lateinit var loading: AlertDialog

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
        loading = requireContext().getAlertDialog()
        requireActivity().actionBar?.hide()
        binding.includeOldStockNoItemState.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.includeOldStockWithItems.ivBack.setOnClickListener {
            findNavController()
        }
        binding.includeOldStockNoItemState.button.setOnClickListener {
            findNavController().navigate(com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket.OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToAddOldStockToBucketFragment())
        }
        binding.includeOldStockNoItemState.btnAddOnTop.setOnClickListener {
            findNavController().navigate(com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket.OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToAddOldStockToBucketFragment())
        }
        binding.includeOldStockWithItems.btnAddOnTop.setOnClickListener {
            findNavController().navigate(com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket.OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToAddOldStockToBucketFragment())

        }
        binding.includeOldStockNoItemState.button.setOnClickListener {
            findNavController().navigate(com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket.OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToAddOldStockToBucketFragment())

        }

        binding.includeOldStockWithItems.root.isVisible = shareViewModel.oldStockInBucketList.value.isNullOrEmpty().not()
        binding.includeOldStockNoItemState.root.isVisible = shareViewModel.oldStockInBucketList.value.isNullOrEmpty()
        val adapter = OldStockBucketRecyclerAdapter({oldStock ->
            findNavController().navigate(
                com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket.OldStockBucketListFragmentDirections.actionOldStockBucketListFragmentToOldStockDetailFragment(
                oldStock,null,if (oldStock.dataFilled) "viewdetail" else null))

        },{item ->
            shareViewModel.removeOldStockBucket(item)
        })
        binding.includeOldStockWithItems.rvOldStockBucket.adapter = adapter
        shareViewModel.oldStockInBucketList.observe(viewLifecycleOwner){
            binding.includeOldStockWithItems.root.isVisible = it.isNullOrEmpty().not()
            binding.includeOldStockNoItemState.root.isVisible = it.isNullOrEmpty()

            binding.includeOldStockWithItems.tvTotalAddedItems.text = getString(R.string.total_added_item,it.count().toString())

            binding.includeOldStockWithItems.tvValueDataFilledItems.text =
                it?.count { it.dataFilled}
                    .toString()
            binding.includeOldStockWithItems.tvValueDataEmptyItems.text =
                it?.count { !it.dataFilled }
                    .toString()
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
    }
}