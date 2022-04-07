package com.example.socialmediaapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_third.*
import kotlinx.android.synthetic.main.fragment_third.view.*

class ThirdFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_third, container, false) // Inflated with the xml file

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // Once the view has been created
        super.onViewCreated(view, savedInstanceState)

        signOutButton.setOnClickListener(){ // In fragments, items in the xml don't have to be assigned variables in Kotlin as this is done with the inflation stage, when button pressed

            signOut() // Run signOut function

        }

    }

    private fun signOut() {
        val intent = Intent(activity, FirebaseAuth::class.java) // Set the intent to go to the firebase auth so that the user can sign out
        startActivity(intent) // Run the activity
    }

}