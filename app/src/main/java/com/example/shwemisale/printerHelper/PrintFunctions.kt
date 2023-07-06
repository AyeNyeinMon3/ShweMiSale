package com.example.shwemisale.printerHelper

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import com.example.shwemisale.R
import java.lang.Exception

fun printPdf(path: String,context: Context) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    try {
        val printAdapter = PdfDocumentAdapter(context, path)
        printManager.print(
            "${context.getString(R.string.app_name)} Document", printAdapter,
            PrintAttributes.Builder().build()
        )
    } catch (e: Exception) {
        Log.e("AkpDev", e.message.orEmpty())
    }
}