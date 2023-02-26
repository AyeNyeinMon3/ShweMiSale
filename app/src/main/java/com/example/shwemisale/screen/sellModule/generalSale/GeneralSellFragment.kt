package com.example.shwemisale.screen.sellModule.generalSale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.shwemisale.R
import com.example.shwemisale.databinding.DialogGeneralSellAddProductBinding
import com.example.shwemisale.databinding.FragmentGeneralSellBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralSellFragment:Fragment() {

    lateinit var binding: FragmentGeneralSellBinding
    lateinit var dialogAlertBinding:DialogGeneralSellAddProductBinding
    private val viewModel by viewModels<GeneralSaleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentGeneralSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = GeneralSellRecyclerAdapter()
        binding.rvGeneralSell.adapter = adapter
        adapter.submitList(listOf(
            GeneralSellData("1","Group 1","အရောင်တင်","3","3.5 gm","0K 0P 1Y","1,000","15,000"),
            GeneralSellData("2","Group 2","အရောင်တင်","3","3.5 gm","0K 0P 1Y","1,000","15,000"),
            GeneralSellData("3","Group 3","အရောင်တင်","3","3.5 gm","0K 0P 1Y","1,000","15,000"),
            GeneralSellData("4","Group 4","အရောင်တင်","3","3.5 gm","0K 0P 1Y","1,000","15,000"),
            GeneralSellData("5","Group 5","အရောင်တင်","3","3.5 gm","0K 0P 1Y","1,000","15,000"),
            GeneralSellData("6","Group 6","အရောင်တင်","3","3.5 gm","0K 0P 1Y","1,000","15,000"),
            GeneralSellData("7","Group 7","အရောင်တင်","3","3.5 gm","0K 0P 1Y","1,000","15,000"),
            GeneralSellData("8","Group 8","အရောင်တင်","3","3.5 gm","0K 0P 1Y","1,000","15,000"),
            GeneralSellData("9","Group 9","အရောင်တင်","3","3.5 gm","0K 0P 1Y","1,000","15,000"),
        ))

        viewModel.submitGeneralSaleLiveData
        binding.btnAdd.setOnClickListener {
            showDialogAddProduct()
        }

    }

    fun showDialogAddProduct(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogAlertBinding = DialogGeneralSellAddProductBinding.inflate(inflater,
            ConstraintLayout(builder.context),false)
        builder.setView(dialogAlertBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        val spinnerGroupAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.group, R.layout.spinner_text_style_2)
       // dialogAlertBinding.spinnerGroup.adapter = spinnerGroupAdapter

        val spinnerContentAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.reason, R.layout.spinner_text_style_2)
       // dialogAlertBinding.spinnerContent.adapter = spinnerContentAdapter

        dialogAlertBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        //alertDialog.window?.setLayout(750,900)

        dialogAlertBinding.btnContinue.setOnClickListener {
            binding.rvGeneralSell.visibility = View.VISIBLE
            binding.linearLayoutPoloValue.visibility = View.VISIBLE
            alertDialog.dismiss()
        }
    }

}