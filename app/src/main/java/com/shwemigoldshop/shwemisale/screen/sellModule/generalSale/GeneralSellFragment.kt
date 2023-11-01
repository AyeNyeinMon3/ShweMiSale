package com.shwemigoldshop.shwemisale.screen.sellModule.generalSale

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.epson.epos2.Epos2Exception
import com.epson.epos2.printer.Printer
import com.shwemigoldshop.shwemisale.printerHelper.AkpDownloader
import com.shwemigoldshop.shwemisale.util.*
import com.shwemigoldshop.shwemisale.R
import com.shwemigoldshop.shwemisale.data_layers.domain.generalSale.GeneralSaleListDomain
import com.shwemigoldshop.shwemisale.data_layers.dto.GeneralSalePrintItem
import com.shwemigoldshop.shwemisale.databinding.DialogGeneralSellAddProductBinding
import com.shwemigoldshop.shwemisale.databinding.FragmentGeneralSellBinding
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.printerHelper.printPdf
import com.shwemigoldshop.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.shwemigoldshop.shwemisale.screen.goldFromHome.getYwaeFromKPY
import com.shwemigoldshop.shwemisale.util.calculateLineLength
import com.shwemigoldshop.shwemisale.util.combineListsGeneralSalePrint
import com.shwemigoldshop.shwemisale.util.generateNumberFromEditText
import com.shwemigoldshop.shwemisale.util.generateQRCode
import com.shwemigoldshop.shwemisale.util.getRoundDownForPrice
import com.shwemigoldshop.shwemisale.util.showDropdown
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.util.showGeneralSalePrintDialog
import com.shwemigoldshop.shwemisale.util.showSuccessDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeneralSellFragment : Fragment() {

    lateinit var binding: FragmentGeneralSellBinding
    lateinit var dialogAlertBinding: DialogGeneralSellAddProductBinding
    private val viewModel by viewModels<GeneralSaleViewModel>()
    lateinit var loading: AlertDialog
    lateinit var adapter: GeneralSellRecyclerAdapter
    var generalSaleItemId = ""
    private val downloader by lazy { AkpDownloader(requireContext()) }

    @Inject
    lateinit var mPrinter: Printer

    @Inject
    lateinit var localDatabase: LocalDatabase
    private val paperLength = calculateLineLength(80)
    private val magicSpace = "          "
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentGeneralSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        binding.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
        binding.btnCalculate.setOnClickListener {
            //           ကျန်ငွေ= ပိုလိုတန်ဖိုး- လျော့ပေးငွေ- ပေးသွင်းငွေ
            generateNumberFromEditText(binding.edtBalance)
            var poloValue = generateNumberFromEditText(binding.edtCharge).toInt() -
                    generateNumberFromEditText(binding.edtGoldFromHomeValue).toInt()

            var remainedMoney = poloValue -
                    generateNumberFromEditText(binding.edtDeposit).toInt() -
                    generateNumberFromEditText(binding.edtReducedPay).toInt()

            binding.edtPoloValue.setText(poloValue.toString())
            binding.edtBalance.setText(remainedMoney.toString())
        }
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(
                com.shwemigoldshop.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections.actionGlobalGoldFromHomeFragment(
                    "Global",
                    null
                )
            )
        }
        viewModel.getGeneralSaleReasons()
        viewModel.generalSaleReasonLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.generalSaleItemListForMap = it.data

                }

                is Resource.Error -> {
                    loading.dismiss()
                    if (it.message == "Session key not found!") {
                        adapter.submitList(emptyList())
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                }
            }
        }

        viewModel.logoutLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
