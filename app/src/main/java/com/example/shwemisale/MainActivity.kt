package com.example.shwemisale

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemisale.databinding.ActivityMainBinding
import com.example.shwemisale.sellModule.sellStart.SellStartFragmentDirections
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var loading : AlertDialog


    private val startDestinationList = listOf<Int>(
        R.id.loginFragment
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loading = this.getAlertDialog()

        //  setContentView(R.layout.activity_main)
         binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        setSupportActionBar(binding.materialToolbar)


        val navController = navHostFragment.navController
        drawerLayout = binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
        NavigationUI.setupWithNavController(binding.navView,navController)
//        val logoutbutton = this.findViewById<Button>(R.id.btn_logout)
//        logoutbutton.setOnClickListener {
//            viewModel.logout()
//
//        }
        binding.navView.getHeaderView(0).findViewById<MaterialButton>(R.id.btn_logout).setOnClickListener {
            viewModel.logout()
        }
        binding.navView.setNavigationItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.btn_logout->{
                    viewModel.logout()
                }
                else -> {}
            }
            binding.drawerLayout.close()
            true
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
    override fun onSupportNavigateUp() : Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }



}