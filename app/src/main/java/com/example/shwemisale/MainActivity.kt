package com.example.shwemisale

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.epson.epos2.Epos2Exception
import com.epson.epos2.printer.Printer
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemi.util.showSuccessDialog
import com.example.shwemisale.databinding.ActivityMainBinding
import com.example.shwemisale.databinding.DialogIpAddressBinding
import com.example.shwemisale.databinding.ShwemiSuccessDialogBinding
import com.example.shwemisale.room_database.AppDatabase
import com.example.shwemisale.screen.sellModule.sellStart.SellStartFragmentDirections
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var dialogIpAddressBinding: DialogIpAddressBinding
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var loading : AlertDialog
    private lateinit var navController:NavController
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle


    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>

    @Inject
    lateinit var printer:Printer


    private val startDestinationList = listOf<Int>(
        R.id.loginFragment
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loading = this.getAlertDialog()
        storagePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
               //do something
            }
        }
        if (isExternalStoragePermissionGranted().not()) {
            requestStoragePermission()
        } else {
            //do something
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        //  setContentView(R.layout.activity_main)
         binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//
//        setSupportActionBar(binding.materialToolbar)
//
//
//        navController = navHostFragment.navController
//        drawerLayout = binding.drawerLayout
//        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
//        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
//        NavigationUI.setupWithNavController(binding.navView,navController)
////        val logoutbutton = this.findViewById<Button>(R.id.btn_logout)
////        logoutbutton.setOnClickListener {
////            viewModel.logout()
////
////        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        loading = this.getAlertDialog()

        setSupportActionBar(binding.materialToolbar)

        val navController = navHostFragment.navController
        drawerLayout = binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
        NavigationUI.setupWithNavController(binding.navView,navController)
        binding.navView.getHeaderView(0).findViewById<MaterialButton>(R.id.btn_logout).setOnClickListener {
            viewModel.logout()
        }

        navController.addOnDestinationChangedListener{nc:NavController,nd:NavDestination, _->
            binding.materialToolbar.visibility =
                if(startDestinationList.contains(nd.id))
                    View.GONE
            else View.VISIBLE
        }//custom toolbar


        viewModel.logoutLiveData.observe(this){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    Toast.makeText(this,"log out successful", Toast.LENGTH_LONG).show()
                    navController.navigate(
                        SellStartFragmentDirections.actionSellStartFragmentToLoginFragment()
                    )
                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(this,it.message, Toast.LENGTH_LONG).show()
                    navController.navigate(
                        SellStartFragmentDirections.actionSellStartFragmentToLoginFragment()
                    )
                }
                else -> {}
            }
        }


    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.sellStartFragment->{
//                navController.navigate(R.id.sellStartFragment)
//            }
//            R.id.pawnInterestFragment->{
//                navController.navigate(R.id.pawnInterestFragment)
//
//            }
//            R.id.payBalanceFragment->{
//                navController.navigate(R.id.payBalanceFragment)
//
//            }
//
//            else->{}
//        }
//        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//
//    }
    override fun onSupportNavigateUp() : Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteDatabase("AppDatabase")
    }


    private fun requestStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
//        } else {
        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

//        }
    }

    private fun isExternalStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
           this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}