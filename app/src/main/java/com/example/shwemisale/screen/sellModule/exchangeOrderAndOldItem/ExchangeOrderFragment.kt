package com.example.shwemisale.screen.sellModule.exchangeOrderAndOldItem

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.*
import com.example.shwemisale.databinding.DialogExchangeOrderBinding
import com.example.shwemisale.databinding.FragmentExchangeOrderBinding
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class ExchangeOrderFragment : Fragment() {

    lateinit var binding: FragmentExchangeOrderBinding
    lateinit var dialogExchangeOrderBinding: DialogExchangeOrderBinding
    private val viewModel by viewModels<ExchangeOrderViewModel>()

    private val args by navArgs<ExchangeOrderFragmentArgs>()
    private lateinit var barlauncer: Any
    private lateinit var loading: AlertDialog
    private var goldType18KId = ""

    @Inject
    lateinit var localDatabase: LocalDatabase

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
            if (args.scannedCodesList.toList().contains(it)) {
                Toast.makeText(
                    requireContext(),
                    "This code is already scanned in old stocks",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.scanVoucher(it)
            }
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
                    if (args.scannedCodesList.toList()
                            .contains(binding.edtVoucherBalance.text.toString())
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "This code is already scanned in old stocks",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        viewModel.scanVoucher(binding.edtVoucherBalance.text.toString())
                    }
                    hideKeyboard(activity, binding.edtVoucherBalance)
                    return true
                }
                return false
            }
        })



        viewModel.stockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()

                    var totalPawnPrice = 0
                    var totalGoldWeightYwae = 0.0
                    var totalBVoucherBuyingPrice = 0
                    it.data.orEmpty().forEach {
                        totalPawnPrice += it.calculated_for_pawn!!.toInt()
                        totalGoldWeightYwae += it.f_voucher_shown_gold_weight_ywae!!.toDouble()
                        totalBVoucherBuyingPrice += it.b_voucher_buying_value!!.toInt()
                    }
                    viewModel.saveTotalPawnPrice(totalPawnPrice.toString())
                    viewModel.saveTotalGoldWeightYwae(totalGoldWeightYwae.toString())
                    viewModel.saveTotalBVoucherBuyingPrice(totalBVoucherBuyingPrice.toString())

                    view.findNavController().navigate(
                        ExchangeOrderFragmentDirections.actionExchangeOrderFragmentToWithKPYFragment(
                            args.scannedProducts,
                            generateNumberFromEditText(binding.edtOldVoucherPayment).toInt(),
                            if (binding.edtGoldFromHomeVoucher.text.isNullOrEmpty()) null else binding.edtGoldFromHomeVoucher.text.toString()
                        )
                    )
                }

                is Resource.Error -> {
                    loading.dismiss()
                    if (it.message == "Session key not found!") {
                        view.findNavController().navigate(
                            ExchangeOrderFragmentDirections.actionExchangeOrderFragmentToWithKPYFragment(
                                args.scannedProducts,
                                generateNumberFromEditText(binding.edtOldVoucherPayment).toInt(),
                                if (binding.edtGoldFromHomeVoucher.text.isNullOrEmpty()) null else binding.edtGoldFromHomeVoucher.text.toString(),
                            )
                        )
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.updateEValueLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.getStockFromHomeList()
                    Toast.makeText(requireContext(), "Old Stock's Data Updated", Toast.LENGTH_LONG)
                        .show()

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.getGoldTypePrice()
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    goldType18KId =
                        it.data?.find { it.name == "WG" }?.id.orEmpty()

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        viewModel.scanVoucherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    binding.edtOldVoucherPayment.setText(it.data?.paid_amount)
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
        dialogExchangeOrderBinding.btnWithKPY.isVisible =
            args.scannedProducts.map { it.gold_type_id }.toSet().toList().size == 1 &&
                    args.scannedProducts.map { it.gold_type_id }.toSet()
                        .toList()[0] != goldType18KId &&
                    args.scannedProducts.map { it.edited_gold_price }.toSet().toList().size == 1
        dialogExchangeOrderBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()


        dialogExchangeOrderBinding.btnWithValue.setOnClickListener {
            view?.findNavController()
                ?.navigate(
                    ExchangeOrderFragmentDirections.actionExchangeOrderFragmentToWithValueFragment(
                        args.scannedProducts,
                        generateNumberFromEditText(binding.edtOldVoucherPayment).toInt(),
                        if (binding.edtGoldFromHomeVoucher.text.isNullOrEmpty()) null else binding.edtGoldFromHomeVoucher.text.toString()
                    )
                )
            alertDialog.dismiss()
        }
        dialogExchangeOrderBinding.btnWithKPY.setOnClickListener {
            if (args.goldPrice.isNullOrEmpty().not()) {
                if (localDatabase.getStockFromHomeSessionKey().isNullOrEmpty()) {
                    findNavController().navigate(
                        ExchangeOrderFragmentDirections.actionExchangeOrderFragmentToWithKPYFragment(
                            args.scannedProducts,
                            generateNumberFromEditText(binding.edtOldVoucherPayment).toInt(),
                            if (binding.edtGoldFromHomeVoucher.text.isNullOrEmpty()) null else binding.edtGoldFromHomeVoucher.text.toString()
                        )
                    )
                } else {
                    viewModel.updateEvalue(
                        args.goldPrice.orEmpty()
                    )
                }

            } else {
                Toast.makeText(requireContext(), "Gold Price is empty", Toast.LENGTH_LONG).show()
            }

            alertDialog.dismiss()
        }
    }

}