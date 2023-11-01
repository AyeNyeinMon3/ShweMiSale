package com.shwemigoldshop.shwemisale.util

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shwemigoldshop.shwemisale.R
import com.shwemigoldshop.shwemisale.databinding.AlertDialogABuyingPriceChangeBinding
import com.shwemigoldshop.shwemisale.databinding.AlertDialogBBuyingPriceChangeBinding
import com.shwemigoldshop.shwemisale.databinding.AlertDialogCBuyingPriceChangeBinding
import com.shwemigoldshop.shwemisale.databinding.AlertDialogFWieghtChangeBinding
import com.shwemigoldshop.shwemisale.databinding.AlertDialogGoldCaratChangeBinding
import com.shwemigoldshop.shwemisale.databinding.CustomChipItemBinding
import com.shwemigoldshop.shwemisale.databinding.DialogUploadImageBinding
import com.shwemigoldshop.shwemisale.databinding.GeneralSalePrintOptionDialogBinding
import com.shwemigoldshop.shwemisale.databinding.LoadingDialogBinding
import com.shwemigoldshop.shwemisale.databinding.ShwemiSuccessDialogBinding
import com.shwemigoldshop.shwemisale.screen.goldFromHome.getKPYFromYwae

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

fun Context.showGoldCaratChangeAlertDialog(fromValue:String,changedValue:String,  onClick: (value:String) -> Unit) {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater: LayoutInflater = LayoutInflater.from(builder.context)
    val alertDialogBinding = AlertDialogGoldCaratChangeBinding.inflate(
        inflater, ConstraintLayout(builder.context), false
    )
    builder.setView(alertDialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)

    alertDialogBinding.tvFromValue.text = getString(R.string.mmk_value,fromValue)
    alertDialogBinding.tvToValue.text = getString(R.string.mmk_value,changedValue)
    alertDialogBinding.ivCross.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialogBinding.btnContinue.setOnClickListener {
        alertDialog.dismiss()
        onClick(changedValue)
    }
    alertDialogBinding.tvStop.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialog.show()
}

fun Context.showABuyingPriceChangeAlertDialog(fromValue:String,changedValue:String, fromBValue:String,toBValue:String, onClick: (value:String) -> Unit) {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater: LayoutInflater = LayoutInflater.from(builder.context)
    val alertDialogBinding = AlertDialogABuyingPriceChangeBinding.inflate(
        inflater, ConstraintLayout(builder.context), false
    )
    builder.setView(alertDialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)

    alertDialogBinding.tvFromValue.text = getString(R.string.mmk_value,fromValue)
    alertDialogBinding.tvToValue.text = getString(R.string.mmk_value,changedValue)

    alertDialogBinding.tvFromBValue.text = getString(R.string.mmk_value,fromBValue)
    alertDialogBinding.tvToBValue.text = getString(R.string.mmk_value,toBValue)

//    val fromKpy = getKPYFromYwae(fromYwae)
//    alertDialogBinding.tvFromKpy.text = getString(R.string.kpy_value,fromKpy[0].toInt().toString(),fromKpy[1].toInt().toString(),fromKpy[2].toString())
//
//    val toKpy = getKPYFromYwae(toYwae)
//    alertDialogBinding.tvToKpy.text = getString(R.string.kpy_value,toKpy[0].toInt().toString(),toKpy[1].toInt().toString(),toKpy[2].toString())
    alertDialogBinding.ivCross.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialogBinding.btnContinue.setOnClickListener {
        alertDialog.dismiss()
        onClick(changedValue)
    }
    alertDialogBinding.tvStop.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialog.show()
}

