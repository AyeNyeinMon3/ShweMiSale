package com.example.shwemisale

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.shwemisale.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val startDestinationList = listOf<Int>(
        R.id.loginFragment
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_main)
         binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        setSupportActionBar(binding.materialToolbar)


        val navController = navHostFragment.navController
        drawerLayout = binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)
        NavigationUI.setupWithNavController(binding.navView,navController)



        navController.addOnDestinationChangedListener{nc:NavController,nd:NavDestination, _->
            binding.materialToolbar.visibility =
                if(startDestinationList.contains(nd.id))
                    View.GONE
            else View.VISIBLE
        }//custom toolbar



    }
    override fun onSupportNavigateUp() : Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }



}