package com.shwemigoldshop.shwemisale.network

import android.content.Context
import com.epson.epos2.printer.Printer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PrinterModule {

    @Provides
    @Singleton
    fun providePrinter(@ApplicationContext context: Context): Printer {
        return Printer(Printer.TM_M30, Printer.MODEL_ANK, context)
    }
}