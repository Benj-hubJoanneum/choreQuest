package com.example.chorequest

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chorequest.databinding.ActivityMainBinding
import com.example.chorequest.model.Group
import com.example.chorequest.model.LineItem
import com.example.chorequest.model.User
import com.example.chorequest.service.FireStoreService
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.runBlocking
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_mychores, R.id.navigation_add, R.id.navigation_all
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.hide()
        fetchDataFromFirestore()
    }
    private val fireStoreService = FireStoreService()
    private fun fetchDataFromFirestore() {
        runBlocking {
            try {
                val users = fireStoreService.getUsers()
                val groups = fireStoreService.getGroups()
                val lineItems = fireStoreService.getLineItems()

                Log.d(TAG, "Fetched Users: $users")
                Log.d(TAG, "Fetched Groups: $groups")
                Log.d(TAG, "Fetched Line Items: $lineItems")

                // Process the data as needed
                // You can store them in a ViewModel or pass to UI components
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data from Firestore", e)
            }
        }
    }
}
