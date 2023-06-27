package com.example.shwemisale.screen.sellModule.sellStart

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import com.epson.epos2.Epos2Exception
import com.epson.epos2.printer.Printer
import com.example.shwemi.util.Resource
import com.example.shwemi.util.convertToSqlDate
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.showDropdown
import com.example.shwemisale.CustomerListRecyclerAdapter
import com.example.shwemisale.R
import com.example.shwemisale.data_layers.domain.customers.asUiModel
import com.example.shwemisale.databinding.DialogIpAddressBinding
import com.example.shwemisale.databinding.FragmentStartSellBinding
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SellStartFragment : Fragment() {

    private lateinit var binding: FragmentStartSellBinding
    private val viewModel by viewModels<SellStartViewModel>()
    private lateinit var loading : AlertDialog
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var dialogIpAddressBinding:DialogIpAddressBinding
    private lateinit var barlauncer: Any

    private var townShipList = mutableListOf<String>()
    private var provinceList = mutableListOf<String>()

    @Inject
    lateinit var printer:Printer

    private var selectedProvinceId = ""
    private var selectedTownshipId = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentStartSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose Date of Birth")
            .setSelection(Calendar.getInstance().timeInMillis)
            .build()
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        val adapter = CustomerListRecyclerAdapter{
            view.findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToSellCustomerInfoFragment(it))

        }

        binding.includeSearchResult.rvCustomerList.adapter = adapter
        barlauncer = this.getBarLauncher(requireContext()) {
            viewModel.getCustomerInfo(it,null,null,null, null, null , null , null, null).observe(viewLifecycleOwner) {
                lifecycleScope.launch {
                    adapter.submitData(it.map { it.asUiModel() })
                }
            }
        }
        binding.ivScan.setOnClickListener {
            this.scanQrCode(requireContext(),barlauncer)
        }
        binding.ivScanner.setOnClickListener {
            this.scanQrCode(requireContext(),barlauncer)
        }
        networkcall()
        binding.btnNew.setOnClickListener { view:View->
//            throw RuntimeException("Test Crash")
            view.findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToSellCreateNewFragment())
//            val page: AbstractViewRenderer = object : AbstractViewRenderer(context, com.example.shwemisale.R.layout.fragment_pdf_test) {
//
//
//                override fun initView(view: View) {
//                val tv_hello = view.findViewById<View>(R.id.tv_hello) as TextView
//                tv_hello.text = "Hello My Name is Akp"
//                }
//            }
//
//// you can reuse the bitmap if you want
//
//// you can reuse the bitmap if you want
//            page.isReuseBitmap = true
//
//            val doc = PdfDocument(requireContext())
//
//// add as many pages as you have
//
//            val displayMetrics = DisplayMetrics()
//            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
//
//            val screenWidth = displayMetrics.widthPixels
//            val screenHeight = displayMetrics.heightPixels
//
//// add as many pages as you have
//            doc.addPage(page)
//
//            doc.setRenderWidth(screenWidth)
//            doc.setRenderHeight(screenHeight)
//            doc.orientation = PdfDocument.A4_MODE.LANDSCAPE
//            doc.setProgressTitle(R.string.app_name)
//            doc.setProgressMessage(R.string.loading)
//            doc.fileName = "test"
//            doc.setSaveDirectory(requireContext().getExternalFilesDir(null))
//            doc.setInflateOnMainThread(false)
//            doc.setListener(object : PdfDocument.Callback {
//                override fun onComplete(file: File?) {
//                    Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete")
//                }
//
//                override fun onError(e: Exception?) {
//                    Log.i(PdfDocument.TAG_PDF_MY_XML, "Error")
//                }
//            })
//
//            doc.createPdf(requireContext())
//
//            val builder = PdfDocument.Builder(requireContext())
//
//            builder.addPage(page)
//                .orientation(PdfDocument.A4_MODE.LANDSCAPE)
//                .progressMessage(R.string.loading)
//                .progressTitle(R.string.please_wait)
//                .renderWidth(screenWidth)
//                .renderHeight(screenHeight)
//                .saveDirectory(requireContext().getExternalFilesDir(null))
//                .filename("test")
//                .listener(object : PdfDocument.Callback {
//                    override fun onComplete(file: File) {
//                        Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete")
//                    }
//
//                    override fun onError(e: Exception) {
//                        Log.i(PdfDocument.TAG_PDF_MY_XML, "Error")
//                    }
//                })
//                .create()
//                .createPdf(requireContext())
        }


