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
import com.example.shwemisale.databinding.DialogAkoukSellAddProductBinding
import com.example.shwemisale.databinding.FragmentAkoukSellBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AkoukSellFragment:Fragment() {

    lateinit var binding:FragmentAkoukSellBinding
    lateinit var dialogAlertBinding: DialogAkoukSellAddProductBinding

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
        val adapter = AkoukSellRecyclerAdapter()
        binding.rvAkoukSell.adapter = adapter
        adapter.submitList(listOf(
            AkoukSellData("1","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","1,000,000"),
            AkoukSellData("2","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","2,000,000"),
            AkoukSellData("3","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","3,000,000"),
            AkoukSellData("4","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","4,000,000"),
            AkoukSellData("5","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","5,000,000"),
            AkoukSellData("6","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","6,000,000"),
            AkoukSellData("7","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","7,000,000"),
            AkoukSellData("8","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","7,000,000"),
            AkoukSellData("9","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","7,000,000"),
            AkoukSellData("10","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","7,000,000"),
            AkoukSellData("11","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","7,000,000"),
            AkoukSellData("12","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","7,000,000"),
            AkoukSellData("13","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","7,000,000"),
            AkoukSellData("14","အခဲ","1K2P3Y","0K0P0Y","4,000","4,000","7,000,000"),
        ))


        binding.btnAdd.setOnClickListener {
            showDialogAddProduct()

        }

    }

    fun showDialogAddProduct(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogAlertBinding = DialogAkoukSellAddProductBinding.inflate(inflater,ConstraintLayout(builder.context),false)
        builder.setView(dialogAlertBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        val spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.item_type,R.layout.spinner_text_style_2)
        dialogAlertBinding.spinnerItemType.adapter = spinnerAdapter

        dialogAlertBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        alertDialog.window?.setLayout(750,900)

        dialogAlertBinding.btnContinue.setOnClickListener {
            binding.rvAkoukSell.visibility= View.VISIBLE
            binding.linearLayoutPoloValue.visibility= View.VISIBLE
            alertDialog.dismiss()

        }
    }

}