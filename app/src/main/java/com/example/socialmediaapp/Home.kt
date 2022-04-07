package com.example.socialmediaapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { // No inflation necessary as the user never sees this file, it is used a navigation file between all of the fragments
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home) // Set the xml file items to be usable in the Kotlin file
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView) // Set the previously created bottom nav to a variable
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        // Set navHostFragment equal to all of the fragments together in a container view, so that the program can navigate between them
        val navController = navHostFragment.navController // Set the navController equal to all of the fragments in the file
        bottomNavigationView.setupWithNavController(navController) // Set the bottom navigation xml as the navigation for the user to change between the different fragments
    }
}