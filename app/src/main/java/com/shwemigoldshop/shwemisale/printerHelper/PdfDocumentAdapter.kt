package com.shwemigoldshop.shwemisale.printerHelper

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.util.Log
import okio.IOException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class PdfDocumentAdapter(private val context:Context,private val path:String):PrintDocumentAdapter() {
    override fun onLayout(
        p0: PrintAttributes?,
        p1: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        layoutResultCallback: LayoutResultCallback?,
        p4: Bundle?
    ) {
        if (cancellationSignal!!.isCanceled){
            layoutResultCallback!!.onLayoutCancelled()
        }else{
            val builder = PrintDocumentInfo.Builder("file name")
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()
            layoutResultCallback!!.onLayoutFinished(builder.build(),p1!=p0)
        }
    }

    override fun onWrite(
        pageRanges: Array<out PageRange>?,
        parcelFileDescriptor: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        writeResultCallback: WriteResultCallback?
    ) {
        val file = File(path)
        var `in` :InputStream? = null
        var out :OutputStream?=null
        try {
            `in` = FileInputStream(file)
            out = FileOutputStream(parcelFileDescriptor!!.fileDescriptor)

            if (!cancellationSignal!!.isCanceled){
                `in`.copyTo(out)
                writeResultCallback!!.onWriteFinished(arrayOf(PageRange.ALL_PAGES))

            }else{
                writeResultCallback!!.onWriteCancelled()
            }
        }catch (e:Exception){
            writeResultCallback!!.onWriteFailed(e.message)
            Log.e("AkpDev",e.message.orEmpty())
        }
    }
}