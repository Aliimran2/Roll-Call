package com.miassolutions.rollcall.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.ActivityMainBinding
import com.miassolutions.rollcall.utils.loadBannerAd
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var adView: AdView


    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        MobileAds.initialize(this){}

        setSupportActionBar(binding.toolbar)

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.toolbar.setupWithNavController(navController)

        adView = loadBannerAd(
            context = this,
            container = binding.bannerAdView,
            adUnitId = getString(R.string.banner_ad_unit_id),

            )

    }
}