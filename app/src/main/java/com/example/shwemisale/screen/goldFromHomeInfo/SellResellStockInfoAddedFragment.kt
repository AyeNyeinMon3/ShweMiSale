package com.example.shwemisale.screen.goldFromHomeInfo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.*
import com.example.shwemisale.R
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.data_layers.dto.goldFromHome.Image
import com.example.shwemisale.databinding.DialogGemWeightBinding
import com.example.shwemisale.databinding.DialogMinusPercentageBinding
import com.example.shwemisale.databinding.DialogResellStockInfoBinding
import com.example.shwemisale.databinding.FragmentResellStockInfoAddedSellBinding
import com.example.shwemisale.screen.goldFromHome.*
import com.example.shwemisale.screen.sellModule.ResellStockRecyclerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class SellResellStockInfoAddedFragment : Fragment() {
    lateinit var binding: FragmentResellStockInfoAddedSellBinding
    lateinit var dialogBinding: DialogResellStockInfoBinding
    lateinit var dialogGemWeightBinding: DialogGemWeightBinding
    lateinit var dialogMinusPercentageBinding: DialogMinusPercentageBinding
    private val viewModel by viewModels<GoldFromHomeDetailViewModel>()
    private lateinit var loading: AlertDialog
    private val args by navArgs<SellResellStockInfoAddedFragmentArgs>()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    private var hasOtherReducedCost: Boolean = false
    var count = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentResellStockInfoAddedSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null && data.data != null) {
                    getRealPathFromUri(requireContext(), data.data!!)?.let { path ->
                        viewModel.selectedImagePath = path
                        binding.ivBg.loadImageWithGlide(path)
                        binding.ivCamera.isVisible = false
                    }
                }
            }
        }
        storagePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                chooseImage()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()

        viewModel.createStockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.gemWeightCustomList.removeAll(viewModel.gemWeightCustomList)
                    requireContext().showSuccessDialog("Success") {
                        findNavController().popBackStack()
                    }

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.updateStockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.gemWeightCustomList.removeAll(viewModel.gemWeightCustomList)
                    requireContext().showSuccessDialog("Success") {
                        findNavController().popBackStack()
                    }

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.ivCamera.setOnClickListener {
            if (isExternalStoragePermissionGranted().not()) {
                requestStoragePermission()
            } else {
                chooseImage()
            }
        }
        binding.ivBg.setOnClickListener {
            if (isExternalStoragePermissionGranted().not()) {
                requestStoragePermission()
            } else {
                chooseImage()
            }
        }
        viewModel.horizontalOption = "Damage"
