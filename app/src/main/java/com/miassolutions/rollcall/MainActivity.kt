package com.miassolutions.rollcall

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.miassolutions.rollcall.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)



        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.statsFragment, R.id.attendanceFragment, R.id.studentsFragment, R.id.settingsFragment)
        )




        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)

        binding.bottomNavView.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.statsFragment -> {
                    navController.navigate(R.id.statsFragment)
                    true
                }

                R.id.attendanceFragment -> {
                    navController.navigate(R.id.attendanceFragment)
                    true
                }

                R.id.studentsFragment -> {
                    navController.navigate(R.id.studentsFragment)
                    true
                }


                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else ->false
            }
        }



    }


}