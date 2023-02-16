package com.example.shwemisale.screen.goldFromHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemisale.data_layers.domain.goldFromHome.asUiModel
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel
import com.example.shwemisale.databinding.DialogChangeFeatureBinding
import com.example.shwemisale.databinding.DialogSellTypeBinding
import com.example.shwemisale.databinding.DialogStockCheckBinding
import com.example.shwemisale.databinding.FragmentGoldFromHomeSellBinding
import com.example.shwemisale.screen.sellModule.GoldFromHomeData
import com.example.shwemisale.screen.sellModule.GoldFromHomeRecyclerAdapter
import com.example.shwemisale.screen.sellModule.StockCheckRecyclerAdapter
import com.example.shwemisale.screen.sellModule.sellStart.SellStartViewModel

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellGoldFromHomeFragment : Fragment() {

    lateinit var binding: FragmentGoldFromHomeSellBinding
    lateinit var alertDialogBinding: DialogStockCheckBinding
    lateinit var dialogBinding: DialogChangeFeatureBinding
    lateinit var dialogSellTypeBinding: DialogSellTypeBinding
    private val viewModel by viewModels<GoldFromHomeViewModel>()
    private lateinit var loading: AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentGoldFromHomeSellBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
//            binding.btnSkip.isVisible = isChecked
//        }
        loading = requireContext().getAlertDialog()
        binding.btnSelect.setOnClickListener {
            viewModel.getStockWeightByVoucher(binding.edtScanVoucher.text.toString())
        }
        val adapter = GoldFromHomeRecyclerAdapter({
            findNavController().navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToSellResellStockInfoAddedFragment(it))
        },{
            viewModel.deleteStock(it)
        })
        binding.rvGoldFromHome.adapter = adapter

        if (viewModel.stockFromHomeListInAppDatabase.isNotEmpty()){
            adapter.submitList(viewModel.stockFromHomeListInAppDatabase)
        }
        viewModel.stockWeightByVoucherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    showStockCheckDialog(it.data!!)
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.stockInfoByVoucherLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    adapter.submitList(it.data!!)

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()

                }
            }
        }

        binding.radioGpOther.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                binding.radioSale.id -> {
                    binding.btnSkip.isVisible = true
                    binding.btnContinue.isVisible = true
                    binding.btnPrint.isVisible = false
                    binding.btnContinue.setOnClickListener {
                        showSellTypeDialog()
                    }
                }
                binding.radioPawn.id -> {
                    binding.btnSkip.isVisible = false
                    binding.btnContinue.isVisible = true
                    binding.btnPrint.isVisible = false
                    binding.btnContinue.setOnClickListener {
                        view.findNavController()
                            .navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToCreatePawnFragment())
                    }
                }
                binding.radioBuy.id -> {
                    binding.btnSkip.isVisible = false
                    binding.btnContinue.isVisible = false
                    binding.btnPrint.isVisible = true
                }
            }
        }

        binding.btnSkip.setOnClickListener {
            showSellTypeDialog()
        }



        binding.btnAdd.setOnClickListener {
            view.findNavController()
                .navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToSellResellStockInfoAddedFragment(null))
        }
    }


    fun showStockCheckDialog(list: List<StockWeightByVoucherUiModel>) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        alertDialogBinding =
            DialogStockCheckBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(alertDialogBinding.root)
        val alertDialog = builder.create()
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        alertDialog.setCancelable(false)
        alertDialogBinding.tvVoucherNumber.text = binding.edtScanVoucher.text.toString()
        val adapter = StockCheckRecyclerAdapter()
        alertDialogBinding.rvStockCheck.adapter = adapter
        adapter.submitList(list)
        alertDialogBinding.btnContinue.setOnClickListener {
            binding.rvGoldFromHome.visibility = View.VISIBLE
            alertDialog.dismiss()
        }
        alertDialogBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }


        alertDialogBinding.btnContinue.setOnClickListener {
            val productIdList = viewModel.stockWeightByVoucherLiveData.value!!.data!!.filter { it.isChecked }.map { it.id }
            viewModel.getStockInfoByVoucher(binding.edtScanVoucher.text.toString(),productIdList)
            viewModel.resetstockWeightByVoucherLiveData()
            alertDialog.dismiss()
        }

        alertDialog.show()


    }

    fun showChangeFeatureDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogBinding =
            DialogChangeFeatureBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
//        dialogBinding.btnPawn.setOnClickListener {
//            view?.findNavController()?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToPawnStartFragment())
//            alertDialog.dismiss()
//        }
//        dialogBinding.btnBuy.setOnClickListener {
//            view?.findNavController()?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToBuyStartFragment())
//            alertDialog.dismiss()
//        }

        alertDialog.show()


    }

    fun showSellTypeDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = LayoutInflater.from(builder.context)
        dialogSellTypeBinding =
            DialogSellTypeBinding.inflate(inflater, ConstraintLayout(builder.context), false)
        builder.setView(dialogSellTypeBinding.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        dialogSellTypeBinding.ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()


        dialogSellTypeBinding.btnNormalSell.setOnClickListener {
            view?.findNavController()
                ?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToScanStockFragment())
            alertDialog.dismiss()
        }
        dialogSellTypeBinding.btnReceiveNewOrder.setOnClickListener {
            view?.findNavController()
                ?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToReceiveNewOrderFragment())
            alertDialog.dismiss()
        }
        dialogSellTypeBinding.btnAkoukSell.setOnClickListener {
            view?.findNavController()
                ?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToAkoukSellFragment())
            alertDialog.dismiss()
        }

        dialogSellTypeBinding.btnGeneralSell.setOnClickListener {
            view?.findNavController()
                ?.navigate(SellGoldFromHomeFragmentDirections.actionSellGoldFromHomeFragmentToGeneralSellFragment())
            alertDialog.dismiss()
        }

    }

}