fun Context.showBBuyingPriceChangeAlertDialog(fromValue:String,changedValue:String, fromBValue:String,toBValue:String, onClick: (value:String) -> Unit) {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater: LayoutInflater = LayoutInflater.from(builder.context)
    val alertDialogBinding = AlertDialogBBuyingPriceChangeBinding.inflate(
        inflater, ConstraintLayout(builder.context), false
    )
    builder.setView(alertDialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)

    alertDialogBinding.tvFromValue.text = getString(R.string.mmk_value,fromValue)
    alertDialogBinding.tvToValue.text = getString(R.string.mmk_value,changedValue)

    alertDialogBinding.tvFromAValue.text = getString(R.string.mmk_value,fromBValue)
    alertDialogBinding.tvToAValue.text = getString(R.string.mmk_value,toBValue)

//    val fromKpy = getKPYFromYwae(fromYwae)
//    alertDialogBinding.tvFromKpy.text = getString(R.string.kpy_value,fromKpy[0].toInt().toString(),fromKpy[1].toInt().toString(),fromKpy[2].toString())
//
//    val toKpy = getKPYFromYwae(toYwae)
//    alertDialogBinding.tvToKpy.text = getString(R.string.kpy_value,toKpy[0].toInt().toString(),toKpy[1].toInt().toString(),toKpy[2].toString())
    alertDialogBinding.ivCross.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialogBinding.btnContinue.setOnClickListener {
        alertDialog.dismiss()
        onClick(changedValue)
    }
    alertDialogBinding.tvStop.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialog.show()
}
fun Context.showCBuyingPriceChangeAlertDialog(fromYwae:Double,toYwae:Double,changedValue: String, onClick: (value:String) -> Unit) {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater: LayoutInflater = LayoutInflater.from(builder.context)
    val alertDialogBinding = AlertDialogCBuyingPriceChangeBinding.inflate(
        inflater, ConstraintLayout(builder.context), false
    )
    builder.setView(alertDialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)

    val fromKpy = getKPYFromYwae(fromYwae)
    alertDialogBinding.tvFromKpyValue.text = getString(R.string.kpy_value,fromKpy[0].toInt().toString(),fromKpy[1].toInt().toString(),fromKpy[2].toString())

    val toKpy = getKPYFromYwae(toYwae)
    alertDialogBinding.tvToKpyValue.text = getString(R.string.kpy_value,toKpy[0].toInt().toString(),toKpy[1].toInt().toString(),toKpy[2].toString())
    alertDialogBinding.ivCross.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialogBinding.btnContinue.setOnClickListener {
        alertDialog.dismiss()
        onClick(changedValue)
    }
    alertDialogBinding.tvStop.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialog.show()
}

fun Context.showFGoldWeighteChangeAlertDialog(fromYwae:Double,toYwae:Double,modifiedValueType:String,onClick: () -> Unit) {
    val builder = MaterialAlertDialogBuilder(this)
    val inflater: LayoutInflater = LayoutInflater.from(builder.context)
    val alertDialogBinding = AlertDialogFWieghtChangeBinding.inflate(
        inflater, ConstraintLayout(builder.context), false
    )
    builder.setView(alertDialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.setCancelable(false)
    when(modifiedValueType){
        "wastage"->{
            alertDialogBinding.impurityChangedLayout.isVisible = false
            alertDialogBinding.wastageChangedLayout.isVisible = true
            val fromKpy = getKPYFromYwae(fromYwae)
            alertDialogBinding.tvFromWastageKpyValue.text = getString(R.string.kpy_value,fromKpy[0].toInt().toString(),fromKpy[1].toInt().toString(),fromKpy[2].toString())

            val toKpy = getKPYFromYwae(toYwae)
            alertDialogBinding.tvToWastageKpyValue.text = getString(R.string.kpy_value,toKpy[0].toInt().toString(),toKpy[1].toInt().toString(),toKpy[2].toString())
        }
        "impurity"->{
            alertDialogBinding.impurityChangedLayout.isVisible = true
            alertDialogBinding.wastageChangedLayout.isVisible = false
            val fromKpy = getKPYFromYwae(fromYwae)
            alertDialogBinding.tvFromImpurityKpyValue.text = getString(R.string.kpy_value,fromKpy[0].toInt().toString(),fromKpy[1].toInt().toString(),fromKpy[2].toString())

            val toKpy = getKPYFromYwae(toYwae)
            alertDialogBinding.tvToImpurityKpyValue.text = getString(R.string.kpy_value,toKpy[0].toInt().toString(),toKpy[1].toInt().toString(),toKpy[2].toString())
        }
    }

    alertDialogBinding.ivCross.setOnClickListener {
        alertDialog.dismiss()
    }
    alertDialogBinding.btnContinue.setOnClickListener {
        alertDialog.dismiss()
        onClick()
    }
    alertDialogBinding.tvStop.setOnClickListener {
        alertDialog.dismiss()
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