//        adapter.submitList(listOf(
//            CustomerListData("1","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
//            CustomerListData("2","Daw Than Than"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
//            CustomerListData("3","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
//            CustomerListData("4","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
//            CustomerListData("5","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","မရှိ"),
//            CustomerListData("6","ဒေါ်ကလျာနွဲ့မူရာခင်"," 09 420 12 3456 ","၁၄/ဟသတ(နိုင်)၁၂၃၄၅၆","၀၅-၁၂-၁၉၆၇","ဟင်္သာတ","ရှိ"),
//        ))
        binding.tvBirthDate.setOnClickListener {
            datePicker.show(childFragmentManager, "choose date")
        }
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            calendar.time = Date(it)
            val date = convertToSqlDate(calendar)
            binding.tvBirthDate.text = date
        }

        viewModel.profileLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    viewModel.saveStockFromHomeInfoFinal()
                    val navigationView =
                        requireActivity().findViewById<View>(com.example.shwemisale.R.id.navView) as NavigationView

                    val headerView = navigationView.getHeaderView(0)
                   headerView.findViewById<TextView>(com.example.shwemisale.R.id.tv_name).text = it.data
                    viewModel.getProvince()
                    if (printer.status.connection == Printer.FALSE){
                        showConnectPrinterDialog()
                    }
                    viewModel.resetProfileLiveData()
                }
                is Resource.Error->{
                    loading.dismiss()
                    if (it.message == "TOKEN_EXPIRED" || it.message == "TOKEN_NOT_PROVIDED" || it.message == "TOKEN_INVALID" ){
                        findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToLoginFragment())
                    }else{
                        findNavController().navigate(SellStartFragmentDirections.actionSellStartFragmentToLoginFragment())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }

                    viewModel.resetProfileLiveData()
                }
                else -> {}
            }
        }


        viewModel.provinceLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    provinceList = it.data!!.map { it.name }.filterNotNull().toMutableList()
                    provinceList.add("None")
                    val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,provinceList)
                    binding.actProvince.addTextChangedListener {editable->
                        if (editable.toString() == "None"){
                            selectedProvinceId = ""
                            viewModel.getTownShip(selectedProvinceId)

                        }else{
                            selectedProvinceId = it.data!!.find {
                                it.name==editable.toString()
                            }?.id.toString()
                            viewModel.getTownShip(selectedProvinceId)
                        }
                    }
                    binding.actProvince.setAdapter(arrayAdapter)
                    binding.actProvince.setText(provinceList[0],false)
                    binding.actProvince.setOnClickListener {
                        binding.actProvince.showDropdown(arrayAdapter)
                    }
                    viewModel.resetProvinceLiveData()

                }
                is Resource.Error->{
                    loading.dismiss()

                    viewModel.resetProvinceLiveData()
                }
                else -> {}
            }
        }


        viewModel.townShipLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    if (selectedProvinceId == ""){
                        townShipList = mutableListOf("None")
                        townShipList.toSet()
                    }else{
                        townShipList = it.data!!.map { it.name }.filterNotNull().toMutableList()
                        townShipList.add("None")
                    }
                    val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_text,townShipList)
                    binding.actTownship.addTextChangedListener {editable->
                        if (editable.toString() == "None"){
                            selectedTownshipId = ""
                        }else{
                            selectedTownshipId = it.data!!.find {
                                it.name==binding.actTownship.text.toString()
                            }?.id.toString()
                        }

                    }
                    binding.actTownship.setAdapter(arrayAdapter)
                    binding.actTownship.setText(if (townShipList.isEmpty()) "" else townShipList[0],false)
                    binding.actTownship.setOnClickListener {
                        binding.actTownship.showDropdown(arrayAdapter)
                    }
                    viewModel.resetTownshipLiveData()

                }
                is Resource.Error->{
                    loading.dismiss()

                    viewModel.resetTownshipLiveData()
                }
                else -> {}
            }
        }



        binding.imageBtnSearch.setOnClickListener {
            val name =
                if (binding.editName.text.isNullOrEmpty()) null else binding.editName.text.toString()
            val phoneNumber =
                if (binding.editPhNumber.text.isNullOrEmpty()) null else binding.editPhNumber.text.toString()
            val birthday =
                if (binding.tvBirthDate.text.isNullOrEmpty() || binding.tvBirthDate.text == "မွေးနေ့") null else binding.tvBirthDate.text.toString()
            val nrc =
                if (binding.editNRC.text.isNullOrEmpty()) null else binding.editNRC.text.toString()

            viewModel.getCustomerInfo(
                null,
                name,
                phoneNumber,
                birthday,
                null,
                selectedProvinceId,
                selectedTownshipId,
                null,
                nrc
            ).observe(viewLifecycleOwner) {
                lifecycleScope.launch {
                    adapter.submitData(it.map { it.asUiModel() })
                }
            }
        }
    }

    fun networkcall(){
        viewModel.getProfile()
    }

    fun showConnectPrinterDialog(){
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater: LayoutInflater = LayoutInflater.from(builder.context)
        dialogIpAddressBinding = DialogIpAddressBinding.inflate(
            inflater, ConstraintLayout(builder.context), false
        )
        builder.setView(dialogIpAddressBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogIpAddressBinding.btnConnectToPrinter.setOnClickListener {
            try {
                printer.connect("TCP:"+dialogIpAddressBinding.edtIpAddress.text.toString(), Printer.PARAM_DEFAULT)
            } catch (e: Epos2Exception) {
                showErrorDialog(e.message ?: "UnknownError")

            }
            alertDialog.dismiss()
        }
        alertDialog.show()
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