//        viewModel.getRebuyPrice("Damage", "X", "small", args.id)
        args.stockFromHomeInfo?.let {
            bindPassedData(it)
        }
        binding.edtDecidedPawnPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
               binding.edtPawnPrice.text?.clear()
            }
        })
        setXYZselection()

        binding.cbOtherCosts.setOnCheckedChangeListener { compoundButton, isChecked ->
            hasOtherReducedCost = isChecked
            if (isChecked) {
                enableOtherCosts()

            } else {
                disableOtherCosts()
            }
            calculateBuyPriceFromShop(hasOtherReducedCost)
            calculatePawnPrice(hasOtherReducedCost)
//            calculatePriceB(hasOtherReducedCost)
//            calculatePriceA(hasOtherReducedCost)
//            calculatePriceD(hasOtherReducedCost)

        }
        binding.radioGroupType.setOnCheckedChangeListener { radioGroup, checkedId ->
            var lastValue = binding.edtRepurchasePrice.text.toString()
            if (checkedId == binding.radioBtnOutsideStock.id) {
                binding.edtRepurchasePrice.setText("")
            } else {
                binding.edtRepurchasePrice.setText(lastValue)
//                binding.edtPriceC.setText(lastValue)
            }
        }
        binding.btnCalculateGoldAndGemWeight.setOnClickListener {
            calculateGoldAndGemWeight()
        }
        binding.btnCalculateRebuyPriceFromGq.setOnClickListener {
            calculateRebuyPriceFromGQ()
        }
        binding.btnCalculate4.setOnClickListener {
            binding.btnCalculate2.isEnabled = true
            binding.btnCalculate3.isEnabled = true
            resetPricesValue()
            calculateWhenFValueChange()
        }
        binding.btnCalculate2.setOnClickListener {
            binding.btnCalculate2.isEnabled = false
            calculateGQinCarat()
            calculateGoldWeight()
            calculateBuyPriceFromShop(hasOtherReducedCost)
            resetPricesValue()
            makeCalculateButtonEnableWhenEdit(
                listOf(
                    //Gold and gem weight
                    binding.edtGoldAndGemWeightGm,
                    binding.edtGoldAndGemWeightK,
                    binding.edtGoldAndGemWeightP,
                    binding.edtGoldAndGemWeightY,
                    //gem weight
                    binding.edtGemWeightK,
                    binding.edtGemWeightP,
                    binding.edtGemWeightY,

                    //impurity weight
                    binding.edtGeeWeightK,
                    binding.edtGeeWeightP,
                    binding.edtGeeWeightY,

                    // rebuy price
                    binding.edtRepurchasePrice,

                    //gq in carat
                    binding.edtGoldQuality,

                    //wastage weight
                    binding.edtAddReducedK,
                    binding.edtAddReducedP,
                    binding.edtAddReducedY,

                    //other reduced cost
                    binding.edtGemDiamondValue,
                    binding.edtReducedGemDiamondValue,
                    binding.edtFee,
                    binding.edtPTclipValue

                )
            )
        }
        binding.btnCalculate3.setOnClickListener {
            binding.btnCalculate3.isEnabled = false

            calculateDecidedPawnPrice()
            binding.edtPriceB
                .setText(generateNumberFromEditText(binding.edtPaymentFromShop))
            binding.edtPriceA.setText(generateNumberFromEditText(binding.edtRepurchasePrice))
            binding.edtPriceB.setText(generateNumberFromEditText(binding.edtPaymentFromShop))
            //f value will changed because b valued changed
            var fywae =
                (generateNumberFromEditText(binding.edtPriceB)
                    .toDouble() / generateNumberFromEditText(binding.edtPriceE).toDouble()) * 128
            val fVoucherKpy = getKPYFromYwae(fywae)
            binding.edtPriceFK.setText(fVoucherKpy[0].toInt().toString())
            binding.edtPriceFP.setText(fVoucherKpy[1].toInt().toString())
            binding.edtPriceFY.setText(fVoucherKpy[2].let { String.format("%.2f", it) })
            makeCalculateButtonEnableWhenEdit(
                listOf(
                    //Gold and gem weight
                    binding.edtGoldAndGemWeightGm,
                    binding.edtGoldAndGemWeightK,
                    binding.edtGoldAndGemWeightP,
                    binding.edtGoldAndGemWeightY,
                    //gem weight
                    binding.edtGemWeightK,
                    binding.edtGemWeightP,
                    binding.edtGemWeightY,

                    //impurity weight
                    binding.edtGeeWeightK,
                    binding.edtGeeWeightP,
                    binding.edtGeeWeightY,

                    // rebuy price
                    binding.edtRepurchasePrice,

                    //gq in carat
                    binding.edtGoldQuality,

                    //wastage weight
                    binding.edtAddReducedK,
                    binding.edtAddReducedP,
                    binding.edtAddReducedY,

                    //other reduced cost
                    binding.edtGemDiamondValue,
                    binding.edtReducedGemDiamondValue,
                    binding.edtFee,
                    binding.edtPTclipValue

                )
            )
        }
        binding.btnCalculatePawnPrice.setOnClickListener {
            calculatePawnPrice(hasOtherReducedCost)
        }
        binding.btnCalculatePriceB.setOnClickListener {
            calculatePriceB(hasOtherReducedCost)
        }
        binding.btnCalculatePriceA.setOnClickListener {
//            binding.btnCalculate2.isEnabled = true
//            binding.btnCalculate3.isEnabled = true
            calculatePriceA(hasOtherReducedCost)
        }
        binding.btnCalculatePriceD.setOnClickListener {
            if (generateNumberFromEditText(binding.edtPriceC).toDouble() != 0.0 || binding.edtPriceC.text.isNullOrEmpty()
            ){
                calculatePriceD(hasOtherReducedCost)
            }
        }


        binding.btnAdd.setOnClickListener {
            showDialogResellStockInfo()
        }
        binding.btnAddGemWeightKPY.setOnClickListener {
            showDialogGemWeight()
        }
        binding.btnPercentage.setOnClickListener {
            if (!binding.edtGemDiamondValue.text.isNullOrEmpty()) {
                showDialogMinusPercentageForGemDiamondValue()
            } else {
                Toast.makeText(requireContext(), "Please fill data first", Toast.LENGTH_LONG).show()
            }
        }
        binding.btnPercentageVoucherPurchasePayment.setOnClickListener {
            showDialogMinusPercentageForPriceB()
        }

        binding.btnContinue.setOnClickListener {

            if (binding.tvNameTag.text.isNullOrEmpty() ||
                binding.edtGoldAndGemWeightK.text.isNullOrEmpty() ||
                binding.edtGoldAndGemWeightP.text.isNullOrEmpty() ||
                binding.edtGoldAndGemWeightY.text.isNullOrEmpty() ||
                binding.edtGoldWeightK.text.isNullOrEmpty() ||
                binding.edtGoldWeightP.text.isNullOrEmpty() ||
                binding.edtGoldWeightY.text.isNullOrEmpty() ||
                binding.edtRepurchasePrice.text.isNullOrEmpty() ||
                binding.edtGoldQuality.text.isNullOrEmpty() ||
                binding.edtPawnPrice.text.isNullOrEmpty() ||
                binding.edtDecidedPawnPrice.text.isNullOrEmpty() ||
                binding.edtPaymentFromShop.text.isNullOrEmpty() ||
                binding.edtPriceB.text.isNullOrEmpty() ||
                (viewModel.selectedImagePath.isNullOrEmpty() && args.stockFromHomeInfo?.image?.id.isNullOrEmpty()) ||

                (!binding.radioBtnOutsideStock.isChecked && binding.radioBtnBrandedStock.isChecked.not())
            ) {
                Toast.makeText(requireContext(), "Please Enter Required Values", Toast.LENGTH_LONG)
                    .show()
            } else {
                var gemQtyMultiPartList = mutableListOf<MultipartBody.Part?>()
                var gemWeightYwaeMultiPartList = mutableListOf<MultipartBody.Part?>()
                var gemWeightGmMultiPartList = mutableListOf<MultipartBody.Part?>()

                val gemQtyList = viewModel.gemWeightCustomList.map { it.gem_qty }
                val gemWeightYwaeList =
                    viewModel.gemWeightCustomList.map { it.gem_weight_ywae_per_unit }
                val gemWeightGmList =
                    viewModel.gemWeightCustomList.map { it.gem_weight_gm_per_unit }

                val imageFile = viewModel.selectedImagePath?.let { File(it) }
                repeat(viewModel.gemWeightCustomList.size) {
                    gemQtyMultiPartList.add(
                        MultipartBody.Part.createFormData(
                            "gem_weight_details[$it][gem_qty]",
                            gemQtyList[it]
                        )
                    )
                    gemWeightYwaeMultiPartList.add(
                        MultipartBody.Part.createFormData(
                            "gem_weight_details[$it][gem_weight_ywae_per_unit]",
                            gemWeightYwaeList[it]
                        )
                    )
                    gemWeightGmMultiPartList.add(
                        MultipartBody.Part.createFormData(
                            "gem_weight_details[$it][gem_weight_gm_per_unit]",
                            gemWeightGmList[it]
                        )
                    )
                }
                if (args.stockFromHomeInfo != null) {
                    viewModel.updateStockFromHome(
                        a_buying_price_update = binding.edtPriceA.text.toString(),
                        b_voucher_buying_value_update =
                        if (binding.edtReducedPriceB.text.isNullOrEmpty()) {
                            binding.edtPriceB.text.toString()
                        } else {
                            binding.edtReducedPriceB.text.toString()
                        },
                        c_voucher_buying_price_update = binding.edtPriceC.text.toString(),
                        calculated_buying_value_update = binding.edtPaymentFromShop.text.toString(),
                        calculated_for_pawn_update = binding.edtPawnPrice.text.toString(),
                        d_gold_weight_ywae_update = getYwaeFromKPY(
                            generateNumberFromEditText(binding.edtPriceDK).toInt(),
                            generateNumberFromEditText(binding.edtPriceDP).toInt(),
                            generateNumberFromEditText(binding.edtPriceDY).toDouble()
                        ).toString(),
                        e_price_from_new_voucher_update = binding.edtPriceE.text.toString(),
                        f_voucher_shown_gold_weight_ywae_update = getYwaeFromKPY(
                            generateNumberFromEditText(binding.edtPriceFK).toInt(),
                            generateNumberFromEditText(binding.edtPriceFP).toInt(),
                            generateNumberFromEditText(binding.edtPriceFY).toDouble()
                        ).toString(),
                        gem_value_update = if (binding.cbOtherCosts.isChecked){
                            if (binding.edtReducedGemDiamondValue.text.isNullOrEmpty()) {
                                binding.edtGemDiamondValue.text.toString()
                            } else {
                                binding.edtReducedGemDiamondValue.text.toString()
                            }
                        }
                        else "0"
                       ,
                        gem_weight_ywae_update = getYwaeFromKPY(
                            generateNumberFromEditText(binding.edtGemWeightK).toInt(),
                            generateNumberFromEditText(binding.edtGemWeightP).toInt(),
                            generateNumberFromEditText(binding.edtGemWeightY).toDouble()
                        ).toString(),
                        gold_weight_ywae_update = getYwaeFromKPY(
                            generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
                            generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
                            generateNumberFromEditText(binding.edtGoldWeightY).toDouble()
                        ).toString(),
                        gold_gem_weight_ywae_update = getYwaeFromKPY(
                            generateNumberFromEditText(binding.edtGoldAndGemWeightK).toInt(),
                            generateNumberFromEditText(binding.edtGoldAndGemWeightP).toInt(),
                            generateNumberFromEditText(binding.edtGoldAndGemWeightY).toDouble()
                        ).toString(),
                        gq_in_carat_update = binding.edtGoldQuality.text.toString(),
                        has_general_expenses_update = if (binding.cbOtherCosts.isChecked) "1" else "0",
                        imageId_update = if (imageFile != null) null else args.stockFromHomeInfo?.image?.id,
                        imageFile_update = imageFile,
                        impurities_weight_ywae_update = getYwaeFromKPY(
                            generateNumberFromEditText(binding.edtGeeWeightK).toInt(),
                            generateNumberFromEditText(binding.edtGeeWeightP).toInt(),
                            generateNumberFromEditText(binding.edtGeeWeightY).toDouble()
                        ).toString(),
                        maintenance_cost_update =if (binding.cbOtherCosts.isChecked)  binding.edtFee.text.toString() else "0",
                        price_for_pawn_update = binding.edtDecidedPawnPrice.text.toString(),
                        pt_and_clip_cost_update =if (binding.cbOtherCosts.isChecked)  binding.edtPTclipValue.text.toString() else "0",
                        qty_update = viewModel.totalQty.toString(),
                        rebuy_price_update = binding.edtRepurchasePrice.text.toString(),
                        size_update = viewModel.size,
                        stock_condition_update = viewModel.horizontalOption,
                        stock_name_update = binding.tvNameTag.text.toString(),
                        type_update = if (binding.radioBtnOutsideStock.isChecked) "0" else "1",
                        wastage_ywae_update = getYwaeFromKPY(
                            generateNumberFromEditText(binding.edtAddReducedK).toInt(),
                            generateNumberFromEditText(binding.edtAddReducedP).toInt(),
                            generateNumberFromEditText(binding.edtAddReducedY).toDouble()
                        ).toString(),
                        rebuy_price_vertical_option_update = viewModel.verticalOption,
                        item = args.stockFromHomeInfo!!,
                        itemList = args.stockFromHomeList!!.toList()
                    )


                } else {
                    viewModel.createStockFromHome(
                        listOf<StockFromHomeDomain>(
                            StockFromHomeDomain(
                                a_buying_price = binding.edtPriceA.text.toString(),
                                b_voucher_buying_value = if (binding.edtReducedPriceB.text.isNullOrEmpty()) {
                                    binding.edtPriceB.text.toString()
                                } else {
                                    binding.edtReducedPriceB.text.toString()
                                },
                                c_voucher_buying_price = binding.edtPriceC.text.toString(),
                                calculated_buying_value = binding.edtPaymentFromShop.text.toString(),
                                calculated_for_pawn = binding.edtPawnPrice.text.toString(),
                                d_gold_weight_ywae = getYwaeFromKPY(
                                    generateNumberFromEditText(binding.edtPriceDK).toInt(),
                                    generateNumberFromEditText(binding.edtPriceDP).toInt(),
                                    generateNumberFromEditText(binding.edtPriceDY).toDouble()
                                ).toString(),
                                e_price_from_new_voucher = binding.edtPriceE.text.toString(),
                                f_voucher_shown_gold_weight_ywae = getYwaeFromKPY(
                                    generateNumberFromEditText(binding.edtPriceFK).toInt(),
                                    generateNumberFromEditText(binding.edtPriceFP).toInt(),
                                    generateNumberFromEditText(binding.edtPriceFY).toDouble()
                                ).toString(),
                                gem_value = if (binding.edtReducedGemDiamondValue.text.isNullOrEmpty()) {
                                    binding.edtGemDiamondValue.text.toString()
                                } else {
                                    binding.edtReducedGemDiamondValue.text.toString()
                                },
                                gem_weight_details = viewModel.gemWeightCustomList,
                                gem_weight_ywae = getYwaeFromKPY(
                                    generateNumberFromEditText(binding.edtGemWeightK).toInt(),
                                    generateNumberFromEditText(binding.edtGemWeightP).toInt(),
                                    generateNumberFromEditText(binding.edtGemWeightY).toDouble()
                                ).toString(),
                                gold_weight_ywae = getYwaeFromKPY(
                                    generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
                                    generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
                                    generateNumberFromEditText(binding.edtGoldWeightY).toDouble()
                                ).toString(),
                                gold_gem_weight_ywae = getYwaeFromKPY(
                                    generateNumberFromEditText(binding.edtGoldAndGemWeightK).toInt(),
                                    generateNumberFromEditText(binding.edtGoldAndGemWeightP).toInt(),
                                    generateNumberFromEditText(binding.edtGoldAndGemWeightY).toDouble()
                                ).toString(),
                                gq_in_carat = binding.edtGoldQuality.text.toString(),
                                has_general_expenses = if (binding.cbOtherCosts.isChecked) "1" else "0",
                                image = Image(
                                    null,
                                    null,
                                    viewModel.selectedImagePath
                                ),


                                impurities_weight_ywae = getYwaeFromKPY(
                                    generateNumberFromEditText(binding.edtGeeWeightK).toInt(),
                                    generateNumberFromEditText(binding.edtGeeWeightP).toInt(),
                                    generateNumberFromEditText(binding.edtGeeWeightY).toDouble()
                                ).toString(),
                                maintenance_cost = binding.edtFee.text.toString(),
                                price_for_pawn = binding.edtDecidedPawnPrice.text.toString(),
                                pt_and_clip_cost = binding.edtPTclipValue.text.toString(),
                                qty = viewModel.totalQty.toString(),
                                rebuy_price = binding.edtRepurchasePrice.text.toString(),
                                size = viewModel.size,
                                stock_condition = viewModel.horizontalOption,
                                stock_name = binding.tvNameTag.text.toString(),
                                type = if (binding.radioBtnOutsideStock.isChecked) "0" else "1",
                                wastage_ywae = getYwaeFromKPY(
                                    generateNumberFromEditText(binding.edtAddReducedK).toInt(),
                                    generateNumberFromEditText(binding.edtAddReducedP).toInt(),
                                    generateNumberFromEditText(binding.edtAddReducedY).toDouble()
                                ).toString(),
                                rebuy_price_vertical_option = viewModel.verticalOption,
                                productId = null
                            )
                        )
                    )

                }
            }
        }

        binding.radioGroupStockCondition.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                binding.radioBtnDamage.id -> {
                    viewModel.horizontalOption = "Damage"
                    viewModel.getRebuyPrice(
                        viewModel.horizontalOption,
                        viewModel.verticalOption,
                        viewModel.size,
                    )

                }
                binding.radioBtnGood.id -> {
                    viewModel.horizontalOption = "Good"
                    viewModel.getRebuyPrice(
                        viewModel.horizontalOption,
                        viewModel.verticalOption,
                        viewModel.size,
                    )

                }
                binding.radioBtnNotToGo.id -> {
                    viewModel.horizontalOption = "NotToGo"
                    viewModel.getRebuyPrice(
                        viewModel.horizontalOption,
                        viewModel.verticalOption,
                        viewModel.size,
                    )

                }
            }
        }
        viewModel.rebuyPriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    binding.edtRepurchasePrice.setText(it.data?.price ?: "0")
