package com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shwemigoldshop.shwemisale.databinding.BottomSheetRemoveImageBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoveImageBottomSheetFragment:BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetRemoveImageBinding
    private var onRemoveImageSelectionListener: RemoveImageSelectionListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return BottomSheetRemoveImageBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { bottomSheet ->
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = 0
        }
        binding.mcvRemovePhoto.setOnClickListener {
            onRemoveImageSelectionListener?.onRemoveImageSelected()
        }
        binding.ivCross.setOnClickListener {
            this.dismiss()
        }
    }
    fun setonRemoveImageSelectionListener(listener: RemoveImageSelectionListener) {
        onRemoveImageSelectionListener = listener
    }
}

interface RemoveImageSelectionListener {
    fun onRemoveImageSelected()
}