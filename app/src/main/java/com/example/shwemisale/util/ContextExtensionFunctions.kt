package com.example.shwemi.util

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shwemisale.R
import com.example.shwemisale.databinding.CustomChipItemBinding
import com.example.shwemisale.databinding.DialogUploadImageBinding
import com.example.shwemisale.databinding.GeneralSalePrintOptionDialogBinding
import com.example.shwemisale.databinding.LoadingDialogBinding
import com.example.shwemisale.databinding.ShwemiSuccessDialogBinding

import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.getAlertDialog(): AlertDialog {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater: LayoutInflater = LayoutInflater.from(builder.context)
    val alertDialogBinding = LoadingDialogBinding.inflate(
        inflater, ConstraintLayout(builder.context), false
    )
    builder.setView(alertDialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)
    return alertDialog
}

fun Context.createChip(label: String): Chip {
    val inflater: LayoutInflater = LayoutInflater.from(this)
    val chip = CustomChipItemBinding.inflate(inflater).root
    chip.text = label
    return chip
}

fun ImageView.loadImageWithGlide(url:String?){
    url?.let {
        Glide.with(this).asBitmap().load(it).apply(
            RequestOptions.placeholderOf(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
        ).into(this)
    }
}

fun Context.showSuccessDialog( message: String, onClick: () -> Unit) {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater: LayoutInflater = LayoutInflater.from(builder.context)
    val alertDialogBinding = ShwemiSuccessDialogBinding.inflate(
        inflater, ConstraintLayout(builder.context), false
    )
    builder.setView(alertDialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialogBinding.tvMessage.text = message
    alertDialogBinding.btnOk.setOnClickListener {
        alertDialog.dismiss()
        onClick()
    }
    alertDialog.show()
}
fun Context.showUploadImageDialog( onGalleryClick:()->Unit, onCameraClick: () -> Unit) {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater: LayoutInflater = LayoutInflater.from(builder.context)
    val alertDialogBinding = DialogUploadImageBinding.inflate(
        inflater, ConstraintLayout(builder.context), false
    )
    builder.setView(alertDialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialogBinding.ivCross.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialogBinding.mcvChooseFromGallery.setOnClickListener {
        onGalleryClick()
        alertDialog.dismiss()

    }
    alertDialogBinding.mcvTakePhoto.setOnClickListener {
        onCameraClick()
        alertDialog.dismiss()
    }

    alertDialog.show()
}
fun Context.showGeneralSalePrintDialog( message: String, onSlipPrint: () -> Unit , onPdfPrint:()->Unit) {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater: LayoutInflater = LayoutInflater.from(builder.context)
    val alertDialogBinding = GeneralSalePrintOptionDialogBinding.inflate(
        inflater, ConstraintLayout(builder.context), false
    )
    builder.setView(alertDialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialogBinding.tvMessage.text = message
    alertDialogBinding.btnSlipPrint.setOnClickListener {
        alertDialog.dismiss()
        onSlipPrint()
    }
    alertDialogBinding.btnPdfPrint.setOnClickListener {
        alertDialog.dismiss()
        onPdfPrint()
    }
    alertDialog.show()
}

