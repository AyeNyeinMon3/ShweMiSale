package com.example.shwemisale.screen.oldStockDetail.gemWeightDetail

import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.shwemisale.util.generateNumberFromEditText
import com.example.shwemisale.databinding.DialogKpyInputFragmentBinding

class KpyInputDialogFragment(private val gemWeightDetailId:String) : DialogFragment() {
    private lateinit var binding: DialogKpyInputFragmentBinding
    private var onKpyDialogDismissListener: OnKpyDialogDismissListener? = null

    override fun onDismiss(dialog: DialogInterface) {
        onKpyDialogDismissListener?.onKpyInput(
            gemWeightDetailId,
            generateNumberFromEditText(binding.edtKyat),
            generateNumberFromEditText(binding.edtPae),
            generateNumberFromEditText(binding.edtYwae),

        )
        super.onDismiss(dialog)
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
                        val imm =
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    }
                }
                editText?.performClick()
            }
            false
        }
        return DialogKpyInputFragmentBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun setOnKpyDialogDismissListener(listener: OnKpyDialogDismissListener) {
        onKpyDialogDismissListener = listener
    }

}

interface OnKpyDialogDismissListener {
    fun onKpyInput(id:String,kyat: String, pae: String, ywae: String)
}