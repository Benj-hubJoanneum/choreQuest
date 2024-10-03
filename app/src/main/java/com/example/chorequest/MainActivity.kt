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
import com.example.chorequest.service.FirebaseService
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseService = FirebaseService()

        val household = Group(
            uuid = UUID.randomUUID().toString(),
            name = "Household",
            lineItems = listOf(),  // Empty at first, will be populated with chores (LineItems)
            users = listOf()       // Empty at first, will be populated with residents (Users)
        )

        val user1 = User(
            uuid = UUID.randomUUID().toString(),
            name = "Anna"
        )

        val user2 = User(
            uuid = UUID.randomUUID().toString(),
            name = "Michael"
        )

        val user3 = User(
            uuid = UUID.randomUUID().toString(),
            name = "Hello. My name is Inigo Montoya. You killed my father. Prepare to die"
        )

        val chore1 = LineItem(
            uuid = UUID.randomUUID().toString(),
            title = "Orchidee gießen",
            date = "2024-10-03",
            assignee = "John Doe"
        )

        val chore2 = LineItem(
            uuid = UUID.randomUUID().toString(),
            title = "Staubsaugen",
            date = "2024-10-04",
            assignee = "Jane Doe"
        )

        val chore3 = LineItem(
            uuid = UUID.randomUUID().toString(),
            title = "Meet local King",
            date = "2024-10-05",
            assignee = "Alice Doe"
        )

        CoroutineScope(Dispatchers.IO).launch {
            firebaseService.addGroup(household)

            // Add users to Firebase
            firebaseService.addUser(user1)
            firebaseService.addUser(user2)
            firebaseService.addUser(user3)

            // Add users to group
            firebaseService.addUserToGroup(user1, household.uuid.toString())
            firebaseService.addUserToGroup(user2, household.uuid.toString())
            firebaseService.addUserToGroup(user3, household.uuid.toString())

            // Add chores (LineItems) to Firebase and assign to the group
            firebaseService.addLineItem(chore1, household.uuid.toString())
            firebaseService.addLineItem(chore2, household.uuid.toString())
            firebaseService.addLineItem(chore3, household.uuid.toString())
        }




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
    }
}