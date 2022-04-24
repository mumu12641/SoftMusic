package com.example.softmusic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.softmusic.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        DynamicColors.applyIfAvailable(this)
        val navController: NavController =
            findNavController(R.id.nav_host_fragment_activity_main)
        val navigationView: BottomNavigationView = activityMainBinding.navView
        setupWithNavController(navigationView, navController)
    }

    fun setTitle(title: String?) {
        activityMainBinding.appBar.title = title
    }
}