//                    binding.edtPriceC.setText(it.data?.price ?: "0")

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()

                    viewModel.goldPrice =
                        (it.data?.find { it.name == "Rebuy Price [100%]" }?.price.let { if (it.isNullOrEmpty()) 0 else it.toInt() }).toInt()
                            .toString()

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    fun bindPassedData(stockFromHomeInfo: StockFromHomeDomain) {
        if (stockFromHomeInfo.image != null) {
            binding.ivBg.loadImageWithGlide(stockFromHomeInfo.image.url)
            binding.ivCamera.isVisible = false
        }
        viewModel.totalQty = stockFromHomeInfo.qty.let { if (it.isNullOrEmpty()) 0 else it.toInt() }
        hasOtherReducedCost = stockFromHomeInfo.has_general_expenses == "1"
        binding.tvNameTag.text =
            stockFromHomeInfo.stock_name
        if (stockFromHomeInfo.type == "1") {
            binding.radioBtnBrandedStock.isChecked = true
        } else {
            binding.radioBtnOutsideStock.isChecked = true
        }
        when (stockFromHomeInfo.stock_condition) {
            "Damage" -> {
                binding.radioBtnDamage.isChecked = true
            }
            "Good" -> {
                binding.radioBtnGood.isChecked = true

            }
            "Not to Go" -> {
                binding.radioBtnNotToGo.isChecked = true

            }
        }
        binding.edtRepurchasePrice.setText(stockFromHomeInfo.rebuy_price)
        stockFromHomeInfo.gem_weight_details?.forEach{
            it.id = count++
        }
        viewModel.addGemDetailList(stockFromHomeInfo.gem_weight_details.orEmpty())
            val gemWeightKpy = getKPYFromYwae(stockFromHomeInfo.gem_weight_ywae.let { if (it.isNullOrEmpty()) 0.0 else it.toDouble() })
            binding.edtGemWeightK.setText(gemWeightKpy[0].toInt().toString())
            binding.edtGemWeightP.setText(gemWeightKpy[1].toInt().toString())
            binding.edtGemWeightY.setText(gemWeightKpy[2].let { String.format("%.2f", it) })


        binding.edtGoldAndGemWeightGm.setText(
            String.format(
                "%.2f",
                getGramFromYwae(stockFromHomeInfo.gold_gem_weight_ywae!!.toDouble())
            )
        )


        val goldAndGemKpy = getKPYFromYwae(stockFromHomeInfo.gold_gem_weight_ywae.toDouble())
        binding.edtGoldAndGemWeightK.setText(goldAndGemKpy[0].toInt().toString())
        binding.edtGoldAndGemWeightP.setText(goldAndGemKpy[1].toInt().toString())
        binding.edtGoldAndGemWeightY.setText(goldAndGemKpy[2].let { String.format("%.2f", it) })

        val goldKpy = getKPYFromYwae(stockFromHomeInfo.gold_weight_ywae!!.toDouble())
        binding.edtGoldWeightK.setText(goldKpy[0].toInt().toString())
        binding.edtGoldWeightP.setText(goldKpy[1].toInt().toString())
        binding.edtGoldWeightY.setText(goldKpy[2].let { String.format("%.2f", it) })

        //gem weight detail
//        var totalWeightYwae = 0.0
//        stockFromHomeInfo.gem_weight_details.orEmpty().forEach {
//            totalWeightYwae += it.gem_weight_ywae_per_unit.toDouble() * it.gem_qty.toDouble()
//        }
//        val totalGemWeightKpy = getKPYFromYwae(totalWeightYwae)
//        binding.edtGemWeightK.setText(totalGemWeightKpy[0].toInt().toString())
//        binding.edtGemWeightP.setText(totalGemWeightKpy[1].toInt().toString())
//        binding.edtGemWeightY.setText(totalGemWeightKpy[2].toString())

        val impurityWeightKpy =
            getKPYFromYwae(stockFromHomeInfo.impurities_weight_ywae!!.toDouble())
        binding.edtGeeWeightK.setText(impurityWeightKpy[0].toInt().toString())
        binding.edtGeeWeightP.setText(impurityWeightKpy[1].toInt().toString())
        binding.edtGeeWeightY.setText(impurityWeightKpy[2].let { String.format("%.2f", it) })

        binding.edtGoldQuality.setText(stockFromHomeInfo.gq_in_carat)
        binding.cbOtherCosts.isChecked = stockFromHomeInfo.has_general_expenses == "1"
        binding.edtGemDiamondValue.setText(stockFromHomeInfo.gem_value)
        val reducedKpy = getKPYFromYwae(stockFromHomeInfo.wastage_ywae!!.toDouble())
        binding.edtAddReducedK.setText(reducedKpy[0].toInt().toString())
        binding.edtAddReducedP.setText(reducedKpy[1].toInt().toString())
        binding.edtAddReducedY.setText(reducedKpy[2].let { String.format("%.2f", it) })

        binding.edtFee.setText(stockFromHomeInfo.maintenance_cost)
        binding.edtPTclipValue.setText(stockFromHomeInfo.pt_and_clip_cost)
        binding.edtPaymentFromShop.setText(stockFromHomeInfo.calculated_buying_value)
        binding.edtDecidedPawnPrice.setText(stockFromHomeInfo.price_for_pawn)
        binding.edtPawnPrice.setText(stockFromHomeInfo.calculated_for_pawn)
        binding.edtPriceA.setText(stockFromHomeInfo.a_buying_price)
        binding.edtPriceB.setText(stockFromHomeInfo.b_voucher_buying_value)
        binding.edtPriceC.setText(stockFromHomeInfo.c_voucher_buying_price)
        binding.edtPriceE.setText(stockFromHomeInfo.e_price_from_new_voucher)

        val fVoucherKpy =
            getKPYFromYwae(stockFromHomeInfo.f_voucher_shown_gold_weight_ywae!!.toDouble())
        binding.edtPriceFK.setText(fVoucherKpy[0].toInt().toString())
        binding.edtPriceFP.setText(fVoucherKpy[1].toInt().toString())
        binding.edtPriceFY.setText(fVoucherKpy[2].let { String.format("%.2f", it) })

    }


    fun calculateGoldWeight() {
        val goldAndGemYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtGoldAndGemWeightK).toInt(),
            generateNumberFromEditText(binding.edtGoldAndGemWeightP).toInt(),
            generateNumberFromEditText(binding.edtGoldAndGemWeightY).toDouble()
        )

        val gemWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtGemWeightK).toInt(),
            generateNumberFromEditText(binding.edtGemWeightP).toInt(),
            generateNumberFromEditText(binding.edtGemWeightY).toDouble(),
        )

        val impurityYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtGeeWeightK).toInt(),
            generateNumberFromEditText(binding.edtGeeWeightP).toInt(),
            generateNumberFromEditText(binding.edtGeeWeightY).toDouble(),
        )
        val goldYwae = goldAndGemYwae - gemWeightYwae - impurityYwae
        val goldKpy = getKPYFromYwae(goldYwae)
        binding.edtGoldWeightK.setText(goldKpy[0].toInt().toString())
        binding.edtGoldWeightP.setText(goldKpy[1].toInt().toString())
        binding.edtGoldWeightY.setText(goldKpy[2].let { String.format("%.2f", it) })
    }

    fun calculateGQinCarat() {
        val rebuyPrice = generateNumberFromEditText(binding.edtRepurchasePrice).toDouble()
        val gqInCarat = (rebuyPrice / viewModel.goldPrice.toInt()) * 24
        binding.edtGoldQuality.setText(String.format("%.2f", gqInCarat))
    }

    fun calculateRebuyPriceFromGQ() {
        val gqInCarat = generateNumberFromEditText(binding.edtGoldQuality).toDouble()
        val rebuyPrice = gqInCarat / 24 * viewModel.goldPrice.toInt()
        binding.edtRepurchasePrice.setText(rebuyPrice.toInt().toString())
//        binding.edtPriceC.setText(rebuyPrice.toInt().toString())
    }

    fun calculateGoldAndGemWeight() {
        val goldAndGemWeightYwae =
            getYwaeFromGram(generateNumberFromEditText(binding.edtGoldAndGemWeightGm).toDouble())
        val goldAndGemKpy = getKPYFromYwae(goldAndGemWeightYwae)
        binding.edtGoldAndGemWeightK.setText(goldAndGemKpy[0].toInt().toString())
        binding.edtGoldAndGemWeightP.setText(goldAndGemKpy[1].toInt().toString())
        binding.edtGoldAndGemWeightY.setText(goldAndGemKpy[2].let { String.format("%.2f", it) })
    }

    fun calculateBuyPriceFromShop(reducedCost: Boolean) {
        //(GoldWeight(Ywae)+Htae Pay ayawt(Ywae)->(Kyat))*RebuyPrice + otherCostFromUI(only one for gem Discount)

        val goldKyat = getKyatsFromKPY(
            generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightY).toDouble(),
        )
        val reducedKyat = getKyatsFromKPY(
            generateNumberFromEditText(binding.edtAddReducedK).toInt(),
            generateNumberFromEditText(binding.edtAddReducedP).toInt(),
            generateNumberFromEditText(binding.edtAddReducedY).toDouble(),
        )
        val diamondGemValue = if (binding.edtReducedGemDiamondValue.text.isNullOrEmpty()) {
            generateNumberFromEditText(binding.edtGemDiamondValue).toDouble()
        } else {
            generateNumberFromEditText(binding.edtReducedGemDiamondValue).toDouble()
        }


        val otherReducedCosts =
            diamondGemValue + generateNumberFromEditText(binding.edtFee).toDouble() + generateNumberFromEditText(
                binding.edtPTclipValue
            ).toDouble()


        val buyPriceFromShop = if (reducedCost) {
            (goldKyat + reducedKyat) * generateNumberFromEditText(binding.edtRepurchasePrice).toDouble() + otherReducedCosts
        } else {
            goldKyat * generateNumberFromEditText(binding.edtRepurchasePrice).toDouble()
        }

        binding.edtPaymentFromShop.setText(buyPriceFromShop.toInt().toString())
    }

    fun calculateDecidedPawnPrice() {
        val decidedPawnPrice =
            generateNumberFromEditText(binding.edtRepurchasePrice).toDouble() - viewModel.pawnDiffValue.toDouble()
        val decidedPawnPriceDecimal = decidedPawnPrice.toInt().toString()
        binding.edtDecidedPawnPrice.setText(decidedPawnPriceDecimal)
    }


    fun calculatePawnPrice(hasotherReducedCost: Boolean) {

        val goldKyat = getKyatsFromKPY(
            generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightY).toDouble(),
        )
        val diamondGemValue = if (binding.edtReducedGemDiamondValue.text.isNullOrEmpty()) {
            generateNumberFromEditText(binding.edtGemDiamondValue).toDouble()
        } else {
            generateNumberFromEditText(binding.edtReducedGemDiamondValue).toDouble()
        }

        val pawnPrice = if (hasotherReducedCost) {
            (goldKyat) * generateNumberFromEditText(binding.edtDecidedPawnPrice).toDouble() + diamondGemValue

        } else {
            (goldKyat) * generateNumberFromEditText(binding.edtDecidedPawnPrice).toDouble()
        }
        binding.edtPawnPrice.setText(pawnPrice.toInt().toString())
    }

    fun calculatePriceB(hasotherReducedCost: Boolean) {

        val goldKyat = getKyatsFromKPY(
            generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightY).toDouble(),
        )
        val reducedKyat = getKyatsFromKPY(
            generateNumberFromEditText(binding.edtAddReducedK).toInt(),
            generateNumberFromEditText(binding.edtAddReducedP).toInt(),
            generateNumberFromEditText(binding.edtAddReducedY).toDouble(),
        )
        val diamondGemValue = if (binding.edtReducedGemDiamondValue.text.isNullOrEmpty()) {
            generateNumberFromEditText(binding.edtGemDiamondValue).toDouble()
        } else {
            generateNumberFromEditText(binding.edtReducedGemDiamondValue).toDouble()
        }

        val otherReducedCosts =
            diamondGemValue + generateNumberFromEditText(binding.edtFee).toDouble() + generateNumberFromEditText(
                binding.edtPTclipValue
            ).toDouble()
        val priceB = if (hasotherReducedCost) {
            (goldKyat + reducedKyat) * generateNumberFromEditText(binding.edtPriceA).toDouble() + otherReducedCosts
        } else {
            (goldKyat) * generateNumberFromEditText(binding.edtPriceA).toDouble()
        }

        val priceA = generateNumberFromEditText(binding.edtPriceA).toInt()
        val decidedPawnPrice =
            priceA - viewModel.pawnDiffValue.toDouble()
        val decidedPawnPriceDecimal = decidedPawnPrice.toInt().toString()
        binding.edtDecidedPawnPrice.setText(decidedPawnPriceDecimal)
        binding.edtPawnPrice.setText("")
        binding.edtPriceB.setText(priceB.toInt().toString())
        //f value will changed because b valued changed
        //f value will changed because b valued changed
        var fywae =
            (generateNumberFromEditText(binding.edtPriceB)
                .toDouble() / generateNumberFromEditText(binding.edtPriceE).toDouble()) * 128
        val fVoucherKpy = getKPYFromYwae(fywae)
        binding.edtPriceFK.setText(fVoucherKpy[0].toInt().toString())
        binding.edtPriceFP.setText(fVoucherKpy[1].toInt().toString())
        binding.edtPriceFY.setText(fVoucherKpy[2].let { String.format("%.2f", it) })
        binding.edtReducedPriceB.setText("")
    }

    fun calculatePriceA(hasotherReducedCost: Boolean) {
        val goldKyat = getKyatsFromKPY(
            generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightY).toDouble(),
        )
        val reducedKyat = getKyatsFromKPY(
            generateNumberFromEditText(binding.edtAddReducedK).toInt(),
            generateNumberFromEditText(binding.edtAddReducedP).toInt(),
            generateNumberFromEditText(binding.edtAddReducedY).toDouble(),
        )
        val diamondGemValue = if (binding.edtReducedGemDiamondValue.text.isNullOrEmpty()) {
            generateNumberFromEditText(binding.edtGemDiamondValue).toDouble()
        } else {
            generateNumberFromEditText(binding.edtReducedGemDiamondValue).toDouble()
        }

        val otherReducedCosts =
            diamondGemValue + generateNumberFromEditText(binding.edtFee).toDouble() + generateNumberFromEditText(
                binding.edtPTclipValue
            ).toDouble()
        val priceB = if (binding.edtReducedPriceB.text.isNullOrEmpty()) {
            generateNumberFromEditText(binding.edtPriceB).toDouble()
        } else {
            generateNumberFromEditText(binding.edtReducedPriceB).toDouble()
        }

        val priceA = if (hasotherReducedCost) {
            (priceB - otherReducedCosts) / (goldKyat + reducedKyat)

        } else {
            (priceB) / goldKyat
        }
        // need to change other values
