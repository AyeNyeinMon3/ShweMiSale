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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemisale.databinding.DialogAddStockTypeBinding
import com.example.shwemisale.databinding.DialogGemWeightDetailBinding
import com.example.shwemisale.databinding.DialogKpyInputFragmentBinding
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.example.shwemisale.screen.goldFromHomeInfo.GemWeightRecyclerAdapter
import com.example.shwemisale.screen.oldStockDetail.ChooseStockTypeDialogFragment
import com.example.shwemisale.screen.oldStockDetail.OldStockDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogGemWeightDetailFragment : DialogFragment(), OnKpyDialogDismissListener {
    private lateinit var binding: DialogGemWeightDetailBinding
    private val viewModel by viewModels<OldStockDetailViewModel>()
    private lateinit var kpyInputDialogFragment: KpyInputDialogFragment
    private lateinit var totalGemWeightListener: TotalGemWeightListener
    private lateinit var loading : AlertDialog

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)

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

        return DialogGemWeightDetailBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        this.isCancelable = false
        binding.ivCancel.setOnClickListener {
            this.dismiss()
        }
        val adapter = GemWeightDetailRecyclerAdapter(
            { id, qty, oneGemGm, oneGemYwae ->
                viewModel.updateGemWeightDetaiil(
                    id = id,
                    qty = qty.toString(),
                    weightGmPerUnit = oneGemGm.toString(),
                    weightYwaePerUnit = oneGemYwae.toString()
                )
            },
            { id ->
                viewModel.deleteGemWeightDetaiil(id)
            },
            { id, changedQty ->
                viewModel.onChangeGemQty(id, changedQty)
            },
            { id, changedGemWeightGm ->
                viewModel.onChangeGemWeightGm(id, changedGemWeightGm)
            },
            { id ->
                kpyInputDialogFragment = KpyInputDialogFragment(id)
                kpyInputDialogFragment.setOnKpyDialogDismissListener(this)
                kpyInputDialogFragment.show(childFragmentManager, "kpyInputDialog")
            },
            { id, qty, gemWeightYwaePerUnit ->
                viewModel.onChangeTotalGemWeight(id, qty, gemWeightYwaePerUnit)
            }
        )
        binding.rvGemWeight.adapter = adapter
        viewModel.getGemWeightDetaiil()
        viewModel.getGemWeightDetailLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    adapter.submitList(it.data)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.createGemWeightDetailLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    viewModel.getGemWeightDetaiil()
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.updateGemWeightDetailLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    viewModel.getGemWeightDetaiil()
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.deleteGemWeightDetailLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    viewModel.getGemWeightDetaiil()
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.btnNewCustom.setOnClickListener {
            viewModel.createGemWeightDetaiil("0", "0.0", "0.0")
        }
        binding.btnContinue.setOnClickListener {
            this.dismiss()
            totalGemWeightListener.onTotalGemWeightCalculated(viewModel.getTotalCalculatedGemWeightYwae())
        }
    }

    override fun onKpyInput(id: String, kyat: String, pae: String, ywae: String) {
        val ywae = getYwaeFromKPY(
            kyat.toInt(),
            pae.toInt(),
            ywae.toDouble()
        )
        viewModel.onChangeGemWeightDetailKpy(id, ywae)
    }

    fun setOnTotalGemWeightListener(listener: TotalGemWeightListener) {
        totalGemWeightListener = listener
    }

}

interface TotalGemWeightListener {
    fun onTotalGemWeightCalculated(totalGemWeightYwae: Double)
}
