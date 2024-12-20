package com.example.chorequest

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chorequest.databinding.ActivityMainBinding
import com.example.chorequest.service.FireStoreService
import com.example.chorequest.service.server.OpenStackService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fireStoreService = FireStoreService()
    private val serverService = OpenStackService()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null) {
            // Redirect to Login activity if the user is not authenticated
            navigateToLoginActivity()
            return
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val bundle = Bundle().apply {
            putString("GROUP_ID", "6CL3twvvJP0AoNvPMVoT") // Replace with actual group ID
        }
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_mychores, R.id.navigation_add, R.id.navigation_all
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.hide()

        Toast.makeText(
            this,
            "Welcome, ${firebaseAuth.currentUser?.email}",
            Toast.LENGTH_SHORT
        ).show()

        // Fetch data from Firestore
        // fetchDataFromFirestore()

        // Fetch data from the Python server
        // fetchDataFromServer()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Fetch data from Firestore
     */
    private fun fetchDataFromFirestore() {
        runBlocking {
            try {
                val anna = fireStoreService.getUsersByID("AyQrw9UMDn10irljeoC4") // Example
                val group = fireStoreService.getGroupByID("6CL3twvvJP0AoNvPMVoT") // Example
                val lineitem = group?.lineItems?.first()
                    ?.let { fireStoreService.getLineItemByID(it) }
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
                val userJson = JSONObject()
                userJson.put("id", "0066")
                userJson.put("name", "Jane Doe")
                userJson.put("email", "janedoe@example.com")

                serverService.fetchDataById("0066") { response ->
                    response?.let {
                        Log.d(TAG, "Fetched Data from Server: $it")
                    } ?: run {
                        Log.e(TAG, "No response from server for ID 0066")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error communicating with server", e)
            }
        }
    }
}
