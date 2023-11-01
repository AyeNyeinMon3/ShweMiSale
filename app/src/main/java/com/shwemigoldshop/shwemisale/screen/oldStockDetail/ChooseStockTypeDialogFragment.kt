package com.shwemigoldshop.shwemisale.screen.oldStockDetail

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.hideKeyboard
import com.shwemigoldshop.shwemisale.util.showKeyBoard
import com.shwemigoldshop.shwemisale.databinding.DialogAddStockTypeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseStockTypeDialogFragment:DialogFragment() {
    private lateinit var binding:DialogAddStockTypeBinding
    private val viewModel by viewModels<OldStockDetailViewModel>()
    private var onChooseStockTypeListener: ChooseStockTypeListener? = null

    override fun onStart() {
        super.onStart()
            val width = ViewGroup.LayoutParams.WRAP_CONTENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)

    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialog = dialog
        dialog?.window?.decorView?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val editText = view?.findFocus()
                if (editText is EditText) {
                    val outRect = Rect()
                    editText.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        editText.clearFocus()
                        editText.isCursorVisible = false
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    }
                }
                editText?.performClick()
            }
            false
        }
        return DialogAddStockTypeBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.isCancelable = false
        binding.ivCancel.setOnClickListener {
            this.dismiss()
        }
        val rebuyItemRecyclerAdapter = RebuyItemsRecyclerAdapter({id,text,size->
            viewModel.onNameChanged(id, text,size)
        },{id,size->
            viewModel.qtyIncrease(id,size)
        },{id,size->
            viewModel.qtyDecrease(id,size)
        },{id,size,isEditable->
            binding.rBtnBig.isEnabled = !isEditable
            binding.rBtnSmall.isEnabled = !isEditable
            if (isEditable) {
                showKeyBoard(binding.root)
                binding.radioGpChooseSize.alpha = 0.3f
            } else {
                binding.radioGpChooseSize.alpha = 1f
                hideKeyboard(null,binding.root)
            }
            viewModel.changeToEditView(id,size,isEditable)
        })
        binding.rvStock.adapter = rebuyItemRecyclerAdapter
        viewModel.getRebuyItem("small")
        viewModel.setSize("small")
        binding.radioGpChooseSize.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == binding.rBtnSmall.id) {
                viewModel.getRebuyItem("small")
                viewModel.setSize("small")

            } else {
                viewModel.getRebuyItem("large")
                viewModel.setSize("large")

            }
        }
        viewModel.rebuyItemeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    if (binding.rBtnSmall.isChecked){
                        rebuyItemRecyclerAdapter.submitList(it.data?.smallSizeItems)

                    }else{
                        rebuyItemRecyclerAdapter.submitList(it.data?.largeSizeItems)
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.btnAdd.setOnClickListener {
            val stockTypeWithQty = viewModel.getSelectedStockType()
            onChooseStockTypeListener?.selectedName(stockTypeWithQty.first,stockTypeWithQty.second,viewModel.sizeLiveData.value.orEmpty())
        }


    }
    fun setonChooseStockTypeListener(listener: ChooseStockTypeListener ) {
        onChooseStockTypeListener = listener
    }
}

interface ChooseStockTypeListener{
    fun selectedName(nameTag:String,totalQty:Int,size:String)
}