package com.example.shwemisale.sellModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.R
import com.example.shwemisale.databinding.DialogAddProductBinding
import com.example.shwemisale.databinding.FragmentAkoukSellBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AkoukSellFragment:Fragment() {

    lateinit var binding:FragmentAkoukSellBinding
    lateinit var dialogAlertBinding: DialogAddProductBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAkoukSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnAdd.setOnClickListener {
            showDialogAddProduct()
        }

    }

    fun showDialogAddProduct(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogAlertBinding = DialogAddProductBinding.inflate(inflater,ConstraintLayout(builder.context),false)
        builder.setView(dialogAlertBinding.root)
        val alertDialog = builder.create()

        val spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.item_type,R.layout.spinner_text_style_2)
        dialogAlertBinding.spinnerItemType.adapter = spinnerAdapter

        dialogAlertBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        alertDialog.window?.setLayout(750,900)

        dialogAlertBinding.btnContinue.setOnClickListener {
            view?.findNavController()?.navigate(AkoukSellFragmentDirections.actionAkoukSellFragmentToAkoukSellAddedFragment())
            alertDialog.dismiss()
        }
    }

}