package com.example.shwemisale.screen.oldStockDetail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.Resource
import com.example.shwemisale.util.generateNumberFromEditText
import com.example.shwemi.util.getAlertDialog
import com.example.shwemisale.util.getRealPathFromUri
import com.example.shwemisale.util.getRoundDownForPrice
import com.example.shwemi.util.loadImageWithGlide
import com.example.shwemi.util.showABuyingPriceChangeAlertDialog
import com.example.shwemi.util.showBBuyingPriceChangeAlertDialog
import com.example.shwemi.util.showCBuyingPriceChangeAlertDialog
import com.example.shwemi.util.showFGoldWeighteChangeAlertDialog
import com.example.shwemi.util.showGoldCaratChangeAlertDialog
import com.example.shwemi.util.showSuccessDialog
import com.example.shwemi.util.showUploadImageDialog
import com.example.shwemisale.R
import com.example.shwemisale.databinding.FragmentOldStockDetailBinding
import com.example.shwemisale.screen.goldFromHome.bucket.BucketShareViewModel
import com.example.shwemisale.screen.goldFromHome.bucket.RemoveImageBottomSheetFragment
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.example.shwemisale.screen.oldStockDetail.gemWeightDetail.DialogGemWeightDetailFragment
import com.example.shwemisale.screen.oldStockDetail.gemWeightDetail.TotalGemWeightListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class OldStockDetailFragment : Fragment(), ChooseStockTypeListener, TotalGemWeightListener {
    private lateinit var binding: FragmentOldStockDetailBinding
    private val viewModel by viewModels<OldStockDetailViewModel>()
    private val sharedViewModel by activityViewModels<BucketShareViewModel>()
    private val args by navArgs<OldStockDetailFragmentArgs>()
    private lateinit var loading: AlertDialog
    private lateinit var chooseSTockTypeDialogFragment: ChooseStockTypeDialogFragment
    private lateinit var dialogGemWeightDetailFragment: DialogGemWeightDetailFragment
    private lateinit var launchChooseImage: ActivityResultLauncher<Intent>
    private lateinit var removeImageBottomSheetFragment: RemoveImageBottomSheetFragment

    //to controle gq changes when rebuypricecurrent is observed, conflict with passed gq data
    private var gqChangeToggle = 0

    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>

    fun disableAllViews(view: View) {
        view.isEnabled = false

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                disableAllViews(view.getChildAt(i))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                requireContext().showUploadImageDialog(
                    //onGalleryClick
                    {
                        chooseImage()
                    },
                    //onCameraClick
                    {
                        dispatchTakePictureIntent()
                    })
            }
        }

        launchChooseImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null && data.data != null) {
                    getRealPathFromUri(requireContext(), data.data!!)?.let { path ->
                        viewModel.setImagePathLiveData(path)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentOldStockDetailBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        uiDeployment()
        bindPassedData()
        chooseStockNameFeature()
        editStockCondition()
        binding.includeAmountList.uiAfterStockName.isVisible =
            args.stockfromhomeinfo.calculated_buying_value!!.toInt() > 0
        binding.includeAmountList.uiAfterRebuyPrice.isVisible =
            args.stockfromhomeinfo.calculated_buying_value!!.toInt() > 0
        if (args.viewType == "viewdetail") {
            disableAllViews(binding.root)
        }
        binding.btnSave.setOnClickListener {
            if (args.stockfromhomeinfo.id != null) {
                viewModel.updateStockFromHome(
                    oldImageUrl = args.stockfromhomeinfo.image.url,
                    oldStockId = args.stockfromhomeinfo.id.toString(),
                    imageId = args.stockfromhomeinfo.image.id,
                    itemType = if (binding.includeAmountList.includeItemType.rBtnOutsideItem.isChecked) "0" else "1",
                    hasGeneralExpense = if (binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked) "1" else "0",
                    productIdList = args.stockfromhomeinfo.productId,
                    isEditable = true,
                    isChecked = false,
                    isPawn = args.backpresstype?.startsWith("Pawn") ?: false,
                )
            } else {
                viewModel.createStockFromHome(
                    imageId = args.stockfromhomeinfo.image.id,
                    itemType = if (binding.includeAmountList.includeItemType.rBtnOutsideItem.isChecked) "0" else "1",
                    hasGeneralExpense = if (binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked) "1" else "0",
                    productIdList = args.stockfromhomeinfo.productId,
                    isEditable = true,
                    isChecked = false,
                    isPawn = args.backpresstype?.startsWith("Pawn") ?: false,
                )
            }
        }
        binding.btnCalculateAll.setOnClickListener {
            viewModel.getCalculateStateLiveData()
            viewModel.getGoldTypePrice()
            viewModel.fValueChanged()
            viewModel.getPawnDiffValue()
            viewModel.calculateDecidedPawnPrice()

            //rebuyprice here is ဝယ်စျေး
            val rebuyPrice =
                generateNumberFromEditText(binding.includeAmountList.includeItemType.edtRebuyPrice).toInt()
            if (rebuyPrice > 0) {
                viewModel.setpriceC(rebuyPrice.toLong())
                val dWeightFromTo = viewModel.calculateWhenCbuyingPriceChange(
                    rebuyPrice,
                    hasGeneralExpense = binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked,
                    viewModel.wastageYwaeLiveData.value!!,
                    generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice).toInt(),
                    generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee).toInt(),
                )
                viewModel.setdWeightInVoucher(dWeightFromTo.second)
            }
            binding.includeAmountList.uiAfterRebuyPrice.isVisible = true
            val wastageYwae = getYwaeFromKPY(
                generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceK).toInt(),
                generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceP).toInt(),
                generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceY).toDouble(),
            )
            if (binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked) {
                viewModel.setWastageYwae(wastageYwae)
                viewModel.setPtClipCost(generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice).toInt())
                viewModel.setMaintainenceCost(generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee).toInt())
            }

            when (viewModel.getSaveButtonState().first) {
                "ဝယ်စျေး" -> {
                    Toast.makeText(requireContext(), "Please fill ဝယ်စျေး", Toast.LENGTH_LONG)
                        .show()
                    binding.btnSave.isVisible = false
                }

                "သတ်မှတ်အပေါင်စျေး" -> {
                    Toast.makeText(
                        requireContext(),
                        "Please fill သတ်မှတ်အပေါင်စျေး",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnSave.isVisible = false
                }

                "ဘောင်ချာဖွင့်ဝယ်စျေး" -> {
                    Toast.makeText(
                        requireContext(),
                        "Please fill ဘောင်ချာဖွင့်ဝယ်စျေး",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnSave.isVisible = false
                }

                "Success" -> {
                    binding.btnSave.isVisible = true
                }
            }
            viewModel.setRebuyPriceCurrentData(generateNumberFromEditText(binding.includeAmountList.includeItemType.edtRebuyPrice).toLong())
            viewModel.calculateGoldQWhenRebuyPriceChange()
            viewModel.setpriceA(generateNumberFromEditText(binding.includeAmountList.includeItemType.edtRebuyPrice).toLong())
            viewModel.calculateWhenAbuyingPriceChange(
                generateNumberFromEditText(binding.includeAmountList.includeItemType.edtRebuyPrice).toInt(),
                hasGeneralExpense = binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked,
                viewModel.wastageYwaeLiveData.value!!,
                generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice).toInt(),
                generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee).toInt(),
                true
            )

        }

        binding.btnEditImage.setOnClickListener {
            if (isExternalStoragePermissionGranted().not()) {
                requestStoragePermission()
            } else {
                requireContext().showUploadImageDialog(
                    //onGalleryClick
                    {
                        chooseImage()
                    },
                    //onCameraClick
                    {
                        dispatchTakePictureIntent()
                    })
            }

        }


        viewModel.updateStockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Successfully updated") {
                        findNavController().popBackStack()
                    }


                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.createStockFromHomeInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    val selectedOldStock = args.stockfromhomeinfo
                    sharedViewModel.fillData(
                        id = selectedOldStock.id,
                        a_buying_price = viewModel.priceALiveData.value.toString(),
                        b_voucher_buying_value = viewModel.priceBLiveData.value.toString(),
                        c_voucher_buying_price = viewModel.priceCLiveData.value.toString(),
                        calculated_buying_value = viewModel.rebuyPriceFromShopLiveData.value.toString(),
                        calculated_for_pawn = viewModel.decidedPawnPriceLiveData.value.toString(),
                        d_gold_weight_ywae = viewModel.dWeightInVoucherYwaeLiveData.value.toString(),
                        e_price_from_new_voucher = viewModel.priceELiveData.value.toString(),
                        f_voucher_shown_gold_weight_ywae = viewModel.fWeightYwaeLiveData.value.toString(),
                        gem_value = viewModel.gemValueLiveData.value.toString(),
                        gem_weight_details_session_key = viewModel.getGemWeightDetailSessionKey(),
                        gold_and_gem_weight_gm = viewModel.goldAndGemWeightGmLiveData.value.toString(),
                        gem_weight_ywae = viewModel.gemWeightYwaeLiveData.value.toString(),
                        gold_gem_weight_ywae = viewModel.goldAndGemWeightYwaeLiveData.value.toString(),
                        gold_weight_ywae = viewModel.goldWeightYwaeLiveData.value.toString(),
                        gq_in_carat = viewModel.goldCaratLiveData.value.toString(),
                        has_general_expenses = if (binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked) "1" else "0",
                        image = args.stockfromhomeinfo.image.copy(url = viewModel.imagePathLiveData.value),
                        impurities_weight_ywae = viewModel.impurityWeightYwaeLiveData.value.toString(),
                        maintenance_cost = viewModel.maintainenceCostLiveData.value.toString(),
                        price_for_pawn = viewModel.calculatedPawnPiceLiveData.value.toString(),
                        pt_and_clip_cost = viewModel.ptClipCostLiveData.value.toString(),
                        qty = viewModel.totalQtyLiveData.value.toString(),
                        rebuy_price = viewModel.rebuyPriceCurrentLiveData.value!!.toString(),
                        size = viewModel.sizeLiveData.value.toString(),
                        stock_condition = viewModel.horizontalOptionLiveData.value.toString(),
                        stock_name = viewModel.nameTagLiveData.value.toString(),
                        type = if (binding.includeAmountList.includeItemType.rBtnOutsideItem.isChecked) "0" else "1",
                        wastage_ywae = viewModel.wastageYwaeLiveData.value.toString(),
                        rebuy_price_vertical_option = viewModel.verticalOptionLiveData.value.toString(),
                        localId = selectedOldStock.localId,
                        derived_gold_type_id = selectedOldStock.derived_gold_type_id,
                        dataFilled = true
                    )
                    requireContext().showSuccessDialog("Successfully created") {
                        findNavController().popBackStack()
                    }


                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.calculateStaeLiveData.observe(viewLifecycleOwner) {
            if (it == true) {
//                viewModel.calculateDecidedPawnPrice()
                val wastageYwae = getYwaeFromKPY(
                    generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceK).toInt(),
                    generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceP).toInt(),
                    generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceY).toDouble(),
                )
                viewModel.setRebuyPriceFromShop(
                    wastageYwae = wastageYwae,
                    maintenanceCost = generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee).toInt(),
                    hasGeneralExpense = binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked,
                    ptClipCost = generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice).toInt(),
                )
                viewModel.setGoldWeightYwae()
            }
        }

        viewModel.imagePathLiveData.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.iv.setImageResource(R.drawable.dotted_line_upload_image)
            } else {
                binding.iv.loadImageWithGlide(it)
            }
        }
        viewModel.nameTagLiveData.observe(viewLifecycleOwner) {
            binding.tvOldstockName.text = it
            binding.includeAmountList.uiAfterStockName.isVisible = it.isNullOrEmpty().not()
        }
        viewModel.gemWeightYwaeLiveData.observe(viewLifecycleOwner) {
            binding.includeAmountList.includeGemWeight.btnAddFromServer.isVisible = it == 0.0
            val kpy = getKPYFromYwae(it)
            binding.includeAmountList.includeGemWeight.tvGemWeightK.text =
                getString(R.string.kyat_value, kpy[0].toInt().toString())
            binding.includeAmountList.includeGemWeight.tvGemWeightP.text =
                getString(R.string.pae_value, kpy[1].toInt().toString())
            binding.includeAmountList.includeGemWeight.tvGemWeightY.text =
                getString(R.string.ywae_value, kpy[2].toString())

            binding.includeAmountList.includeGemWeight.tvServerGemWeightK.text =
                getString(R.string.kyat_value, kpy[0].toInt().toString())
            binding.includeAmountList.includeGemWeight.tvServerGemWeightP.text =
                getString(R.string.pae_value, kpy[1].toInt().toString())
            binding.includeAmountList.includeGemWeight.tvServerGemWeightY.text =
                getString(R.string.ywae_value, kpy[2].toString())

            binding.includeInvoiceDetails.tvGemWeightKpy.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )

        }
        viewModel.impurityWeightYwaeLiveData.observe(viewLifecycleOwner) {
            val kpy = getKPYFromYwae(it)
            binding.includeAmountList.includeImpurityWeight.tvImpurityWeightK.text =
                getString(R.string.kyat_value, kpy[0].toInt().toString())

            binding.includeAmountList.includeImpurityWeight.tvImpurityWeightP.text =
                getString(R.string.pae_value, kpy[1].toInt().toString())

            binding.includeAmountList.includeImpurityWeight.tvImpurityWeightY.text =
                getString(R.string.ywae_value, kpy[2].toString())
            binding.includeInvoiceDetails.tvGeeWeightKpy.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )

        }
        viewModel.rebuyPriceCurrentLiveData.observe(viewLifecycleOwner) {

            binding.includeAmountList.includeItemType.edtRebuyPrice.setText(it.toString())
            binding.includeInvoiceDetails.tvRebuyPrice.text = it.toString()
        }
        viewModel.rebuyPriceLiveFromServerData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    val rebuyPriceFromServer = if (args.stockfromhomeinfo.derived_gold_type_id == viewModel.whiteGoldId){
                       ( (it.data?:0L).toDouble() * 16.6).toInt()
                    }else (it.data?:0L).toInt()
                    binding.includeAmountList.includeItemType.edtRebuyPrice.setText(
                        getRoundDownForPrice(rebuyPriceFromServer).toString()
                    )

                    //calculate button

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.rebuyPriceFromShopLiveData.observe(viewLifecycleOwner) {
//            binding.includeInvoiceDetails.tvRebuyPriceFromShop.text = it.toString()
        }
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.whiteGoldId = it.data?.find { it.name == "WG" }?.id.orEmpty()
                    viewModel.setGoldPrice((it.data?.find { it.name == "Rebuy Price [100%]" }?.price.let { if (it.isNullOrEmpty()) 0 else it.toInt() }).toInt())
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.sizeLiveData.observe(viewLifecycleOwner) {
            if (binding.includeAmountList.includeItemType.rBtnInsideItem.isChecked) viewModel.getRebuyPrice()
        }
        viewModel.horizontalOptionLiveData.observe(viewLifecycleOwner) {
            if (binding.includeAmountList.includeItemType.rBtnInsideItem.isChecked) viewModel.getRebuyPrice()
            when(it){
                "Good"->binding.includeAmountList.includeItemType.rBtnGood.isChecked = true
                "Damage"->binding.includeAmountList.includeItemType.rBtnDamage.isChecked = true
                "NotToGo"->binding.includeAmountList.includeItemType.rBtnNotToGo.isChecked = true
            }

        }
        viewModel.verticalOptionLiveData.observe(viewLifecycleOwner) {
            val itemTypeLayout = binding.includeAmountList.includeItemType
            when (it) {
                "X" -> {
                    itemTypeLayout.mcvX.setCardBackgroundColor(requireContext().getColor(R.color.base_pink))
                    itemTypeLayout.mcvY.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
                    itemTypeLayout.mcvZ.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))

                    itemTypeLayout.tvX.setTextColor(requireContext().getColor(R.color.white))
                    itemTypeLayout.tvY.setTextColor(requireContext().getColor(R.color.primary))
                    itemTypeLayout.tvZ.setTextColor(requireContext().getColor(R.color.primary))
                }

                "Y" -> {
                    itemTypeLayout.mcvX.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
                    itemTypeLayout.mcvY.setCardBackgroundColor(requireContext().getColor(R.color.base_pink))
                    itemTypeLayout.mcvZ.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
                    itemTypeLayout.tvX.setTextColor(requireContext().getColor(R.color.primary))
                    itemTypeLayout.tvY.setTextColor(requireContext().getColor(R.color.white))
                    itemTypeLayout.tvZ.setTextColor(requireContext().getColor(R.color.primary))
                }

                "Z" -> {
                    itemTypeLayout.mcvX.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
                    itemTypeLayout.mcvY.setCardBackgroundColor(requireContext().getColor(R.color.base_low_pink))
                    itemTypeLayout.mcvZ.setCardBackgroundColor(requireContext().getColor(R.color.base_pink))
                    itemTypeLayout.tvX.setTextColor(requireContext().getColor(R.color.primary))
                    itemTypeLayout.tvY.setTextColor(requireContext().getColor(R.color.primary))
                    itemTypeLayout.tvZ.setTextColor(requireContext().getColor(R.color.white))
                }
            }
            if (binding.includeAmountList.includeItemType.rBtnInsideItem.isChecked) viewModel.getRebuyPrice()

        }
        viewModel.goldCaratLiveData.observe(viewLifecycleOwner) {
            binding.includeAmountList.includeGoldQ.tvGoldQ.text = it.toString()
            binding.includeInvoiceDetails.tvGoldQ.text = it.toString()
        }
        viewModel.goldAndGemWeightGmLiveData.observe(viewLifecycleOwner) {
            binding.includeAmountList.includeGoldAndGemWeightGm.tvGoldAndGemWeightGm.text =
                getString(
                    R.string.gram_value,
                    it.toString()
                )
        }
        viewModel.goldAndGemWeightYwaeLiveData.observe(viewLifecycleOwner) {
            val kpy = getKPYFromYwae(it)
            binding.includeAmountList.includeGoldAndGemWeightKpy.tvGoldAndGemWeightK.text =
                getString(R.string.kyat_value, kpy[0].toInt().toString())


            binding.includeAmountList.includeGoldAndGemWeightKpy.tvGoldAndGemWeightP.text =
                getString(R.string.pae_value, kpy[1].toInt().toString())


            binding.includeAmountList.includeGoldAndGemWeightKpy.tvGoldAndGemWeightY.text =
                getString(R.string.ywae_value, kpy[2].toString())

            binding.includeInvoiceDetails.tvGoldAndGemWeightKpy.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )
        }

        viewModel.priceALiveData.observe(viewLifecycleOwner) {
            binding.includeAmountList.includePriceA.tvPurchasePrice.text =
                getString(R.string.mmk_value, it.toString())
            binding.includeInvoiceDetails.tvPriceA.text = it.toString()
        }
        viewModel.priceBLiveData.observe(viewLifecycleOwner) {
            binding.includeAmountList.includePriceB.tvVoucherOpenPay.text =
                getString(R.string.mmk_value, getRoundDownForPrice(it.toInt()).toString())
            binding.includeInvoiceDetails.tvPriceB.text = it.toString()

        }
        viewModel.priceCLiveData.observe(viewLifecycleOwner) {
            binding.includeAmountList.includePriceC.tvVoucherOpenPrice.text =
                getString(R.string.mmk_value, it.toString())
            binding.includeVoucherDetails.tvPriceC.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.priceELiveData.observe(viewLifecycleOwner) {
            binding.includeVoucherDetails.tvPriceEValue.text =
                getString(R.string.mmk_value, it.toString())
        }
        viewModel.dWeightInVoucherYwaeLiveData.observe(viewLifecycleOwner) {
            if (it != 0.0) {
                val kpy = getKPYFromYwae(it)
                binding.includeVoucherDetails.tvDGoldWeightKpyValue.text = getString(
                    R.string.kpy_value,
                    kpy[0].toInt().toString(),
                    kpy[1].toInt().toString(),
                    kpy[2].toString()
                )
            }


        }
        viewModel.fWeightYwaeLiveData.observe(viewLifecycleOwner) {
            val kpy = getKPYFromYwae(it)
            binding.includeAmountList.includeFGoldWeight.tvPurchaseGoldWeightK.text =
                getString(R.string.kyat_value, kpy[0].toInt().toString())


            binding.includeAmountList.includeFGoldWeight.tvPurchaseGoldWeightP.text =
                getString(R.string.pae_value, kpy[1].toInt().toString())


            binding.includeAmountList.includeFGoldWeight.tvPurchaseGoldWeightY.text =
                getString(R.string.ywae_value, kpy[2].toString())
            binding.includeVoucherDetails.tvFWeightKpy.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )

        }
        viewModel.pawnDiffValueLiveData.observe(viewLifecycleOwner) {

        }
        viewModel.decidedPawnPriceLiveData.observe(viewLifecycleOwner) {
            binding.includeAmountList.includeMortgagePrice.tvMortgagePrice.text =
                getString(R.string.mmk_value, it.toString())
            binding.includeInvoiceDetails.tvDecidedPawnPrice.text = it.toString()
            viewModel.calculatePawnPrice(binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked)
        }
        viewModel.calculatedPawnPiceLiveData.observe(viewLifecycleOwner) {
            binding.includeInvoiceDetails.tvPawnMoney.text = it.toString()
        }

        viewModel.goldWeightYwaeLiveData.observe(viewLifecycleOwner) {
            val kpy = getKPYFromYwae(it)
            binding.includeInvoiceDetails.tvGoldWeightKpy.text = getString(
                R.string.kpy_value,
                kpy[0].toInt().toString(),
                kpy[1].toInt().toString(),
                kpy[2].toString()
            )
        }

    }

    fun chooseStockNameFeature() {
        binding.btnAddStockType.setOnClickListener {
            chooseSTockTypeDialogFragment = ChooseStockTypeDialogFragment()
            chooseSTockTypeDialogFragment.setonChooseStockTypeListener(this)
            chooseSTockTypeDialogFragment.show(
                childFragmentManager,
                "ChooseStockTypeDialog"
            )
        }
    }

    fun editGemWeightDetailCustom() {
        dialogGemWeightDetailFragment = DialogGemWeightDetailFragment()
        dialogGemWeightDetailFragment.setOnTotalGemWeightListener(this)
        dialogGemWeightDetailFragment.show(childFragmentManager, "DialogGemWeightDetail")
    }

    fun editGemWeightDetailManually() {
        viewModel.resetPrices()
        val gemWeightLayout = binding.includeAmountList.includeGemWeight
        val gemWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(gemWeightLayout.edtGemWeightK).toInt(),
            generateNumberFromEditText(gemWeightLayout.edtGemWeightP).toInt(),
            generateNumberFromEditText(gemWeightLayout.edtGemWeightY).toDouble()
        )
        viewModel.setgemWEightYwae(gemWeightYwae)
    }

    fun editImpurityWeightKpy() {
        viewModel.resetPrices()
        val impurityWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.includeAmountList.includeImpurityWeight.edtImpurityWeightK).toDouble()
                .toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeImpurityWeight.edtImpurityWeightP).toDouble()
                .toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeImpurityWeight.edtImpurityWeightY).toDouble(),
        )

        viewModel.setimpurityWeightYwae(impurityWeightYwae)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun editStockCondition() {
        val itemTypeLayout = binding.includeAmountList.includeItemType

        setXYZselection()
        itemTypeLayout.radioGpChooseItemType.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == itemTypeLayout.rBtnOutsideItem.id) {
                binding.includeAmountList.includeItemType.edtRebuyPrice.setText("0")
            } else {
                viewModel.getRebuyPrice()
            }
        }
        itemTypeLayout.radioGpChooseCondition.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                itemTypeLayout.rBtnDamage.id -> {
                    viewModel.setHorizontalOption("Damage")

                }

                itemTypeLayout.rBtnGood.id -> {
                    viewModel.setHorizontalOption("Good")

                }

                itemTypeLayout.rBtnNotToGo.id -> {
                    viewModel.setHorizontalOption("NotToGo")

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setXYZselection() {
        val itemTypeLayout = binding.includeAmountList.includeItemType
        itemTypeLayout.mcvX.setOnClickListener {
            viewModel.setVerticalOption("X")
        }
        itemTypeLayout.mcvY.setOnClickListener {
            viewModel.setVerticalOption("Y")
        }
        itemTypeLayout.mcvZ.setOnClickListener {
            viewModel.setVerticalOption("Z")
        }
    }

    fun editGoldCarat(onContinue: () -> Unit) {
        val fromToRebuyPrice =
            viewModel.calculateRebuyPriceFromGoldCarat(generateNumberFromEditText(binding.includeAmountList.includeGoldQ.edtGoldQ).toDouble())
        requireContext().showGoldCaratChangeAlertDialog(
            fromToRebuyPrice.first.toString(),
            fromToRebuyPrice.second.toString()
        ) {
            viewModel.setRebuyPriceCurrentData(fromToRebuyPrice.second.toLong())
            viewModel.setgoldCarat(generateNumberFromEditText(binding.includeAmountList.includeGoldQ.edtGoldQ).toDouble())
            viewModel.calculateDecidedPawnPrice()
            onContinue()
        }
    }

    fun editDecidedPawnPrice() {
        viewModel.setdecidedPawnPrice(generateNumberFromEditText(binding.includeAmountList.includeMortgagePrice.edtMortgagePrice).toLong())
    }

    fun editAbuyingPrice(onContinue: () -> Unit) {
        val priceA =
            generateNumberFromEditText(binding.includeAmountList.includePriceA.edtABuyingPrice).toInt()
        val wastageYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceK).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceP).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceY).toDouble(),
        )
        val fromToValues = viewModel.calculateWhenAbuyingPriceChange(
            priceA,
            binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked,
            wastageYwae,
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee).toInt(),
            false
        )
        requireContext().showABuyingPriceChangeAlertDialog(
            fromToValues.first.first.toString(), fromToValues.first.second.toString(),
            fromToValues.second.first.toString(), fromToValues.second.second.toString(),
        ) {
            viewModel.setdecidedPawnPrice(fromToValues.first.second.toLong())
            viewModel.setpriceA(getRoundDownForPrice(priceA).toLong())
            viewModel.setpriceC(getRoundDownForPrice(priceA).toLong())
            viewModel.setpriceB(fromToValues.second.second.toLong())
            val dWeightFromTo = viewModel.calculateWhenCbuyingPriceChange(
                priceA,
                hasGeneralExpense = binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked,
                viewModel.wastageYwaeLiveData.value!!,
                generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice).toInt(),
                generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee).toInt(),
            )
            viewModel.setdWeightInVoucher(dWeightFromTo.second)
            viewModel.fValueChanged()
            onContinue()
        }
    }

    fun editBbuyingPrice(onContinue: () -> Unit) {
        val priceB = getPriceBFromDiscountCalculation()
        val wastageYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceK).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceP).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceY).toDouble(),
        )
        val fromToValues = viewModel.calculateWhenBbuyingPriceChange(
            priceB,
            binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked,
            wastageYwae,
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee).toInt(),
        )
        requireContext().showBBuyingPriceChangeAlertDialog(
            fromToValues.first.first.toString(), fromToValues.first.second.toString(),
            fromToValues.second.first.toString(), fromToValues.second.second.toString(),
        ) {
            viewModel.setdecidedPawnPrice(fromToValues.first.second.toLong())
            viewModel.setpriceA(getRoundDownForPrice(fromToValues.second.second).toLong())
            viewModel.setpriceC(getRoundDownForPrice(fromToValues.second.second).toLong())
            viewModel.setpriceB(getRoundDownForPrice(priceB).toLong())
            val dWeightFromTo = viewModel.calculateWhenCbuyingPriceChange(
                fromToValues.second.second,
                hasGeneralExpense = binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked,
                viewModel.wastageYwaeLiveData.value!!,
                generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice).toInt(),
                generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee).toInt(),
            )
            viewModel.setdWeightInVoucher(dWeightFromTo.second)
            viewModel.fValueChanged()

            if (binding.includeAmountList.includePriceB.check.isChecked) {
                binding.includeAmountList.includePriceB.tvLblDis.text = getString(
                    R.string.discount_percentage,
                    binding.includeAmountList.includePriceB.edtPercentageDisPrice.text.toString()
                )
            }
            onContinue()
        }
    }

    fun getPriceBFromDiscountCalculation(): Int {
        val layoutPriceB = binding.includeAmountList.includePriceB

        layoutPriceB.btnCalculate.setOnClickListener {
            val priceB =
                generateNumberFromEditText(layoutPriceB.edtVoucherOpenPay).toInt()
            val percentage =
                generateNumberFromEditText(layoutPriceB.edtPercentageDisPrice).toInt()
            val result = priceB - ((priceB / 100) * percentage)

            layoutPriceB.edtCalculatePercentageDisPrice.setText(result.toString())
        }
        val valueToSave = if (layoutPriceB.check.isChecked) {
            generateNumberFromEditText(layoutPriceB.edtCalculatePercentageDisPrice).toLong()
        } else {
            generateNumberFromEditText(layoutPriceB.edtVoucherOpenPay).toLong()
        }
        return valueToSave.toInt()
    }

    fun editCbuyingPrice(onContinue: () -> Unit) {
        val priceC =
            getRoundDownForPrice(generateNumberFromEditText(binding.includeAmountList.includePriceC.edtVoucherOpenPrice).toInt())
        val wastageYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceK).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceP).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceY).toDouble(),
        )
        val fromToValues = viewModel.calculateWhenCbuyingPriceChange(
            priceC,
            binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked,
            wastageYwae,
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee).toInt(),
        )
        requireContext().showCBuyingPriceChangeAlertDialog(
            fromToValues.first, fromToValues.second, changedValue = priceC.toString()
        ) {
            viewModel.setdWeightInVoucher(fromToValues.second)
            viewModel.setpriceC(priceC.toLong())
            onContinue()
        }
    }

    fun editFGoldWeight(onContinue: () -> Unit) {
        val fweightYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.includeAmountList.includeFGoldWeight.edtPurchaseGoldWeightK).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeFGoldWeight.edtPurchaseGoldWeightP).toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeFGoldWeight.edtPurchaseGoldWeightY).toDouble(),
        )
        val fromToValue = viewModel.calculateWhenFWeightYwaeChanged(fweightYwae)
        requireContext().showFGoldWeighteChangeAlertDialog(fromYwae = fromToValue.second.first, toYwae = fromToValue.second.second, modifiedValueType = fromToValue.first){
            if (fromToValue.first == "wastage"){
                viewModel.setWastageYwae(fromToValue.second.second)
                viewModel.setfWeightYwae(fweightYwae)
            }else{
                viewModel.setfWeightYwae(fweightYwae)
                viewModel.setWastageYwae(0.0)
                viewModel.setimpurityWeightYwae(fromToValue.second.second)
            }
            onContinue()
        }
    }


    fun editGeneralExpense() {
        val generalExpenseLayout = binding.includeAmountList.includeGeneralExpense
        val generalExpenseExpandedLayout =
            binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense

        generalExpenseExpandedLayout.btnEditGemAndDiaPrice.setOnClickListener {
            generalExpenseExpandedLayout.gemValueEditLayout.isVisible = true
            generalExpenseExpandedLayout.btnEditGemAndDiaPrice.isVisible = false
            generalExpenseExpandedLayout.btnCancelGemValue.isVisible = true
        }
        generalExpenseExpandedLayout.btnCancelGemValue.setOnClickListener {
            generalExpenseExpandedLayout.gemValueEditLayout.isVisible = false
            generalExpenseExpandedLayout.btnEditGemAndDiaPrice.isVisible = true
            generalExpenseExpandedLayout.btnCancelGemValue.isVisible = false
        }

        generalExpenseExpandedLayout.checkPercentage.setOnCheckedChangeListener { buttonView, isChecked ->
            generalExpenseExpandedLayout.edtPercentageDisPrice.isEnabled = isChecked
            binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.tvLabelReducedPercent.isVisible =
                isChecked

        }

        generalExpenseExpandedLayout.btnCalculate.setOnClickListener {
            val gemValue =
                generateNumberFromEditText(generalExpenseExpandedLayout.edtGemValue).toInt()
            val percentage =
                generateNumberFromEditText(generalExpenseExpandedLayout.edtPercentageDisPrice).toInt()
            val result = gemValue - ((gemValue / 100) * percentage)

            generalExpenseExpandedLayout.edtCalculatePercentageDisPrice.setText(result.toString())
        }

        generalExpenseExpandedLayout.btnSaveGemValue.setOnClickListener {
            //save changed value
            val valueToSave = if (generalExpenseExpandedLayout.checkPercentage.isChecked) {
                generateNumberFromEditText(generalExpenseExpandedLayout.edtCalculatePercentageDisPrice).toLong()
            } else {
                generateNumberFromEditText(generalExpenseExpandedLayout.edtGemValue).toLong()
            }
            viewModel.setgemValue(valueToSave.toInt())
            generalExpenseExpandedLayout.gemValueEditLayout.isVisible = false
            generalExpenseExpandedLayout.btnCancelGemValue.isVisible = false
            generalExpenseExpandedLayout.btnEditGemAndDiaPrice.isVisible = true
        }

//        generalExpenseExpandedLayout.edtFee.addTextChangedListener {
//            if (it.isNullOrEmpty().not()){
//                viewModel.setMaintainenceCost(it.toString().toInt())
//            }
//        }
//        generalExpenseExpandedLayout.edtPTClipPrice.addTextChangedListener {
//            if (it.isNullOrEmpty().not()){
//                viewModel.setPtClipCost(it.toString().toInt())
//            }
//        }
    }

    fun editGoldAndGemWeightGm() {
        viewModel.resetPrices()
        viewModel.setgoldAndgemWeightGm(
            binding.includeAmountList.includeGoldAndGemWeightGm.edtGoldAndGemWeightGm.text.toString()
                .toDouble()
        )
    }

    fun editGoldAndGemWeightKpy() {
        viewModel.resetPrices()
        val goldAndGemWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndWeightK).toDouble()
                .toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightP).toDouble()
                .toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightY).toDouble(),
        )

        viewModel.setgoldAndGemWeightYwae(goldAndGemWeightYwae)

    }


    fun bindPassedData() {
        viewModel.setImagePathLiveData(args.stockfromhomeinfo.image.url.orEmpty())
        viewModel.setNameTag(args.stockfromhomeinfo.stock_name.orEmpty())
        viewModel.setTotalQty(args.stockfromhomeinfo.qty.toString())
        viewModel.setSize(args.stockfromhomeinfo.size.orEmpty())

        if (args.stockfromhomeinfo.gold_and_gem_weight_gm != "0.0") {
            viewModel.setgoldAndgemWeightGm(args.stockfromhomeinfo.gold_and_gem_weight_gm.toDouble())
        }
        if (args.stockfromhomeinfo.gold_gem_weight_ywae != "0.0") {
            viewModel.setgoldAndGemWeightYwae(
                (args.stockfromhomeinfo.gold_gem_weight_ywae ?: "0.0").toDouble()
            )
        }

        viewModel.setGoldWeightYwaeOnlyValue(
            (args.stockfromhomeinfo.gold_weight_ywae ?: "0.0").toDouble()
        )
        viewModel.setgemWEightYwae((args.stockfromhomeinfo.gem_weight_ywae ?: "0.0").toDouble())
        viewModel.saveGemWeightDetailSessionKey(args.stockfromhomeinfo.gem_weight_details_session_key)
        viewModel.setimpurityWeightYwae(
            (args.stockfromhomeinfo.impurities_weight_ywae ?: "0.0").toDouble()
        )
        when (args.stockfromhomeinfo.type) {
            "0" -> binding.includeAmountList.includeItemType.rBtnOutsideItem.isChecked = true
            "1" -> binding.includeAmountList.includeItemType.rBtnInsideItem.isChecked = true
            else -> binding.includeAmountList.includeItemType.rBtnOutsideItem.isChecked = true
        }
        when (args.stockfromhomeinfo.stock_condition) {
            "Damage" -> viewModel.setHorizontalOption("Damage")
            "Good" -> viewModel.setHorizontalOption("Good")
            "NotToGo" -> viewModel.setHorizontalOption("NotToGo")
            else -> viewModel.setHorizontalOption("Damage")
        }
        when (args.stockfromhomeinfo.rebuy_price_vertical_option) {
            "X" -> viewModel.setVerticalOption("X")
            "Y" -> viewModel.setVerticalOption("Y")
            "Z" -> viewModel.setVerticalOption("Z")
            else -> viewModel.setVerticalOption("X")
        }
        viewModel.setRebuyPriceCurrentData(args.stockfromhomeinfo.rebuy_price.toLong())
        viewModel.setRebuyPriceFromShopOnlyValue(
            (args.stockfromhomeinfo.calculated_buying_value ?: "0").toLong()
        )
        if (!args.stockfromhomeinfo.gq_in_carat.isNullOrEmpty() || args.stockfromhomeinfo.gq_in_carat != "0.0") {
            viewModel.setgoldCarat(args.stockfromhomeinfo.gq_in_carat!!.toDouble())
        }

        when (args.stockfromhomeinfo.has_general_expenses) {
            "0" -> binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked =
                false

            "1" -> binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked =
                true

            else -> binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked =
                false
        }
        viewModel.setWastageYwae((args.stockfromhomeinfo.wastage_ywae ?: "0.0").toDouble())
        viewModel.setgemValue((args.stockfromhomeinfo.gem_value ?: "0").toInt())
        viewModel.setMaintainenceCost((args.stockfromhomeinfo.maintenance_cost ?: "0").toInt())
        viewModel.setPtClipCost((args.stockfromhomeinfo.pt_and_clip_cost ?: "0").toInt())

        viewModel.setpriceA(args.stockfromhomeinfo.a_buying_price?.toLong() ?: 0L)
        viewModel.setpriceB(args.stockfromhomeinfo.b_voucher_buying_value?.toLong() ?: 0L)
        viewModel.setpriceC(args.stockfromhomeinfo.c_voucher_buying_price?.toLong() ?: 0L)
        viewModel.setdecidedPawnPrice(args.stockfromhomeinfo.price_for_pawn?.toLong() ?: 0L)
        viewModel.setCalculatedPawnPrice(args.stockfromhomeinfo.calculated_for_pawn?.toLong() ?: 0L)
        viewModel.setdWeightInVoucher(args.stockfromhomeinfo.d_gold_weight_ywae?.toDouble() ?: 0.0)
        viewModel.setfWeightYwae(
            args.stockfromhomeinfo.f_voucher_shown_gold_weight_ywae?.toDouble() ?: 0.0
        )
        viewModel.setpriceE(args.stockfromhomeinfo.e_price_from_new_voucher?.toLong() ?: 0L)
//        binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndWeightK.setText(kpy[0].toString())
//        binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightP.setText(kpy[1].toString())
//        binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightY.setText(
//            getString(
//                R.string.ywae_value,
//                kpy[2].toString()
//            )
//        )

        binding.tvOldstockName.text = args.stockfromhomeinfo?.stock_name.orEmpty()

    }

    fun uiDeployment() {
        val include = binding.includeAmountList.includeGoldAndGemWeightGm
        val startList = listOf(
            include.btnCancelGoldAndGemWeightGm,
            include.textInputLayoutGoldAndGemWeightGm,
            include.btnAddGoldAndGemWeightGm,
            include.view,
            include.view2
        )
        val endList = listOf(include.btnEdtGoldAndGemWeightGm, include.tvGoldAndGemWeightGm)
        startList.forEach {
            it.isVisible = false
        }
        binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.setOnCheckedChangeListener { button, isChecked ->
            binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.root.isVisible =
                isChecked
            editGeneralExpense()
            if (isChecked) {
                viewModel.gemValueLiveData.observe(viewLifecycleOwner) {
                    val generalExpenseExpandedLayout =
                        binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense
                    generalExpenseExpandedLayout.tvGemAndDiaPrice.text =
                        getString(R.string.mmk_value, it.toString())
                    generalExpenseExpandedLayout.tvLblDis.isVisible =
                        generalExpenseExpandedLayout.checkPercentage.isChecked
                    generalExpenseExpandedLayout.tvLblDis.text = getString(
                        R.string.discount_percentage,
                        generalExpenseExpandedLayout.edtPercentageDisPrice.text.toString()
                    )
                    generalExpenseExpandedLayout.tvLabelReducedPercent.text = getString(
                        R.string.discount_percentage,
                        generalExpenseExpandedLayout.edtPercentageDisPrice.text.toString()
                    )

                    binding.includeInvoiceDetails.tvGemAndDiaPrice.text = it.toString()
                }
                viewModel.wastageYwaeLiveData.observe(viewLifecycleOwner) {
                    if (it > 0.0){
                        binding.includeAmountList.includeGeneralExpense.switchGeneralExpense.isChecked = true
                    }
                    val kpy = getKPYFromYwae(it)
                    binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceK.setText(
                        kpy[0].toInt().toString()
                    )
                    binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceP.setText(
                        kpy[1].toInt().toString()
                    )
                    binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtAddedReduceY.setText(
                        kpy[2].toString()
                    )
                    binding.includeInvoiceDetails.tvWastageKpy.text = getString(
                        R.string.kpy_value,
                        kpy[0].toInt().toString(),
                        kpy[1].toInt().toString(),
                        kpy[2].toString()
                    )
                }
                viewModel.maintainenceCostLiveData.observe(viewLifecycleOwner) {
                    binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtFee.setText(
                        it.toString()
                    )
                    binding.includeInvoiceDetails.tvFee.text = it.toString()
                }
                viewModel.ptClipCostLiveData.observe(viewLifecycleOwner) {
                    binding.includeAmountList.includeGeneralExpense.expandedGeneralExpense.edtPTClipPrice.setText(
                        it.toString()
                    )
                    binding.includeInvoiceDetails.tvPTClipPrice.text = it.toString()
                }
            } else {
                binding.includeInvoiceDetails.tvWastageKpy.text = getString(
                    R.string.kpy_value,
                    "0", "0", "0"
                )
                binding.includeInvoiceDetails.tvFee.text = "0"
                binding.includeInvoiceDetails.tvPTClipPrice.text = "0"
                binding.includeInvoiceDetails.tvGemAndDiaPrice.text = "0"

            }

        }
        include.btnEdtGoldAndGemWeightGm.setOnClickListener {
            startList.forEach {
                it.isVisible = true
            }
            endList.forEach {
                it.isVisible = false
            }
            blurView(include.btnEdtGoldAndGemWeightGm)
        }
        include.btnCancelGoldAndGemWeightGm.setOnClickListener {
            endList.forEach {
                it.isVisible = true
            }
            startList.forEach {
                it.isVisible = false
            }
            unBlurView()
        }
        include.btnAddGoldAndGemWeightGm.setOnClickListener {
            endList.forEach {
                it.isVisible = true
            }
            startList.forEach {
                it.isVisible = false
            }
            unBlurView()
            editGoldAndGemWeightGm()

        }

        val includeGoldAndGemKPY = binding.includeAmountList.includeGoldAndGemWeightKpy
        val goldAndGemKPYStart = listOf(
            includeGoldAndGemKPY.btnCancelGoldAndGemWeightKPY,
            includeGoldAndGemKPY.textInputLayoutGoldAndGemWeightK,
            includeGoldAndGemKPY.textInputLayoutGoldAndGemWeightP,
            includeGoldAndGemKPY.textInputLayoutGoldAndGemWeightY,
            includeGoldAndGemKPY.btnAddGoldAndGemWeight,
            includeGoldAndGemKPY.view,
            includeGoldAndGemKPY.view2
        )
        val goldAndGemKPYEnd = listOf(
            includeGoldAndGemKPY.btnEditGoldAndGemWeightKPY,
            includeGoldAndGemKPY.tvGoldAndGemWeightK,
            includeGoldAndGemKPY.tvGoldAndGemWeightP,
            includeGoldAndGemKPY.tvGoldAndGemWeightY
        )

        goldAndGemKPYStart.forEach {
            it.isVisible = false
        }
        includeGoldAndGemKPY.btnEditGoldAndGemWeightKPY.setOnClickListener {

            goldAndGemKPYStart.forEach {
                it.isVisible = true
            }
            goldAndGemKPYEnd.forEach {
                it.isVisible = false
            }
            blurView(includeGoldAndGemKPY.btnEditGoldAndGemWeightKPY)
        }
        includeGoldAndGemKPY.btnCancelGoldAndGemWeightKPY.setOnClickListener {
            goldAndGemKPYStart.forEach {
                it.isVisible = false
            }
            goldAndGemKPYEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }
        binding.includeAmountList.includeGoldAndGemWeightKpy.btnAddGoldAndGemWeight.setOnClickListener {
            editGoldAndGemWeightKpy()
            goldAndGemKPYStart.forEach {
                it.isVisible = false
            }
            goldAndGemKPYEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }

        val includeGemWeight = binding.includeAmountList.includeGemWeight
        val gemWeightStart = listOf(
            includeGemWeight.btnCancelGemWeightKPY,
            includeGemWeight.rBtnManually,
            includeGemWeight.rBtnAddFromServer,
            includeGemWeight.textInputLayoutGemWeightK,
            includeGemWeight.textInputLayoutGemWeightP,
            includeGemWeight.textInputLayoutGemWeightY,
            includeGemWeight.tvServerGemWeightK,
            includeGemWeight.tvServerGemWeightP,
            includeGemWeight.tvServerGemWeightY,
            includeGemWeight.btnEditServerGemWeightKPY,
            includeGemWeight.btnAddFromServer,
            includeGemWeight.btnAddGemWeightKPY,
            includeGemWeight.btnAddGemWeightManually,
            includeGemWeight.view,
            includeGemWeight.view2,
            includeGemWeight.view3
        )

        val gemWeightFromServer = listOf(
            includeGemWeight.btnAddGemWeightKPY,
            includeGemWeight.btnEditGemWeightKPY,
            includeGemWeight.tvServerGemWeightY,
            includeGemWeight.tvServerGemWeightK,
            includeGemWeight.tvServerGemWeightP,
            includeGemWeight.btnEditServerGemWeightKPY,
            includeGemWeight.btnAddFromServer,
            includeGemWeight.view3,

            )

        val gemWeightManually = listOf(
            includeGemWeight.btnAddGemWeightKPY,
            includeGemWeight.btnEditGemWeightKPY,
            includeGemWeight.btnAddGemWeightManually,
            includeGemWeight.view2,
            includeGemWeight.textInputLayoutGemWeightK,
            includeGemWeight.textInputLayoutGemWeightP,
            includeGemWeight.textInputLayoutGemWeightY,
        )

        val gemWeightCancel = listOf(
            includeGemWeight.tvGemWeightY,
            includeGemWeight.tvGemWeightP,
            includeGemWeight.tvGemWeightK,
            includeGemWeight.btnEditGemWeightKPY
        )

        gemWeightStart.forEach {
            it.isVisible = false
        }

        includeGemWeight.btnEditGemWeightKPY.setOnClickListener {
            gemWeightStart.forEach {
                it.isVisible = true
            }

            gemWeightFromServer.forEach {
                it.isVisible = false
            }
            includeGemWeight.tvGemWeightY.isVisible = false
            includeGemWeight.tvGemWeightP.isVisible = false
            includeGemWeight.tvGemWeightK.isVisible = false
            includeGemWeight.rBtnManually.isChecked = true

            blurView(includeGemWeight.btnEditGemWeightKPY)
        }

        includeGemWeight.radioGpChooseMethod.setOnCheckedChangeListener { group, checkedId ->
            if (includeGemWeight.rBtnManually.id == checkedId) {

                gemWeightFromServer.forEach {
                    it.isVisible = false
                }

                gemWeightManually.forEach {
                    it.isVisible = true
                }

            } else {

                gemWeightFromServer.forEach {
                    it.isVisible = true
                }

                gemWeightManually.forEach {
                    it.isVisible = false
                }
            }
        }
        includeGemWeight.btnAddGemWeightManually.setOnClickListener {
            gemWeightStart.forEach {
                it.isVisible = false
            }
            gemWeightCancel.forEach {
                it.isVisible = true
            }
            editGemWeightDetailManually()
            unBlurView()
        }
        includeGemWeight.btnAddFromServer.setOnClickListener {
            editGemWeightDetailCustom()
        }
        includeGemWeight.btnEditServerGemWeightKPY.setOnClickListener {
            editGemWeightDetailCustom()
        }


        includeGemWeight.btnCancelGemWeightKPY.setOnClickListener {
            gemWeightStart.forEach {
                it.isVisible = false
            }
            gemWeightCancel.forEach {
                it.isVisible = true
            }
            unBlurView()

        }

        val includeImpurityWeight = binding.includeAmountList.includeImpurityWeight
        val geeWeightStart = listOf(
            includeImpurityWeight.btnCancelImpurityWeightKPY,
            includeImpurityWeight.textInputLayoutImpurityWeightP,
            includeImpurityWeight.textInputLayoutImpurityWeightY,
            includeImpurityWeight.textInputLayoutImpurityWeightK,
            includeImpurityWeight.btnAddImpurityWeight,
            includeImpurityWeight.view,
            includeImpurityWeight.view2,
        )
        val geeWeightEnd = listOf(
            includeImpurityWeight.btnEditImpurityWeightKPY,
            includeImpurityWeight.tvImpurityWeightY,
            includeImpurityWeight.tvImpurityWeightP,
            includeImpurityWeight.tvImpurityWeightK
        )

        geeWeightStart.forEach {
            it.isVisible = false
        }
        includeImpurityWeight.btnEditImpurityWeightKPY.setOnClickListener {
            geeWeightStart.forEach {
                it.isVisible = true
            }
            geeWeightEnd.forEach {
                it.isVisible = false
            }
            blurView(includeImpurityWeight.btnEditImpurityWeightKPY)
        }
        includeImpurityWeight.btnCancelImpurityWeightKPY.setOnClickListener {
            geeWeightStart.forEach {
                it.isVisible = false
            }
            geeWeightEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }
        includeImpurityWeight.btnAddImpurityWeight.setOnClickListener {
            editImpurityWeightKpy()
            geeWeightStart.forEach {
                it.isVisible = false
            }
            geeWeightEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }

        val includeGoldQ = binding.includeAmountList.includeGoldQ
        val goldQStart = listOf(
            includeGoldQ.btnCancelGoldQ,
            includeGoldQ.btnAddGoldQ,
            includeGoldQ.textInputLayoutGoldQ,
            includeGoldQ.view,
            includeGoldQ.view2
        )
        val goldQEnd = listOf(
            includeGoldQ.btnEditGoldQ,
            includeGoldQ.tvGoldQ
        )

        goldQStart.forEach {
            it.isVisible = false
        }

        includeGoldQ.btnEditGoldQ.setOnClickListener {
            goldQStart.forEach {
                it.isVisible = true
            }
            goldQEnd.forEach {
                it.isVisible = false
            }
            blurView(includeGoldQ.btnEditGoldQ)
        }
        includeGoldQ.btnCancelGoldQ.setOnClickListener {
            goldQStart.forEach {
                it.isVisible = false
            }
            goldQEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }
        includeGoldQ.btnAddGoldQ.setOnClickListener {
            editGoldCarat {
                goldQStart.forEach {
                    it.isVisible = false
                }
                goldQEnd.forEach {
                    it.isVisible = true
                }
            }
            unBlurView()
        }

        val includePriceA = binding.includeAmountList.includePriceA
        val purchasePriceStart = listOf(
            includePriceA.btnCancelPurchasePrice,
            includePriceA.textInputLayoutPurchasePrice,
            includePriceA.btnAddPurchasePrice,
            includePriceA.view,
            includePriceA.view2
        )
        val purchasePriceEnd = listOf(
            includePriceA.btnEditPurchasePrice,
            includePriceA.tvPurchasePrice,
        )

        purchasePriceStart.forEach {
            it.isVisible = false
        }
        includePriceA.btnEditPurchasePrice.setOnClickListener {
            purchasePriceStart.forEach {
                it.isVisible = true
            }
            purchasePriceEnd.forEach {
                it.isVisible = false
            }
            blurView(includePriceA.btnEditPurchasePrice)
        }
        includePriceA.btnCancelPurchasePrice.setOnClickListener {
            purchasePriceStart.forEach {
                it.isVisible = false
            }
            purchasePriceEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }
        includePriceA.btnAddPurchasePrice.setOnClickListener {
            editAbuyingPrice {
                purchasePriceStart.forEach {
                    it.isVisible = false
                }
                purchasePriceEnd.forEach {
                    it.isVisible = true
                }
                unBlurView()
            }
        }

        val includeMortgagePrice = binding.includeAmountList.includeMortgagePrice
        val mortgagePriceStart = listOf(
            includeMortgagePrice.btnCancelMortgagePrice,
            includeMortgagePrice.btnAddMortgagePrice,
            includeMortgagePrice.view,
            includeMortgagePrice.view2,
            includeMortgagePrice.textInputLayoutMortgagePrice
        )
        val mortgagePriceEnd = listOf(
            includeMortgagePrice.btnEditMortgagePrice,
            includeMortgagePrice.tvMortgagePrice
        )
        mortgagePriceStart.forEach {
            it.isVisible = false
        }
        includeMortgagePrice.btnEditMortgagePrice.setOnClickListener {
            mortgagePriceStart.forEach {
                it.isVisible = true
            }
            mortgagePriceEnd.forEach {
                it.isVisible = false
            }
            blurView(includeMortgagePrice.btnEditMortgagePrice)
        }
        includeMortgagePrice.btnCancelMortgagePrice.setOnClickListener {
            mortgagePriceStart.forEach {
                it.isVisible = false
            }
            mortgagePriceEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }
        includeMortgagePrice.btnAddMortgagePrice.setOnClickListener {
            editDecidedPawnPrice()
            mortgagePriceStart.forEach {
                it.isVisible = false
            }
            mortgagePriceEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }

        val includeVoucherPay = binding.includeAmountList.includePriceB
        val priceBEditViews = listOf(
            includeVoucherPay.btnCancelVoucherOpenPay,
            includeVoucherPay.check,
            includeVoucherPay.textInputLayoutVoucherOpenPay,
            includeVoucherPay.textInputLayoutPercentageDisPrice,
            includeVoucherPay.textInputLayoutCalculatePercentageDisPrice,
            includeVoucherPay.tvLblDis,
            includeVoucherPay.tvLblLearnMore,
            includeVoucherPay.btnCalculate,
            includeVoucherPay.btnSaveVoucherOpenPay,
            includeVoucherPay.view,
            includeVoucherPay.view2
        )
        val priceBEditDiscountView = listOf(
            includeVoucherPay.btnEditVoucherOpenPay,
            includeVoucherPay.tvVoucherOpenPay,
            includeVoucherPay.textInputLayoutCalculatePercentageDisPrice,
            includeVoucherPay.textInputLayoutPercentageDisPrice,
            includeVoucherPay.tvLblDis,
            includeVoucherPay.tvLblLearnMore,
            includeVoucherPay.btnCalculate
        )
        val priceBValueToShow = listOf(
            includeVoucherPay.btnEditVoucherOpenPay,
            includeVoucherPay.tvVoucherOpenPay,
        )

        priceBEditViews.forEach {
            it.isVisible = false
        }

        includeVoucherPay.btnEditVoucherOpenPay.setOnClickListener {
            if (includeVoucherPay.check.isChecked) {
                priceBEditViews.forEach {
                    it.isVisible = true
                }
                priceBEditDiscountView.forEach {
                    it.isVisible = true
                }
                priceBValueToShow.forEach {
                    it.isVisible = false
                }
            } else {
                includeVoucherPay.edtPercentageDisPrice.setText("0")
                includeVoucherPay.edtCalculatePercentageDisPrice.setText("0")
                priceBEditViews.forEach {
                    it.isVisible = true
                }
                priceBEditDiscountView.forEach {
                    it.isVisible = false
                }
                priceBValueToShow.forEach {
                    it.isVisible = false
                }
            }
            blurView(includeVoucherPay.btnEditVoucherOpenPay)

        }

        //for clicklistener
        getPriceBFromDiscountCalculation()

        includeVoucherPay.check.setOnCheckedChangeListener { buttonView, isChecked ->
            includeVoucherPay.edtPercentageDisPrice.isEnabled = isChecked
            if (isChecked) {
                priceBEditViews.forEach {
                    it.isVisible = true
                }
                priceBEditDiscountView.forEach {
                    it.isVisible = true
                }
                priceBValueToShow.forEach {
                    it.isVisible = false
                }
            } else {
                includeVoucherPay.edtPercentageDisPrice.setText("0")
                includeVoucherPay.edtCalculatePercentageDisPrice.setText("0")
                priceBEditViews.forEach {
                    it.isVisible = true
                }
                priceBEditDiscountView.forEach {
                    it.isVisible = false
                }
                priceBValueToShow.forEach {
                    it.isVisible = false
                }
            }
        }
        includeVoucherPay.btnCancelVoucherOpenPay.setOnClickListener {
            priceBEditViews.forEach {
                it.isVisible = false
            }
            priceBEditDiscountView.forEach {
                it.isVisible = false
            }
            priceBValueToShow.forEach {
                it.isVisible = true
            }
            unBlurView()
        }
        includeVoucherPay.btnSaveVoucherOpenPay.setOnClickListener {
            editBbuyingPrice {
                priceBEditViews.forEach {
                    it.isVisible = false
                }
                priceBEditDiscountView.forEach {
                    it.isVisible = false
                }
                priceBValueToShow.forEach {
                    it.isVisible = true
                }
                unBlurView()
            }
        }


        val includeVoucherPrice = binding.includeAmountList.includePriceC
        val voucherPriceStart = listOf(
            includeVoucherPrice.btnCancelVoucherOpenPrice,
            includeVoucherPrice.textInputLayoutVoucherOpenPrice,
            includeVoucherPrice.btnAddVoucherOpenPrice,
            includeVoucherPrice.view,
            includeVoucherPrice.view2
        )
        val voucherPriceEnd = listOf(
            includeVoucherPrice.btnEditVoucherOpenPrice,
            includeVoucherPrice.tvVoucherOpenPrice
        )

        voucherPriceStart.forEach {
            it.isVisible = false
        }
        includeVoucherPrice.btnEditVoucherOpenPrice.setOnClickListener {
            voucherPriceStart.forEach {
                it.isVisible = true
            }
            voucherPriceEnd.forEach {
                it.isVisible = false
            }
            blurView(includeVoucherPrice.btnEditVoucherOpenPrice)
        }
        includeVoucherPrice.btnCancelVoucherOpenPrice.setOnClickListener {
            voucherPriceStart.forEach {
                it.isVisible = false
            }
            voucherPriceEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }
        includeVoucherPrice.btnAddVoucherOpenPrice.setOnClickListener {
            editCbuyingPrice {
                voucherPriceStart.forEach {
                    it.isVisible = false
                }
                voucherPriceEnd.forEach {
                    it.isVisible = true
                }
                unBlurView()
            }
        }
        val includePurchaseGoldWeight = binding.includeAmountList.includeFGoldWeight
        val purchaseGoldStart = listOf(
            includePurchaseGoldWeight.btnCancelPurchaseGoldWeight,
            includePurchaseGoldWeight.textInputLayoutPurchaseGoldWeightK,
            includePurchaseGoldWeight.textInputLayoutPurchaseGoldWeightP,
            includePurchaseGoldWeight.textInputLayoutPurchaseGoldWeightY,
            includePurchaseGoldWeight.btnAddPurchaseGoldWeight,
            includePurchaseGoldWeight.view,
            includePurchaseGoldWeight.view2
        )
        val purchaseGoldEnd = listOf(
            includePurchaseGoldWeight.btnEditPurchaseGoldWeight,
            includePurchaseGoldWeight.tvPurchaseGoldWeightK,
            includePurchaseGoldWeight.tvPurchaseGoldWeightP,
            includePurchaseGoldWeight.tvPurchaseGoldWeightY,
        )

        purchaseGoldStart.forEach {
            it.isVisible = false
        }
        includePurchaseGoldWeight.btnEditPurchaseGoldWeight.setOnClickListener {
            purchaseGoldStart.forEach {
                it.isVisible = true
            }
            purchaseGoldEnd.forEach {
                it.isVisible = false
            }
            blurView(includePurchaseGoldWeight.btnEditPurchaseGoldWeight)
        }
        includePurchaseGoldWeight.btnCancelPurchaseGoldWeight.setOnClickListener {
            purchaseGoldStart.forEach {
                it.isVisible = false
            }
            purchaseGoldEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
        }
        includePurchaseGoldWeight.btnAddPurchaseGoldWeight.setOnClickListener {
            editFGoldWeight {
                purchaseGoldStart.forEach {
                    it.isVisible = false
                }
                purchaseGoldEnd.forEach {
                    it.isVisible = true
                }
                unBlurView()
            }
        }
    }

    private fun blurView(view: View) {

        val myList = listOf(
            Pair(
                binding.includeAmountList.includeGoldAndGemWeightGm.btnEdtGoldAndGemWeightGm,
                binding.includeAmountList.includeGoldAndGemWeightGm.root
            ),
            Pair(
                binding.includeAmountList.includeGoldAndGemWeightKpy.btnEditGoldAndGemWeightKPY,
                binding.includeAmountList.includeGoldAndGemWeightKpy.root
            ),
            Pair(
                binding.includeAmountList.includeGemWeight.btnEditGemWeightKPY,
                binding.includeAmountList.includeGemWeight.root
            ),
            Pair(
                binding.includeAmountList.includeImpurityWeight.btnEditImpurityWeightKPY,
                binding.includeAmountList.includeImpurityWeight.root
            ),
            Pair(
                binding.includeAmountList.includeItemType.rBtnGood,
                binding.includeAmountList.includeItemType.root
            ),
            Pair(
                binding.includeAmountList.includeGoldQ.btnEditGoldQ,
                binding.includeAmountList.includeGoldQ.root
            ),
            Pair(
                binding.includeAmountList.includeGeneralExpense.switchGeneralExpense,
                binding.includeAmountList.includeGeneralExpense.root
            ),
            Pair(
                binding.includeAmountList.includePriceA.btnEditPurchasePrice,
                binding.includeAmountList.includePriceA.root
            ),
            Pair(
                binding.includeAmountList.includeMortgagePrice.btnEditMortgagePrice,
                binding.includeAmountList.includeMortgagePrice.root
            ),
            Pair(
                binding.includeAmountList.includePriceB.btnEditVoucherOpenPay,
                binding.includeAmountList.includePriceB.root
            ),
            Pair(
                binding.includeAmountList.includePriceC.btnEditVoucherOpenPrice,
                binding.includeAmountList.includePriceC.root
            ),
            Pair(
                binding.includeAmountList.includeFGoldWeight.btnEditPurchaseGoldWeight,
                binding.includeAmountList.includeFGoldWeight.root
            )
        )

        myList.forEach {
            if (it.first.id == view.id) {
                it.first.isEnabled = true
                it.second.alpha = 1F

            } else {
                it.first.isEnabled = false
                it.second.alpha = 0.3F
            }
        }
    }

    private fun unBlurView() {
        val myList = listOf(
            Pair(
                binding.includeAmountList.includeGoldAndGemWeightGm.btnEdtGoldAndGemWeightGm,
                binding.includeAmountList.includeGoldAndGemWeightGm.root
            ),
            Pair(
                binding.includeAmountList.includeGoldAndGemWeightKpy.btnEditGoldAndGemWeightKPY,
                binding.includeAmountList.includeGoldAndGemWeightKpy.root
            ),
            Pair(
                binding.includeAmountList.includeGemWeight.btnEditGemWeightKPY,
                binding.includeAmountList.includeGemWeight.root
            ),
            Pair(
                binding.includeAmountList.includeImpurityWeight.btnEditImpurityWeightKPY,
                binding.includeAmountList.includeImpurityWeight.root
            ),
            Pair(
                binding.includeAmountList.includeItemType.rBtnGood,
                binding.includeAmountList.includeItemType.root
            ),
            Pair(
                binding.includeAmountList.includeGoldQ.btnEditGoldQ,
                binding.includeAmountList.includeGoldQ.root
            ),
            Pair(
                binding.includeAmountList.includeGeneralExpense.switchGeneralExpense,
                binding.includeAmountList.includeGeneralExpense.root
            ),
            Pair(
                binding.includeAmountList.includePriceA.btnEditPurchasePrice,
                binding.includeAmountList.includePriceA.root
            ),
            Pair(
                binding.includeAmountList.includeMortgagePrice.btnEditMortgagePrice,
                binding.includeAmountList.includeMortgagePrice.root
            ),
            Pair(
                binding.includeAmountList.includePriceB.btnEditVoucherOpenPay,
                binding.includeAmountList.includePriceB.root
            ),
            Pair(
                binding.includeAmountList.includePriceC.btnEditVoucherOpenPrice,
                binding.includeAmountList.includePriceC.root
            ),
            Pair(
                binding.includeAmountList.includeFGoldWeight.btnEditPurchaseGoldWeight,
                binding.includeAmountList.includeFGoldWeight.root
            )
        )
        myList.forEach { view ->
            view.first.isEnabled = true
            view.second.alpha = 1F
        }
    }

    fun chooseImage() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        launchChooseImage.launch(pickIntent)
    }

    private fun requestStoragePermission() {
//        storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun isExternalStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var photoFile: File
    private var photoUri: Uri? = null
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            val packageManager = requireActivity().packageManager
            val cameraActivities = packageManager.queryIntentActivities(takePictureIntent, 0)
            if (cameraActivities.isNotEmpty()) {
                photoFile = createImageFile()

                // Continue only if the File was successfully created
                photoFile.also {
                    photoUri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.shwemisale.fileprovider",
                        it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            } else {
                Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            viewModel.setImagePathLiveData(absolutePath)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->

            }
        }
    }

    override fun selectedName(nameTag: String, totalQty: Int, size: String) {
        chooseSTockTypeDialogFragment.dismiss()
        viewModel.setNameTag(nameTag)
        viewModel.setTotalQty(totalQty.toString())
        viewModel.setSize(size)
    }

    override fun onTotalGemWeightCalculated(totalGemWeightYwae: Double) {
        viewModel.resetPrices()
        viewModel.setgemWEightYwae(totalGemWeightYwae)
        val includeGemWeight = binding.includeAmountList.includeGemWeight
        val gemWeightStart = listOf(
            includeGemWeight.btnCancelGemWeightKPY,
            includeGemWeight.rBtnManually,
            includeGemWeight.rBtnAddFromServer,
            includeGemWeight.textInputLayoutGemWeightK,
            includeGemWeight.textInputLayoutGemWeightP,
            includeGemWeight.textInputLayoutGemWeightY,
            includeGemWeight.tvServerGemWeightK,
            includeGemWeight.tvServerGemWeightP,
            includeGemWeight.tvServerGemWeightY,
            includeGemWeight.btnEditServerGemWeightKPY,
            includeGemWeight.btnAddFromServer,
            includeGemWeight.btnAddGemWeightKPY,
            includeGemWeight.btnAddGemWeightManually,
            includeGemWeight.view,
            includeGemWeight.view2,
            includeGemWeight.view3
        )
        val gemWeightCancel = listOf(
            includeGemWeight.tvGemWeightY,
            includeGemWeight.tvGemWeightP,
            includeGemWeight.tvGemWeightK,
            includeGemWeight.btnEditGemWeightKPY
        )
        gemWeightStart.forEach {
            it.isVisible = false
        }
        gemWeightCancel.forEach {
            it.isVisible = true
        }
        unBlurView()
    }
}