//                    Toast.makeText(requireContext(),"log out successful", Toast.LENGTH_LONG).show()
                    findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections.actionGlobalLogout())
                }

                is Resource.Error -> {
                    loading.dismiss()
                    findNavController().navigate(com.shwemigoldshop.shwemisale.screen.sellModule.generalSale.GeneralSellFragmentDirections.actionGlobalLogout())

                }

                else -> {}
            }
        }
        viewModel.goldTypePriceLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.goldPrice = it.data?.find { it.name == "15P GQ" }?.price.orEmpty()
                    viewModel.getGeneralSaleItems()

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        viewModel.createGeneralItemLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Success") {
                        viewModel.getGeneralSaleItems()
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.updateGeneralSaleItemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Update Success") {
                        viewModel.getGeneralSaleItems()
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.deleteGeneralSaleItemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Delete Success") {
                        viewModel.getGeneralSaleItems()
                    }
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.submitGeneralSaleLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showGeneralSalePrintDialog(("Choose How To Print"),
                        {
                            //On Slip Print
                            viewModel.getGeneralSalePrintDto(it.data.orEmpty())
                        },
                        {
                            //On Pdf Print
                            viewModel.getPdf(it.data.orEmpty())
                        })
                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.pdfDownloadLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    printPdf(downloader.downloadFile(it.data.orEmpty()).orEmpty(), requireContext())
                    requireContext().showSuccessDialog("Press Ok When Printing is finished!") {
                        viewModel.logout()
                    }
                }

                is Resource.Error -> {

                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.generalsaleSlipPrintLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    if (mPrinter.status.connection == Printer.FALSE) {
                        try {
                            mPrinter.connect(
                                "TCP:" + localDatabase.getPrinterIp(),
                                Printer.PARAM_DEFAULT
                            )
                        } catch (e: Epos2Exception) {
                            //Cannot Connect to Printer IP : ${localDatabase.getPrinterIp()}
                            showErrorDialog(
                                e.message
                                    ?: "Cannot Connect to Printer IP : ${localDatabase.getPrinterIp()}"
                            )
                        }
                    } else if (mPrinter.status.connection == Printer.TRUE) {
                        Toast.makeText(
                            requireContext(),
                            "Printer Connect Success",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    val netAmount = (it.data?.total_cost?.toInt() ?: 0) - (it.data?.reduced_cost
                        ?: 0) - (it.data?.stocks_from_home_cost ?: 0)
                    printSample(
                        it.data?.sold_at.orEmpty(),
                        it.data?.code.orEmpty(),
                        it.data?.user?.address.orEmpty(),
                        it.data?.salesperson.orEmpty(),
                        it.data?.user?.name.orEmpty(),
                        (it.data?.total_cost ?: 0).toString(),
                        (it.data?.reduced_cost ?: 0).toString(),
                        (it.data?.stocks_from_home_cost ?: 0).toString(),
                        (it.data?.paid_amount ?: 0).toString(),
                        netAmount.toString(),
                        (it.data?.remaining_amount ?: 0).toString(),
                        it.data?.items.orEmpty()

                    )
                }

                is Resource.Error -> {

                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.generalSalesItemsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    adapter = GeneralSellRecyclerAdapter(
                        viewModel.goldPrice,
                        viewModel.generalSaleItemListForMap.orEmpty(),
                        {
                            showDialogAddProduct(it)
                        },
                        {
                            viewModel.deleteGeneralSaleItem(it)
                        })
                    binding.rvGeneralSell.adapter = adapter
                    var totalCost = 0
                    var idCount = 0
                    it.data!!.forEach {
                        it.id = idCount++
                        totalCost += (it.maintenance_cost.toInt() + (viewModel.goldPrice.toInt() * (((it.gold_weight_gm.toDouble() / 16.6) + (it.wastage_ywae.toDouble()) / 128)))).toInt()
                    }
                    adapter.submitList(it.data)

                    binding.edtCharge.setText(getRoundDownForPrice(totalCost).toString())
                }

                is Resource.Error -> {
                    loading.dismiss()
                    if (it.message == "Session key not found!") {
                        adapter = GeneralSellRecyclerAdapter(
                            viewModel.goldPrice,
                            viewModel.generalSaleItemListForMap.orEmpty(),
                            {
                                showDialogAddProduct(it)
                            },
                            {
                                viewModel.deleteGeneralSaleItem(it)
                            })
                        binding.rvGeneralSell.adapter = adapter
                        adapter.submitList(emptyList())
                        binding.edtCharge.text?.clear()
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                }

            }
        }
        binding.btnAdd.setOnClickListener {
            showDialogAddProduct(null)
        }
        binding.btnPrint.setOnClickListener {
            viewModel.submitGeneralSale(
                generateNumberFromEditText(binding.edtDeposit),
                generateNumberFromEditText(binding.edtReducedPay),
            )
        }
    }

    fun showDialogAddProduct(item: GeneralSaleListDomain?) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogAlertBinding = DialogGeneralSellAddProductBinding.inflate(
            inflater,
            ConstraintLayout(builder.context), false
        )
        builder.setView(dialogAlertBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        viewModel.getGeneralSaleReasons()
        viewModel.generalSaleReasonLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    viewModel.generalSaleItemListForMap = it.data
                    val reasonList = it.data.orEmpty().map { it.name.orEmpty() }
                    val reasonListAdapter =
                        ArrayAdapter(requireContext(), R.layout.item_drop_down_text, reasonList)
                    dialogAlertBinding.actReason.setAdapter(reasonListAdapter)
                    dialogAlertBinding.actReason.addTextChangedListener { editable ->
                        generalSaleItemId = it.data!!.find {
                            it.name == dialogAlertBinding.actReason.text.toString()
                        }?.id.toString()
                    }
                    dialogAlertBinding.actReason.setText(reasonList[0], false)
                    dialogAlertBinding.actReason.setOnClickListener {
                        dialogAlertBinding.actReason.showDropdown(reasonListAdapter)
                    }

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                }
            }
        }

        if (item != null) {
            dialogAlertBinding.actReason.setText(viewModel.generalSaleItemListForMap!!.find {
                it.id == item.general_sale_item_id
            }?.name.toString())

            dialogAlertBinding.edtGoldWeightGm.setText(item.gold_weight_gm)
            dialogAlertBinding.edtFee.setText(item.maintenance_cost)
            dialogAlertBinding.edtQuantity.setText(item.qty)
            val wastageKpy = getKPYFromYwae(item.wastage_ywae.toDouble())
            dialogAlertBinding.edtUnderCountK.setText(wastageKpy[0].toInt().toString())
            dialogAlertBinding.edtUnderCountP.setText(wastageKpy[1].toInt().toString())
            dialogAlertBinding.edtUnderCountY.setText(wastageKpy[2].let {
                String.format(
                    "%.2f",
                    it
                )
            })
            dialogAlertBinding.btnContinue.setOnClickListener {
                viewModel.updateGeneralSalesItems(
                    GeneralSaleListDomain(
                        item.id,
                        generalSaleItemId,
                        gold_weight_gm = generateNumberFromEditText(dialogAlertBinding.edtGoldWeightGm),
                        maintenance_cost = generateNumberFromEditText(dialogAlertBinding.edtFee),
                        qty = generateNumberFromEditText(dialogAlertBinding.edtQuantity),
                        getYwaeFromKPY(
                            generateNumberFromEditText(dialogAlertBinding.edtUnderCountK).toInt(),
                            generateNumberFromEditText(dialogAlertBinding.edtUnderCountP).toInt(),
                            generateNumberFromEditText(dialogAlertBinding.edtUnderCountY).toDouble()
                        ).toString()
                    )

                )
                alertDialog.dismiss()
            }
        } else {
            dialogAlertBinding.btnContinue.setOnClickListener {
                viewModel.createGeneralSaleItem(
                    generalSaleItemId,
                    gold_weight_gm = generateNumberFromEditText(dialogAlertBinding.edtGoldWeightGm),
                    maintenance_cost = generateNumberFromEditText(dialogAlertBinding.edtFee),
                    qty = generateNumberFromEditText(dialogAlertBinding.edtQuantity),
                    getYwaeFromKPY(
                        generateNumberFromEditText(dialogAlertBinding.edtUnderCountK).toInt(),
                        generateNumberFromEditText(dialogAlertBinding.edtUnderCountP).toInt(),
                        generateNumberFromEditText(dialogAlertBinding.edtUnderCountY).toDouble()
                    ).toString()
                )
                alertDialog.dismiss()
            }
        }

        dialogAlertBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        //alertDialog.window?.setLayout(750,900)


    }

    private fun printSample(
        date: String,
        voucherNumber: String,
        address: String,
        salesPerson: String,
        customerName: String,
        totalPrice: String,
        reducedAmount: String,
        oldStockAmount: String,
        paidAmount: String,
        netAmount: String,
        remainingAmount: String,
        generalSalePrintItemList: List<GeneralSalePrintItem>
    ) {
        try {
            // Start the print job
            mPrinter.beginTransaction()
            val lineLength = paperLength
            var numSpaces = 0
            var spaces = ""

            mPrinter.addText("----------------------------------------------\n")
            mPrinter.addTextAlign(Printer.ALIGN_CENTER)
            mPrinter.addText("General Sale Voucher\n")
            mPrinter.addText("----------------------------------------------\n")


            // Print an image
            mPrinter.addTextAlign(Printer.ALIGN_LEFT)
            mPrinter.addText("Date")

            numSpaces = lineLength - date.length - "Date".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$date\n")

            mPrinter.addText("Voucher Number")

            numSpaces =
                lineLength - voucherNumber.length - "Voucher Number".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$voucherNumber\n")


            val qrCodeContent = voucherNumber // Replace with your desired content

            val qrCodeWidth = 150 // Adjust the size based on your requirements

            val qrCodeHeight = 150
            val qrCodeBitmap = generateQRCode(qrCodeContent, qrCodeWidth, qrCodeHeight)
            mPrinter.addTextAlign(Printer.ALIGN_RIGHT)
            mPrinter.addImage(
                qrCodeBitmap,
                0,
                0,
                qrCodeWidth,
                qrCodeHeight,
                Printer.PARAM_DEFAULT,
                Printer.PARAM_DEFAULT,
                Printer.PARAM_DEFAULT,
                Printer.PARAM_DEFAULT.toDouble(),
                Printer.PARAM_DEFAULT
            );

            mPrinter.addText("\n")
            mPrinter.addTextAlign(Printer.ALIGN_LEFT)
            mPrinter.addText("Customer Name")

            numSpaces =
                lineLength - customerName.length - "Customer Name".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$customerName\n")

            mPrinter.addText("Address")
            numSpaces = lineLength - address.length - "Address".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$address\n")
            mPrinter.addText("------------------------------------------\n")


            val columnWidths = listOf(10, 10, 10, 10)
            printTableRowWithFixPosition(generalSalePrintItemList, columnWidths, mPrinter)


            mPrinter.addText("\n")
            mPrinter.addText("Total Price")
            numSpaces =
                lineLength - totalPrice.length - "Total Price Kyats".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$totalPrice Kyats\n")
            mPrinter.addText("----------------------------------------------\n")

            mPrinter.addText("\n")
            mPrinter.addText("Reduced Amount")
            numSpaces =
                lineLength - reducedAmount.length - "Reduced Amount Kyats".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$reducedAmount Kyats\n")

            mPrinter.addText("\n")
            mPrinter.addText("OldStock Amount")
            numSpaces =
                lineLength - oldStockAmount.length - "OldStock Amount Kyats".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$oldStockAmount Kyats\n")
            mPrinter.addText("----------------------------------------------\n")

            mPrinter.addText("\n")
            mPrinter.addText("Net Amount")
            numSpaces = lineLength - netAmount.length - "Net Amount Kyats".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$netAmount Kyats\n")

            mPrinter.addText("\n")
            mPrinter.addText("Customer Given Amount")
            numSpaces =
                lineLength - paidAmount.length - "Customer Given Amount Kyats".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$paidAmount Kyats\n")
            mPrinter.addText("----------------------------------------------\n")

            mPrinter.addText("\n")
            mPrinter.addText("Remaining Amount")
            numSpaces =
                lineLength - remainingAmount.length - "Remaining Amount Kyats".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$remainingAmount Kyats\n")


            mPrinter.addTextAlign(Printer.ALIGN_RIGHT)
            mPrinter.addText("SalePerson")
            numSpaces = lineLength - salesPerson.length - "SalePerson".length + magicSpace.length
            spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces$salesPerson\n")
            mPrinter.addFeedLine(9)
            mPrinter.addCut(Printer.CUT_FEED)
            // End the print
            mPrinter.sendData(Printer.PARAM_DEFAULT)

        } catch (e: Epos2Exception) {
            e.printStackTrace()
        } finally {
            try {
                // End the transaction
                mPrinter.endTransaction()
                viewModel.logout()
            } catch (e: Epos2Exception) {
                e.printStackTrace()
            }
        }
    }

    fun printTableRowWithFixPosition(
        columns: List<GeneralSalePrintItem>,
        columnWidths: List<Int>,
        printer: Printer
    ) {
        val headerList = listOf("Item/Service", "Qty", "Price")

        val combineList = combineListsGeneralSalePrint(headerList, columns)
        for (item in combineList) {
            mPrinter.addTextAlign(Printer.ALIGN_LEFT)
            mPrinter.addText(item.first + magicSpace)


            val numSpaces = paperLength - item.second.length - item.first.length
            val spaces = " ".repeat(numSpaces)
            mPrinter.addText("$spaces${item.second}")
            mPrinter.addText("\n")
            if (item.first == "Price") {
                mPrinter.addText("----------------------------------------------\n")
            }
        }

    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("OK") { dialog, which ->
            // do nothing
        }

        val dialog = builder.create()
        dialog.show()
    }
}