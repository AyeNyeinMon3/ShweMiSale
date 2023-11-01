package com.shwemigoldshop.shwemisale.screen.sellModule.openVoucher.withValue

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shwemigoldshop.shwemisale.printerHelper.AkpDownloader
import com.shwemigoldshop.shwemisale.util.Resource
import com.shwemigoldshop.shwemisale.util.generateNumberFromEditText
import com.shwemigoldshop.shwemisale.util.getAlertDialog
import com.shwemigoldshop.shwemisale.util.showSuccessDialog
import com.shwemigoldshop.shwemisale.databinding.FragmentWithValueBinding
import com.shwemigoldshop.shwemisale.printerHelper.printPdf
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody

@AndroidEntryPoint
class WithValueFragment : Fragment() {
    lateinit var binding: FragmentWithValueBinding
    private val viewModel by viewModels<WithValueViewModel>()
    private val args by navArgs<com.shwemigoldshop.shwemisale.screen.sellModule.openVoucher.withValue.WithValueFragmentArgs>()
    private lateinit var loading: AlertDialog
    private var redeemMoney = 0
    private val downloader by lazy { AkpDownloader(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.updateEvalue("0")
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentWithValueBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        bindPassedData()
        viewModel.getStockFromHomeList()

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

                    binding.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
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
//                    binding.edtReducedPay.setText(
//                       ( generateNumberFromEditText(binding.edtReducedPay).toInt() + it.data.orEmpty()
//                            .let { if (it.isEmpty()) 0 else it.toInt() }).toString())
                    redeemMoney = (it.data ?: "0").toInt()
                    binding.tvRedeemMoney.text = redeemMoney.toString()

                }

                is Resource.Error -> {

                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
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
                    findNavController().popBackStack()

                }
                is Resource.Error -> {
                    loading.dismiss()
                    findNavController().popBackStack()
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

                }

                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
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

        viewModel.submitWithValueLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }

                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Press Ok To Download And Print!") {
                        viewModel.getPdf(it.data.orEmpty())
                    }

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
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(
                com.shwemigoldshop.shwemisale.screen.sellModule.openVoucher.withValue.WithValueFragmentDirections.actionGlobalGoldFromHomeFragment(
                    "Global",
                    null
                )
            )
        }

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


            viewModel.submitWithValue(
                productIdList,
                viewModel.getCustomerId(),
                paid_amount,
                reduced_cost,
                binding.edtRedeemPoint.text.toString(),
                args.oldVoucherCode,
                MultipartBody.Part.createFormData(
                    "old_voucher_paid_amount",
                    args.oldVoucherPaidAmount.toString()
                )
            )
        }
        binding.btnCalculate.setOnClickListener {
            viewModel.getRedeemMoney(generateNumberFromEditText(binding.edtRedeemPoint))

            //ကျသင့်ငွေ= အထည်တန်ဖိုးပေါင်း - အိမ်ပါရွှေတန်ဖိုး - ဘောင်ချာဟောင်းပေးသွင်းငွေ - လျော့ပေးငွေ ဖြစ်ရန်
            val chargeAmount = generateNumberFromEditText(binding.edtTotalValue).toInt() -
                    generateNumberFromEditText(binding.edtGoldFromHomeValue).toInt() - generateNumberFromEditText(
                binding.edtOldVoucherPayment
            ).toInt() -
                    generateNumberFromEditText(binding.edtReducedPay).toInt() - redeemMoney
            binding.edtCharge.setText(chargeAmount.toString())

            val remainAmount =
                generateNumberFromEditText(binding.edtCharge).toInt() - generateNumberFromEditText(
                    binding.edtDeposit
                ).toInt()
            binding.edtBalance.setText(remainAmount.toString())
        }


    }

    fun bindPassedData() {
        viewModel.getGoldPrice(args.scannedProducts.map { it.id })
        var totalProductsCost = 0
        var charge = 0

        args.scannedProducts.forEach {
            totalProductsCost += it.cost.toInt()
            charge += it.pt_and_clip_cost.toInt()
            charge += it.maintenance_cost.toInt()
            charge += it.cost.toInt()
            charge -= it.promotion_discount.toInt()
        }
        var oldVoucherPaidAmount = args.oldVoucherPaidAmount
        binding.edtTotalValue.setText(totalProductsCost.toString())
        binding.edtOldVoucherPayment.setText(oldVoucherPaidAmount.toString())
//        binding.edtCharge.setText(charge.toString())

    }

    override fun onResume() {
        super.onResume()
        binding.edtGoldFromHomeValue.setText(viewModel.getTotalCVoucherBuyingPrice())
    }

}