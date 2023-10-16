package com.example.shwemisale.screen.goldFromHome.bucket.addOldStockToBucket

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.compressImage
import com.example.shwemi.util.generateNumberFromEditText
import com.example.shwemi.util.getRealPathFromUri
import com.example.shwemi.util.loadImageWithGlide
import com.example.shwemi.util.showUploadImageDialog
import com.example.shwemisale.R
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.dto.goldFromHome.Image
import com.example.shwemisale.data_layers.ui_models.OldStockBucketUiModel
import com.example.shwemisale.databinding.FragmentOldStockAddToBucketBinding
import com.example.shwemisale.screen.goldFromHome.bucket.BucketShareViewModel
import com.example.shwemisale.screen.goldFromHome.bucket.RemoveImageBottomSheetFragment
import com.example.shwemisale.screen.goldFromHome.bucket.RemoveImageSelectionListener
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddOldStockToBucketFragment : Fragment(), RemoveImageSelectionListener {
    private lateinit var binding: FragmentOldStockAddToBucketBinding
    private val viewModel by viewModels<AddOldStockToBucketViewModel>()
    private val shareViewModel by activityViewModels<BucketShareViewModel>()
    private lateinit var launchChooseImage: ActivityResultLauncher<Intent>
    private lateinit var removeImageBottomSheetFragment: RemoveImageBottomSheetFragment
    private var imageUrl = ""

    //    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentOldStockAddToBucketBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        storagePermissionLauncher = registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted ->
//            if (isGranted) {
//                requireContext().showUploadImageDialog(
//                    //onGalleryClick
//                    {
//                        chooseImage()
//                    },
//                    //onCameraClick
//                    {
//                        dispatchTakePictureIntent()
//                    })
//            }
//        }
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
                        binding.includeOldStockAddItemPhoto.ivThreeDotsMenu.isVisible = true
                        binding.includeOldStockAddItemPhoto.ivUploadImage.loadImageWithGlide(path)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.includeOldStockAddItemPhoto.radioGpCalculation.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == binding.includeOldStockAddItemPhoto.radioGm.id) {
                binding.includeOldStockAddItemPhoto.tilWeightGm.isVisible = true
                binding.includeOldStockAddItemPhoto.tilWeightKyat.isVisible = false
                binding.includeOldStockAddItemPhoto.tilWeightPae.isVisible = false
                binding.includeOldStockAddItemPhoto.tilWeightYwae.isVisible = false

            } else {
                binding.includeOldStockAddItemPhoto.tilWeightGm.isVisible = false
                binding.includeOldStockAddItemPhoto.tilWeightKyat.isVisible = true
                binding.includeOldStockAddItemPhoto.tilWeightPae.isVisible = true
                binding.includeOldStockAddItemPhoto.tilWeightYwae.isVisible = true

            }
        }
        binding.includeOldStockAddItemPhoto.radioGm.isChecked = true

        binding.includeOldStockAddItemPhoto.btnAdd.setOnClickListener {
            if (imageUrl.isEmpty()) {
                Toast.makeText(requireContext(), "Please Select an image first", Toast.LENGTH_LONG)
                    .show()
            } else {
                addOldStockToBucket()
            }
        }

        viewModel.imagePathLiveData.observe(viewLifecycleOwner) {
            binding.includeOldStockAddItemPhoto.ivThreeDotsMenu.isVisible = !it.isNullOrEmpty()
            binding.includeOldStockAddItemPhoto.ivUploadImage.loadImageWithGlide(it)
            imageUrl = it
        }
        binding.includeOldStockAddItemPhoto.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.includeOldStockAddItemPhoto.ivThreeDotsMenu.setOnClickListener {
            removeImageBottomSheetFragment = RemoveImageBottomSheetFragment()
            removeImageBottomSheetFragment.setonRemoveImageSelectionListener(this)
            removeImageBottomSheetFragment.show(
                childFragmentManager,
                "RemoveImageDialogFragment"
            )
        }

        binding.includeOldStockAddItemPhoto.ivUploadImage.setOnClickListener {
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

    override fun onRemoveImageSelected() {
        removeImageBottomSheetFragment.dismiss()
        viewModel.setImagePathLiveData("")
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

    fun addOldStockToBucket() {
        if (binding.includeOldStockAddItemPhoto.radioGm.isChecked) {
            val weight =
                generateNumberFromEditText(binding.includeOldStockAddItemPhoto.edtWeightGm)
            shareViewModel.addToOldStockBucket(
                StockFromHomeDomain(
                    id = null,
                    a_buying_price = "0",
                    b_voucher_buying_value = "0",
                    c_voucher_buying_price = "0",
                    calculated_buying_value = "0",
                    calculated_for_pawn = "0",
                    d_gold_weight_ywae = "0",
                    e_price_from_new_voucher = "0",
                    f_voucher_shown_gold_weight_ywae = "0.0",
                    gem_value = "0",
                    gem_weight_details_session_key = "",
                    gold_and_gem_weight_gm = weight,
                    gem_weight_ywae = "0.0",
                    gold_gem_weight_ywae = "0.0",
                    gold_weight_ywae = "0.0",
                    gq_in_carat = "0.0",
                    has_general_expenses = "0",
                    image = Image(null, null, imageUrl),
                    impurities_weight_ywae = "0.0",
                    maintenance_cost = "0",
                    price_for_pawn = "0",
                    pt_and_clip_cost = "0",
                    qty = "0",
                    rebuy_price = "0",
                    size = "",
                    stock_condition = "",
                    stock_name = "",
                    type = "",
                    wastage_ywae = "0.0",
                    rebuy_price_vertical_option = "",
                    productId = emptyList<String>(),
                    isEditable = true,
                    isChecked = false,
                    localId = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toInt(),
                    derived_gold_type_id = "",
                )
            )
        } else {

            shareViewModel.addToOldStockBucket(
                StockFromHomeDomain(
                    id = null,
                    a_buying_price = "0",
                    b_voucher_buying_value = "0",
                    c_voucher_buying_price = "0",
                    calculated_buying_value = "0",
                    calculated_for_pawn = "0",
                    d_gold_weight_ywae = "0",
                    e_price_from_new_voucher = "0",
                    f_voucher_shown_gold_weight_ywae = "0.0",
                    gem_value = "0",
                    gem_weight_details_session_key = "",
                    gold_and_gem_weight_gm = "0.0",
                    gem_weight_ywae = "0.0",
                    gold_gem_weight_ywae = getYwaeFromKPY(
                        generateNumberFromEditText(binding.includeOldStockAddItemPhoto.edtWeightKyat).toInt(),
                        generateNumberFromEditText(binding.includeOldStockAddItemPhoto.edtWeightPae).toInt(),
                        generateNumberFromEditText(binding.includeOldStockAddItemPhoto.edtWeightYwae).toDouble(),
                    ).toString(),
                    gold_weight_ywae = "0.0",
                    gq_in_carat = "0.0",
                    has_general_expenses = "0",
                    image = Image(null, null, imageUrl),
                    impurities_weight_ywae = "0.0",
                    maintenance_cost = "0",
                    price_for_pawn = "0",
                    pt_and_clip_cost = "0",
                    qty = "0",
                    rebuy_price = "0",
                    size = "",
                    stock_condition = "",
                    stock_name = "",
                    type = "",
                    wastage_ywae = "0.0",
                    rebuy_price_vertical_option = "",
                    productId = emptyList<String>(),
                    isEditable = true,
                    isChecked = false,
                    localId = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toInt(),
                    derived_gold_type_id = "",
                )
            )
        }
        findNavController().popBackStack()
    }
}