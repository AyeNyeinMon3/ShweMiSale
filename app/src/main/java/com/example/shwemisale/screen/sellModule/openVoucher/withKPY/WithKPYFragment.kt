package com.example.shwemisale.screen.sellModule.openVoucher.withKPY

import android.annotation.SuppressLint
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
import com.example.shwemi.util.*
import com.example.shwemisale.databinding.FragmentWithKpyBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
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
    private var redeemMoney = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentWithKpyBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        val totalGoldWeightKpy = getKPYFromYwae(
            viewModel.getTotalGoldWeightYwae().let { if (it.isEmpty()) 0.0 else it.toDouble() })
        binding.edtGoldFromHomeWeightK.setText(totalGoldWeightKpy[0].toInt().toString())
        binding.edtGoldFromHomeWeightP.setText(totalGoldWeightKpy[1].toInt().toString())
        binding.edtGoldFromHomeWeightY.setText(totalGoldWeightKpy[2].let {
            String.format(
                "%.2f",
                it
            )
        })
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        binding.edtOldVoucherPayment.setText(args.oldVoucherPaidAmount.toString())
        setTotalPromotionPrice()
        binding.btnEditGoldFromHomeValue.setOnClickListener {
            findNavController().navigate(
                WithKPYFragmentDirections.actionGlobalGoldFromHomeFragment(
                    "Global",
                    null
                )
            )
        }
        binding.btnEditGoldFromHomeWeight.setOnClickListener {
            findNavController().navigate(
                WithKPYFragmentDirections.actionGlobalGoldFromHomeFragment(
                    "Global",
                    null
                )
            )
        }

        viewModel.getUserRedeemPointsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    binding.edtCustomerPoint.setText(it.data)
                }

                is Resource.Error -> {

                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.getRedeemMoneyLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    redeemMoney = (it.data?:"0").toInt()
//                    binding.edtReducedPay.setText(
//                        (generateNumberFromEditText(binding.edtReducedPay).toInt() + it.data.orEmpty()
//                            .let { if (it.isEmpty()) 0 else it.toInt() }).toString()
//                    )
                }

                is Resource.Error -> {

                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.submitWithKPYLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog(it.data.orEmpty()) {
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

//                    val totalPrice =
//                        generateNumberFromEditText(binding.edtPoloValue).toDouble() + generateNumberFromEditText(
//                            binding.edtTotalFee
//                        ).toDouble() +
//                                generateNumberFromEditText(binding.edtPTclipValue).toDouble() + generateNumberFromEditText(
//                            binding.edtTotalGemValue
//                        ).toDouble() - generateNumberFromEditText(binding.edtGoldFromHomeValue).toDouble()
//
//                    binding.edtCharge.setText(totalPrice.toInt().toString())

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
                    ).toDouble() - goldFromHomeValue - args.oldVoucherPaidAmount
                binding.edtGoldFromHomeValue.text?.clear()
                val totalGoldWeightKpy = getKPYFromYwae(
                    viewModel.getTotalGoldWeightYwae()
                        .let { if (it.isEmpty()) 0.0 else it.toDouble() })
                binding.edtGoldFromHomeWeightK.setText(totalGoldWeightKpy[0].toInt().toString())
                binding.edtGoldFromHomeWeightP.setText(totalGoldWeightKpy[1].toInt().toString())
                binding.edtGoldFromHomeWeightY.setText(totalGoldWeightKpy[2].let {
                    String.format(
                        "%.2f",
                        it
                    )
                })
                calculateValues()
//                binding.edtCharge.setText(totalPrice.toInt().toString())
            } else if (checkedId == binding.radioBtnValue.id) {
                binding.edtGoldFromHomeWeightK.text?.clear()
                binding.edtGoldFromHomeWeightP.text?.clear()
                binding.edtGoldFromHomeWeightY.text?.clear()
                binding.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())
