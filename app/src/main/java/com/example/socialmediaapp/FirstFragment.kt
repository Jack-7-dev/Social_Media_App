package com.example.socialmediaapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.NullPointerException
import kotlin.math.log


class FirstFragment : Fragment() {

    private lateinit var database: DatabaseReference // Initialise the database early so that dependencies can be created before loading main code

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_first, container, false) // How the fragment_first.xml layout file is "inflated" to show on screen and use variables

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){ // Once everything is initialised do this
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference // Firebase database location imported from google-services.json and set to the late initialised variable database
        val storage = Firebase.storage // Firebase storage location imported from google-services.json
        val localfile1 = File.createTempFile("tempImage", ".jpg") // localfile1 to be used for user images as a temporary file
        val localfile2 = File.createTempFile("tempImage1", ".jpg") // localfile2 to be used for user profile pictures as a temporary file
        val databaseReference = database.child("images") // As all image data is stored in the "images" path of the database
        var pictureBitmap: Bitmap
        var pfpBitmap: Bitmap
        var httpsReference: StorageReference
        var pfpReference: StorageReference
        var iterator = 1
        var iterator2 = 1
        var itemIterator = -1
        var pictureAuthor: String // Initialising variables now so that they don't have to be created each time the for loop runs
        databaseReference.get().addOnSuccessListener { res -> // Get all data from the database, and create temporary variable "res" to store the result of this
            val split: List<String> = res.toString().split("=".toRegex()) // Split all the data as a string at every "=" sign
            for (item in split) { // For every string separated by a space in split
                itemIterator += 1 // Index counter for the list
                if (item.startsWith("{author")) { // If the current item starts with "{author"
                    httpsReference = storage.getReferenceFromUrl(split[itemIterator + 3].split(",")[0]) // Get the third item after the current one as the image storage reference
                    pfpReference = storage.getReferenceFromUrl(split[itemIterator + 2].split(",")[0]) // Get the second item after the current one as the profile picture storage reference
                    httpsReference.getFile(localfile1).addOnSuccessListener { // Retrieve the file from the image storage reference and store it in the temporary file "localfile1"
                        if(BitmapFactory.decodeFile(localfile1.absolutePath)!=null){pictureBitmap = BitmapFactory.decodeFile(localfile1.absolutePath)
                            // Once file retrieved and it isn't empty, decode the binary to an image file
                        pictureAuthor = split[itemIterator - 4].split(",")[0] // Get the fourth previous item from the database and set it to the image author
                        if (iterator == 1) { // If it is the first picture decoded
                            homeImage1.setImageBitmap(pictureBitmap) // Set it as the first image
                            author1.text = pictureAuthor // Set the author equal to the image author text
                        }
                        if (iterator == 2) { // If it is the second picture decoded
                            homeImage2.setImageBitmap(pictureBitmap) // Set it as the second image
                            author2.text = pictureAuthor // Set the author equal to the image author text
                        }
                        if (iterator == 3) { // If it is the third picture decoded
                            homeImage3.setImageBitmap(pictureBitmap) // Set it as the third image
                            author3.text = pictureAuthor // Set the author equal to the image author text
                        }
                        if (iterator == 4) { // If it is the fourth picture decoded
                            homeImage4.setImageBitmap(pictureBitmap) // Set it as the fourth image
                            author4.text = pictureAuthor // Set the author equal to the image author text
                        }
                        if (iterator == 5) { // If it is the fifth picture decoded
                            homeImage5.setImageBitmap(pictureBitmap) // Set it as the fifth image
                            author5.text = pictureAuthor // Set the author equal to the image author text
                        }
                        iterator += 1 // Counts the amount of images decoded
                    }}
                    pfpReference.getFile(localfile2).addOnSuccessListener { // Retrieve the file from the image storage reference and store it in the temporary file "localfile2"
                        if(BitmapFactory.decodeFile(localfile2.absolutePath)!=null){pfpBitmap = BitmapFactory.decodeFile(localfile2.absolutePath) // Once file retrieved and it isn't empty, decode the binary to an image file
                        if (iterator2 == 1) { // If it is the first profile picture decoded
                            pfp1.setImageBitmap(pfpBitmap) // Set it as the first profile picture
                        }
                        if (iterator2 == 2) { // If it is the second profile picture decoded
                            pfp2.setImageBitmap(pfpBitmap) // Set it as the second profile picture
                        }
                        if (iterator2 == 3) { // If it is the third profile picture decoded
                            pfp3.setImageBitmap(pfpBitmap) // Set it as the third profile picture
                        }
                        if (iterator2 == 4) { // If it is the fourth profile picture decoded
                            pfp4.setImageBitmap(pfpBitmap) // Set it as the fourth profile picture
                        }
                        if (iterator2 == 5) { // If it is the fifth profile picture decoded
                            pfp5.setImageBitmap(pfpBitmap) // Set it as the fifth profile picture
                        }
                        iterator2 += 1 // Counts the amount of profile pictures decoded
                    }}
                }
            }
        }
    }
}