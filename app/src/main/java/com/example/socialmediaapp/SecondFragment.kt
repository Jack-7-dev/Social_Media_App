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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_second, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()

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

    }

    private fun initialise() {

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val pFPStorageReference = FirebaseStorage.getInstance().getReference("images/userUploaded/${user?.uid.toString()}/pfp.jpg")
        val localFile = File.createTempFile("tempImage", "jpg")
        Username.text = user?.displayName.toString()
        pFPStorageReference.getFile(localFile).addOnSuccessListener {
            userPFP.setImageBitmap(BitmapFactory.decodeFile(localFile.absolutePath))
        }

    }

    private fun setPFP(){

        val userFolder = Firebase.auth.currentUser?.uid.toString()
        val storageReference = FirebaseStorage.getInstance().getReference("images/userUploaded/$userFolder/pfp.jpg")
        storageReference.putFile(ImageUri).addOnSuccessListener { image1.setImageURI(null) }
        userPFP.setImageURI(ImageUri)

    }


    private fun retrieveImages() {

        val user = Firebase.auth.currentUser?.uid
        val storage = Firebase.storage
        val userFolder = user.toString()
        val localfile = File.createTempFile("tempImage", ".jpg")
        val storageRef = FirebaseStorage.getInstance().reference.child("images/userUploaded/$userFolder")
        var iterator = 1
        var httpsReference: StorageReference
        var bitmap: Bitmap

        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAllTask.addOnCompleteListener { result ->
            val items: List<StorageReference> = result.result!!.items
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener { it ->

                    httpsReference = storage.getReferenceFromUrl("$it")
                    httpsReference.getFile(localfile).addOnCompleteListener {
                        Log.d("localfile",localfile.toString())
                        Log.d("localfile.absolutePath",localfile.absolutePath.toString())
                        if (BitmapFactory.decodeFile(localfile.absolutePath)!=null){bitmap = BitmapFactory.decodeFile(localfile.absolutePath)

                        if (iterator==1){image1.setImageBitmap(bitmap)}
                        if (iterator==2){image2.setImageBitmap(bitmap)}
                        if (iterator==3){image3.setImageBitmap(bitmap)}

                        Log.d("images", "$it")
                        Log.d("iterator", "$iterator")
                        iterator+=1
                    }}
                }
            }
        }
    }

    private fun uploadImage() {

        @IgnoreExtraProperties
        data class Image(
            val author: String? = null,
            val url: String? = null,
            val pfp: String? = null
        )

        database = Firebase.database.reference
        val databaseReference = database.child("images")
        var formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        var fileFormat = formatter.format(Date())
        var fileName = "$fileFormat.jpg"
        val user = Firebase.auth.currentUser?.uid
        val userName = Firebase.auth.currentUser?.displayName
        val userFolder = user.toString()
        val pfp =
            FirebaseStorage.getInstance().getReference("images/userUploaded/$userFolder/pfp.jpg")

        val storageReference =
            FirebaseStorage.getInstance().getReference("images/userUploaded/$userFolder/$fileName")

        if(try {
                storageReference.putFile(ImageUri)
            } catch (e: UninitializedPropertyAccessException) {
                null
            }!=null){storageReference.putFile(ImageUri).addOnSuccessListener {
            image1.setImageURI(null)
            pfp.downloadUrl.addOnSuccessListener { res ->
                var pfpUrl = res.toString().split("=".toRegex())[0]
                storageReference.downloadUrl.addOnSuccessListener { res ->
                    var imageStructure = Image(userName, res.toString(), pfpUrl)
                    databaseReference.child(fileFormat).setValue(imageStructure)
                }
            }
        }}
        else{
            Toast.makeText(activity,"Make sure to add an image before pressing upload!",Toast.LENGTH_LONG).show()
        }
    }



    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK){
            ImageUri= data?.data!!
            image1.setImageURI(ImageUri)
        }
    }
}
