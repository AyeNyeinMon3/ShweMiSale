//package com.example.shwemisale.screen.oldStockDetail
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import com.shwemigoldshop.shwemisale.util.Resource
//import com.example.shwemisale.util.hideKeyboard
//import com.example.shwemisale.util.showKeyBoard
//import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
//import com.example.shwemisale.databinding.DialogAddStockTypeBinding
//import com.example.shwemisale.databinding.FragmentOldStockDetailBinding
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SampleFragment:Fragment(),ChooseStockTypeListener {
//    private lateinit var binding:DialogAddStockTypeBinding
//    private val viewModel by viewModels<OldStockDetailViewModel>()
//    private lateinit var chooseSTockTypeDialogFragment: ChooseStockTypeDialogFragment
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return DialogAddStockTypeBinding.inflate(inflater).also {
//            binding = it
//        }.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val rebuyItemRecyclerAdapter = RebuyItemsRecyclerAdapter({id,text,size->
//            viewModel.onNameChanged(id, text,size)
//        },{id,size->
//            viewModel.qtyIncrease(id,size)
//        },{id,size->
//            viewModel.qtyDecrease(id,size)
//        },{id,size,isEditable->
//            binding.rBtnBig.isEnabled = !isEditable
//            binding.rBtnSmall.isEnabled = !isEditable
//            if (isEditable) {
//                showKeyBoard(binding.root)
//                binding.radioGpChooseSize.alpha = 0.3f
//            } else {
//                binding.radioGpChooseSize.alpha = 1f
//                hideKeyboard(null,binding.root)
//            }
//            viewModel.changeToEditView(id,size,isEditable)
//        })
//        binding.rvStock.adapter = rebuyItemRecyclerAdapter
//        viewModel.getRebuyItem("small")
//        binding.radioGpChooseSize.setOnCheckedChangeListener { radioGroup, checkedId ->
//            if (checkedId == binding.rBtnSmall.id) {
//                viewModel.getRebuyItem("small")
//                viewModel.size = "small"
//
//            } else {
//                viewModel.getRebuyItem("large")
//                viewModel.size = "large"
//
//            }
//        }
//        viewModel.rebuyItemeLiveData.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Loading -> {
//
//                }
//
//                is Resource.Success -> {
//                    if (binding.rBtnSmall.isChecked){
//                        rebuyItemRecyclerAdapter.submitList(it.data?.smallSizeItems)
//                    }else{
//                        rebuyItemRecyclerAdapter.submitList(it.data?.largeSizeItems)
//                    }
//                }
//
//                is Resource.Error -> {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//        binding.btnAdd.setOnClickListener {
//            chooseSTockTypeDialogFragment = ChooseStockTypeDialogFragment()
//            chooseSTockTypeDialogFragment.setonChooseStockTypeListener(this)
//            chooseSTockTypeDialogFragment.show(
//                childFragmentManager,
//                "ChooseStockTypeDialog"
//            )
//        }
//
//
//    }
//
//    override fun selectedName(name: String, totalQty: Int) {
//        TODO("Not yet implemented")
//    }
//}