package com.example.shwemisale.screen.goldFromHomeInfo

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.shwemi.util.*
import com.example.shwemisale.R
import com.example.shwemisale.databinding.DialogGemWeightBinding
import com.example.shwemisale.databinding.DialogMinusPercentageBinding
import com.example.shwemisale.databinding.DialogResellStockInfoBinding
import com.example.shwemisale.databinding.FragmentResellStockInfoAddedSellBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getKyatsFromKPY
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellResellStockInfoAddedFragment : Fragment() {

    lateinit var binding: FragmentResellStockInfoAddedSellBinding
    lateinit var dialogBinding: DialogResellStockInfoBinding
    lateinit var dialogGemWeightBinding: DialogGemWeightBinding
    lateinit var dialogMinusPercentageBinding: DialogMinusPercentageBinding
    private val viewModel by viewModels<GoldFromHomeDetailViewModel>()
    private lateinit var loading: AlertDialog
    private val args by navArgs<SellResellStockInfoAddedFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentResellStockInfoAddedSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
        viewModel.horizontalOption = "Damage"
//        viewModel.getRebuyPrice("Damage", "X", "small", args.id)
        args.id?.let {
            bindPassedData(it)
        }
        setXYZselection()

        var hasOtherReducedCost: Boolean = false
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
            if (checkedId == binding.radioBtnOldStock.id){
                binding.edtRepurchasePrice.setText("")
            }else{
                binding.edtRepurchasePrice.setText(lastValue)
            }
        }
        binding.btnCalculateGoldAndGemWeight.setOnClickListener {
            calculateGoldAndGemWeight()
        }
        binding.btnCalculateRebuyPriceFromGq.setOnClickListener {
            calculateRebuyPriceFromGQ()
        }
        binding.btnCalculate2.setOnClickListener {
            calculateGQinCarat()
            calculateGoldWeight()
            calculateBuyPriceFromShop(hasOtherReducedCost)
            resetPricesValue()
        }
        binding.btnCalculate3.setOnClickListener {
            calculateDecidedPawnPrice()
            binding.edtPriceB
                .setText(generateNumberFromEditText(binding.edtPaymentFromShop))
            binding.edtPriceA.setText(generateNumberFromEditText(binding.edtRepurchasePrice))
            binding.edtPriceB.setText(generateNumberFromEditText(binding.edtPaymentFromShop))

        }
        binding.btnCalculatePawnPrice.setOnClickListener {
            calculatePawnPrice(hasOtherReducedCost)
        }
        binding.btnCalculatePriceB.setOnClickListener {
            calculatePriceB(hasOtherReducedCost)
        }
        binding.btnCalculatePriceA.setOnClickListener {
            calculatePriceA(hasOtherReducedCost)
        }
        binding.btnCalculatePriceD.setOnClickListener {
            calculatePriceD(hasOtherReducedCost)
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
            val item = viewModel.getStockInfoFromDataBase(args.id.orEmpty())
            if (args.id != null){

            }else{
                viewModel.saveStockFromHome(

                )
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
                        args.id
                    )

                }
                binding.radioBtnGood.id -> {
                    viewModel.horizontalOption = "Good"
                    viewModel.getRebuyPrice(
                        viewModel.horizontalOption,
                        viewModel.verticalOption,
                        viewModel.size,
                        args.id
                    )

                }
                binding.radioBtnNotToGo.id -> {
                    viewModel.horizontalOption = "Not to go"
                    viewModel.getRebuyPrice(
                        viewModel.horizontalOption,
                        viewModel.verticalOption,
                        viewModel.size,
                        args.id
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
                    viewModel.hundredPercentGoldPrice =
                        it.data!!.find { it.name == "100%" }?.price.orEmpty()

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

    }

    fun bindPassedData(id: String) {
        val item = viewModel.getStockInfoFromDataBase(id)
        binding.edtGoldAndGemWeightGm.setText(item.gold_and_gem_weight_gm?.let {
            String.format(
                "%.2f",
                it.toDouble()
            )
        })

        val goldAndGemYwae = getYwaeFromGram(item.gold_and_gem_weight_gm!!.toDouble())
        val goldAndGemKpy = getKPYFromYwae(goldAndGemYwae)

        val gemKpy = getKPYFromYwae(item.gem_weight_ywae!!.toDouble())
        binding.edtGemWeightK.setText(gemKpy[0].toInt().toString())
        binding.edtGemWeightP.setText(gemKpy[1].toInt().toString())
        binding.edtGemWeightY.setText(gemKpy[2].let { String.format("%.2f", it) })

        binding.edtGoldAndGemWeightK.setText(goldAndGemKpy[0].toInt().toString())
        binding.edtGoldAndGemWeightP.setText(goldAndGemKpy[1].toInt().toString())
        binding.edtGoldAndGemWeightY.setText(goldAndGemKpy[2].let { String.format("%.2f", it) })

        val goldYwae = goldAndGemYwae - item.gem_weight_ywae.toDouble()
        val goldKpy = getKPYFromYwae(goldYwae)
        binding.edtGoldWeightK.setText(goldKpy[0].toInt().toString())
        binding.edtGoldWeightP.setText(goldKpy[1].toInt().toString())
        binding.edtGoldWeightY.setText(goldKpy[2].let { String.format("%.2f", it) })
        binding.edtRepurchasePrice.setText(item.rebuyPrice.toString())

        binding.edtFee.setText(item.maintenance_cost)
        binding.edtPTclipValue.setText(item.pt_and_clip_cost)

        val wastageKPY = getKPYFromYwae(item.wastage_ywae?.toDouble()?:0.0)
        binding.edtAddReducedK.setText(wastageKPY[0].toInt().toString())
        binding.edtAddReducedP.setText(wastageKPY[1].toInt().toString())
        binding.edtAddReducedY.setText(wastageKPY[2].let { String.format("%.2f", it) })


        binding.ivBg.loadImageWithGlide(item.image)
        binding.ivCamera.isVisible = false

        binding.edtRepurchasePrice.setText(item.gold_price)

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
        val gqInCarat = rebuyPrice * 24 / viewModel.hundredPercentGoldPrice.toInt()
        binding.edtGoldQuality.setText(String.format("%.2f", gqInCarat))
    }

    fun calculateRebuyPriceFromGQ(){
        val gqInCarat = generateNumberFromEditText(binding.edtGoldQuality).toDouble()
        val rebuyPrice = gqInCarat /24 * viewModel.hundredPercentGoldPrice.toInt()
        binding.edtRepurchasePrice.setText(rebuyPrice.toInt().toString())
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

        binding.edtPaymentFromShop.setText( buyPriceFromShop.toInt().toString())
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
            (goldKyat) * generateNumberFromEditText(binding.edtDecidedPawnPrice).toDouble()+diamondGemValue

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
            (goldKyat + reducedKyat) * generateNumberFromEditText(binding.edtPriceA).toDouble()+otherReducedCosts
        } else {
            (goldKyat) * generateNumberFromEditText(binding.edtPriceA).toDouble()
        }

        binding.edtPriceB.setText(priceB.toInt().toString())
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
        binding.edtPriceA.setText(priceA.toInt().toString())
    }

    fun calculatePriceD(hasotherReducedCost: Boolean) {
        val priceB = if (binding.edtReducedPriceB.text.isNullOrEmpty()){
            generateNumberFromEditText(binding.edtPriceB).toDouble()
        }else{
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
            ((priceB - otherReducedCosts) / priceC)*128 - reducedYwae
        } else {
            ((priceB) / priceC)*128
        }

        val priceDKPY = getKPYFromYwae(priceD)
        binding.edtPriceDK.setText(priceDKPY[0].toInt().toString())
        binding.edtPriceDP.setText(priceDKPY[1].toInt().toString())
        binding.edtPriceDY.setText(String.format("%.2f", priceDKPY[2]))

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
        dialogBinding.tvSizeTag.text = requireContext().getString(R.string.small_size)

        dialogBinding.chipGp.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds[0] == dialogBinding.chipSmall.id) {
                viewModel.getRebuyItem("small")
                dialogBinding.tvSizeTag.text = requireContext().getString(R.string.small_size)
                dialogBinding.tvNameTag.text = ""
            } else {
                viewModel.getRebuyItem("large")
                dialogBinding.tvSizeTag.text = requireContext().getString(R.string.large_size)
                dialogBinding.tvNameTag.text = ""

            }
        }

        viewModel.rebuyItemeLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    dialogBinding.chipGpRebuyItems.removeAllViews()
                    for (item in it.data!!) {
                        val chip = requireContext().createChip(item.name)
                        chip.id = item.id.toInt()
                        chip.isCheckable = true
                        dialogBinding.chipGpRebuyItems.addView(chip)
                    }
                    dialogBinding.chipGpRebuyItems.setOnCheckedStateChangeListener { group, checkedIds ->
                        dialogBinding.chipGp.isEnabled = checkedIds.size == 0
                        var nameTag = ""
                        repeat(checkedIds.size) { index ->
                            val checkedItems =
                                it.data!!.find { it.id == checkedIds[index].toString() }
                            nameTag += checkedItems?.name.orEmpty() + " "
                        }
                        dialogBinding.tvNameTag.text = nameTag
                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        var count = 0

        dialogBinding.btnPlus.setOnClickListener {
            count++
            dialogBinding.tvNumber.text = count.toString()
            dialogBinding.tvCountTag.text = count.toString()
        }
        dialogBinding.btnMinus.setOnClickListener {
            count--
            dialogBinding.tvNumber.text = count.toString()
            dialogBinding.tvCountTag.text = count.toString()
        }


        dialogBinding.btnContinue.setOnClickListener {
            viewModel.nameTag =
                dialogBinding.tvNameTag.text.toString() + "," + dialogBinding.tvCountTag.text.toString()
            binding.tvNameTag.text = viewModel.nameTag
            viewModel.size = dialogBinding.tvSizeTag.text.toString().lowercase()
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
        val adapter = GemWeightRecyclerAdapter(viewModel) {id->
            viewModel.gemWeightCustomList.remove(viewModel.gemWeightCustomList.find { it.id == id  })

        }
        dialogGemWeightBinding.rvGemWeight.adapter = adapter

        var count = 0

        dialogGemWeightBinding.btnAdd.setOnClickListener {
            count++
            viewModel.gemWeightCustomList.add(
                GemWeightInStockFromHome(
                    count.toString(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
            adapter.submitList(viewModel.gemWeightCustomList)
            adapter.notifyDataSetChanged()
        }


        dialogGemWeightBinding.btnContinue.setOnClickListener {
            var totalWeightYwae = 0.0
            viewModel.gemWeightCustomList.forEach {
                totalWeightYwae += it.totalWeightKPY.toDouble()
            }
            val totalWeightKPY = getKPYFromYwae(totalWeightYwae)
            binding.edtGemWeightK.setText(totalWeightKPY[0].toInt().toString())
            binding.edtGemWeightP.setText(totalWeightKPY[1].toInt().toString())
            binding.edtGemWeightY.setText(totalWeightKPY[2].toString())
            viewModel.gemWeightCustomList.removeAll(viewModel.gemWeightCustomList)
            alertDialog.dismiss()
        }
        dialogGemWeightBinding.ivClose.setOnClickListener {
            viewModel.gemWeightCustomList.removeAll(viewModel.gemWeightCustomList)
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

            val result = generateNumberFromEditText(binding.edtGemDiamondValue).toDouble() - percentValue
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

            binding.edtReducedPriceB.setText(result.toInt().toString())
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
                viewModel.size,
                args.id
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
                viewModel.size,
                args.id
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
                viewModel.size,
                args.id
            )

        }
    }

    fun disableOtherCosts() {
        binding.textInputLayoutAddReducedK.isEnabled = false
        binding.textInputLayoutAddReducedP.isEnabled = false
        binding.textInputLayoutAddReducedY.isEnabled = false
        binding.textInputLayoutGemDiamondValue.isEnabled = false
        binding.textInputLayoutReducedGemDiamondValue.isEnabled = false
        binding.textInputLayoutFee.isEnabled = false
        binding.textInputLayoutPTclipValue.isEnabled = false


    }

    fun enableOtherCosts() {
        binding.textInputLayoutAddReducedK.isEnabled = true
        binding.textInputLayoutAddReducedP.isEnabled = true
        binding.textInputLayoutAddReducedY.isEnabled = true
        binding.textInputLayoutGemDiamondValue.isEnabled = true
        binding.textInputLayoutReducedGemDiamondValue.isEnabled = true
        binding.textInputLayoutFee.isEnabled = true
        binding.textInputLayoutPTclipValue.isEnabled = true
    }
    fun resetPricesValue(){
        binding.edtDecidedPawnPrice.setText("")
        binding.edtPawnPrice.setText("")
        binding.edtPriceA.setText("")
        binding.edtPriceB.setText("")
        binding.edtReducedPriceB.setText("")
        binding.edtPriceC.setText("")
        binding.edtPriceDK.setText("")
        binding.edtPriceDP.setText("")
        binding.edtPriceDY.setText("")
        binding.edtPriceE.setText("")
        binding.edtPriceFK.setText("")
        binding.edtPriceFP.setText("")
        binding.edtPriceFY.setText("")
    }
}
