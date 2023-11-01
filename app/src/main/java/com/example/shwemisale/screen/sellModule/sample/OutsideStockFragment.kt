package com.example.shwemisale.screen.sellModule.sample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.*
import com.example.shwemisale.databinding.FragmentOutsideStockBinding
import com.example.shwemisale.util.compressImage
import com.example.shwemisale.util.getRealPathFromUri
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class OutsideStockFragment:Fragment() {

    lateinit var binding: FragmentOutsideStockBinding
    private val viewModel by viewModels<OutsideStockViewModel>()
    private lateinit var loading: AlertDialog
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentOutsideStockBinding.inflate(inflater).also {
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
                        viewModel.selectedImgUri = File(path)
                        binding.ivOutsideItem.loadImageWithGlide(path)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireContext().getAlertDialog()
        val adapter = OutsideStockRecyclerAdapter{
            viewModel.removeSampleFromRoom(it)
        }
        binding.rvOutsideStockItem.adapter = adapter
        binding.ivOutsideItem.setOnClickListener {
            if (isExternalStoragePermissionGranted().not()) {
                requestStoragePermission()
            } else {
                chooseImage()
            }
        }
        binding.btnTake.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSelect.setOnClickListener {
            val name = binding.edtOrderItemName.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val weight = binding.edtWeight.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val specification = binding.edtNote.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            var photo: MultipartBody.Part? = null
            var imageRequestBody: RequestBody? = null
            viewModel.selectedImgUri?.let {
                imageRequestBody = compressImage(it.path)

                photo = MultipartBody.Part.createFormData(
                    "image",
                    it.name,
                    imageRequestBody!!
                )
            }
            if (photo == null){
                Toast.makeText(requireContext(),"Please Enter image",Toast.LENGTH_LONG).show()
            }else{
                viewModel.saveOutside(
                    name,weight,specification,photo!!
                )
            }
        }
        viewModel.samplesFromRoom.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.filter { it.isInventory.not() })
        }

        viewModel.outsideSampleLiveData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()

                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun requestStoragePermission() {
        storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
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
}