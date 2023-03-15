package com.example.shwemisale.screen.sellModule.sample

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.hideKeyboard
import com.example.shwemi.util.showSuccessDialog
import com.example.shwemisale.databinding.FragmentInventoryStockBinding
import com.example.shwemisale.qrscan.getBarLauncher
import com.example.shwemisale.qrscan.scanQrCode
import com.example.shwemisale.room_database.entity.asEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InventoryStockFragment : Fragment() {

    lateinit var binding: FragmentInventoryStockBinding
    private val viewModel by viewModels<InventoryStockViewModel>()
    private lateinit var loading: AlertDialog
    private lateinit var barlauncer: Any

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentInventoryStockBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = InventoryStockRecyclerAdapter {
            viewModel.removeSampleFromRoom(it)
        }
        loading = requireContext().getAlertDialog()
        binding.rvInventoryStockItem.adapter = adapter
        barlauncer = this.getBarLauncher(requireContext()) {
            binding.edtScanStockCode.setText(it)
            viewModel.getProductId(it)
        }
        binding.textInputLayoutScanStockCode.setEndIconOnClickListener {
            this.scanQrCode(requireContext(), barlauncer)
        }
        binding.edtScanStockCode.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    // Perform action on key press
                    viewModel.getProductId(binding.edtScanStockCode.text.toString())
                    hideKeyboard(activity, binding.edtScanStockCode)
                    return true
                }
                return false
            }
        })



        viewModel.productIdLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    viewModel.checkSample(it.data.orEmpty())
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.samplesFromRoom.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.btnTakeSample.setOnClickListener {
                if(list.filter { it.isNew }.isEmpty()){
                    requireContext().showSuccessDialog("Sample Taken") {
                        findNavController().popBackStack()
                    }
                }else{
                    viewModel.takeSample(list.filter { it.isNew })
                }
            }
        }
        viewModel.inventorySampleLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()

                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.saveSampleLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    loading.show()
                }
                is Resource.Success -> {
                    loading.dismiss()
                    requireContext().showSuccessDialog("Sample Taken") {
                        findNavController().popBackStack()
                    }
                }
                is Resource.Error -> {
                    loading.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}