//        binding.edtRepurchasePrice.setText(priceA.toInt().toString())
//        binding.edtPaymentFromShop.setText(priceB.toInt().toString())
        val decidedPawnPrice =
            priceA - viewModel.pawnDiffValue.toDouble()
        val decidedPawnPriceDecimal = decidedPawnPrice.toInt().toString()
        binding.edtDecidedPawnPrice.setText(decidedPawnPriceDecimal)
        binding.edtPawnPrice.setText("")
        binding.edtPriceA.setText(priceA.toInt().toString())
    }

    fun calculatePriceD(hasotherReducedCost: Boolean) {
        val priceB = if (binding.edtReducedPriceB.text.isNullOrEmpty()) {
            generateNumberFromEditText(binding.edtPriceB).toDouble()
        } else {
            generateNumberFromEditText(binding.edtReducedPriceB).toDouble()
        }
        val priceC = generateNumberFromEditText(binding.edtPriceC).toDouble()
        val diamondGemValue = if (binding.edtReducedGemDiamondValue.text.isNullOrEmpty()) {
            generateNumberFromEditText(binding.edtGemDiamondValue).toDouble()
        } else {
            generateNumberFromEditText(binding.edtReducedGemDiamondValue).toDouble()
        }
        val reducedYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtAddReducedK).toInt(),
            generateNumberFromEditText(binding.edtAddReducedP).toInt(),
            generateNumberFromEditText(binding.edtAddReducedY).toDouble(),
        )
        val otherReducedCosts =
            diamondGemValue + generateNumberFromEditText(binding.edtFee).toDouble() + generateNumberFromEditText(
                binding.edtPTclipValue
            ).toDouble()
        val priceD = if (hasotherReducedCost) {
            ((priceB - otherReducedCosts) / priceC) * 128 - reducedYwae
        } else {
            ((priceB) / priceC) * 128
        }

        if (priceC != 0.0){
            val priceDKPY = getKPYFromYwae(priceD)
            binding.edtPriceDK.setText(priceDKPY[0].toInt().toString())
            binding.edtPriceDP.setText(priceDKPY[1].toInt().toString())
            binding.edtPriceDY.setText(String.format("%.2f", priceDKPY[2]))
        }


    }

    fun calculateWhenFValueChange() {

        binding.edtRepurchasePrice.setText(binding.edtPriceE.text.toString())
        //
        val fKyat = getKyatsFromKPY(
            generateNumberFromEditText(binding.edtPriceFK).toInt(),
            generateNumberFromEditText(binding.edtPriceFP).toInt(),
            generateNumberFromEditText(binding.edtPriceFY).toDouble(),
        )
        val paymentFromShop =
            (generateNumberFromEditText(binding.edtPriceE).toInt() * fKyat).toInt()
        //ထည့်ပေးအရော့= [(calculated ဆိုင်မှ၀ယ်ပေးငွေ- လက်ခ- ကျောက်စိန်ဖိုး- PT, ကလစ်ဖိုး)/ Rebuy price]KPY - Gold wt KPY
        val diamondGemValue = if (binding.edtReducedGemDiamondValue.text.isNullOrEmpty()) {
            generateNumberFromEditText(binding.edtGemDiamondValue).toDouble()
        } else {
            generateNumberFromEditText(binding.edtReducedGemDiamondValue).toDouble()
        }
        val otherReducedCosts =
            diamondGemValue + generateNumberFromEditText(binding.edtFee).toDouble() + generateNumberFromEditText(
                binding.edtPTclipValue
            ).toDouble()
        val goldWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.edtGoldWeightK).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightP).toInt(),
            generateNumberFromEditText(binding.edtGoldWeightY).toDouble(),
        )
        val addReducedYwae =
            (((paymentFromShop - otherReducedCosts) / generateNumberFromEditText(binding.edtPriceE).toInt()) * 128) - goldWeightYwae
        if (addReducedYwae.isNaN() || addReducedYwae.isInfinite()) {
            Toast.makeText(
                requireContext(),
                "E value must not be zero for calculation",
                Toast.LENGTH_LONG
            ).show()
        } else if (addReducedYwae > 0.0 && addReducedYwae.isNaN().not()) {
            binding.cbOtherCosts.isChecked = true

            val addReducedKpy = getKPYFromYwae(addReducedYwae)
            binding.edtAddReducedK.setText(addReducedKpy[0].toInt().toString())
            binding.edtAddReducedP.setText(addReducedKpy[1].toInt().toString())
            binding.edtAddReducedY.setText(addReducedKpy[2].let { String.format("%.2f", it) })

//            binding.edtGeeWeightK.setText("0")
//            binding.edtGeeWeightP.setText("0")
//            binding.edtGeeWeightY.setText("0")
        } else {
            val originalGeeWeight = args.stockFromHomeInfo?.impurities_weight_ywae.let {
                if (it.isNullOrEmpty())0.0 else it.toDouble()
            }
            val impuritiesWeight = getKPYFromYwae((addReducedYwae* (-1))+originalGeeWeight )
            binding.edtGeeWeightK.setText(impuritiesWeight[0].toInt().toString())
            binding.edtGeeWeightP.setText(impuritiesWeight[1].toInt().toString())
            binding.edtGeeWeightY.setText(impuritiesWeight[2].let { String.format("%.2f", it) })

            binding.edtAddReducedK.setText("0")
            binding.edtAddReducedP.setText("0")
            binding.edtAddReducedY.setText("0")
        }
    }

    fun showDialogResellStockInfo() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogBinding = DialogResellStockInfoBinding.inflate(
            inflater,
            ConstraintLayout(builder.context), false
        )
        builder.setView(dialogBinding.root)
        val alertDialog = builder.create()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        alertDialog.setCancelable(false)
        viewModel.getRebuyItem("small")
        val recyclerAdapter = ResellStockRecyclerAdapter()
        dialogBinding.recyclerView.adapter = recyclerAdapter

