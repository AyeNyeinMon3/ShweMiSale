package com.example.shwemisale.screen.sellModule.openVoucher.withKPY

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.Resource
import com.example.shwemi.util.generateNumberFromEditText
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.showSuccessDialog
import com.example.shwemisale.databinding.FragmentWithKpyBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class WithKPYFragment : Fragment() {

    lateinit var binding: FragmentWithKpyBinding
    private val viewModel by viewModels<WithKPYViewModel>()
    private lateinit var loading: AlertDialog
    private val args by navArgs<WithKPYFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentWithKpyBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        //TODO fill product infos that scanned
        binding.edtOldVoucherPayment.setText(args.oldVoucherPaidAmount.toString())
        binding.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())
        val  totalGoldWeightKpy= getKPYFromYwae(viewModel.getTotalGoldWeightYwae().toDouble())
        binding.edtGoldFromHomeWeightK.setText(totalGoldWeightKpy[0].toInt().toString())
        binding.edtGoldFromHomeWeightP.setText(totalGoldWeightKpy[1].toInt().toString())
        binding.edtGoldFromHomeWeightY.setText(totalGoldWeightKpy[2].let { String.format("%.2f", it) })

        viewModel.submitWithKPYLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog(it.data.orEmpty()){
                        findNavController().popBackStack()
                    }
                }
                is Resource.Error -> {

                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.getGoldPriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.goldPrice = it.data?.gold_price.toString()
                    val poloGoldKyat = getKyatsFromKPY(
                        generateNumberFromEditText(binding.edtPoloGoldK).toInt(),
                        generateNumberFromEditText(binding.edtPoloGoldP).toInt(),
                        generateNumberFromEditText(binding.edtPoloGoldY).toDouble(),
                    )

                    val poloValue = poloGoldKyat * it.data?.gold_price.orEmpty()
                        .let { if (it.isEmpty()) 0.0 else it.toDouble() }
                    binding.edtPoloValue.setText(poloValue.toInt().toString())
                    val totalPrice =
                        generateNumberFromEditText(binding.edtPoloValue).toDouble() + generateNumberFromEditText(
                            binding.edtTotalFee
                        ).toDouble() +
                                generateNumberFromEditText(binding.edtPTclipValue).toDouble() + generateNumberFromEditText(
                            binding.edtTotalGemValue
                        ).toDouble()- generateNumberFromEditText(binding.edtGoldFromHomeValue).toDouble()

                    binding.edtCharge.setText(totalPrice.toInt().toString())

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == binding.radioBtnKpy.id) {
                val goldWeightKyat = getKyatsFromKPY(
                    generateNumberFromEditText(binding.edtGoldFromHomeWeightK).toInt(),
                    generateNumberFromEditText(binding.edtGoldFromHomeWeightP).toInt(),
                    generateNumberFromEditText(binding.edtGoldFromHomeWeightY).toDouble(),
                )

                val goldFromHomeValue = goldWeightKyat * viewModel.goldPrice
                    .let { if (it.isEmpty()) 0.0 else it.toDouble() }
                val totalPrice =
                    generateNumberFromEditText(binding.edtPoloValue).toDouble() + generateNumberFromEditText(
                        binding.edtTotalFee
                    ).toDouble() +
                            generateNumberFromEditText(binding.edtPTclipValue).toDouble() + generateNumberFromEditText(
                        binding.edtTotalGemValue
                    ).toDouble()-goldFromHomeValue

                binding.edtCharge.setText(totalPrice.toInt().toString())
            } else if (checkedId == binding.radioBtnValue.id) {
                val totalPrice =
                    generateNumberFromEditText(binding.edtPoloValue).toDouble() + generateNumberFromEditText(
                        binding.edtTotalFee
                    ).toDouble() +
                            generateNumberFromEditText(binding.edtPTclipValue).toDouble() + generateNumberFromEditText(
                        binding.edtTotalGemValue
                    ).toDouble()- generateNumberFromEditText(binding.edtGoldFromHomeValue).toDouble()

                binding.edtCharge.setText(totalPrice.toInt().toString())
            }
        }

        binding.btnCalculate.setOnClickListener {
            val leftMoney =
                generateNumberFromEditText(binding.edtReducedPay).toInt() + generateNumberFromEditText(
                    binding.edtCharge
                ).toInt() - generateNumberFromEditText(binding.edtDeposit).toInt()
            binding.edtBalance.setText(leftMoney.toInt().toString())
        }

            val scannedProducts = args.scannedProducts.toList()
            viewModel.getGoldPrice(args.scannedProducts.map { it.id })
            var totalGoldWeight = 0.0
            var totalGemWeight = 0.0
            var totalWastageWeight = 0.0
            var totalMaintenanceFees = 0.0
            var totalGemValue = 0
            var totalPtClipFees = 0
            scannedProducts.forEach {
                totalGoldWeight += it.gold_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() }
                totalGemWeight += it.gem_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() }
                totalWastageWeight += it.wastage_weight_ywae.let { if (it.isEmpty()) 0.0 else it.toDouble() }
                totalMaintenanceFees += it.maintenance_cost.let { if (it.isEmpty()) 0.0 else it.toDouble() }
                totalGemValue += it.gem_value.let { if (it.isEmpty()) 0 else it.toInt() }
                totalPtClipFees += it.pt_and_clip_cost.let { if (it.isEmpty()) 0 else it.toInt() }
            }

            var neededGoldWeight = totalGoldWeight - viewModel.getTotalGoldWeightYwae().toDouble()
            var totalWastageAndGoldWeight = totalGoldWeight + totalWastageWeight

            val totalProductGoldWeightKpy = getKPYFromYwae(totalGoldWeight)
            binding.edtTotalGoldWeightK.setText(totalProductGoldWeightKpy[0].toInt().toString())
            binding.edtTotalGoldWeightP.setText(totalProductGoldWeightKpy[1].toInt().toString())
            binding.edtTotalGoldWeightY.setText(totalProductGoldWeightKpy[2].let { String.format("%.2f", it) })

            val totalWastageWeightkpy = getKPYFromYwae(totalWastageWeight)
            binding.edtTotalDiseaseK.setText(totalWastageWeightkpy[0].toInt().toString())
            binding.edtTotalDiseaseP.setText(totalWastageWeightkpy[1].toInt().toString())
            binding.edtTotalDiseaseY.setText(totalWastageWeightkpy[2].let { String.format("%.2f", it) })

            val totalWastageAndGoldWeightkpy = getKPYFromYwae(totalWastageAndGoldWeight)
            binding.edtTotalDiseaseGWK.setText(totalWastageAndGoldWeightkpy[0].toInt().toString())
            binding.edtTotalDiseaseGWP.setText(totalWastageAndGoldWeightkpy[1].toInt().toString())
            binding.edtTotalDiseaseGWY.setText(totalWastageAndGoldWeightkpy[2].let {
                String.format(
                    "%.2f",
                    it
                )
            })

            val neededGoldWeightkpy = getKPYFromYwae(neededGoldWeight)
            binding.edtPoloGoldK.setText(neededGoldWeightkpy[0].toInt().toString())
            binding.edtPoloGoldP.setText(neededGoldWeightkpy[1].toInt().toString())
            binding.edtPoloGoldY.setText(neededGoldWeightkpy[2].let { String.format("%.2f", it) })
            var neededGoldPrice = getKyatsFromKPY(
                neededGoldWeightkpy[0].toInt(),
                neededGoldWeightkpy[1].toInt(),
                neededGoldWeightkpy[2]
            )// multiply with gold price

            binding.edtTotalFee.setText(totalMaintenanceFees.toString())
            binding.edtTotalGemValue.setText(totalGemValue.toString())
            binding.edtPTclipValue.setText(totalPtClipFees.toString())



        binding.btnPrint.setOnClickListener {
            val productIdList = mutableListOf<MultipartBody.Part>()
            val list = args.scannedProducts.map { it.id }
            repeat(list.size) {
                productIdList.add(
                    MultipartBody.Part.createFormData(
                        "product_id[]",
                        list[it]
                    )
                )
            }
            val paid_amount = binding.edtDeposit.text.toString()
            val reduced_cost = binding.edtReducedPay.text.toString()

            /** old stock list manipulation */

            viewModel.submitWithKPY(
                productIdList,
                viewModel.getCustomerId(),
                paid_amount,
                reduced_cost,
                MultipartBody.Part.createFormData(
                    "old_voucher_paid_amount",
                    args.oldVoucherPaidAmount.toString()
                )
            )
        }
    }


}