package com.example.socialmediaapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.android.synthetic.main.fragment_second.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class SecondFragment : Fragment() {

    private lateinit var ImageUri : Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var database: DatabaseReference
    // Late initialising important variables that need to be imported before functions can be run

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_second, container, false) // Inflate the file with the correct xml file

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // When the view is created
        super.onViewCreated(view, savedInstanceState)

        initialise() // Run initialise()

        selectButton.setOnClickListener{

            selectImage()

        }

        uploadButton.setOnClickListener{

            uploadImage()

        }

        retrieveButton.setOnClickListener{

            retrieveImages()

        }

        setPFPButton.setOnClickListener{

            setPFP()

        }
        // Let all buttons check for when they are pressed and run their corresponding functions
    }

    private fun initialise() { // Runs on second fragment in the foreground

        auth = Firebase.auth // All current session firebase authorisation details
        val user = Firebase.auth.currentUser // Current user authorisation details
        val pFPStorageReference = FirebaseStorage.getInstance().getReference("images/userUploaded/${user?.uid.toString()}/pfp.jpg")
        // Profile picture storage reference equal to the authenticated user UID path in cloud storage, "?" indicating that user is possibly null if they are not authenticated correctly
        val localFile = File.createTempFile("tempImage", "jpg") // Create a temporary file for the profile picture
        Username.text = user?.displayName.toString() // Set the text at the top of the screen equal to the user-set display name
        pFPStorageReference.getFile(localFile).addOnSuccessListener { // Retrieve the profile picture from the given path in cloud storage
            userPFP.setImageBitmap(BitmapFactory.decodeFile(localFile.absolutePath)) // Set the profile picture image equal to the image in storage
        }

    }

    private fun setPFP(){ // Runs when the user sets a new profile picture

        val userFolder = Firebase.auth.currentUser?.uid.toString() // Finds the user UID from authentication
        val storageReference = FirebaseStorage.getInstance().getReference("images/userUploaded/$userFolder/pfp.jpg") // Finds the user folder and profile picture storage location
        storageReference.putFile(ImageUri).addOnSuccessListener { image1.setImageURI(null) } // Put the new profile picture in storage and remove the selected photo from the screen
        userPFP.setImageURI(ImageUri) // Set the new profile picture on the picture spot

    }


    private fun retrieveImages() { // Runs when the user presses the retrieve button

        val user = Firebase.auth.currentUser?.uid // Finds the user UID from authentication
        val storage = Firebase.storage // Finds the firebase storage file from the google-services.json
        val userFolder = user.toString() // Sets the Int type UID to String
        val localfile = File.createTempFile("tempImage", ".jpg") // Create a temporary file for the images to be downloaded to
        val storageRef = FirebaseStorage.getInstance().reference.child("images/userUploaded/$userFolder") // Create a storage reference location to the user folder in cloud storage
        var iterator = 1
        var httpsReference: StorageReference
        var bitmap: Bitmap
        // Initialise variables now so that they don't have to be rewritten each time the loop runs
        val listAllTask: Task<ListResult> = storageRef.listAll() // List all images within the user folder, stored within the variable "listAllTask"
        listAllTask.addOnCompleteListener { result -> // When the download has completed, set the result of the download equal to the temporary variable "result"
            val items: List<StorageReference> = result.result!!.items // Sets "items" equal to each different item in the result of the bulk image download
            items.forEachIndexed { index, item -> // For each item within the list, with variables index and item
                item.downloadUrl.addOnSuccessListener { it -> // Get the cloud storage download URL for each image, and set it to the temp variable "it"
                    httpsReference = storage.getReferenceFromUrl("$it") // Get the image reference for the downloaded image URL
                    httpsReference.getFile(localfile).addOnCompleteListener { // Download the image from the image reference, and set it to the temp file "localfile"
                        if (BitmapFactory.decodeFile(localfile.absolutePath)!=null){bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                            // If the downloaded file is not empty, decode the file into a bitmap format
                            if (iterator==1){image1.setImageBitmap(bitmap)} // If it is the first image downloaded, set it as the first image on the screen
                            if (iterator==2){image2.setImageBitmap(bitmap)} // If it is the second image downloaded, set it as the second image on the screen
                            if (iterator==3){image3.setImageBitmap(bitmap)} // If it is the third image downloaded, set it as the third image on the screen
                            iterator+=1 // Counts the amount of images downloaded
                    }}
                }
            }
        }
    }

    private fun uploadImage() { // Runs when the user uploads an image

        @IgnoreExtraProperties // Ignore the rest of the properties that need to be called in a Class structure if they are not defined within it
        data class Image( // "data class" is just a user edited Class that is OOP
            val author: String? = null, // Sets the variable "author" to null with the expected type of data input to be a String
            val url: String? = null, // Sets the variable "url" to null with the expected type of data input to be a String
            val pfp: String? = null // Sets the variable "pfp" to null with the expected type of data input to be a String
        )

        database = Firebase.database.reference // Retrieves the database location from the google-services.json
        val databaseReference = database.child("images") // All images need to go in the "images" folder within the database
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        // Creates a formatter for the current date, with the Locale taken from the device's default country
        var fileFormat = formatter.format(Date()) // Makes the fileFormat variable the current date and time
        var fileName = "$fileFormat.jpg" // Sets the fileName of the image to be the current date and time with the "jpg" file format
        val user = Firebase.auth.currentUser?.uid // Gets the user UID from firebase authentication
        val userName = Firebase.auth.currentUser?.displayName // Sets the userName equal to the user-set display name
        val userFolder = user.toString() // Sets the userFolder equal to the string format of the user UID
        val pfp =
            FirebaseStorage.getInstance().getReference("images/userUploaded/$userFolder/pfp.jpg")
            // Sets the variable "pfp" equal to where the profile picture is in cloud storage

        val storageReference =
            FirebaseStorage.getInstance().getReference("images/userUploaded/$userFolder/$fileName")
            // Sets the variable "storageReference" equal to where the image will be uploaded to in cloud storage

        if(try { // Try this code
                storageReference.putFile(ImageUri)
            } catch (e: UninitializedPropertyAccessException) {
                null // If "UninitializedPropertyAccessException" error occurs, do this
            }!=null){storageReference.putFile(ImageUri).addOnSuccessListener { // If the image was uploaded successfully
            image1.setImageURI(null) // Remove the image from the imageView
            pfp.downloadUrl.addOnSuccessListener { res -> // Get the download URL for the user profile picture and set it to the temporary variable "res"
                var pfpUrl = res.toString().split("=".toRegex())[0] // Get the user profile picture download url from the result as the first word in the string downloaded
                storageReference.downloadUrl.addOnSuccessListener { res -> // Download the cloud storage URL for the uploaded image
                    var imageStructure = Image(userName, res.toString(), pfpUrl)
                    // Set the imageStructure variable equal to the Class object Image with the author set as the username, the download URl set to the URL, and the profile picture URL equal to the pfpURL
                    databaseReference.child(fileFormat).setValue(imageStructure)
                    // Upload the Class Object into the database
                }
            }
        }}
        else{
            Toast.makeText(activity,"Make sure to add an image before pressing upload!",Toast.LENGTH_LONG).show()
            // If the user has not selected an image before uploading, this will display a message telling them why it didn't work
        }
    }



    private fun selectImage() { // Runs when the user selects an image
        val intent = Intent() // Sets intent equal to the factory function Intent which allows programs to open other programs
        intent.type = "image/*" // Sets the intent type equal to getting user stored images
        intent.action = Intent.ACTION_GET_CONTENT // Sets the action type to getting the images so they can be used in the program once selected
        startActivityForResult(intent, 100) // Run the activity with the selected intent and the default OK request code 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // Once any Activity result has finished
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK){ // If the request code was 100 i.e. was it the selectImage() intent, and the image was selected sucessfully
            ImageUri= data?.data!! // Set the ImageUri data equal to the data from the image selected from storage
            image1.setImageURI(ImageUri) // Set the first image bitmap equal to the ImageUri selected from the intent
        }
    }
}
