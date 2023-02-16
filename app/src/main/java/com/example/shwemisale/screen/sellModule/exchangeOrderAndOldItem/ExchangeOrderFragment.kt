package com.example.shwemisale.screen.sellModule.exchangeOrderAndOldItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.shwemisale.databinding.DialogExchangeOrderBinding
import com.example.shwemisale.databinding.FragmentExchangeOrderBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ExchangeOrderFragment:Fragment() {

    lateinit var binding: FragmentExchangeOrderBinding
    lateinit var dialogExchangeOrderBinding: DialogExchangeOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentExchangeOrderBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnContinue.setOnClickListener {
            showExchangeOrderDialog()
        }
        binding.btnSkip.setOnClickListener {
            showExchangeOrderDialog()
        }

    }


    fun showExchangeOrderDialog(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogExchangeOrderBinding = DialogExchangeOrderBinding.inflate(inflater,ConstraintLayout(builder.context),false)
        builder.setView(dialogExchangeOrderBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogExchangeOrderBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()


        dialogExchangeOrderBinding.btnWithValue.setOnClickListener {
            view?.findNavController()?.navigate(ExchangeOrderFragmentDirections.actionExchangeOrderFragmentToWithValueFragment())
            alertDialog.dismiss()
        }
        dialogExchangeOrderBinding.btnWithKPY.setOnClickListener {
            view?.findNavController()?.navigate(ExchangeOrderFragmentDirections.actionExchangeOrderFragmentToWithKPYFragment())
            alertDialog.dismiss()
        }
    }

}