//        dialogBinding.tvSizeTag.text = requireContext().getString(R.string.small_size)
//
        dialogBinding.chipGp.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds[0] == dialogBinding.chipSmall.id) {
                viewModel.getRebuyItem("small")
                viewModel.size = "small"
            } else {
                viewModel.getRebuyItem("large")
                viewModel.size = "large"
            }
        }

        viewModel.rebuyItemeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    recyclerAdapter.submitList(it.data)
//                    dialogBinding.chipGpRebuyItems.removeAllViews()
//                    for (item in it.data!!) {
//                        val chip = requireContext().createChip(item.name)
//                        chip.id = item.id.toInt()
//                        chip.isCheckable = true
//                        dialogBinding.chipGpRebuyItems.addView(chip)
//                    }
//                    dialogBinding.chipGpRebuyItems.setOnCheckedStateChangeListener { group, checkedIds ->
//                        dialogBinding.chipGp.isEnabled = checkedIds.size == 0
//                        var nameTag = ""
//                        repeat(checkedIds.size) { index ->
//                            val checkedItems =
//                                it.data!!.find { it.id == checkedIds[index].toString() }
//                            nameTag += checkedItems?.name.orEmpty() + " "
//                        }
//                        dialogBinding.tvNameTag.text = nameTag
//                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }



        dialogBinding.btnContinue.setOnClickListener {
            viewModel.rebuyItemList =
                viewModel.rebuyItemeLiveData.value!!.data!!.filter { it.qty > 0 }
            var totalqty = 0
            var name = ""
            viewModel.rebuyItemList.forEach {
                totalqty += it.qty
                name += it.name + ":" + it.qty.toString() + ","
            }
            viewModel.nameTag = name.dropLast(1)
            binding.tvNameTag.text = name.dropLast(1)
            viewModel.totalQty = totalqty


//            viewModel.nameTag =
//                "{"+dialogBinding.tvNameTag.text.toString() + "," + dialogBinding.tvCountTag.text.toString()+"}"
//            binding.tvNameTag.text = viewModel.nameTag
//            viewModel.size = dialogBinding.tvSizeTag.text.toString().lowercase()
            alertDialog.dismiss()
        }
        dialogBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        // alertDialog.window?.setLayout(900,750)
    }

    fun showDialogGemWeight() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogGemWeightBinding =
            DialogGemWeightBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogGemWeightBinding.root)
        val alertDialog = builder.create()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialogGemWeightBinding.rvGemWeight.post(Runnable {
            alertDialog.window?.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            )
        })
        alertDialog.setCancelable(false)

        if (viewModel.gemWeightCustomList.isNotEmpty().not()) {
            val adapter = GemWeightRecyclerAdapter(viewModel) { data ->
                viewModel.removeGemDetail(data)
            }
            dialogGemWeightBinding.rvGemWeight.adapter = adapter
            adapter.submitList(viewModel.gemWeightCustomList)
        } else if (args.stockFromHomeInfo != null) {
            if (!args.stockFromHomeInfo!!.gem_weight_details.isNullOrEmpty()) {
                args.stockFromHomeInfo!!.gem_weight_details!!.forEach {
                    count++
                    it.id = count
                }
                val adapter = GemWeightRecyclerAdapter(viewModel) { data ->
                    viewModel.removeGemDetail(data)
                }
                dialogGemWeightBinding.rvGemWeight.adapter = adapter
                adapter.submitList(args.stockFromHomeInfo!!.gem_weight_details)
            }
        }
        viewModel.gemWeightCustomListLiveData.observe(viewLifecycleOwner) {
//            viewModel.gemWeightCustomList = it.toMutableList()
            val adapter = GemWeightRecyclerAdapter(viewModel) { data ->
                viewModel.removeGemDetail(data)
            }
            dialogGemWeightBinding.rvGemWeight.adapter = adapter
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        dialogGemWeightBinding.btnAdd.setOnClickListener {
            count++
            viewModel.addGemDetail(
                GemWeightDetailDomain(
                    count,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )

        }


        dialogGemWeightBinding.btnContinue.setOnClickListener {
            var totalWeightYwae = 0.0
            viewModel.gemWeightCustomListLiveData.value?.forEach {
                totalWeightYwae += it.gem_weight_ywae_per_unit.let { if (it.isEmpty()) 0.0 else it.toDouble() } * it.gem_qty.let{ if (it.isEmpty()) 0.0 else it.toDouble() }
            }
            val totalWeightKPY = getKPYFromYwae(totalWeightYwae)
            binding.edtGemWeightK.setText(totalWeightKPY[0].toInt().toString())
            binding.edtGemWeightP.setText(totalWeightKPY[1].toInt().toString())
            binding.edtGemWeightY.setText(totalWeightKPY[2].toString())
            calculateGoldWeight()
            binding.btnCalculate2.isEnabled = true
            binding.btnCalculate3.isEnabled = true
            alertDialog.dismiss()
        }
        dialogGemWeightBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        // alertDialog.window?.setLayout(900,750)
    }

    fun showDialogMinusPercentageForGemDiamondValue() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogMinusPercentageBinding =
            DialogMinusPercentageBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogMinusPercentageBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogMinusPercentageBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        dialogMinusPercentageBinding.btnContinue.setOnClickListener {
            val percent = if (dialogMinusPercentageBinding.edtPercent.text.isNullOrEmpty()) {
                0.0
            } else {
                dialogMinusPercentageBinding.edtPercent.text.toString().toDouble()
            }
            val percentValue =
                (generateNumberFromEditText(binding.edtGemDiamondValue).toDouble()) * percent / 100

            val result =
                generateNumberFromEditText(binding.edtGemDiamondValue).toDouble() - percentValue
            binding.edtReducedGemDiamondValue.setText(result.toInt().toString())
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    fun showDialogMinusPercentageForPriceB() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogMinusPercentageBinding =
            DialogMinusPercentageBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogMinusPercentageBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogMinusPercentageBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        dialogMinusPercentageBinding.btnContinue.setOnClickListener {
            val percent = if (dialogMinusPercentageBinding.edtPercent.text.isNullOrEmpty()) {
                0.0
            } else {
                dialogMinusPercentageBinding.edtPercent.text.toString().toDouble()
            }
            val percentValue =
                (generateNumberFromEditText(binding.edtPriceB).toDouble()) * percent / 100
            val result = generateNumberFromEditText(binding.edtPriceB).toDouble() - percentValue
            // need to change
            binding.edtReducedPriceB.setText(result.toInt().toString())
            //f value will changed because b valued changed
            var fywae =
                (generateNumberFromEditText(binding.edtReducedPriceB)
                    .toDouble() / generateNumberFromEditText(binding.edtPriceE).toDouble()) * 128
            val fVoucherKpy = getKPYFromYwae(fywae)
            binding.edtPriceFK.setText(fVoucherKpy[0].toInt().toString())
            binding.edtPriceFP.setText(fVoucherKpy[1].toInt().toString())
            binding.edtPriceFY.setText(fVoucherKpy[2].let { String.format("%.2f", it) })
            alertDialog.dismiss()
        }
        alertDialog.show()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun setXYZselection() {
        viewModel.verticalOption = "X"
        binding.mcvX.setOnClickListener {
            viewModel.verticalOption = "X"
            binding.mcvX.setCardBackgroundColor(requireContext().getColor(R.color.base_pink))
            binding.mcvY.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
            binding.mcvZ.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))

            binding.tvX.setTextColor(requireContext().getColor(R.color.white))
            binding.tvY.setTextColor(requireContext().getColor(R.color.primary))
            binding.tvZ.setTextColor(requireContext().getColor(R.color.primary))
            viewModel.getRebuyPrice(
                viewModel.horizontalOption,
                viewModel.verticalOption,
                viewModel.size
            )

        }
        binding.mcvY.setOnClickListener {
            viewModel.verticalOption = "Y"
            binding.mcvX.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
            binding.mcvY.setCardBackgroundColor(requireContext().getColor(R.color.base_pink))
            binding.mcvZ.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
            binding.tvX.setTextColor(requireContext().getColor(R.color.primary))
            binding.tvY.setTextColor(requireContext().getColor(R.color.white))
            binding.tvZ.setTextColor(requireContext().getColor(R.color.primary))
            viewModel.getRebuyPrice(
                viewModel.horizontalOption,
                viewModel.verticalOption,
                viewModel.size
            )

        }
        binding.mcvZ.setOnClickListener {
            viewModel.verticalOption = "Z"
            binding.mcvX.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
            binding.mcvY.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
            binding.mcvZ.setCardBackgroundColor(requireContext().getColor(R.color.base_pink))
            binding.tvX.setTextColor(requireContext().getColor(R.color.primary))
            binding.tvY.setTextColor(requireContext().getColor(R.color.primary))
            binding.tvZ.setTextColor(requireContext().getColor(R.color.white))
            viewModel.getRebuyPrice(
                viewModel.horizontalOption,
                viewModel.verticalOption,
                viewModel.size
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun disableOtherCosts() {
        binding.textInputLayoutAddReducedK.isEnabled = false
        binding.textInputLayoutAddReducedP.isEnabled = false
        binding.textInputLayoutAddReducedY.isEnabled = false
        binding.textInputLayoutGemDiamondValue.isEnabled = false
        binding.textInputLayoutReducedGemDiamondValue.isEnabled = false
        binding.textInputLayoutFee.isEnabled = false
        binding.textInputLayoutPTclipValue.isEnabled = false

        binding.edtAddReducedK.text?.clear()
        binding.edtAddReducedP.text?.clear()
        binding.edtAddReducedY.text?.clear()
        binding.edtGemDiamondValue.text?.clear()
        binding.edtReducedGemDiamondValue.text?.clear()
        binding.edtFee.text?.clear()
        binding.edtPTclipValue.text?.clear()

        binding.layoutOtherCosts.setBackgroundColor(requireContext().getColor(R.color.base_grey))

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun enableOtherCosts() {
        binding.textInputLayoutAddReducedK.isEnabled = true
        binding.textInputLayoutAddReducedP.isEnabled = true
        binding.textInputLayoutAddReducedY.isEnabled = true
        binding.textInputLayoutGemDiamondValue.isEnabled = true
        binding.textInputLayoutReducedGemDiamondValue.isEnabled = true
        binding.textInputLayoutFee.isEnabled = true
        binding.textInputLayoutPTclipValue.isEnabled = true
        binding.layoutOtherCosts.setBackgroundColor(requireContext().getColor(R.color.white))

    }

    fun resetPricesValue() {
        binding.edtDecidedPawnPrice.setText("")
        binding.edtPawnPrice.setText("")
        binding.edtPriceA.setText("")
        binding.edtPriceB.setText("")
        binding.edtReducedPriceB.setText("")
        binding.edtPriceDK.setText("")
        binding.edtPriceDP.setText("")
        binding.edtPriceDY.setText("")
//        binding.edtPriceE.setText("")
//        binding.edtPriceFK.setText("")
//        binding.edtPriceFP.setText("")
//        binding.edtPriceFY.setText("")
    }

    private fun requestStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
//        } else {
        storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

//        }
    }

    private fun isExternalStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun chooseImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_PICK
        imagePickerLauncher.launch(i)
    }

    private fun makeCalculateButtonEnableWhenEdit(fieldList: List<EditText>) {
        fieldList.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // TODO Auto-generated method stub
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    binding.btnCalculate2.isEnabled = true
                    binding.btnCalculate3.isEnabled = true
                    resetPricesValue()
                }
            })
        }
    }
}
