package com.miassolutions.rollcall.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.ActivityMainBinding
import com.miassolutions.rollcall.ui.fragments.StudentsFragment.Companion.klassName

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)




        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.statsFragment,
                R.id.attendanceFragment,
                R.id.studentsFragment,
                R.id.settingsFragment
            )
        )




        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)

//        binding.bottomNavView.setOnItemSelectedListener {item ->
//            when(item.itemId){
//                R.id.statsFragment -> {
//                    navController.navigate(R.id.statsFragment)
//                    binding.toolbar.subtitle = null
//                    true
//                }
//
//                R.id.attendanceFragment -> {
//                    navController.navigate(R.id.attendanceFragment)
//                    binding.toolbar.apply {
//                        subtitle = "06.06.2025"
//                        isSubtitleCentered = true
//                    }
//                    true
//                }
//
//                R.id.studentsFragment -> {
//                    navController.navigate(R.id.studentsFragment)
//                    binding.toolbar.subtitle = klassName
//                    binding.toolbar.isSubtitleCentered = true
//                    true
//                }
//
//
//                R.id.settingsFragment -> {
//                    navController.navigate(R.id.settingsFragment)
//                    binding.toolbar.subtitle = null
//                    true
//                }
//                else ->false
//            }
//        }



    }


}