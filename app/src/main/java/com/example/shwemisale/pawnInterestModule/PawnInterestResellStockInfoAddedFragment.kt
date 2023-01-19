package com.example.shwemisale.pawnInterestModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.R
import com.example.shwemisale.databinding.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PawnInterestResellStockInfoAddedFragment:Fragment (){

    lateinit var binding: FragmentResellStockInfoAddedPawnInterestBinding
    lateinit var dialogBinding: DialogResellStockInfoBinding
    lateinit var dialogGemWeightBinding: DialogGemWeightBinding
    lateinit var dialogMinusPercentageBinding: DialogMinusPercentageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentResellStockInfoAddedPawnInterestBinding.inflate(inflater).also {
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
        binding.imageBtnPercentage.setOnClickListener {
            showDialogMinusPercentage()
        }
        binding.imageBtnPercentageVoucherPurchasePayment.setOnClickListener {
            showDialogMinusPercentage()
        }
    }

    fun showDialogResellStockInfo(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogBinding = DialogResellStockInfoBinding.inflate(inflater,
            ConstraintLayout(builder.context),false)
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
        alertDialog.window?.setLayout(900,750)
    }
    fun showDialogGemWeight(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogGemWeightBinding = DialogGemWeightBinding.inflate(inflater,ConstraintLayout(builder.context),false)
        builder.setView(dialogGemWeightBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        dialogGemWeightBinding.btnContinue.setOnClickListener {
            alertDialog.dismiss()
        }
        dialogGemWeightBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        alertDialog.window?.setLayout(900,750)
    }
    fun showDialogMinusPercentage(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogMinusPercentageBinding = DialogMinusPercentageBinding.inflate(inflater,ConstraintLayout(builder.context),false)
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
