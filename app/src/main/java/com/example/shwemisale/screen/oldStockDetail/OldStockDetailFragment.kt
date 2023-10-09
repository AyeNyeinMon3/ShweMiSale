package com.example.shwemisale.screen.oldStockDetail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.Resource
import com.example.shwemi.util.generateNumberFromEditText
import com.example.shwemi.util.getAlertDialog
import com.example.shwemisale.R
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.databinding.DialogAddStockTypeBinding
import com.example.shwemisale.databinding.DialogUploadImageBinding
import com.example.shwemisale.databinding.FragmentOldStockDetailBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.example.shwemisale.screen.oldStockDetail.gemWeightDetail.DialogGemWeightDetailFragment
import com.example.shwemisale.screen.oldStockDetail.gemWeightDetail.TotalGemWeightListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldStockDetailFragment : Fragment(), ChooseStockTypeListener ,TotalGemWeightListener{
    private lateinit var binding: FragmentOldStockDetailBinding
    private val viewModel by viewModels<OldStockDetailViewModel>()
    private val args by navArgs<OldStockDetailFragmentArgs>()
    private lateinit var loading: AlertDialog
    private lateinit var chooseSTockTypeDialogFragment: ChooseStockTypeDialogFragment
    private lateinit var dialogGemWeightDetailFragment: DialogGemWeightDetailFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentOldStockDetailBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        uiDeployment()
        bindForGoldAndGemWeight()
        chooseStockNameFeature()

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
        val gemWeightLayout = binding.includeAmountList.includeGemWeight
        gemWeightLayout.tvGemWeightK.text =
            generateNumberFromEditText(gemWeightLayout.edtGemWeightK)
        gemWeightLayout.tvGemWeightP.text =
            generateNumberFromEditText(gemWeightLayout.edtGemWeightP)
        gemWeightLayout.tvGemWeightY.text =
            generateNumberFromEditText(gemWeightLayout.edtGemWeightY)

        val gemWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(gemWeightLayout.edtGemWeightK).toInt(),
            generateNumberFromEditText(gemWeightLayout.edtGemWeightP).toInt(),
            generateNumberFromEditText(gemWeightLayout.edtGemWeightY).toDouble()
        )
        viewModel.gemWeightYwae = gemWeightYwae
    }

    fun editGoldAndGemWeightGm() {
        binding.includeAmountList.includeGoldAndGemWeightGm.tvGoldAndGemWeightGm.text =
            getString(
                R.string.gram_value,
                binding.includeAmountList.includeGoldAndGemWeightGm.edtGoldAndGemWeightGm.text.toString()
            )
        viewModel.goldAndGemWeightGm =
            binding.includeAmountList.includeGoldAndGemWeightGm.edtGoldAndGemWeightGm.text.toString()
                .toDouble()
    }

    fun editGoldAndGemWeightKpy() {
        val goldAndGemWeightYwae = getYwaeFromKPY(
            generateNumberFromEditText(binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndWeightK).toDouble()
                .toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightP).toDouble()
                .toInt(),
            generateNumberFromEditText(binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightY).toDouble(),
        )
        binding.includeAmountList.includeGoldAndGemWeightKpy.tvGoldAndGemWeightK.text =
            getString(
                R.string.kyat_value,
                generateNumberFromEditText(binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndWeightK)
            )

        binding.includeAmountList.includeGoldAndGemWeightKpy.tvGoldAndGemWeightP.text =
            getString(
                R.string.kyat_value,
                generateNumberFromEditText(binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightP)
            )

        binding.includeAmountList.includeGoldAndGemWeightKpy.tvGoldAndGemWeightY.text =
            getString(
                R.string.kyat_value,
                generateNumberFromEditText(binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightY)
            )
        viewModel.goldAndGemWeightYwae = goldAndGemWeightYwae

    }


    fun bindForGoldAndGemWeight() {
        binding.includeAmountList.includeGoldAndGemWeightGm.tvGoldAndGemWeightGm.text =
            if (args.goldAndGemWeightGm.isEmpty())
                getString(
                    R.string.gram_value,
                    "0.0"
                ) else getYwaeFromGram(args.goldAndGemWeightYwae.toDouble()).toString()
        binding.includeAmountList.includeGoldAndGemWeightGm.edtGoldAndGemWeightGm.setText(args.goldAndGemWeightGm)
        val kpy =
            if (args.goldAndGemWeightYwae.isNotEmpty()) getKPYFromYwae(args.goldAndGemWeightYwae.toDouble()) else getKPYFromYwae(
                getYwaeFromGram(args.goldAndGemWeightGm.toDouble())
            )
        binding.includeAmountList.includeGoldAndGemWeightKpy.tvGoldAndGemWeightK.text =
            getString(R.string.kyat_value, "${kpy[0].toInt()}")

        binding.includeAmountList.includeGoldAndGemWeightKpy.tvGoldAndGemWeightP.text =
            getString(R.string.kyat_value, "${kpy[1].toInt()}")

        binding.includeAmountList.includeGoldAndGemWeightKpy.tvGoldAndGemWeightY.text =
            getString(R.string.kyat_value, "${kpy[2]}")

        binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndWeightK.setText(kpy[0].toString())
        binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightP.setText(kpy[1].toString())
        binding.includeAmountList.includeGoldAndGemWeightKpy.edtGoldAndGemWeightY.setText(kpy[2].toString())

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
            if (isChecked) {
                blurView(binding.includeAmountList.includeGeneralExpense.switchGeneralExpense)
            } else {
                unBlurView()
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
            includeGemWeight.tvGemWeightY.isVisible= false
            includeGemWeight.tvGemWeightP.isVisible= false
            includeGemWeight.tvGemWeightK.isVisible= false
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
            gemWeightStart.forEach {
                it.isVisible = false
            }
            gemWeightCancel.forEach {
                it.isVisible = true
            }
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

        val includeGeeWeight = binding.includeAmountList.includeGeeWeight
        val geeWeightStart = listOf(
            includeGeeWeight.btnCancelGeeWeightKPY,
            includeGeeWeight.textInputLayoutGeeWeightP,
            includeGeeWeight.textInputLayoutGeeWeightY,
            includeGeeWeight.textInputLayoutGeeWeightK,
            includeGeeWeight.btnAddGeeWeight,
            includeGeeWeight.view,
            includeGeeWeight.view2,
        )
        val geeWeightEnd = listOf(
            includeGeeWeight.btnEditGeeWeightKPY,
            includeGeeWeight.tvGeeWeightY,
            includeGeeWeight.tvGeeWeightP,
            includeGeeWeight.tvGeeWeightK
        )

        geeWeightStart.forEach {
            it.isVisible = false
        }
        includeGeeWeight.btnEditGeeWeightKPY.setOnClickListener {
            geeWeightStart.forEach {
                it.isVisible = true
            }
            geeWeightEnd.forEach {
                it.isVisible = false
            }
            blurView(includeGeeWeight.btnEditGeeWeightKPY)
        }
        includeGeeWeight.btnCancelGeeWeightKPY.setOnClickListener {
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

        val includePurchasePrice = binding.includeAmountList.includePurchasePrice
        val purchasePriceStart = listOf(
            includePurchasePrice.btnCancelPurchasePrice,
            includePurchasePrice.textInputLayoutPurchasePrice,
            includePurchasePrice.btnAddPurchasePrice,
            includePurchasePrice.view,
            includePurchasePrice.view2
        )
        val purchasePriceEnd = listOf(
            includePurchasePrice.btnEditPurchasePrice,
            includePurchasePrice.tvPurchasePrice,
        )

        purchasePriceStart.forEach {
            it.isVisible = false
        }
        includePurchasePrice.btnEditPurchasePrice.setOnClickListener {
            purchasePriceStart.forEach {
                it.isVisible = true
            }
            purchasePriceEnd.forEach {
                it.isVisible = false
            }
            blurView(includePurchasePrice.btnEditPurchasePrice)
        }
        includePurchasePrice.btnCancelPurchasePrice.setOnClickListener {
            purchasePriceStart.forEach {
                it.isVisible = false
            }
            purchasePriceEnd.forEach {
                it.isVisible = true
            }
            unBlurView()
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

        val includeVoucherPay = binding.includeAmountList.includeVoucherOpenPay
        val voucherPayStart = listOf(
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
        val voucherPayEdit = listOf(
            includeVoucherPay.btnEditVoucherOpenPay,
            includeVoucherPay.tvVoucherOpenPay,
            includeVoucherPay.textInputLayoutCalculatePercentageDisPrice,
            includeVoucherPay.textInputLayoutPercentageDisPrice,
            includeVoucherPay.tvLblDis,
            includeVoucherPay.tvLblLearnMore,
            includeVoucherPay.btnCalculate
        )
        val voucherPayDis = listOf(
            includeVoucherPay.btnEditVoucherOpenPay,
            includeVoucherPay.tvVoucherOpenPay,
        )

        voucherPayStart.forEach {
            it.isVisible = false
        }

        includeVoucherPay.btnEditVoucherOpenPay.setOnClickListener {
            voucherPayStart.forEach {
                it.isVisible = true
            }
            voucherPayEdit.forEach {
                it.isVisible = false
            }
            blurView(includeVoucherPay.btnEditVoucherOpenPay)

        }

        includeVoucherPay.check.setOnClickListener {
            voucherPayStart.forEach {
                it.isVisible = true
            }
            voucherPayEdit.forEach {
                it.isVisible = true
            }
            voucherPayDis.forEach {
                it.isVisible = false
            }

        }

        includeVoucherPay.btnCancelVoucherOpenPay.setOnClickListener {
            voucherPayStart.forEach {
                it.isVisible = false
            }
            voucherPayEdit.forEach {
                it.isVisible = false
            }
            voucherPayDis.forEach {
                it.isVisible = true
            }
            unBlurView()
        }

        val includeVoucherPrice = binding.includeAmountList.includeVoucherOpenPrice
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

        val includePurchaseGoldWeight = binding.includeAmountList.includePurchaseGoldWeight
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
                binding.includeAmountList.includeGeeWeight.btnEditGeeWeightKPY,
                binding.includeAmountList.includeGeeWeight.root
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
                binding.includeAmountList.includePurchasePrice.btnEditPurchasePrice,
                binding.includeAmountList.includePurchasePrice.root
            ),
            Pair(
                binding.includeAmountList.includeMortgagePrice.btnEditMortgagePrice,
                binding.includeAmountList.includeMortgagePrice.root
            ),
            Pair(
                binding.includeAmountList.includeVoucherOpenPay.btnEditVoucherOpenPay,
                binding.includeAmountList.includeVoucherOpenPay.root
            ),
            Pair(
                binding.includeAmountList.includeVoucherOpenPrice.btnEditVoucherOpenPrice,
                binding.includeAmountList.includeVoucherOpenPrice.root
            ),
            Pair(
                binding.includeAmountList.includePurchaseGoldWeight.btnEditPurchaseGoldWeight,
                binding.includeAmountList.includePurchaseGoldWeight.root
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
                binding.includeAmountList.includeGeeWeight.btnEditGeeWeightKPY,
                binding.includeAmountList.includeGeeWeight.root
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
                binding.includeAmountList.includePurchasePrice.btnEditPurchasePrice,
                binding.includeAmountList.includePurchasePrice.root
            ),
            Pair(
                binding.includeAmountList.includeMortgagePrice.btnEditMortgagePrice,
                binding.includeAmountList.includeMortgagePrice.root
            ),
            Pair(
                binding.includeAmountList.includeVoucherOpenPay.btnEditVoucherOpenPay,
                binding.includeAmountList.includeVoucherOpenPay.root
            ),
            Pair(
                binding.includeAmountList.includeVoucherOpenPrice.btnEditVoucherOpenPrice,
                binding.includeAmountList.includeVoucherOpenPrice.root
            ),
            Pair(
                binding.includeAmountList.includePurchaseGoldWeight.btnEditPurchaseGoldWeight,
                binding.includeAmountList.includePurchaseGoldWeight.root
            )
        )
        myList.forEach { view ->
            view.first.isEnabled = true
            view.second.alpha = 1F
        }
    }

    override fun selectedName(name: String, totalQty: Int) {
        chooseSTockTypeDialogFragment.dismiss()
        binding.tvOldstockName.text = name
    }

    override fun onTotalGemWeightCalculated(totalGemWeightYwae: Double) {
        viewModel.gemWeightYwae = totalGemWeightYwae
        val kpy = getKPYFromYwae(viewModel.gemWeightYwae)
        binding.includeAmountList.includeGemWeight.tvGemWeightK.text = getString(R.string.kyat_value,kpy[0].toInt().toString())
        binding.includeAmountList.includeGemWeight.tvGemWeightP.text = getString(R.string.pae_value,kpy[1].toInt().toString())
        binding.includeAmountList.includeGemWeight.tvGemWeightY.text = getString(R.string.ywae_value,kpy[2].toString())
        unBlurView()
    }
}