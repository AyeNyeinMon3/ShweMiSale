package com.example.shwemisale.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.databinding.DialogChangeFeatureBinding
import com.example.shwemisale.databinding.DialogSellTypeBinding
import com.example.shwemisale.databinding.DialogStockCheckBinding
import com.example.shwemisale.databinding.FragmentGoldFromHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GoldFromHomeFragment:Fragment() {

    lateinit var binding: FragmentGoldFromHomeBinding
    lateinit var alertDialogBinding: DialogStockCheckBinding
    lateinit var dialogBinding: DialogChangeFeatureBinding
    lateinit var dialogSellTypeBinding: DialogSellTypeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentGoldFromHomeBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.imageBtnSelect.setOnClickListener {
            showStockCheckDialog()
        }
        binding.btnOther.setOnClickListener {
            showChangeFeatureDialog()
        }
        binding.btnContinue.setOnClickListener {
            view.findNavController().navigate(GoldFromHomeFragmentDirections.actionGoldFromHomeFragmentToCreatePawnFragment())
        }
        binding.btnSkip.setOnClickListener {
            showSellTypeDialog()
        }
    }


    fun showStockCheckDialog() {
            val builder = MaterialAlertDialogBuilder(requireContext())
            val inflater = LayoutInflater.from(builder.context)
            alertDialogBinding = DialogStockCheckBinding.inflate(inflater, ConstraintLayout(builder.context), false)
            builder.setView(alertDialogBinding.root)
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialogBinding.btnContinue.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialogBinding.ivClose.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
            alertDialog.window?.setLayout(1000, 1500)


    }

    fun showChangeFeatureDialog(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogBinding = DialogChangeFeatureBinding.inflate(inflater,ConstraintLayout(builder.context),false)
        builder.setView(dialogBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        alertDialog.window?.setLayout(1000,900)

    }

    fun showSellTypeDialog(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogSellTypeBinding = DialogSellTypeBinding.inflate(inflater,ConstraintLayout(builder.context),false)
        builder.setView(dialogSellTypeBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogSellTypeBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        alertDialog.window?.setLayout(1000,1100)

        dialogSellTypeBinding.btnNormalSell.setOnClickListener {
            view?.findNavController()?.navigate(GoldFromHomeFragmentDirections.actionGoldFromHomeFragmentToScanStockFragment())
            alertDialog.dismiss()
        }
        dialogSellTypeBinding.btnReceiveNewOrder.setOnClickListener {
            view?.findNavController()?.navigate(GoldFromHomeFragmentDirections.actionGoldFromHomeFragmentToReceiveNewOrderFragment())
            alertDialog.dismiss()
        }

    }

}
