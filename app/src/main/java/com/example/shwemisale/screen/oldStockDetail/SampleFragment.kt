package com.example.shwemisale.screen.oldStockDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.databinding.DialogAddStockTypeBinding
import com.example.shwemisale.databinding.FragmentOldStockDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SampleFragment:Fragment() {
    private lateinit var binding:DialogAddStockTypeBinding
    private val viewModel by viewModels<OldStockDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DialogAddStockTypeBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rebuyItemRecyclerAdapter = RebuyItemsRecyclerAdapter({id,text->
            viewModel.onNameChanged(id, text)
        },{id->
            viewModel.qtyIncrease(id)
        },{id->
            viewModel.qtyDecrease(id)
        })
        binding.rvStock.adapter = rebuyItemRecyclerAdapter
        viewModel.getRebuyItem("small")
        binding.radioGpChooseSize.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == binding.rBtnSmall.id) {
                viewModel.getRebuyItem("small")
                viewModel.size = "small"

            } else {
                viewModel.getRebuyItem("large")
                viewModel.size = "large"

            }
        }
        viewModel.rebuyItemeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    rebuyItemRecyclerAdapter.submitList(it.data)

                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}