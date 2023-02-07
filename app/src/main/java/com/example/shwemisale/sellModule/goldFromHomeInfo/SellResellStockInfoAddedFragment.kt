package com.example.shwemisale.sellModule.goldFromHomeInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.shwemisale.databinding.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SellResellStockInfoAddedFragment : Fragment() {

    lateinit var binding: FragmentResellStockInfoAddedSellBinding
    lateinit var dialogBinding: DialogResellStockInfoBinding
    lateinit var dialogGemWeightBinding: DialogGemWeightBinding
    lateinit var dialogMinusPercentageBinding: DialogMinusPercentageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentResellStockInfoAddedSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnAdd.setOnClickListener {
            showDialogResellStockInfo()
        }
        binding.btnAddGemWeightKPY.setOnClickListener {
            showDialogGemWeight()
        }
        binding.btnPercentage.setOnClickListener {
            showDialogMinusPercentage()
        }
        binding.btnPercentageVoucherPurchasePayment.setOnClickListener {
            showDialogMinusPercentage()
        }
    }

    fun showDialogResellStockInfo() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogBinding = DialogResellStockInfoBinding.inflate(
            inflater,
            ConstraintLayout(builder.context), false
        )
        builder.setView(dialogBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        dialogBinding.btnContinue.setOnClickListener {
            alertDialog.dismiss()
        }
        dialogBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        // alertDialog.window?.setLayout(900,750)
    }

    fun showDialogGemWeight() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogGemWeightBinding =
            DialogGemWeightBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogGemWeightBinding.root)
        val alertDialog = builder.create()
        alertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        alertDialog.setCancelable(false)
        val adapter = GemWeightRecyclerAdapter()
        dialogGemWeightBinding.rvGemWeight.adapter = adapter
        adapter.submitList(
            listOf(
                GemWeightInResellStock(
                    "1", "2", "30", "2", "12345",
                ),
                GemWeightInResellStock(
                    "2", "2", "30", "2", "3",
                    ),
                GemWeightInResellStock(
                    "3", "2", "30", "2", "3"
                ),
                GemWeightInResellStock(
                    "4", "2", "30", "2", "3"
                ),
                GemWeightInResellStock(
                    "5", "2", "30", "2", "3"
                ),
                GemWeightInResellStock(
                    "6", "2", "30", "2", "3"
                ),

            )
        )

        dialogGemWeightBinding.btnContinue.setOnClickListener {
            alertDialog.dismiss()
        }
        dialogGemWeightBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        // alertDialog.window?.setLayout(900,750)
    }

    fun showDialogMinusPercentage() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogMinusPercentageBinding =
            DialogMinusPercentageBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogMinusPercentageBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogMinusPercentageBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        dialogMinusPercentageBinding.btnContinue.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
}