//                val totalPrice =
//                    generateNumberFromEditText(binding.edtPoloValue).toDouble() + generateNumberFromEditText(
//                        binding.edtTotalFee
//                    ).toDouble() +
//                            generateNumberFromEditText(binding.edtPTclipValue).toDouble() + generateNumberFromEditText(
//                        binding.edtTotalGemValue
//                    ).toDouble() - generateNumberFromEditText(binding.edtGoldFromHomeValue).toDouble()- args.oldVoucherPaidAmount
                calculateValues()

            }
        }

        binding.btnCalculate.setOnClickListener {
            viewModel.getRedeemMoney(generateNumberFromEditText(binding.edtRedeemPoint))
            calculateValues()
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

        var neededGoldWeight =
            totalGoldWeight + totalWastageWeight - viewModel.getTotalGoldWeightYwae()
                .let { if (it.isEmpty()) 0.0 else it.toDouble() }
        var totalWastageAndGoldWeight = totalGoldWeight + totalWastageWeight

        val totalProductGoldWeightKpy = getKPYFromYwae(totalGoldWeight)
        binding.edtTotalGoldWeightK.setText(totalProductGoldWeightKpy[0].toInt().toString())
        binding.edtTotalGoldWeightP.setText(totalProductGoldWeightKpy[1].toInt().toString())
        binding.edtTotalGoldWeightY.setText(totalProductGoldWeightKpy[2].let {
            String.format(
                "%.2f",
                it
            )
        })

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

//        val neededGoldWeightkpy = getKPYFromYwae(neededGoldWeight)
//        binding.edtPoloGoldK.setText(neededGoldWeightkpy[0].toInt().toString())
//        binding.edtPoloGoldP.setText(neededGoldWeightkpy[1].toInt().toString())
//        binding.edtPoloGoldY.setText(neededGoldWeightkpy[2].let { String.format("%.2f", it) })


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
            viewModel.submitWithKPY(
                productIdList,
                viewModel.getCustomerId(),
                paid_amount,
                reduced_cost,
                args.oldVoucherCode,
                MultipartBody.Part.createFormData(
                    "old_voucher_paid_amount",
                    args.oldVoucherPaidAmount.toString()
                ),
            )
        }
    }

    fun setTotalPromotionPrice() {
        var totalPromotionPrice = 0
        args.scannedProducts.map { it.promotion_discount.toInt() }.forEach {
            totalPromotionPrice += it
        }
        binding.edtTotalPromotionPrice.setText(totalPromotionPrice.toString())

    }

    fun calculateValues() {
        val goldFromHomeValue = if (binding.radioBtnKpy.isChecked) {
            0.0
        } else {
            generateNumberFromEditText(binding.edtGoldFromHomeValue).toDouble()
        }


        val goldFromHomeTotalYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtGoldFromHomeWeightK).toInt(),
            generateNumberFromEditText(binding.edtGoldFromHomeWeightP).toInt(),
            generateNumberFromEditText(binding.edtGoldFromHomeWeightY).toDouble(),
        )
        val totalGoldWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtTotalGoldWeightK).toInt(),
            generateNumberFromEditText(binding.edtTotalGoldWeightP).toInt(),
            generateNumberFromEditText(binding.edtTotalGoldWeightY).toDouble(),
        )
        val totalWastageYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtTotalDiseaseK).toInt(),
            generateNumberFromEditText(binding.edtTotalDiseaseP).toInt(),
            generateNumberFromEditText(binding.edtTotalDiseaseY).toDouble(),
        )
        var neededGoldWeight =
            totalGoldWeightYwae + totalWastageYwae - goldFromHomeTotalYwae

        val neededGoldWeightkpy = getKPYFromYwae(neededGoldWeight)
        binding.edtPoloGoldK.setText(neededGoldWeightkpy[0].toInt().toString())
        binding.edtPoloGoldP.setText(neededGoldWeightkpy[1].toInt().toString())
        binding.edtPoloGoldY.setText(neededGoldWeightkpy[2].let { String.format("%.2f", it) })

        val poloGoldKyat = getKyatsFromKPY(
            generateNumberFromEditText(binding.edtPoloGoldK).toInt(),
            generateNumberFromEditText(binding.edtPoloGoldP).toInt(),
            generateNumberFromEditText(binding.edtPoloGoldY).toDouble(),
        )

        val poloValue = poloGoldKyat * viewModel.goldPrice
            .let { if (it.isEmpty()) 0.0 else it.toDouble() }
        binding.edtPoloValue.setText(getRoundDownForPrice(poloValue.toInt()).toString())
        val totalPrice =
            generateNumberFromEditText(binding.edtPoloValue).toDouble() + generateNumberFromEditText(
                binding.edtTotalFee
            ).toDouble() +
                    generateNumberFromEditText(binding.edtPTclipValue).toDouble() + generateNumberFromEditText(
                binding.edtTotalGemValue
            ).toDouble() - goldFromHomeValue - args.oldVoucherPaidAmount - generateNumberFromEditText(binding.edtTotalPromotionPrice).toInt() - redeemMoney

        binding.edtCharge.setText(totalPrice.toInt().toString())
        val leftMoney =
            +generateNumberFromEditText(
                binding.edtCharge
            ).toInt() - generateNumberFromEditText(binding.edtDeposit).toInt() - generateNumberFromEditText(
                binding.edtReducedPay
            ).toInt()
        binding.edtBalance.setText(leftMoney.toInt().toString())
    }


}