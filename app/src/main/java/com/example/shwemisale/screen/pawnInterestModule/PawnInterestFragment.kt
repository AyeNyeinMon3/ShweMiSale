package com.example.shwemisale.screen.pawnInterestModule

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.hideKeyboard
import com.example.shwemisale.databinding.FragmentPawnInterestBinding
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PawnInterestFragment:Fragment() {

    lateinit var binding:FragmentPawnInterestBinding
    private val viewModel by viewModels<PawnInterestViewModel>()
    private lateinit var barlauncer: Any
    private lateinit var loading :AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentPawnInterestBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        barlauncer = this.getBarLauncher(requireContext()) {
            binding.edtScanVoucher.setText(it)
            viewModel.pawnVoucherScan(it)
        }
        binding.textInputLayoutScanVoucher.setEndIconOnClickListener {
            this.scanQrCode(requireContext(), barlauncer)
        }
        binding.edtScanVoucher.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    // Perform action on key press
                    viewModel.pawnVoucherScan(binding.edtScanVoucher.text.toString())
                    hideKeyboard(activity, binding.edtScanVoucher)
                    return true
                }
                return false
            }
        })
        var checkedAction = ""
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            binding.radioGroup2.clearCheck()
            when(checkedId){
                binding.radioPreInputOutput.id->{
                    binding.includePaymentBox.tilOne.isVisible = true
                    binding.includePaymentBox.tvOne.isVisible = true
                    binding.includePaymentBox.tvOne.text = "အရင်းကြိုသွင်းငွေ"

                    binding.includePaymentBox.tilTwo.isVisible = false
                    binding.includePaymentBox.tilThree.isVisible = false

                    binding.includePaymentBox.tvTwo.isVisible = false
                    binding.includePaymentBox.tvThree.isVisible = false

                    binding.includePaymentBox.btnEdit.isVisible = false

                    binding.includePaymentBox.btnClick.text = "ကြိုသွင်း/ထုတ်"
                    checkedAction = "ကြိုသွင်း/ထုတ်"
                }
                binding.radioPreInterestPay.id->{
                    binding.includePaymentBox.tilOne.isVisible = true
                    binding.includePaymentBox.tvOne.isVisible = true
                    binding.includePaymentBox.tvOne.text = "လထည့်ရန်"

                    binding.includePaymentBox.tvThree.isVisible = true
                    binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                    binding.includePaymentBox.tilThree.isVisible = true

                    binding.includePaymentBox.tilTwo.isVisible = false
                    binding.includePaymentBox.tvTwo.isVisible = false

                    binding.includePaymentBox.btnEdit.isVisible = false
                    binding.includePaymentBox.btnClick.text = "ကြိုတိုးရှင်း"
                    checkedAction = "ကြိုတိုးရှင်း"

                }
                binding.radioOnlyInterest.id->{
                    binding.includePaymentBox.tilOne.isVisible = true
                    binding.includePaymentBox.tvOne.isVisible = true
                    binding.includePaymentBox.tvOne.text = "တိုးယူငွေ"

                    binding.includePaymentBox.tvThree.isVisible = false
                    binding.includePaymentBox.tilThree.isVisible = false

                    binding.includePaymentBox.tilTwo.isVisible = false
                    binding.includePaymentBox.tvTwo.isVisible = false

                    binding.includePaymentBox.btnEdit.isVisible = true
                    binding.includePaymentBox.btnClick.text = "တိုးယူသက်သက်"
                    checkedAction = "တိုးယူသက်သက်"

                }
                binding.radioInterestPay.id->{
                    binding.includePaymentBox.tilOne.isVisible = true
                    binding.includePaymentBox.tvOne.isVisible = true
                    binding.includePaymentBox.tvOne.text = "အတိုးရက်"

                    binding.includePaymentBox.tvThree.isVisible = true
                    binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                    binding.includePaymentBox.tilThree.isVisible = true

                    binding.includePaymentBox.tilTwo.isVisible = false
                    binding.includePaymentBox.tvTwo.isVisible = false

                    binding.includePaymentBox.btnEdit.isVisible = false
                    binding.includePaymentBox.btnClick.text = "အတိုးရှင်း"
                    checkedAction = "အတိုးရှင်း"
                }
                binding.radioInvestmentInterestPay.id->{
                    binding.includePaymentBox.tilOne.isVisible = true
                    binding.includePaymentBox.tvOne.isVisible = true
                    binding.includePaymentBox.tvOne.text = "အတိုးရက်"

                    binding.includePaymentBox.tvTwo.isVisible = true
                    binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                    binding.includePaymentBox.tilTwo.isVisible = true

                    binding.includePaymentBox.tilThree.isVisible = true
                    binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                    binding.includePaymentBox.tvThree.isVisible = true

                    binding.includePaymentBox.btnEdit.isVisible = false
                    binding.includePaymentBox.btnClick.text = "အရင်းသွင်းအတိုးရှင်း"
                    checkedAction = "အရင်းသွင်းအတိုးရှင်း"
                }
            }
        }
        binding.radioGroup2.setOnCheckedChangeListener { radioGroup, checkedId ->
            binding.radioGroup.clearCheck()
            when(checkedId){
                binding.radioSelectInvestment.id->{
                    binding.includePaymentBox.tilOne.isVisible = true
                    binding.includePaymentBox.tvOne.isVisible = true
                    binding.includePaymentBox.tvOne.text = "အတိုးရက်"

                    binding.includePaymentBox.tvTwo.isVisible = true
                    binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                    binding.includePaymentBox.tilTwo.isVisible = true

                    binding.includePaymentBox.tilThree.isVisible = true
                    binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                    binding.includePaymentBox.tvThree.isVisible = true

                    binding.includePaymentBox.btnEdit.isVisible = true

                    binding.includePaymentBox.btnClick.text = "အရင်းသွင်း ခွဲရွေး"
                    checkedAction = "အရင်းသွင်း ခွဲရွေး"
                }
                binding.radioClearingInterest.id->{
                    binding.includePaymentBox.tilOne.isVisible = true
                    binding.includePaymentBox.tvOne.isVisible = true
                    binding.includePaymentBox.tvOne.text = "အတိုးရက်"

                    binding.includePaymentBox.tvTwo.isVisible = true
                    binding.includePaymentBox.tvTwo.text = "တိုးယူငွေ"
                    binding.includePaymentBox.tilTwo.isVisible = true

                    binding.includePaymentBox.tilThree.isVisible = true
                    binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                    binding.includePaymentBox.tvThree.isVisible = true

                    binding.includePaymentBox.btnEdit.isVisible = true

                    binding.includePaymentBox.btnClick.text = "တိုးယူ အတိုးရှင်း"
                    checkedAction = "တိုးယူ အတိုးရှင်း"

                }
                binding.radioChoose.id->{
                    binding.includePaymentBox.tilOne.isVisible = true
                    binding.includePaymentBox.tvOne.isVisible = true
                    binding.includePaymentBox.tvOne.text = "အတိုးရက်"

                    binding.includePaymentBox.tvTwo.isVisible = true
                    binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                    binding.includePaymentBox.tilTwo.isVisible = true

                    binding.includePaymentBox.tilThree.isVisible = true
                    binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                    binding.includePaymentBox.tvThree.isVisible = true

                    binding.includePaymentBox.btnEdit.isVisible = false

                    binding.includePaymentBox.btnClick.text = "ရွေးယူ"
                    checkedAction = "ရွေးယူ"

                }
                binding.radioInterestPay.id->{
                    binding.includePaymentBox.tilOne.isVisible = true
                    binding.includePaymentBox.tvOne.isVisible = true
                    binding.includePaymentBox.tvOne.text = "အတိုးရက်"

                    binding.includePaymentBox.tvTwo.isVisible = true
                    binding.includePaymentBox.tvTwo.text = "အရင်းသွင်းငွေ"
                    binding.includePaymentBox.tilTwo.isVisible = true

                    binding.includePaymentBox.tilThree.isVisible = true
                    binding.includePaymentBox.tvThree.text = "အတိုးကျသင့်ငွေ"
                    binding.includePaymentBox.tvThree.isVisible = true

                    binding.includePaymentBox.btnEdit.isVisible = true

                    binding.includePaymentBox.btnClick.text = "ရောင်းချ"
                    checkedAction = "ရောင်းချ"
                }
            }
        }

        binding.includePaymentBox.btnClick.setOnClickListener {
            when(checkedAction){
                "ကြိုသွင်း/ထုတ်"->{

                }
                "ကြိုတိုးရှင်း"->{

                }
                "တိုးယူသက်သက်"->{

                }
                "အတိုးရှင်း"->{

                }
                "အရင်းသွင်းအတိုးရှင်း"->{

                }
                "အရင်းသွင်း ခွဲရွေး"->{

                }
                "တိုးယူ အတိုးရှင်း"->{

                }
                "ရွေးယူ"->{

                }
                "ရောင်းချ"->{

                }
            }
        }


        viewModel.getPawnVoucherScanLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()

                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                }
            }
        }

    }

}