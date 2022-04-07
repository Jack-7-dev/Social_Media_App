package com.example.socialmediaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar

class FirebaseAuth : AppCompatActivity() {

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()){ // Start another activity within the app with the firebase sign-in flow UI
        res -> // Set the result of this function to temporary variable "res"
        this.onSignInResult(res) // Run onSignInResult with the parameter of the result of the registerForActivityResult function
    }

    override fun onCreate(savedInstanceState: Bundle?) { // Once everything is initialised
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_auth) // Relate the firebase_auth.xml file to this kotlin file

        val signInButton = findViewById<Button>(R.id.button2) // Assign the variable signInButton to the sign-in button in the xml
        signInButton.setOnClickListener{ // If the button is pressed

            fun createSignInIntent() { // Create a firebase sign-in flow UI with the following providers and intent
                val providers = arrayListOf(
                    EmailBuilder().build(), // Email sign-in method
                    IdpConfig.GoogleBuilder().build()) // Google sign-in method, this was never fully implemented as I needed to pay a £25 google developer fee for it

                val signInIntent = AuthUI.getInstance() // Get the user authorisation ID
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers) // Set available providers to the previous variable array of providers
                    .build() // Build the sign in intent
                signInLauncher.launch(signInIntent) // Launch the signInLauncher
            }

            createSignInIntent() // Run the function to create the sign-in screen

        }

        val signOutButton = findViewById<Button>(R.id.button3) // Assign the variable signOutButton to the sign-out button in the xml
        signOutButton.setOnClickListener{ // When button pressed

            AuthUI.getInstance() // Retrieve the firebase authorisation user ID
                .signOut(this) // Sign out the specific user from the device
                .addOnCompleteListener { // When completed
                    Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show() // Show a pop-up at the bottom of the screen saying "Signed out successfully"
                }


        }

    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) { // When onSignInResult is called with the result of the firebase UI authentication for the user
        if (result.resultCode == RESULT_OK) { // If the result comes back with a result code of RESULT_OK
            val intent = Intent(this, Home::class.java) // Set the intent to go to the Home screen
            startActivity(intent) // Run the Home activity to let the user on the app
        }
    }

}