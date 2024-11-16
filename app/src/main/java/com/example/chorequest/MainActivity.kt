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
import com.example.chorequest.service.FireStoreService
import com.example.chorequest.service.server.OpenStackService
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fireStoreService = FireStoreService()
    private val serverService = OpenStackService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_mychores, R.id.navigation_add, R.id.navigation_all
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.hide()

        // Fetch data from Firestore
        fetchDataFromFirestore()

        // Fetch data from the Python server
        fetchDataFromServer()
    }

    /**
     * Fetch data from Firestore
     */
    private fun fetchDataFromFirestore() {
        runBlocking {
            try {
                val users = fireStoreService.getUsers()
                val groups = fireStoreService.getGroups()
                val lineItems = fireStoreService.getLineItems()

                Log.d(TAG, "Fetched Users: $users")
                Log.d(TAG, "Fetched Groups: $groups")
                Log.d(TAG, "Fetched Line Items: $lineItems")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching data from Firestore", e)
            }
        }
    }

    /**
     * Fetch data from the Python server
     */
    private fun fetchDataFromServer() {
        runBlocking {
            try {


                // Example of saving data to the server
                val userJson = JSONObject()
                userJson.put("id", "0066")
                userJson.put("name", "Jane Doe")
                userJson.put("email", "janedoe@example.com")

                /*serverService.saveDataById("0066", userJson) { success ->
                    if (success) {
                        Log.d(TAG, "Successfully saved data to server")
                    } else {
                        Log.e(TAG, "Failed to save data to server")
                    }
                }*/


                // Fetch data with another ID for testing
                serverService.fetchDataById("0066") { response ->
                    response?.let {
                        Log.d(TAG, "Fetched Data from Server: $it")
                    } ?: run {
                        Log.e(TAG, "No response from server for ID 0045")
                    }
                }


            } catch (e: Exception) {
                Log.e(TAG, "Error communicating with server", e)
            }
        }
    }
}
