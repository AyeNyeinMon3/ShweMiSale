package com.example.shwemisale.screen.sellModule.exchangeOrderAndOldItem

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.Resource
import com.example.shwemi.util.generateNumberFromEditText
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.hideKeyboard
import com.example.shwemisale.databinding.DialogExchangeOrderBinding
import com.example.shwemisale.databinding.FragmentExchangeOrderBinding
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import com.example.shwemisale.screen.sellModule.normalSaleScanStock.ScanStockViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeOrderFragment : Fragment() {

    lateinit var binding: FragmentExchangeOrderBinding
    lateinit var dialogExchangeOrderBinding: DialogExchangeOrderBinding
    private val viewModel by viewModels<ExchangeOrderViewModel>()

    private val args by navArgs<ExchangeOrderFragmentArgs>()
    private lateinit var barlauncer: Any
    private lateinit var loading: AlertDialog

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
        loading = requireContext().getAlertDialog()
        barlauncer = this.getBarLauncher(requireContext()) {
            binding.edtVoucherBalance.setText(it)
            binding.edtGoldFromHomeVoucher.setText(it)
            viewModel.scanVoucher(it)
        }
        binding.textInputLayoutVoucherBalance.setEndIconOnClickListener {
            this.scanQrCode(requireContext(), barlauncer)
        }
        binding.edtVoucherBalance.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    // Perform action on key press
                    viewModel.scanVoucher(binding.edtVoucherBalance.text.toString())
                    hideKeyboard(activity, binding.edtVoucherBalance)
                    return true
                }
                return false
            }
        })

        viewModel.scanVoucherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    binding.edtOldVoucherPayment.setText(it.data)
                    binding.edtGoldFromHomeVoucher.setText(binding.edtVoucherBalance.text.toString())

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnContinue.setOnClickListener {
            showExchangeOrderDialog()
        }
        binding.btnSkip.setOnClickListener {
            showExchangeOrderDialog()
        }

    }


    fun showExchangeOrderDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogExchangeOrderBinding =
            DialogExchangeOrderBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogExchangeOrderBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogExchangeOrderBinding.btnWithKPY.isEnabled =
            args.scannedProducts.map { it.jewellery_type_id }.toSet().toList().size == 1
        dialogExchangeOrderBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()


        dialogExchangeOrderBinding.btnWithValue.setOnClickListener {
            view?.findNavController()
                ?.navigate(ExchangeOrderFragmentDirections.actionExchangeOrderFragmentToWithValueFragment())
            alertDialog.dismiss()
        }
        dialogExchangeOrderBinding.btnWithKPY.setOnClickListener {
            view?.findNavController()?.navigate(
                ExchangeOrderFragmentDirections.actionExchangeOrderFragmentToWithKPYFragment(args.scannedProducts,
                    generateNumberFromEditText(binding.edtOldVoucherPayment).toInt()
                )
            )
            alertDialog.dismiss()
        }
    }

}