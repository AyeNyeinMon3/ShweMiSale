package com.example.shwemisale.qrscan

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.shwemisale.R
import com.example.shwemisale.databinding.ActivityQrScanBinding
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ViewfinderView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

class CaptureAct:Activity() {
    private lateinit var binding: ActivityQrScanBinding
    private lateinit var capture : CaptureManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityQrScanBinding>(this, R.layout.activity_qr_scan)
        val barcodeScannerView = binding.zxingBarcodeScanner

//        val browse = binding.btnBrowseFromGallery

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
//        if (!hasFlash()) {
//            switchFlashlightButton.visibility = View.GONE
//        }
        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.setShowMissingCameraPermissionDialog(false)
        capture.decode()
//        changeMaskColor(null)
//        changeLaserVisibility(true)
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return binding.zxingBarcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

//    fun switchFlashlight(view: View?) {
//        if (getString(R.string.turn_on_flashlight) == switchFlashlightButton.getText()) {
//            barcodeScannerView.setTorchOn()
//        } else {
//            barcodeScannerView.setTorchOff()
//        }
//    }

    fun changeMaskColor(view: View?) {
        val viewfinderView = findViewById<View>(R.id.zxing_viewfinder_view) as ViewfinderView
        val rnd = Random()
        val color: Int = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        viewfinderView.setMaskColor(color)
    }

    fun changeLaserVisibility(visible: Boolean) {
        val viewfinderView = findViewById<View>(R.id.zxing_viewfinder_view) as ViewfinderView
        viewfinderView.setLaserVisibility(visible)
    }

//    fun onTorchOn() {
//        switchFlashlightButton.setText(R.string.turn_off_flashlight)
//    }
//
//    fun onTorchOff() {
//        switchFlashlightButton.setText(R.string.turn_on_flashlight)
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



//    override fun onTorchOn() {
//        switchFlashlightButton.setText(R.string.turn_off_flashlight)
//    }
//
//    override fun onTorchOff() {
//        switchFlashlightButton.setText(R.string.turn_on_flashlight)
//    }

}