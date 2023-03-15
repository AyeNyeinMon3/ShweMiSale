package com.example.shwemisale.screen.sellModule.recieveNewOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.*
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentReceiveNewOrderBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.example.shwemisale.screen.sellModule.SampleListRecyclerAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

@AndroidEntryPoint
class ReceiveNewOrderFragment : Fragment() {

    lateinit var binding: FragmentReceiveNewOrderBinding
    private val viewModel by viewModels<ReceiveNewOrderViewModel>()
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var loading: AlertDialog
    private var selectedGoldType = ""

    var oldStockTotalGoldWeightYwae = 0.0
    var totalEstimatedWastageYwae = 0.0
    var totalGoldAndWastageYwae = 0.0
    var neededGoldWeightYwae = 0.0
    var orderedGoldWeightYwae = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentReceiveNewOrderBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date")
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
        val adapter = SampleListRecyclerAdapter()
        binding.includeRvSample.rvSampleList.adapter = adapter
        // binding.spinnerGoldType.adapter = adapter
        binding.tvOrderDate.setOnClickListener {
            datePicker.show(childFragmentManager, "choose date")
        }
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvOrderDate.text = date
        }
        viewModel.samplesFromRoom.observe(viewLifecycleOwner) {
            viewModel.takenSamples = it
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    val goldTypeList = it.data!!.map {
                        it.name.orEmpty()
                    }
                    val reasonArrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.item_drop_down_text, goldTypeList)
                    binding.actGoldType.addTextChangedListener { editable ->
                        selectedGoldType = it.data!!.find {
                            it.name == binding.actGoldType.text.toString()
                        }?.id.orEmpty()
                        val goldPrice = it.data!!.find {
                            it.name == binding.actGoldType.text.toString()
                        }?.price
                        binding.edtGoldPrice.setText(goldPrice.toString())
                    }
                    binding.actGoldType.setAdapter(reasonArrayAdapter)
                    binding.actGoldType.setText(goldTypeList[0], false)
                    binding.actGoldType.setOnClickListener {
                        binding.actGoldType.showDropdown(reasonArrayAdapter)
                    }

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        viewModel.receiveNewOrderLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog(it.data!!){

                    }

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        viewModel.stockFromHomeListInRoom.observe(viewLifecycleOwner) {
            viewModel.stockFromHomeList = it
            it.forEach {
                oldStockTotalGoldWeightYwae += it.goldWeightYwae.orEmpty()
                    .let { if (it.isEmpty()) 0.0 else it.toDouble() }
            }
            val oldStockGoldWeightKpy = getKPYFromYwae(oldStockTotalGoldWeightYwae)
            binding.edtGoldFromHomeWeightK.setText(oldStockGoldWeightKpy[0].toInt().toString())
            binding.edtGoldFromHomeWeightP.setText(oldStockGoldWeightKpy[1].toInt().toString())
            binding.edtGoldFromHomeWeightY.setText(oldStockGoldWeightKpy[2].let {
                String.format(
                    "%.2f",
                    it
                )
            })
        }

        viewModel.stockFromHomeFinalInfoInRoom.observe(viewLifecycleOwner) {
            viewModel.stockFromHomeFinalInfo = it
            binding.edtGoldFromHomeValue.setText(it.finalVoucherPaidAmount)
        }

        binding.btnInventory.setOnClickListener {
            view.findNavController()
                .navigate(ReceiveNewOrderFragmentDirections.actionReceiveNewOrderFragmentToInventoryStockFragment())
        }
        binding.btnOutside.setOnClickListener {
            view.findNavController()
                .navigate(ReceiveNewOrderFragmentDirections.actionReceiveNewOrderFragmentToOutsideStockFragment())
        }

        binding.btnCalculate.setOnClickListener {

            val singleWastageYwae = getYwaeFromKPY(
                generateNumberFromEditText(binding.edtSingleEstimatedWastageK).toInt(),
                generateNumberFromEditText(binding.edtSingleEstimatedWastageP).toInt(),
                generateNumberFromEditText(binding.edtSingleEstimatedWastageY).toDouble(),
            )

            orderedGoldWeightYwae = getYwaeFromKPY(
                generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
                generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
                generateNumberFromEditText(binding.edtGoldWeightY).toDouble(),
            )

            totalEstimatedWastageYwae =
                generateNumberFromEditText(binding.edtQuantity).toInt() * singleWastageYwae
            val totalEstimatedWastageKpy = getKPYFromYwae(totalEstimatedWastageYwae)
            binding.edtEstimatedUnderCountK.setText(totalEstimatedWastageKpy[0].toInt().toString())
            binding.edtEstimatedUnderCountP.setText(totalEstimatedWastageKpy[1].toInt().toString())
            binding.edtEstimatedUnderCountY.setText(totalEstimatedWastageKpy[2].let {
                String.format(
                    "%.2f",
                    it
                )
            })

            totalGoldAndWastageYwae = orderedGoldWeightYwae + totalEstimatedWastageYwae
            val totalGoldAndWastageKpy = getKPYFromYwae(totalGoldAndWastageYwae)
            binding.edtDiseaseGoldWeightK.setText(totalGoldAndWastageKpy[0].toInt().toString())
            binding.edtDiseaseGoldWeightP.setText(totalGoldAndWastageKpy[1].toInt().toString())
            binding.edtDiseaseGoldWeightY.setText(totalGoldAndWastageKpy[2].let {
                String.format(
                    "%.2f",
                    it
                )
            })

            neededGoldWeightYwae = totalGoldAndWastageYwae - oldStockTotalGoldWeightYwae
            val neededGoldWeightKpy = getKPYFromYwae(neededGoldWeightYwae)
            binding.edtPoloGoldWeightK.setText(neededGoldWeightKpy[0].toInt().toString())
            binding.edtPoloGoldWeightP.setText(neededGoldWeightKpy[1].toInt().toString())
            binding.edtPoloGoldWeightY.setText(neededGoldWeightKpy[2].let {
                String.format(
                    "%.2f",
                    it
                )
            })

            val estimatedPrice = (neededGoldWeightYwae/128) * generateNumberFromEditText(binding.edtGoldPrice).toDouble()
            binding.edtEstimatedCharge.setText(estimatedPrice.toInt().toString())

            val remainedMoney = generateNumberFromEditText(binding.edtEstimatedCharge).toInt() - generateNumberFromEditText(binding.edtDeposit).toInt()
            binding.edtBalance.setText(remainedMoney.toString())
        }



        binding.btnPrint.setOnClickListener {
            val productIdList = mutableListOf<MultipartBody.Part>()
            val paid_amount = generateNumberFromEditText(binding.edtDeposit)
            val finalInfo = viewModel.stockFromHomeFinalInfo

            /** old stock list manipulation */
            //List from room
            val oldStockList = viewModel.stockFromHomeList

            //Fields from oldStockInfo
            val old_stocks_nameList = mutableListOf<MultipartBody.Part>()
            val oldStockImageIds = mutableListOf<MultipartBody.Part>()
            val oldStockImageFile = mutableListOf<MultipartBody.Part>()
            val oldStockCondition = mutableListOf<MultipartBody.Part>()
            val oldStockQty = mutableListOf<MultipartBody.Part>()
            val oldStockSize = mutableListOf<MultipartBody.Part>()

            val oldStockGemWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockGoldGemWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockImpurityWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockGoldWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockWastageWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockRebuyPrice = mutableListOf<MultipartBody.Part>()
            val oldStockGQinCarat = mutableListOf<MultipartBody.Part>()
            val oldStockMaintenance_cost = mutableListOf<MultipartBody.Part>()
            val oldStockGemValue = mutableListOf<MultipartBody.Part>()
            val oldStockGemDetailQty = mutableListOf<MultipartBody.Part>()
            val oldStockGemDetailGm = mutableListOf<MultipartBody.Part>()
            val oldStockGemDetailYwae = mutableListOf<MultipartBody.Part>()
            val oldStockPTAndClipCost = mutableListOf<MultipartBody.Part>()
            val oldStockCalculatedBuyingValue = mutableListOf<MultipartBody.Part>()
            val oldStockPriceForPawn = mutableListOf<MultipartBody.Part>()
            val oldStockCalculatedForPawn = mutableListOf<MultipartBody.Part>()
            val oldStockABuyingPrice = mutableListOf<MultipartBody.Part>()
            val oldStockb_voucher_buying_value = mutableListOf<MultipartBody.Part>()
            val oldStockc_voucher_buying_price = mutableListOf<MultipartBody.Part>()
            val oldStockDGoldWeightY = mutableListOf<MultipartBody.Part>()
            val oldStockEPriceFromNewVoucher = mutableListOf<MultipartBody.Part>()
            val oldStockFVoucherShownGoldWeightY = mutableListOf<MultipartBody.Part>()

            val sampleIdMultiPartList = mutableListOf<MultipartBody.Part>()
            val sampleIdList = viewModel.takenSamples.map { it.id }
            repeat(sampleIdList.size){
                sampleIdMultiPartList.add(
                    MultipartBody.Part.createFormData(
                        "samples[][sample_id]",
                        sampleIdList[it].toString()
                    )
                )
            }

            repeat(oldStockList.size) {
                val imageFile = oldStockList[it].image?.let { File(it) }
                val imageId = oldStockList[it].imageId
                old_stocks_nameList.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][stock_name]",
                        oldStockList[it].name.toString()
                    )
                )
                imageId?.let { id ->
                    oldStockImageIds.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][image][id]",
                            id
                        )
                    )
                }
                imageFile?.let { file ->
                    oldStockImageFile.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][image][file]",
                            file.name,
                            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        )
                    )
                }
                oldStockCondition.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][stock_condition]",
                        oldStockList[it].oldStockCondition.toString()
                    )
                )
                oldStockQty.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][qty]",
                        oldStockList[it].qty.toString()
                    )
                )

                oldStockSize.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][size]",
                        oldStockList[it].size.toString()
                    )
                )

                oldStockGemWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gem_weight_ywae]",
                        oldStockList[it].gem_weight_ywae.toString()
                    )
                )
                oldStockGoldGemWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gold_gem_weight_ywae]",
                        getYwaeFromGram(
                            oldStockList[it].gold_and_gem_weight_gm?.toDouble() ?: 0.0
                        ).toString()
                    )
                )
                oldStockImpurityWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][impurities_weight_ywae]",
                        oldStockList[it].oldStockImpurityWeightY.toString()
                    )
                )
                oldStockGoldWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gold_weight_ywae]",
                        oldStockList[it].goldWeightYwae.toString()
                    )
                )
                oldStockWastageWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][wastage_ywae]",
                        oldStockList[it].wastage_ywae.toString()
                    )
                )
                oldStockRebuyPrice.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][rebuy_price]",
                        oldStockList[it].rebuyPrice.toString()
                    )
                )
                oldStockGQinCarat.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gq_in_carat]",
                        oldStockList[it].oldStockGQinCarat.toString()
                    )
                )
                oldStockMaintenance_cost.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][maintenance_cost]",
                        oldStockList[it].maintenance_cost.toString()
                    )
                )
                oldStockGemValue.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][gem_value]",
                        oldStockList[it].gem_value.toString()
                    )
                )
                repeat(oldStockList[it].gem_details_qty.size) { insideIndex ->
                    oldStockGemDetailQty.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$it][gem_qty]",
                            oldStockList[it].gem_details_qty[insideIndex]
                        )
                    )
                }

                repeat(oldStockList[it].gem_details_gm_per_units.size) { insideIndex ->
                    oldStockGemDetailGm.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$it][gem_weight_gm_per_unit]",
                            oldStockList[it].gem_details_gm_per_units[insideIndex]
                        )
                    )
                }
                repeat(oldStockList[it].gem_details_ywae_per_units.size) { insideIndex ->
                    oldStockGemDetailYwae.add(
                        MultipartBody.Part.createFormData(
                            "old_stocks[$it][gem_weight_details][$it][gem_weight_ywae_per_unit]",
                            oldStockList[it].gem_details_ywae_per_units[insideIndex]
                        )
                    )
                }

                oldStockPTAndClipCost.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][pt_and_clip_cost]",
                        oldStockList[it].pt_and_clip_cost.toString()
                    )
                )
                // need to discuss
                oldStockCalculatedBuyingValue.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][calculated_buying_value]",
                        oldStockList[it].rebuyPrice.toString()
                    )
                )

                oldStockPriceForPawn.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][price_for_pawn]",
                        oldStockList[it].priceForPawn.toString()
                    )
                )

                oldStockCalculatedForPawn.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][calculated_for_pawn]",
                        oldStockList[it].calculatedPriceForPawn.toString()
                    )
                )
                oldStockABuyingPrice.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][a_buying_price]",
                        oldStockList[it].oldStockABuyingPrice.toString()
                    )
                )
                oldStockb_voucher_buying_value.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][b_voucher_buying_value]",
                        oldStockList[it].oldStockb_voucher_buying_value.toString()
                    )
                )

                oldStockc_voucher_buying_price.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][c_voucher_buying_price]",
                        oldStockList[it].oldStockc_voucher_buying_value.toString()
                    )
                )
                oldStockDGoldWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][d_gold_weight_ywae]",
                        oldStockList[it].oldStockDGoldWeightY.toString()
                    )
                )
                oldStockEPriceFromNewVoucher.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][e_price_from_new_voucher]",
                        oldStockList[it].oldStockEPriceFromNewVoucher.toString()
                    )
                )
                oldStockFVoucherShownGoldWeightY.add(
                    MultipartBody.Part.createFormData(
                        "old_stocks[$it][f_voucher_shown_gold_weight_ywae]",
                        oldStockList[it].oldStockFVoucherShownGoldWeightY.toString()
                    )
                )

            }

            viewModel.submit(
                binding.edtOrderItem.text.toString(),
                selectedGoldType,
                binding.edtGoldPrice.text.toString(),
                orderedGoldWeightYwae.toString(),
                totalEstimatedWastageYwae.toString(),
                generateNumberFromEditText(binding.edtQuantity),
                generateNumberFromEditText(binding.edtGemValue),
                generateNumberFromEditText(binding.edtFee),
                binding.tvOrderDate.text.toString(),
                binding.edtNote.text.toString(),
                viewModel.getCustomerId(),
                paid_amount,
                "0",
                old_stocks_nameList,
                oldStockImageIds,
                oldStockImageFile,
                oldStockCondition,
                oldStockQty,
                oldStockSize,
                oldStockGemWeightY,
                oldStockGoldGemWeightY,
                oldStockImpurityWeightY,
                oldStockGoldWeightY,
                oldStockWastageWeightY,
                oldStockRebuyPrice,
                oldStockGQinCarat,
                oldStockMaintenance_cost,
                oldStockGemValue,
                oldStockGemDetailQty,
                oldStockGemDetailGm,
                oldStockGemDetailYwae,
                oldStockPTAndClipCost,
                oldStockCalculatedBuyingValue,
                oldStockPriceForPawn,
                oldStockCalculatedForPawn,
                oldStockABuyingPrice,
                oldStockb_voucher_buying_value,
                oldStockc_voucher_buying_price,
                oldStockDGoldWeightY,
                oldStockEPriceFromNewVoucher,
                oldStockFVoucherShownGoldWeightY,
                oldStockSampleListId = sampleIdMultiPartList
            )
        }

    }


}